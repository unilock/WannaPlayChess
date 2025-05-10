package net.fieldb0y.wanna_play_chess.chess.gameStates;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessGameOverReason;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class ChessGameOverState extends ChessState{
    private ChessGrid grid;
    public UUID[] players = new UUID[2];

    public ChessGameOverReason gameOverReason;
    public int winnerRole;
    public Vector2i matedKingCell;

    public ChessGameOverState(ChessBoardBlockEntity blockEntity) {
        super(blockEntity, List.of(GameState.GAME_OVER));
        grid = new ChessGrid(blockEntity);
    }

    public void transferData(ChessGameState chessGameState, UUID[] players, ChessGameOverReason gameOverReason, int winnerRole){
        this.grid = chessGameState.getGrid();
        this.gameOverReason = gameOverReason;
        this.winnerRole = winnerRole;
        this.players = Arrays.copyOf(players, 2);
        this.matedKingCell = grid.findPiecePos(chessGameState.getCheckedKing());

        updateClientAndServer();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (int i = 0; i < players.length; i++) {
            UUID uuid = players[i];
            if (uuid != null) {
                list.add(i, NbtHelper.fromUuid(uuid));
            }
        }
        nbt.put("Players", list);

        grid.writeNbt(nbt);

        nbt.putInt("GameOverReason", gameOverReason.nbtValue);
        nbt.putInt("WinnerRole", winnerRole);

        nbt.putIntArray("MatedKingCell", List.of(matedKingCell.x, matedKingCell.y));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("Players")){
            NbtList list = (NbtList) nbt.get("Players");
            for (int i = 0; i < 2; i++) {
                if (i < list.size()){
                    players[i] = NbtHelper.toUuid(list.get(i));
                }
            }
        }

        grid.readNbt(nbt);

        if (nbt.contains("GameOverReason"))
            this.gameOverReason = ChessGameOverReason.getWithNbtValue(nbt.getInt("GameOverReason"));
        if (nbt.contains("WinnerRole"))
            this.winnerRole = nbt.getInt("WinnerRole");
        if (nbt.contains("MatedKingCell")){
            int[] array = nbt.getIntArray("MatedKingCell");
            this.matedKingCell = new Vector2i(array[0], array[1]);
        }
    }

    @Override
    public void afterSwitch() {
        for (ChessState state : blockEntity.states){
            state.clear();
        }
    }

    @Override
    public void clear() {
        grid.clearGrid();
        updateClientAndServer();
    }

    public boolean isPlayerInList(UUID uuid){
        return players[WHITE].compareTo(uuid) == 0 || players[BLACK].compareTo(uuid) == 0;
    }

    public ChessGrid getGrid() {
        return grid;
    }
}
