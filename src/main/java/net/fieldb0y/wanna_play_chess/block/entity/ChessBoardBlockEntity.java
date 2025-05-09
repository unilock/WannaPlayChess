package net.fieldb0y.wanna_play_chess.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fieldb0y.wanna_play_chess.block.custom.ChessBoardBlock;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameOverState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessState;
import net.fieldb0y.wanna_play_chess.item.ModComponents;
import net.fieldb0y.wanna_play_chess.network.payloads.BlockPosPayload;
import net.fieldb0y.wanna_play_chess.screenhandler.ChessBoardScreenHandler;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;
import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState.*;

public class ChessBoardBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    public static final int LOBBY_STATE = 0;
    public static final int GAME_STATE = 1;
    public static final int GAME_OVER_STATE = 2;

    private GameState gameState;

    public List<ChessState> states = new ArrayList<>();
    public ChessState currentState;
    public boolean whiteSetInsereted, blackSetInserted;

    public ChessBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHESS_BOARD_BLOCK_ENTITY, pos, state);
        this.setGameState(Arrays.stream(GameState.values()).filter((GameState value)->value.nbtValue == state.get(ChessBoardBlock.GAME_STATE)).toList().getFirst());

        setupStates();
    }

    private void setupStates(){
        states.add(new ChessLobbyState(this));
        states.add(new ChessGameState(this));
        states.add(new ChessGameOverState(this));

        if (currentState == null){
            for (ChessState state : states){
                if (state.shouldUse()){
                    this.currentState = state; break;
                }
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state){
        changeGameStates();
        if (!world.isClient()){
            currentState.tick();
        }
    }

    public void changeGameStates(){
        ChessLobbyState chessLobbyState = (ChessLobbyState)states.get(LOBBY_STATE);

        if (gameState == GameState.NOT_READY_TO_PLAY){
            if ((chessLobbyState.getPlayersInLobby()[FIRST] != null && chessLobbyState.getPlayersInLobby()[SECOND] == null)
                    || (chessLobbyState.getPlayersInLobby()[FIRST] == null && chessLobbyState.getPlayersInLobby()[SECOND] != null)){
                setGameState(GameState.READY_FOR_SINGLEPLAYER_GAME);
            }
        }

        if (gameState == GameState.READY_FOR_SINGLEPLAYER_GAME){
            if (chessLobbyState.getPlayersInLobby()[FIRST] != null && chessLobbyState.getPlayersInLobby()[SECOND] != null){
                setGameState(GameState.READY_TO_PLAY);
            }
        }

        if (gameState == GameState.READY_TO_PLAY || gameState == GameState.READY_FOR_SINGLEPLAYER_GAME){
            if (chessLobbyState.getPlayersInLobby()[FIRST] == null && chessLobbyState.getPlayersInLobby()[SECOND] == null){
                setGameState(GameState.NOT_READY_TO_PLAY);
            }
            if ((chessLobbyState.getPlayersInLobby()[FIRST] != null && chessLobbyState.getPlayersInLobby()[SECOND] == null)
                    || (chessLobbyState.getPlayersInLobby()[FIRST] == null && chessLobbyState.getPlayersInLobby()[SECOND] != null)){
                setGameState(GameState.READY_FOR_SINGLEPLAYER_GAME);
            }
        }

        if (currentState != null && !currentState.shouldUse()){
            for (ChessState state : states){
                if (state.shouldUse()){
                    currentState.afterSwitch();
                    this.currentState = state; break;
                }
            }
        }
    }

    public void insertPiecesSet(int role){
        if (role == WHITE)
            this.whiteSetInsereted = true;
        else this.blackSetInserted = true;
        updateClient();
    }

    public void removePiecesSet(int role){
        if (role == WHITE)
            this.whiteSetInsereted = false;
        else this.blackSetInserted = false;
        updateClient();
    }

    public boolean isSetInserted(int role){
        return role == WHITE ? whiteSetInsereted : blackSetInserted;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        currentState.writeNbt(nbt);

        nbt.putInt("GameState", gameState.nbtValue);
        nbt.putBoolean("WhiteSetInserted", whiteSetInsereted);
        nbt.putBoolean("BlackSetInserted", blackSetInserted);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("GameState", NbtElement.INT_TYPE))
            this.gameState = Arrays.stream(GameState.values()).filter((GameState value)->value.nbtValue == nbt.getInt("GameState")).toList().getFirst();
        if (nbt.contains("WhiteSetInserted"))
            this.whiteSetInsereted = nbt.getBoolean("WhiteSetInserted");
        if (nbt.contains("BlackSetInserted"))
            this.blackSetInserted = nbt.getBoolean("BlackSetInserted");

        currentState.readNbt(nbt);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(ModComponents.INSERTED_PIECES_SETS, List.of(whiteSetInsereted, blackSetInserted));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        List<Boolean> list = components.getOrDefault(ModComponents.INSERTED_PIECES_SETS, Collections.emptyList());
        if (list.size() > 1){
            this.whiteSetInsereted = list.getFirst();
            this.blackSetInserted = list.getLast();
        }
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
        if (getCachedState().get(ChessBoardBlock.GAME_STATE) != gameState.nbtValue){
            getWorld().setBlockState(getPos(), getCachedState().with(ChessBoardBlock.GAME_STATE, gameState.nbtValue));
        }
        updateClient();
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Chess Board");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ChessBoardScreenHandler(syncId, playerInventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void updateClient() {
        this.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public GameState getGameState() {
        return gameState;
    }
}
