package net.fieldb0y.wanna_play_chess.chess.utils;

import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public enum ChessPieces {
    EMPTY(0, null),

    WHITE_PAWN(1, ((ChessPiece) ModItems.WHITE_PAWN)),
    WHITE_KNIGHT(2, (ChessPiece)ModItems.WHITE_KNIGHT),
    WHITE_BISHOP(3, (ChessPiece)ModItems.WHITE_BISHOP),
    WHITE_ROOK(4, (ChessPiece)ModItems.WHITE_ROOK),
    WHITE_QUEEN(5, (ChessPiece)ModItems.WHITE_QUEEN),
    WHITE_KING(6, (ChessPiece)ModItems.WHITE_KING),

    BLACK_PAWN(7, (ChessPiece)ModItems.BLACK_PAWN),
    BLACK_KNIGHT(8, (ChessPiece)ModItems.BLACK_KNIGHT),
    BLACK_BISHOP(9, (ChessPiece)ModItems.BLACK_BISHOP),
    BLACK_ROOK(10, (ChessPiece)ModItems.BLACK_ROOK),
    BLACK_QUEEN(11, (ChessPiece)ModItems.BLACK_QUEEN),
    BLACK_KING(12, (ChessPiece)ModItems.BLACK_KING);

    public final int id;
    public final ChessPiece item;

    ChessPieces(int id, ChessPiece item){
        this.id = id;
        this.item = item;
    }

    public static List<Integer> getAllIds(){
        List<Integer> ids = new ArrayList<>();
        for (ChessPieces piece : values()){
            if (piece != EMPTY)
                ids.add(piece.id);
        }
        return ids;
    }

    public static List<Integer> getAllIdsExcept(List<Integer> exceptList){
        List<Integer> ids = new ArrayList<>();
        for (ChessPieces piece : values()){
            if (piece != EMPTY && !exceptList.contains(piece.id))
                ids.add(piece.id);
        }
        return ids;
    }

    public static List<Integer> getAllIdsWithRoleExcept(int role, List<Integer> exceptList){
        List<Integer> ids = new ArrayList<>();
        for (ChessPieces piece : values()){
            if (piece != EMPTY && !exceptList.contains(piece.id) && ChessGrid.getPieceRole(piece.id) == role)
                ids.add(piece.id);
        }
        return ids;
    }

    public static Integer getId(ChessPiece piece){
        for (ChessPieces pieceValue : values()){
            if (pieceValue.item == piece){
                return pieceValue.id;
            }
        }
        return -1;
    }
}
