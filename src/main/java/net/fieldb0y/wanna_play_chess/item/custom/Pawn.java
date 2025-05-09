package net.fieldb0y.wanna_play_chess.item.custom;

import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.utils.PieceAction;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.fieldb0y.wanna_play_chess.utils.Role;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class Pawn extends ChessPiece {
    public Pawn(Settings settings, Role role) {
        super(settings, role);
    }

    @Override
    public ItemStack getWhiteStack() {
        return ModItems.WHITE_PAWN.getDefaultStack();
    }

    @Override
    public ItemStack getBlackStack() {
        return ModItems.BLACK_PAWN.getDefaultStack();
    }

    @Override
    public void render(ItemRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int xSquare, int ySquare) {
        super.render(renderer, matrices, vertexConsumers, world, light, overlay, xSquare, ySquare);
    }

    @Override
    protected Map<PieceAction, List<Vector2i>> calculateStandardMoves(int x, int y, int role, ChessGrid grid, List<Vector2i> moveCells, List<Vector2i> takeCells, boolean allPossible) {
        List<Vector2i> enPassantCells = new ArrayList<>();
        List<Vector2i> unshowedMoves = new ArrayList<>();

        Vector2i upPos, up2Pos, ldPos, rdPos;
        boolean canMove2CellsUp = (role == WHITE && y == 6) || (role == BLACK && y == 1);

        if (role == WHITE){
            upPos = new Vector2i(x, y - 1);
            up2Pos = new Vector2i(x, y - 2);
            ldPos = new Vector2i(x - 1, y - 1);
            rdPos = new Vector2i(x + 1, y - 1);
        } else {
            upPos = new Vector2i(x, y + 1);
            up2Pos = new Vector2i(x, y + 2);
            ldPos = new Vector2i(x - 1, y + 1);
            rdPos = new Vector2i(x + 1, y + 1);
        }
        int upPieceId = grid.safeGetPieceId(upPos.x, upPos.y);
        int ldPieceId = grid.safeGetPieceId(ldPos.x, ldPos.y);
        int rdPieceId = grid.safeGetPieceId(rdPos.x, rdPos.y);

        if (upPieceId != -1 && upPieceId == ChessPieces.EMPTY.id) moveCells.add(upPos);
        if (canMove2CellsUp && grid.safeGetPieceId(upPos.x, upPos.y) == ChessPieces.EMPTY.id && grid.safeGetPieceId(up2Pos.x, up2Pos.y) == ChessPieces.EMPTY.id) moveCells.add(up2Pos);

        if (ldPieceId != -1) {
            if (ldPieceId != ChessPieces.EMPTY.id && ChessGrid.getPieceRole(ldPieceId) != role)
                takeCells.add(ldPos);
            else unshowedMoves.add(ldPos);
        }
        if (rdPieceId != -1) {
            if (rdPieceId != ChessPieces.EMPTY.id && ChessGrid.getPieceRole(rdPieceId) != role)
                takeCells.add(rdPos);
            else unshowedMoves.add(rdPos);
        }

        checkEnPassant(x, y, role, enPassantCells, grid);

        return Map.of(PieceAction.MOVE, moveCells, PieceAction.TAKE, takeCells, PieceAction.EN_PASSANT, enPassantCells, PieceAction.OTHER, unshowedMoves);
    }

    private void checkEnPassant(int x, int y, int role, List<Vector2i> enPassantCells, ChessGrid grid){
        Vector2i lPiecePos = new Vector2i(x - 1, y);
        Vector2i rPiecePos = new Vector2i(x + 1, y);

        int lPiece = grid.safeGetPieceId(lPiecePos.x, lPiecePos.y);
        int rPiece = grid.safeGetPieceId(rPiecePos.x, rPiecePos.y);

        if ((lPiece == ChessPieces.WHITE_PAWN.id || lPiece == ChessPieces.BLACK_PAWN.id) && ChessGrid.getPieceRole(lPiece) != role
                && grid.getGameState().piecesData.hasDataTag(lPiecePos.x, lPiecePos.y, PiecesData.DataTag.JUST_MOVED_2CELLS)){
            enPassantCells.add(new Vector2i(lPiecePos.x, lPiecePos.y + (role == WHITE ? - 1 : 1)));
        }

        if ((rPiece == ChessPieces.WHITE_PAWN.id || rPiece == ChessPieces.BLACK_PAWN.id) && ChessGrid.getPieceRole(rPiece) != role
                && grid.getGameState().piecesData.hasDataTag(rPiecePos.x, rPiecePos.y, PiecesData.DataTag.JUST_MOVED_2CELLS)){
            enPassantCells.add(new Vector2i(rPiecePos.x, rPiecePos.y + (role == WHITE ? - 1 : 1)));
        }
    }

    @Override
    public void updatePieceData(Vector2i fromCell, Vector2i toCell, PiecesData data){
        super.updatePieceData(fromCell, toCell, data);

        if(Math.abs(fromCell.y  - toCell.y) == 2){
            data.putData(toCell.x, toCell.y, List.of(PiecesData.DataTag.JUST_MOVED_2CELLS, PiecesData.DataTag.ONE_TURN));
        } else {
            data.removeData(toCell.x, toCell.y, PiecesData.DataTag.JUST_MOVED_2CELLS);
        }
    }
}
