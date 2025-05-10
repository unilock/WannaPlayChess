package net.fieldb0y.wanna_play_chess.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fieldb0y.wanna_play_chess.block.custom.ChessBoardBlock;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.block.entity.model.ChessBoardModel;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameOverState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.chess.utils.PieceAction;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;
import net.fieldb0y.wanna_play_chess.layer.ModModelLayers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;


public class ChessBoardRenderer implements BlockEntityRenderer<ChessBoardBlockEntity> {
    private final BlockEntityRendererFactory.Context context;
    private final ChessBoardModel model;

    public ChessBoardRenderer(BlockEntityRendererFactory.Context context){
        this.context = context;
        this.model = new ChessBoardModel(context.getLayerModelPart(ModModelLayers.CHESS_BOARD));
    }

    @Override
    public void render(ChessBoardBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderBoardModel(blockEntity, matrices, vertexConsumers, light, overlay);
        renderPieces(blockEntity, matrices, vertexConsumers, light, overlay);
        renderPossibleMoves(blockEntity, matrices, vertexConsumers, light, overlay);

        if (blockEntity.currentState != null && blockEntity.currentState.equals(blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE))){
            renderTakenPieces(blockEntity, matrices, vertexConsumers, light, overlay);
        }
    }

    private void renderPieces(ChessBoardBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = context.getItemRenderer();
        World world = blockEntity.getWorld();

        ChessGrid grid = null;
        if (blockEntity.currentState.equals(blockEntity.states.get(ChessBoardBlockEntity.LOBBY_STATE))) return;
        if(blockEntity.currentState.equals(blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE)))
            grid = ((ChessGameState) blockEntity.currentState).getGrid();
        else if(blockEntity.currentState.equals(blockEntity.states.get(ChessBoardBlockEntity.GAME_OVER_STATE)))
            grid = ((ChessGameOverState) blockEntity.currentState).getGrid();

        int[][] gridData = grid.data;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int pieceId = gridData[i][j];
                if (pieceId != ChessPieces.EMPTY.id) {
                    matrices.push();

                    matrices.translate(0.5f, 0, 0.5f);
                    switch (grid.getDirection()) {
                        case EAST -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
                        case SOUTH -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
                        case WEST -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(270));
                        default -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(0));
                    }
                    matrices.translate(-0.5f, 0, -0.5f);

                    ChessPiece pieceItem = ChessPieces.values()[pieceId].item;
                    Vector2i currentCell = new Vector2i(i, j);

                    if (grid.pieceAnimator.isAnimationPlaying && grid.pieceAnimator.toCell.equals(currentCell)) {
                        pieceItem.renderMovingAnimation(grid.pieceAnimator, itemRenderer, matrices, vertexConsumers, world, light, overlay);
                    } else {
                        pieceItem.render(itemRenderer, matrices, vertexConsumers, world, light, overlay, i, j);
                    }

                    matrices.pop();
                }
            }
        }
    }

    private void renderTakenPieces(ChessBoardBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        ItemRenderer itemRenderer = context.getItemRenderer();
        Map<Integer, List<Integer>> takenPieces = ((ChessGameState)blockEntity.currentState).getTakenPieces();
        for (int role = 0; role < 2; role++){
            List<Integer> piecesIds = takenPieces.get(role);
            if (piecesIds == null) return;
            for (int i = 0; i < piecesIds.size(); i++){
                int pieceId = piecesIds.get(i);
                ChessPiece piece = ChessGrid.getPieceById(pieceId);
                matrices.push();
                matrices.translate(0.5f, 0, 0.5f);
                switch(((ChessGameState)blockEntity.currentState).getGrid().getDirection()){
                    case EAST -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
                    case SOUTH -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
                    case WEST -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(270));
                    default -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(0));
                }
                matrices.translate(-0.5f, 0, -0.5f);

                matrices.scale(0.05f, 0.05f, 0.05f);
                matrices.translate((role == WHITE ? 2f : 18f) + i * (role == WHITE ? 1f : -1f), 3f, (role == WHITE ? 18f : 2f));
                itemRenderer.renderItem(role == WHITE ? piece.getBlackStack() : piece.getWhiteStack(), ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);

                matrices.pop();
            }
        }
    }

    private void renderBoardModel(ChessBoardBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        matrices.push();

        matrices.translate(0.5f, 0, 0.5f);
        matrices.scale(0.88f, 1f, 0.88f);

        if (blockEntity.currentState == null || blockEntity.currentState == blockEntity.states.get(ChessBoardBlockEntity.LOBBY_STATE)){
            rotateMatrixByDirection(matrices, blockEntity.getCachedState().get(ChessBoardBlock.FACING), RotationAxis.POSITIVE_Y);
        } else {
            ChessGrid chessGrid;
            if (blockEntity.currentState == blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE))
                chessGrid = ((ChessGameState)blockEntity.currentState).getGrid();
            else chessGrid = ((ChessGameOverState)blockEntity.currentState).getGrid();
            rotateMatrixByDirection(matrices, chessGrid.getDirection(), RotationAxis.POSITIVE_Y);
        }
        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(ChessBoardModel.TEXTURE)), light, overlay);

        matrices.pop();
    }

    private void renderPossibleMoves(ChessBoardBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.currentState instanceof ChessGameState state){
            Map<PieceAction, List<Vector2i>> possibleMoves = state.getPossibleMoves();
            System.out.println(possibleMoves);
            if (!possibleMoves.isEmpty())
                renderSquareOnCell(state.currentChosenCell.x, state.currentChosenCell.y, new Vector4i(252, 186, 3, 141), state.getGrid(), matrices, vertexConsumers, light, overlay);
            for (PieceAction action : possibleMoves.keySet()){
                Vector4i color = action.getColor();
                for (Vector2i pos : possibleMoves.get(action)){
                    if (action != PieceAction.NONE && action != PieceAction.OTHER)
                        renderSquareOnCell(pos.x, pos.y, color, state.getGrid(), matrices, vertexConsumers, light, overlay);
                }
            }

            for (int i = 0; i < ChessGrid.SIZE; i++){
                for (int j = 0; j < ChessGrid.SIZE; j++){
                    int pieceId = state.getGrid().safeGetPieceId(i, j);
                    if (pieceId == ChessPieces.WHITE_KING.id || pieceId == ChessPieces.BLACK_KING.id){
                        if (state.piecesData.hasDataTag(i, j, PiecesData.DataTag.CHECKED)){
                            renderSquareOnCell(i, j, PieceAction.TAKE.getColor(), state.getGrid(), matrices, vertexConsumers, light, overlay);
                        }
                    }
                }
            }
        } else if(blockEntity.currentState instanceof ChessGameOverState state){
            if (!state.matedKingCell.equals(-1, -1))
                renderSquareOnCell(state.matedKingCell.x, state.matedKingCell.y, PieceAction.TAKE.getColor(), state.getGrid(), matrices, vertexConsumers, light, overlay);
        }
    }

    private void renderSquareOnCell(int x, int y, Vector4i rgba, ChessGrid grid, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        matrices.push();

        matrices.translate(0.21, 0, 0.21);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(ChessBoardModel.TEXTURE));
        float size = 0.0415f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        switch(grid.getDirection()){
            case EAST -> {
                matrices.translate(0.58, 0, 0);
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
            }
            case SOUTH -> {
                matrices.translate(0.58f, 0, 0.58f);
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
            }
            case WEST -> {
                matrices.translate(0, 0, 0.58f);
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(270));
            }
            default -> matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(0));
        }

        consumer.vertex(matrix, x * size * 2 - size, 0.1251f, y * size * 2  - size).color(rgba.x, rgba.y, rgba.z, rgba.w).texture(0, 0).overlay(overlay).light(light).normal(0, 1, 0);
        consumer.vertex(matrix, x * size * 2  - size, 0.1251f, y * size * 2  + size).color(rgba.x, rgba.y, rgba.z, rgba.w).texture(0, 0.0234375f).overlay(overlay).light(light).normal(0, 1, 0);
        consumer.vertex(matrix, x * size * 2  + size, 0.1251f, y * size * 2  + size).color(rgba.x, rgba.y, rgba.z, rgba.w).texture(0.0234375f, 0.0234375f).overlay(overlay).light(light).normal(0, 1, 0);
        consumer.vertex(matrix, x * size * 2  + size, 0.1251f, y * size * 2  - size).color(rgba.x, rgba.y, rgba.z, rgba.w).texture(0.0234375f, 0).overlay(overlay).light(light).normal(0, 1, 0);

        matrices.pop();
        RenderSystem.disableBlend();
    }

    private void rotateMatrixByDirection(MatrixStack matrices, Direction direction, RotationAxis rotationAxis){
        switch(direction){
            case EAST -> matrices.multiply(rotationAxis.rotationDegrees(270));
            case SOUTH -> matrices.multiply(rotationAxis.rotationDegrees(180));
            case WEST -> matrices.multiply(rotationAxis.rotationDegrees(90));
            default -> matrices.multiply(rotationAxis.rotationDegrees(0));
        }
    }
}
