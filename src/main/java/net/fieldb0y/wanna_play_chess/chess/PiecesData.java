package net.fieldb0y.wanna_play_chess.chess;

import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import org.joml.Vector2i;

import java.util.*;

public class PiecesData {
    public enum DataTag {
        JUST_MOVED_2CELLS(List.of(ChessPieces.WHITE_PAWN.id, ChessPieces.BLACK_PAWN.id), 0),
        ONE_TURN(ChessPieces.getAllIds(), 1),
        CHECKED(List.of(ChessPieces.WHITE_KING.id, ChessPieces.BLACK_KING.id), 2),
        GIVES_CHECK(ChessPieces.getAllIdsExcept(List.of(ChessPieces.WHITE_KING.id, ChessPieces.BLACK_KING.id)), 3),
        CANT_MOVE(ChessPieces.getAllIdsExcept(List.of(ChessPieces.WHITE_KING.id, ChessPieces.BLACK_KING.id)), 4),
        CAN_CASTLE_SHORT(List.of(ChessPieces.WHITE_KING.id, ChessPieces.BLACK_KING.id), 5),
        CAN_CASTLE_LONG(List.of(ChessPieces.WHITE_KING.id, ChessPieces.BLACK_KING.id), 6),
        ALREADY_MOVED(ChessPieces.getAllIds(), 7),
        SHOULD_TURN(List.of(ChessPieces.WHITE_PAWN.id, ChessPieces.BLACK_PAWN.id), 8);

        public List<Integer> pieces;
        public int nbtValue;

        DataTag(List<Integer> pieces, int nbtValue){
            this.pieces = pieces;
            this.nbtValue = nbtValue;
        }
    }

    private Map<Vector2i, List<DataTag>> data = new HashMap<>();
    public ChessGameState state;

    public PiecesData(ChessGameState state){
        this.state = state;
    }

    public List<DataTag> getData(int x, int y){
        if (hasData(x, y))
            return data.get(new Vector2i(x, y));
        return List.of();
    }

    public void putData(int x, int y, List<DataTag> tags){
        List<DataTag> realTags = new ArrayList<>();
        Vector2i cell = new Vector2i(x, y);
        for (DataTag tag : tags){
            if (state.getGrid().safeGetPieceId(x, y) != -1){
                realTags.add(tag);
            }
        }
        if (data.containsKey(cell)) {
            for (DataTag tag : realTags) {
                if (!this.data.get(cell).contains(tag))
                    this.data.get(cell).add(tag);
            }
        } else this.data.put(cell, realTags);
        state.updateClientAndServer();
    }

    public void removeData(int x, int y, DataTag tag){
        if (hasDataTag(x, y, tag)){
            if (data.get(new Vector2i(x, y)).size() <= 1)
                clearData(x, y);
            else this.data.get(new Vector2i(x, y)).remove(tag);
            state.updateClientAndServer();
        }
    }

    public void clearData(int x, int y){
        if (hasData(x, y)){
            this.data.get(new Vector2i(x, y)).clear();
            this.data.keySet().remove(new Vector2i(x, y));
            state.updateClientAndServer();
        }
    }

    public boolean hasDataTag(int x, int y, DataTag tag){
        Vector2i pos = new Vector2i(x, y);
        return data.containsKey(pos) && data.get(pos).contains(tag);
    }

    public boolean hasData(int x, int y){
        return data.containsKey(new Vector2i(x, y));
    }

    public void saveData(NbtCompound nbt) {
        NbtList piecesDataNbtList = new NbtList();

        for (Map.Entry<Vector2i, List<DataTag>> entry : data.entrySet()) {
            Vector2i pos = entry.getKey();
            List<DataTag> tags = entry.getValue();

            NbtList tagsList = new NbtList();
            for (DataTag tag : tags) {
                tagsList.add(NbtInt.of(tag.nbtValue));
            }

            NbtCompound entryCompound = new NbtCompound();
            entryCompound.putIntArray("Pos", new int[]{pos.x, pos.y});
            entryCompound.put("Tags", tagsList);

            piecesDataNbtList.add(entryCompound);
        }
        nbt.put("PiecesData", piecesDataNbtList);
    }

    public void readData(NbtCompound nbt) {
        data.clear();
        NbtList piecesDataNbtList = nbt.getList("PiecesData", NbtCompound.COMPOUND_TYPE);

        for (NbtElement element : piecesDataNbtList) {
            NbtCompound entryCompound = (NbtCompound) element;
            int[] posArray = entryCompound.getIntArray("Pos");
            Vector2i pos = new Vector2i(posArray[0], posArray[1]);

            NbtList tagsList = entryCompound.getList("Tags", NbtInt.INT_TYPE);
            List<DataTag> tags = new ArrayList<>();

            for (NbtElement tagElement : tagsList) {
                int nbtValue = ((NbtInt) tagElement).intValue();
                for (DataTag tag : DataTag.values()) {
                    if (tag.nbtValue == nbtValue) {
                        tags.add(tag);
                        break;
                    }
                }
            }
            data.put(pos, tags);
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
