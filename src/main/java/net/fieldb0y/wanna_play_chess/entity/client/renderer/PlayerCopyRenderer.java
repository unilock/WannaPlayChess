package net.fieldb0y.wanna_play_chess.entity.client.renderer;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.entity.custom.PlayerCopyEntity;
import net.fieldb0y.wanna_play_chess.layer.ModModelLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.fieldb0y.wanna_play_chess.entity.client.model.PlayerCopyModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlayerCopyRenderer extends LivingEntityRenderer<PlayerCopyEntity, PlayerCopyModel<PlayerCopyEntity>> {

    public PlayerCopyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerCopyModel<>(ctx.getPart(ModModelLayers.PLAYER_COPY)), 1);
    }

    @Override
    public Identifier getTexture(PlayerCopyEntity entity) {
        return entity.getSkin() != null ? entity.getSkin() : Identifier.of(WannaPlayChess.MOD_ID,"textures/entity/player_copy.png");
    }

    @Override
    public void render(PlayerCopyEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected void renderLabelIfPresent(PlayerCopyEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
        super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light, tickDelta);
    }
}
