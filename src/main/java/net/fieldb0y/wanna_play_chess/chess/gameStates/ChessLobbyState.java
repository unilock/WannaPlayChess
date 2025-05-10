package net.fieldb0y.wanna_play_chess.chess.gameStates;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.fieldb0y.wanna_play_chess.utils.Timer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState.FIRST;
import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState.SECOND;

public class ChessLobbyState extends ChessState {
    private UUID[] playersInLobby;
    private Timer timer;

    public int gameTimeInSec = 600;
    public boolean noTimeControl = false;
    public int firstPlayerRole = 2;

    public ChessLobbyState(ChessBoardBlockEntity blockEntity) {
        super(blockEntity, List.of(GameState.NOT_READY_TO_PLAY, GameState.READY_FOR_SINGLEPLAYER_GAME, GameState.READY_TO_PLAY));

        this.playersInLobby = new UUID[2];
        this.timer = new Timer(this::clearLobby, 60);
    }

    @Override
    public void tick() {
        if(playersInLobby[FIRST] != null && getWorld().getPlayerByUuid(playersInLobby[FIRST]) == null)
            removePlayerFromLobby(playersInLobby[FIRST]);
        if(playersInLobby[SECOND] != null && getWorld().getPlayerByUuid(playersInLobby[SECOND]) == null)
            removePlayerFromLobby(playersInLobby[SECOND]);

        timer.tick(playersInLobby[0] != null);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (int i = 0; i < playersInLobby.length; i++) {
            UUID uuid = playersInLobby[i];
            if (uuid != null) {
                list.add(NbtHelper.fromUuid(uuid));
            }
        }
        nbt.put("PlayersInLobby", list);
        nbt.putInt("GameTimeInSec", gameTimeInSec);
        nbt.putInt("FirstPlayerRole", firstPlayerRole);
        nbt.putBoolean("NoTimeControl", noTimeControl);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("PlayersInLobby")){
            NbtList list = (NbtList) nbt.get("PlayersInLobby");
            for (int i = 0; i < 2; i++) {
                if (i < list.size()){
                    playersInLobby[i] = NbtHelper.toUuid(list.get(i));
                } else playersInLobby[i] = null;
            }
        }
        if (nbt.contains("GameTimeInSec"))
            this.gameTimeInSec = nbt.getInt("GameTimeInSec");
        if (nbt.contains("FirstPlayerRole"))
            this.firstPlayerRole = nbt.getInt("FirstPlayerRole");
        if (nbt.contains("NoTimeControl"))
            this.noTimeControl = nbt.getBoolean("NoTimeControl");
    }

    public PlayerEntity getPlayerInLobby(int id){
        return playersInLobby[id] != null ? getWorld().getPlayerByUuid(playersInLobby[id]) : null;
    }

    public void setGameTimeInSec(int gameTimeInSec){
        this.gameTimeInSec = gameTimeInSec;
        updateClient();
    }

    public void setNoTimeControl(boolean checked) {
        this.noTimeControl = checked;
        updateClient();
    }

    public void setFirstPlayerRole(int role) {
        this.firstPlayerRole = role;
        updateClient();
    }

    public void addPlayer(PlayerEntity player){
        if (this.blockEntity.getGameState() == GameState.NOT_READY_TO_PLAY || this.blockEntity.getGameState() == GameState.READY_FOR_SINGLEPLAYER_GAME){
            if (!Arrays.stream(playersInLobby).toList().contains(player.getUuid()))
                this.playersInLobby[playersInLobby[FIRST] == null ? FIRST : SECOND] = player.getUuid();
        }
        updateClient();
    }

    public void removePlayerFromLobby(UUID uuid){
        for (int i = 0; i < playersInLobby.length; i++){
            UUID currentUuid = playersInLobby[i];
            if (currentUuid != null && currentUuid.compareTo(uuid) == 0) playersInLobby[i] = null;
        }
        updateClient();
    }

    public void removePlayerFromLobby(PlayerEntity player){
        if (player != null)
            removePlayerFromLobby(player.getUuid());
    }

    public void clearLobby(){
        this.playersInLobby = new UUID[2];
        updateClient();
    }

    public UUID[] getPlayersInLobby() {
        return Arrays.copyOf(playersInLobby, playersInLobby.length);
    }

    @Override
    public void afterSwitch() {
        ((ChessGameState)blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE)).startGame(playersInLobby, gameTimeInSec, noTimeControl, firstPlayerRole, playersInLobby[FIRST] == null || playersInLobby[SECOND] == null);
    }

    public boolean isPlayerInLobby(UUID uuid){
        return playersInLobby[FIRST].compareTo(uuid) == 0 || playersInLobby[SECOND].compareTo(uuid) == 0;
    }

    @Override
    public void clear() {
        clearLobby();
    }
}
