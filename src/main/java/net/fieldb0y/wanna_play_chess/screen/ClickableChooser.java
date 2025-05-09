package net.fieldb0y.wanna_play_chess.screen;

import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessRenderingState;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState.*;

public class ClickableChooser {
    public List<ClickableRect> rects = new ArrayList<>();
    public ChessRenderingState renderingState;

    public int renderX, renderY;
    public int width, height;
    public int buttonsCount;

    public boolean active = true;

    public ClickableChooser(int renderX, int renderY, int width, int height, int buttonsCount, ChessRenderingState renderingState){
        this.renderX = renderX;
        this.renderY = renderY;
        this.width = width;
        this.height = height;
        this.buttonsCount = buttonsCount;
        this.renderingState = renderingState;
        createRects();
    }

    private void createRects(){
        rects.clear();
        int rectWidth = width / buttonsCount;
        for (int i = 0; i < buttonsCount; i++){
            Vector2f cornerPos = Utils.toRelative(new Vector2i(renderX + rectWidth * i, renderY), DEF_WINDOW_WIDTH, DEF_WINDOW_HEIGHT);
            Vector2f relativeDimensions = Utils.toRelative(new Vector2i(rectWidth, height), DEF_WINDOW_WIDTH, DEF_WINDOW_HEIGHT);

            ClickableRect rect = new ClickableRect(List.of(cornerPos, new Vector2f(cornerPos.x + relativeDimensions.x, cornerPos.y),
                    new Vector2f(cornerPos.x + relativeDimensions.x, cornerPos.y + relativeDimensions.y), new Vector2f(cornerPos.x, cornerPos.y + relativeDimensions.y)),
                    i, 0, DEF_WINDOW_WIDTH, DEF_WINDOW_HEIGHT, DEF_WINDOW_HEIGHT);

            this.rects.add(rect);
        }
    }

    public void render(DrawContext context){
        if (active){
            for (ClickableRect rect : rects)
                rect.render(context);
        }
    }

    public void renderRect(DrawContext context, int id, int color){
        if (active)
            rects.get(id).render(context, color);
    }

    public void onScreenResize(boolean setYOffset){
        int x = renderingState.getScreenWidth() / 2 - (DEF_WINDOW_WIDTH / 2);
        int y = renderingState.getScreenHeight() / 2 - (DEF_WINDOW_HEIGHT / 2);

        for (ClickableRect rect : rects){
            rect.setOffset(x, setYOffset ? y : 0);
            rect.updateVerticesX();
        }
    }

    public void setPos(int x, int y){
        this.renderX = x;
        this.renderY = y;
        for(ClickableRect rect : rects){
            rect.setOffset(x, y);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, Consumer<Integer> executableMethod) {
        if (active){
            for (ClickableRect rect : rects){
                if (rect.isMouseInRect(mouseX, mouseY)){
                    executableMethod.accept(rect.gridX);
                    return true;
                }
            }
        }
        return false;
    }

    public int getMouseIntersectedRect(double mouseX, double mouseY){
        if (active){
            for (ClickableRect rect : rects){
                if (rect.isMouseInRect(mouseX, mouseY)){
                    return rect.gridX;
                }
            }
        }
        return -1;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
