package net.fieldb0y.wanna_play_chess.item.custom;

import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.block.entity.renderer.PieceAnimator;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.utils.PieceAction;
import net.fieldb0y.wanna_play_chess.utils.Role;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.WHITE;

public abstract class ChessPiece extends Item {
    protected Role role;

    public static final float SQUARE_SIZE = 0.915f;

    public ChessPiece(Settings settings, Role role) {
        super(settings);
        this.role = role;
    }

    public abstract ItemStack getWhiteStack();
    public abstract ItemStack getBlackStack();

    public void render(ItemRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int xSquare, int ySquare){
        matrices.push();

        matrices.scale(0.09f, 0.09f, 0.09f);
        matrices.translate(2.35f + xSquare * SQUARE_SIZE, 1.75f, 2.35f + ySquare * SQUARE_SIZE);
        if (role == Role.BLACK)
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

        renderer.renderItem(this.role == Role.WHITE ? getWhiteStack() : getBlackStack(), ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);

        matrices.pop();
    }

    public void renderMovingAnimation(PieceAnimator animator, ItemRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay) {
        matrices.push();

        float progress = animator.getProgress();
        Vector2i from = animator.fromCell;
        Vector2i to = animator.toCell;

        float startX = from.x * SQUARE_SIZE;
        float startZ = from.y * SQUARE_SIZE;
        float endX = to.x * SQUARE_SIZE;
        float endZ = to.y * SQUARE_SIZE;

        float currentX = startX + (endX - startX) * progress;
        float currentZ = startZ + (endZ - startZ) * progress;

        matrices.scale(0.09f, 0.09f, 0.09f);
        matrices.translate(2.35f + currentX, 1.75f, 2.35f + currentZ);

        if (role == Role.BLACK) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        }

        renderer.renderItem(role == Role.WHITE ? getWhiteStack() : getBlackStack(), ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);

        matrices.pop();
    }

    protected static void loopDirection(int dl, int x, int y, int xDir, int yDir, int role, List<Vector2i> moveCells, List<Vector2i> takeCells, ChessGrid grid, boolean getAllPossible){
        for (int i = 1; i < dl; i++){
            Vector2i pos = new Vector2i(x + i * xDir, y + i * yDir);
            int pieceId = grid.safeGetPieceId(pos.x, pos.y);

            PieceAction action = getAction(pieceId, role, getAllPossible);

            if (action == PieceAction.NONE) break;
            if (action == PieceAction.OTHER){
                moveCells.add(pos); break;
            }
            if (action == PieceAction.MOVE){
                moveCells.add(pos);
            } else {
                takeCells.add(pos);
                if (!getAllPossible) break;
            }
        }
    }

    protected static PieceAction getAction(int pieceId, int role, boolean getAllPossible){
        if (pieceId == -1)
            return PieceAction.NONE;
        if(ChessGrid.getPieceRole(pieceId) == role){
            return getAllPossible ? PieceAction.OTHER : PieceAction.NONE;
        }
        if (pieceId == ChessPieces.EMPTY.id) return PieceAction.MOVE;
        return PieceAction.TAKE;
    }

    protected abstract Map<PieceAction, List<Vector2i>> calculateStandardMoves(int x, int y, int role, ChessGrid grid, List<Vector2i> moveCells, List<Vector2i> takeCells, boolean allPossible);
    public Map<PieceAction, List<Vector2i>> getPossibleMoves(int x, int y, int role, ChessGrid grid){
        return getPossibleMoves(x, y, role, grid, false);
    }
    public Map<PieceAction, List<Vector2i>> getPossibleMoves(int x, int y, int role, ChessGrid grid, boolean allPossible){
        List<Vector2i> moveCells = new ArrayList<>();
        List<Vector2i> takeCells = new ArrayList<>();
        ChessGameState state = grid.getGameState();

        Vector2i kingPos = grid.findPiecePos(role == WHITE ? ChessPieces.WHITE_KING.id : ChessPieces.BLACK_KING.id);
        if (kingPos == null) return Map.of();

        if (state.getCheckedKingRole() == -1 || state.getCheckedKingRole() != role || kingPos.equals(x, y)) {
            Map<PieceAction, List<Vector2i>> standartMoves = calculateStandardMoves(x, y, role, grid, moveCells, takeCells, allPossible);

            List<Vector2i> realTakeCells = new ArrayList<>();
            List<Vector2i> realMoveCells = new ArrayList<>();

            if (state.piecesData.hasDataTag(x, y, PiecesData.DataTag.CANT_MOVE)){
                for (int i = 0; i < ChessGrid.SIZE; i++){
                    for (int j = 0; j < ChessGrid.SIZE; j++){
                        int id = grid.safeGetPieceId(i, j);
                        if (id != ChessPieces.EMPTY.id && id != -1 && id != ChessPieces.WHITE_KING.id && id != ChessPieces.BLACK_KING.id){
                            List<Vector2i> path = getAttackPath(kingPos, new Vector2i(i, j), ChessGrid.getPieceById(id), false);
                            if (path.contains(new Vector2i(x, y))){
                                if (takeCells.contains(new Vector2i(i, j)))
                                    realTakeCells.add(new Vector2i(i, j));
                                for (Vector2i pathCell : path){
                                    if (moveCells.contains(pathCell))
                                        realMoveCells.add(pathCell);
                                }
                            }

                        }
                    }
                }
                return Map.of(PieceAction.MOVE, realMoveCells, PieceAction.TAKE, realTakeCells, PieceAction.NONE, List.of(new Vector2i(x, y)));
            }

            return standartMoves;
        } else {
            if (state.piecesData.hasDataTag(x, y, PiecesData.DataTag.CANT_MOVE))
                return Map.of(PieceAction.NONE, List.of(new Vector2i(x, y)));

            List<Vector2i> checkingPiecesCells = grid.getCheckingPiecesCells();
            if (checkingPiecesCells.size() >= 2) {
                return Map.of();
            }

            Vector2i attackerPos = checkingPiecesCells.getFirst();
            ChessPiece attacker = grid.getPieceAt(attackerPos.x, attackerPos.y);

            List<Vector2i> attackPath = getAttackPath(kingPos, attackerPos, attacker, false);
            calculateStandardMoves(x, y, role, grid, moveCells, takeCells, false);

            filterCheckMoves(moveCells, takeCells, attackPath, attackerPos);
        }

        return Map.of(PieceAction.MOVE, moveCells, PieceAction.TAKE, takeCells);
    }

    public boolean isAbleToMove(int x, int y, int role, ChessGrid grid){
        Map<PieceAction, List<Vector2i>> possibleMoves = getPossibleMoves(x, y, role, grid);
        for (PieceAction action : possibleMoves.keySet()){
            if (!possibleMoves.get(action).isEmpty()) return true;
        }
        return false;
    }

    private void filterCheckMoves(List<Vector2i> moveCells, List<Vector2i> takeCells, List<Vector2i> attackPath, Vector2i attackerPos) {
        moveCells.removeIf(cell -> !attackPath.contains(cell));
        takeCells.removeIf(cell -> !cell.equals(attackerPos));
    }

    public static List<Vector2i> getAttackPath(Vector2i kingPos, Vector2i attackerPos, ChessPiece attacker, boolean includeEndCell) {
        List<Vector2i> path = new ArrayList<>();

        if (attacker instanceof Bishop || attacker instanceof Queen) {
            addDiagonalPath(kingPos, attackerPos, path, includeEndCell);
        }
        if (attacker instanceof Rook || attacker instanceof Queen) {
            addStraightPath(kingPos, attackerPos, path, includeEndCell);
        }

        return path;
    }

    public static void addStraightPath(Vector2i start, Vector2i end, List<Vector2i> path, boolean includeEndCell) {
        int dx = Integer.compare(end.x - start.x, 0);
        int dy = Integer.compare(end.y - start.y, 0);

        if (dx == 0 || dy == 0) {
            Vector2i current = new Vector2i(start.x + dx, start.y + dy);
            while (!current.equals(end)) {
                path.add(current);
                current = new Vector2i(current.x + dx, current.y + dy);
            }
        }
        if (includeEndCell && (dx == 0 || dy == 0) && Utils.isCellInSquare(end.x, end.y, path.isEmpty() ? start.x : path.getLast().x, path.isEmpty() ? start.y : path.getLast().y, 1)) path.add(end);
    }
    public static void addDiagonalPath(Vector2i start, Vector2i end, List<Vector2i> path, boolean includeEndCell) {
        int dx = Integer.compare(end.x - start.x, 0);
        int dy = Integer.compare(end.y - start.y, 0);

        if (Math.abs(end.x - start.x) == Math.abs(end.y- start.y)) {
            Vector2i current = new Vector2i(start.x + dx, start.y + dy);
            while (!current.equals(end.x, end.y)) {
                path.add(current);
                current = new Vector2i(current.x + dx, current.y + dy);
            }
        }
        if (includeEndCell && (Math.abs(end.x - start.x) == Math.abs(end.y- start.y)) && Utils.isCellInSquare(end.x, end.y, path.isEmpty() ? start.x : path.getLast().x, path.isEmpty() ? start.y : path.getLast().y, 1)) path.add(end);
    }

    public void updatePieceData(Vector2i fromCell, Vector2i toCell, PiecesData data){
        data.clearData(fromCell.x, fromCell.y);
        data.clearData(toCell.x, toCell.y);
    }

    public Role getRole() {
        return role;
    }
}
