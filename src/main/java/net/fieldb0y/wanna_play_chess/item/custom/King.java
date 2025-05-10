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

public class King extends ChessPiece {
    public King(Settings settings, Role role) {
        super(settings, role);
    }

    @Override
    public ItemStack getWhiteStack() {
        return ModItems.WHITE_KING.getDefaultStack();
    }

    @Override
    public ItemStack getBlackStack() {
        return ModItems.BLACK_KING.getDefaultStack();
    }

    @Override
    public void render(ItemRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int xSquare, int ySquare) {
        super.render(renderer, matrices, vertexConsumers, world, light, overlay, xSquare, ySquare);
    }

    @Override
    protected Map<PieceAction, List<Vector2i>> calculateStandardMoves(int x, int y, int role, ChessGrid grid, List<Vector2i> moveCells, List<Vector2i> takeCells, boolean allPossible) {
        List<Vector2i> castleCells = new ArrayList<>();

        List<Vector2i> cells = List.of(new Vector2i(x, y - 1), new Vector2i(x, y + 1), new Vector2i(x - 1, y), new Vector2i(x + 1, y),
                new Vector2i(x + 1, y - 1), new Vector2i(x - 1, y - 1), new Vector2i(x + 1, y + 1), new Vector2i(x - 1, y + 1));

        for (Vector2i cell : cells){
            PieceAction kingAction = getKingActionTo(cell.x, cell.y, role, grid);
            if(kingAction == PieceAction.MOVE) moveCells.add(cell);
            else if(kingAction == PieceAction.TAKE) takeCells.add(cell);
        }

        if (grid.getGameState().piecesData.hasDataTag(x, y, PiecesData.DataTag.CAN_CASTLE_SHORT))
            castleCells.add(new Vector2i(x - 2, y));
        if (grid.getGameState().piecesData.hasDataTag(x, y, PiecesData.DataTag.CAN_CASTLE_LONG))
            castleCells.add(new Vector2i(x + 2, y));

        return Map.of(PieceAction.MOVE, moveCells, PieceAction.TAKE, takeCells, PieceAction.CASTLE, castleCells);
    }

    private PieceAction getKingActionTo(int x, int y, int role, ChessGrid grid){
        for (int i = 0; i < ChessGrid.SIZE; i++){
            for (int j = 0; j < ChessGrid.SIZE; j++){
                int currentPieceId = grid.safeGetPieceId(i, j);
                if (ChessGrid.getPieceRole(currentPieceId) != role && currentPieceId != ChessPieces.EMPTY.id && currentPieceId != -1){
                    ChessPiece piece = ChessGrid.getPieceById(currentPieceId);
                    if (currentPieceId == (role == WHITE ? ChessPieces.BLACK_KING.id : ChessPieces.WHITE_KING.id)){
                        if (grid.isCellInArea(new Vector2i(i, j), new Vector2i(x, y), 1))
                            return PieceAction.NONE;
                    } else {
                        if (currentPieceId == ChessPieces.WHITE_PAWN.id || currentPieceId == ChessPieces.BLACK_PAWN.id || currentPieceId == ChessPieces.WHITE_KNIGHT.id  || currentPieceId == ChessPieces.BLACK_KNIGHT.id){
                            Map<PieceAction, List<Vector2i>> piecePossibleMoves = piece.getPossibleMoves(i, j, ChessGrid.getPieceRole(currentPieceId), grid, true);
                            for (List<Vector2i> list : piecePossibleMoves.values()){
                                if (list.contains(new Vector2i(x, y))){
                                    if (piece instanceof Pawn){
                                        if (ChessGrid.getPieceRole(currentPieceId) == WHITE && new Vector2i(x, y).equals(new Vector2i(i, j - 1))) continue;
                                        if (ChessGrid.getPieceRole(currentPieceId) == BLACK && new Vector2i(x, y).equals(new Vector2i(i, j + 1))) continue;
                                    }
                                    return PieceAction.NONE;
                                }
                            }
                        } else {
                            List<Vector2i> path = ChessPiece.getAttackPath(new Vector2i(i, j), new Vector2i(x, y), piece, true);
                            if (!path.isEmpty() && !path.getLast().equals(i, j)){
                                boolean isPieceOnPath = false;
                                for (Vector2i cell : path){
                                    if (cell == path.getLast()) break;
                                    int pieceId = grid.safeGetPieceId(cell.x, cell.y);
                                    if (pieceId != (role == WHITE ? ChessPieces.WHITE_KING.id : ChessPieces.BLACK_KING.id) && pieceId != ChessPieces.EMPTY.id){
                                        isPieceOnPath = true;
                                        break;
                                    }
                                }
                                if (!path.isEmpty() && !isPieceOnPath)
                                    return PieceAction.NONE;
                            }
                        }
                    }
                }
            }
        }
        int pieceId = grid.safeGetPieceId(x, y);
        return pieceId != -1 ? (pieceId == ChessPieces.EMPTY.id ? PieceAction.MOVE : ChessGrid.getPieceRole(pieceId) != role ? PieceAction.TAKE : PieceAction.NONE) : PieceAction.NONE;
    }

    @Override
    public void updatePieceData(Vector2i fromCell, Vector2i toCell, PiecesData data) {
        super.updatePieceData(fromCell, toCell, data);
    }
}
