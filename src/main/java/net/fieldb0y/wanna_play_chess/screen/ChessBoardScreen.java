package net.fieldb0y.wanna_play_chess.screen;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameOverRenderingState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessRenderingState;
import net.fieldb0y.wanna_play_chess.screenhandler.ChessBoardScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardScreen extends HandledScreen<ChessBoardScreenHandler> {
    public List<ChessRenderingState> renderingGameStates = new ArrayList<>();
    public ChessRenderingState currentRenderingState;
    private int previousFov = 70;
    private int currentFov = 0;

    public ChessBoardScreen(ChessBoardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setupRenderingStates();
        client.options.hudHidden = true;
        previousFov = client.options.getFov().getValue();
        currentFov = previousFov;
        currentRenderingState.createWidgets(this);
    }

    @Override
    public void close() {
        client.options.hudHidden = false;
        client.options.getFov().setValue(previousFov);
        super.close();
    }

    private void updateFov(float delta){
        currentFov = MathHelper.lerp(delta, currentFov, 70);
        client.options.getFov().setValue(currentFov);
    }

    public void changeRenderingStates(){
        if (!currentRenderingState.shouldRender()){
            for (ChessRenderingState renderingGameState : renderingGameStates){
                if (renderingGameState.shouldRender()){
                    currentRenderingState.afterSwitch();
                    this.currentRenderingState = renderingGameState; break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        currentRenderingState.mouseClicked(mouseX, mouseY, button, this);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setupRenderingStates(){
        renderingGameStates.add(new ChessLobbyRenderingState(handler.blockEntity.states.get(ChessBoardBlockEntity.LOBBY_STATE),this));
        renderingGameStates.add(new ChessGameRenderingState(handler.blockEntity.states.get(ChessBoardBlockEntity.GAME_STATE), this));
        renderingGameStates.add(new ChessGameOverRenderingState(handler.blockEntity.states.get(ChessBoardBlockEntity.GAME_OVER_STATE), this));

        for (ChessRenderingState renderingGameState : renderingGameStates){
            if (renderingGameState.shouldRender()) {
                this.currentRenderingState = renderingGameState; break;
            }
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        currentRenderingState.onScreenResize(width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateFov(delta);
        changeRenderingStates();
        currentRenderingState.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        currentRenderingState.drawBackground(context, delta, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        if (currentRenderingState.getRenderingProperties().renderScreenTitle){
            super.drawForeground(context, mouseX, mouseY);
        }
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
        if (currentRenderingState.getRenderingProperties().renderInGameBackground){
            super.renderInGameBackground(context);
        }
    }
}
