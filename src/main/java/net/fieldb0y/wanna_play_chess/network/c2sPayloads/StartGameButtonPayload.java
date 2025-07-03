package net.fieldb0y.wanna_play_chess.network.c2sPayloads;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record StartGameButtonPayload(BlockPos blockEntityPos) implements CustomPayload {
    private static final int OFFSET = 3;

    public static final Id<StartGameButtonPayload> ID = new Id<>(Identifier.of(WannaPlayChess.MOD_ID, "start_game_button"));
    public static final PacketCodec<RegistryByteBuf, StartGameButtonPayload> CODEC = PacketCodec.of(((payload, buf) -> buf.writeBlockPos(payload.blockEntityPos)), buf -> new StartGameButtonPayload(buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CustomPayload payload, ServerPlayNetworking.Context context){
        ServerWorld world = context.player().getServerWorld();
        BlockEntity be = world.getBlockEntity(((StartGameButtonPayload)payload).blockEntityPos);
        if(be instanceof ChessBoardBlockEntity blockEntity){
            GameState gameState = blockEntity.getGameState();
            if (gameState.equals(GameState.READY_TO_PLAY) || gameState.equals(GameState.READY_FOR_SINGLEPLAYER_GAME)){
                List<? extends PlayerEntity> players = blockEntity.getWorld().getPlayers();
                BlockPos bePos = blockEntity.getPos();
                Box box = new Box(bePos.getX() - OFFSET, bePos.getY() - OFFSET, bePos.getZ() - OFFSET, bePos.getX() + OFFSET, bePos.getY() + OFFSET, bePos.getZ() + OFFSET);

                UUID[] lobbyUuids = ((ChessLobbyState)blockEntity.currentState).getPlayersInLobby();
                List<PlayerEntity> playersInBox = new ArrayList<>();

                if (!Arrays.stream(lobbyUuids).toList().contains(context.player().getUuid())) return;

                for (PlayerEntity player : players){
                    if (box.contains(player.getPos())){
                        for (UUID uuid : lobbyUuids){
                            if (uuid != null && uuid.equals(player.getUuid())) playersInBox.add(player);
                        }
                    }
                }

                if (gameState.equals(GameState.READY_TO_PLAY) && playersInBox.size() >= 2)
                    blockEntity.setGameState(GameState.PLAYING);
                else if(gameState.equals(GameState.READY_FOR_SINGLEPLAYER_GAME) && playersInBox.size() == 1)
                    blockEntity.setGameState(GameState.PLAYING);
            }
        }
    }
}
