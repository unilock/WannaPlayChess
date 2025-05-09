package net.fieldb0y.wanna_play_chess.chess.renderingStates;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessState;
import net.fieldb0y.wanna_play_chess.mixin.ScreenAccessor;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.fieldb0y.wanna_play_chess.screen.ClickableWidget;
import net.fieldb0y.wanna_play_chess.screenhandler.ChessBoardScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessRenderingState {
    protected RenderingProperties renderingProperties;
    protected ChessState serverState;

    public ChessBoardBlockEntity blockEntity;
    public MinecraftClient client;
    public ChessBoardScreen screen;

    public ChessRenderingState(ChessState serverState, ChessBoardScreen screen){
        this(serverState, screen, RenderingProperties.createDefault());
    }

    public ChessRenderingState(ChessState serverState, ChessBoardScreen screen, RenderingProperties renderingProperties){
        this.blockEntity = serverState.getBlockEntity();
        this.renderingProperties = renderingProperties;
        this.serverState = serverState;
        this.client = ((ScreenAccessor)screen).getClient();
        this.screen = screen;
    }

    public void onScreenResize(int width, int height){
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, ChessBoardScreen screen){
        return true;
    }

    public void createWidgets(ChessBoardScreen screen){}

    public boolean shouldRender(){
        return serverState.shouldUse();
    }

    public abstract void afterSwitch();

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY){}

    public RenderingProperties getRenderingProperties() {
        return renderingProperties;
    }

    public ChessState getServerState() {
        return serverState;
    }

    public int getScreenWidth(){
        return client.getWindow().getScaledWidth();
    }

    public int getScreenHeight(){
        return client.getWindow().getScaledHeight();
    }
}
