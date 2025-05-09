package net.fieldb0y.wanna_play_chess.mixin;

import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.item.ModComponents;
import net.fieldb0y.wanna_play_chess.item.custom.BoxForPieces;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.WHITE;

@Mixin(ItemRenderer.class)
public class BoxForItemsRenderer {
    @Unique private static final int MAX_PIECES_IN_ROW = 4;
    @Unique private static final int MAX_TOTAL_PIECES = MAX_PIECES_IN_ROW * MAX_PIECES_IN_ROW; // 16

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("TAIL"))
    public void renderBox(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (stack.getItem() instanceof BoxForPieces) {
            MinecraftClient client = MinecraftClient.getInstance();
            ItemRenderer itemRenderer = client.getItemRenderer();
            Map<Integer, Integer> piecesInBox = stack.get(ModComponents.PIECES_IN_BOX_COMPONENT);

            if (piecesInBox == null) return;

            List<ItemStack> allPieces = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : piecesInBox.entrySet()) {
                int pieceId = entry.getKey();
                int count = entry.getValue();
                ChessPiece piece = ChessGrid.getPieceById(pieceId);

                if (piece == null) continue;

                boolean isWhite = ChessGrid.getPieceRole(pieceId) == WHITE;
                ItemStack pieceStack = isWhite ? piece.getWhiteStack() : piece.getBlackStack();

                for (int i = 0; i < count; i++) {
                    allPieces.add(pieceStack);
                }
            }

            int piecesToRender = Math.min(allPieces.size(), MAX_TOTAL_PIECES);
            matrices.push();
            if (renderMode.equals(ModelTransformationMode.GUI)){
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-40));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10));
            } else if(renderMode.equals(ModelTransformationMode.FIXED)){
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(30));
            } else if(renderMode.equals(ModelTransformationMode.THIRD_PERSON_LEFT_HAND) || renderMode.equals(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)){
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5));
            }
            for (int i = 0; i < piecesToRender; i++) {
                int gridX = i % MAX_PIECES_IN_ROW;
                int gridY = i / MAX_PIECES_IN_ROW;

                renderPieceInBox(gridX, gridY, renderMode, allPieces.get(i), itemRenderer, matrices, vertexConsumers, client.world, light, overlay);
            }
            matrices.pop();
        }
    }

    @Unique
    private void renderPieceInBox(int gridX, int gridY, ModelTransformationMode renderMode, ItemStack pieceStack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay) {
        matrices.push();
        if (renderMode.equals(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) || renderMode.equals(ModelTransformationMode.FIRST_PERSON_LEFT_HAND)){
            matrices.scale(0.07f, 0.07f, 0.07f);
            matrices.translate(-1 + gridX * 0.62, 1, 0.35 + gridY * 0.55);
        } else if(renderMode.equals(ModelTransformationMode.GUI)){
            matrices.scale(0.13f, 0.13f, 0.13f);
            matrices.translate(-1.1 + gridX * 0.60, 0, -1.15 + gridY * 0.60);
        } else if(renderMode.equals(ModelTransformationMode.FIXED)){
            matrices.scale(0.12f, 0.12f, 0.12f);
            matrices.translate(-1.1 + gridX * 0.75, 0, -2.5 + gridY * 0.75);
        } else if (renderMode.equals(ModelTransformationMode.THIRD_PERSON_LEFT_HAND) || renderMode.equals(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)) {
            matrices.scale(0.075f, 0.075f, 0.075f);
            matrices.translate(-0.8 + gridX * 0.6, 0, -2.4 + gridY * 0.6);
        } else {
            matrices.scale(0.07f, 0.07f, 0.07f);
            matrices.translate(-1 + gridX * 0.62, 0, -1 + gridY * 0.62);
        }
        itemRenderer.renderItem(pieceStack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
    }
}
