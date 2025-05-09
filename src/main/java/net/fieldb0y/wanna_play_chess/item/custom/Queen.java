package net.fieldb0y.wanna_play_chess.item.custom;

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

import java.util.List;
import java.util.Map;

public class Queen extends ChessPiece {
    public Queen(Settings settings, Role role) {
        super(settings, role);
    }

    @Override
    public ItemStack getWhiteStack() {
        return ModItems.WHITE_QUEEN.getDefaultStack();
    }

    @Override
    public ItemStack getBlackStack() {
        return ModItems.BLACK_QUEEN.getDefaultStack();
    }

    @Override
    public void render(ItemRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int xSquare, int ySquare) {
        super.render(renderer, matrices, vertexConsumers, world, light, overlay, xSquare, ySquare);
    }

    protected Map<PieceAction, List<Vector2i>> calculateStandardMoves(int x, int y, int role, ChessGrid grid, List<Vector2i> moveCells, List<Vector2i> takeCells, boolean allPossible) {
        int fl = ChessGrid.SIZE;
        int dl = ChessGrid.SIZE * 2;

        loopDirection(dl, x, y, 1, -1, role, moveCells, takeCells, grid, allPossible);
        loopDirection(dl, x, y, -1, -1, role, moveCells, takeCells, grid, allPossible);
        loopDirection(dl, x, y, 1, 1, role, moveCells, takeCells, grid, allPossible);
        loopDirection(dl, x, y, -1, 1, role, moveCells, takeCells, grid, allPossible);

        loopDirection(fl, x, y, 0, -1, role, moveCells, takeCells, grid, allPossible);
        loopDirection(fl, x, y, 0, 1, role, moveCells, takeCells, grid, allPossible);
        loopDirection(fl, x, y, 1, 0, role, moveCells, takeCells, grid, allPossible);
        loopDirection(fl, x, y, -1, 0, role, moveCells, takeCells, grid, allPossible);

        return Map.of(PieceAction.MOVE, moveCells, PieceAction.TAKE, takeCells);
    }

    @Override
    public void updatePieceData(Vector2i fromCell, Vector2i toCell, PiecesData data) {
        super.updatePieceData(fromCell, toCell, data);
    }
}