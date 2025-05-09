package net.fieldb0y.wanna_play_chess;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.fieldb0y.wanna_play_chess.block.entity.ModBlockEntities;
import net.fieldb0y.wanna_play_chess.block.entity.model.ChessBoardModel;
import net.fieldb0y.wanna_play_chess.block.entity.renderer.ChessBoardRenderer;
import net.fieldb0y.wanna_play_chess.entity.ModEntities;
import net.fieldb0y.wanna_play_chess.entity.client.model.PlayerCopyModel;
import net.fieldb0y.wanna_play_chess.entity.client.renderer.PlayerCopyRenderer;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.fieldb0y.wanna_play_chess.item.TestItemRenderer;
import net.fieldb0y.wanna_play_chess.layer.ModModelLayers;
import net.fieldb0y.wanna_play_chess.network.payloads.SetGameTimeTextFieldPayload;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.fieldb0y.wanna_play_chess.screenhandler.ModScreenHandlers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class WannaPlayChessClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CHESS_BOARD_SCREEN_HANDLER, ChessBoardScreen::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PLAYER_COPY, PlayerCopyModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CHESS_BOARD, ChessBoardModel::getTexturedModelData);

        ClientPlayNetworking.registerGlobalReceiver(SetGameTimeTextFieldPayload.ID, SetGameTimeTextFieldPayload::receive);

        registerRenderers();
    }

    private void registerRenderers(){
        EntityRendererRegistry.register(ModEntities.PLAYER_COPY, PlayerCopyRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.CHESS_BOARD_BLOCK_ENTITY, ChessBoardRenderer::new);
    }
}
