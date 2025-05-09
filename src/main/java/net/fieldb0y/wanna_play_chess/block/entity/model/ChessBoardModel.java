// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.fieldb0y.wanna_play_chess.block.entity.model;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChessBoardModel extends Model {
	private final ModelPart board;
	public static final Identifier TEXTURE = Identifier.of(WannaPlayChess.MOD_ID,"textures/block/chess_board_block.png");

	public ChessBoardModel(ModelPart root) {
		super(RenderLayer::getEntitySolid);
		this.board = root.getChild("board");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData board = modelPartData.addChild("board", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-8.0F, 0.0F, -8.0F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public ModelPart getPart() {
		return board;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		board.render(matrices, vertices, light, overlay, color);
	}
}