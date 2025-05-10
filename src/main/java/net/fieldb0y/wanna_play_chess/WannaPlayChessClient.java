package net.fieldb0y.wanna_play_chess;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fieldb0y.wanna_play_chess.block.entity.ModBlockEntities;
import net.fieldb0y.wanna_play_chess.block.entity.model.ChessBoardModel;
import net.fieldb0y.wanna_play_chess.block.entity.renderer.ChessBoardRenderer;
import net.fieldb0y.wanna_play_chess.entity.ModEntities;
import net.fieldb0y.wanna_play_chess.entity.client.model.PlayerCopyModel;
import net.fieldb0y.wanna_play_chess.entity.client.renderer.PlayerCopyRenderer;
import net.fieldb0y.wanna_play_chess.layer.ModModelLayers;
import net.fieldb0y.wanna_play_chess.network.s2cPayloads.SetGameTimeTextFieldPayload;
import net.fieldb0y.wanna_play_chess.network.s2cPayloads.TimerUpdatePayload;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.fieldb0y.wanna_play_chess.screenhandler.ModScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class WannaPlayChessClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CHESS_BOARD_SCREEN_HANDLER, ChessBoardScreen::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PLAYER_COPY, PlayerCopyModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CHESS_BOARD, ChessBoardModel::getTexturedModelData);

        registerS2CPackets();
        registerRenderers();
    }

    private void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(SetGameTimeTextFieldPayload.ID, SetGameTimeTextFieldPayload::receive);
        ClientPlayNetworking.registerGlobalReceiver(TimerUpdatePayload.ID, TimerUpdatePayload::receive);
    }

    private void registerRenderers(){
        EntityRendererRegistry.register(ModEntities.PLAYER_COPY, PlayerCopyRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CHESS_BOARD_BLOCK_ENTITY, ChessBoardRenderer::new);
    }
}
