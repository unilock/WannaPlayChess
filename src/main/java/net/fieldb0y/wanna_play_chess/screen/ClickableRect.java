package net.fieldb0y.wanna_play_chess.screen;

import net.fieldb0y.wanna_play_chess.utils.GeometryUtils;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.List;

public class ClickableRect {
    public Vector2i[] absoluteVertices = new Vector2i[4];
    public Vector2f[] relativeVertices = new Vector2f[4];

    public int gridX, gridY;
    public Vector2i offset = new Vector2i(0, 0);
    public final int defWindowWidth, defWindowHeight;
    public MinecraftClient client;
    private int color = Colors.GREEN;

    public ClickableRect(List<Vector2f> relativeVertices, int gridX, int gridY, int defWindowWidth, int defWindowHeight, int screenHeight){
        this.defWindowWidth = defWindowWidth;
        this.defWindowHeight = defWindowHeight;
        for (int i = 0; i < 4; i++){
            this.relativeVertices[i] = relativeVertices.get(i);
        }
        updateVertices(screenHeight);
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public void render(DrawContext context){
        for (int i = 0; i < 4; i++) {
            Vector2i current = absoluteVertices[i];
            Vector2i next = absoluteVertices[(i + 1) % 4];
            Utils.drawLine(context, current.x, current.y, next.x, next.y, color);
        }
    }

    public void render(DrawContext context, int color){
        for (int i = 0; i < 4; i++) {
            Vector2i current = absoluteVertices[i];
            Vector2i next = absoluteVertices[(i + 1) % 4];
            Utils.drawLine(context, current.x, current.y, next.x, next.y, color);
        }
    }

    public boolean isMouseInRect(double mouseX, double mouseY){
        double[] xCoords = {absoluteVertices[0].x, absoluteVertices[1].x, absoluteVertices[2].x, absoluteVertices[3].x};
        double[] yCoords = {absoluteVertices[0].y, absoluteVertices[1].y, absoluteVertices[2].y, absoluteVertices[3].y};
        return GeometryUtils.isPointInQuadrilateral(mouseX, mouseY, xCoords, yCoords);
    }

    public void setOffset(int x, int y){
        this.offset.set(x, y);
    }

    public int getOppositeX(){
        return 7 - gridX;
    }

    public int getOppositeY(){
        return 7 - gridY;
    }

    public void updateVertices(int screenHeight){
        for (int i = 0; i < 4; i++){
            this.absoluteVertices[i] = new Vector2i(offset.x + Math.round(relativeVertices[i].x * defWindowWidth * ((float) screenHeight / defWindowHeight)), offset.y + Math.round(relativeVertices[i].y * screenHeight));
        }
    }

    public void updateVerticesX(){
        for (int i = 0; i < 4; i++){
            this.absoluteVertices[i] = new Vector2i(offset.x + Math.round(relativeVertices[i].x * defWindowWidth), offset.y + Math.round(relativeVertices[i].y * defWindowHeight));
        }
    }
}
