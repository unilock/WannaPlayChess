package net.fieldb0y.wanna_play_chess.utils;

import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4i;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static boolean isMouseInBounds(int x, int y, int width, int height, double mouseX, double mouseY){
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    public static boolean isCellInSquare(int cellX, int cellY, int centerX, int centerY, int squareRadius){
        boolean withinX = (cellX >= centerX - squareRadius) && (cellX <= centerX + squareRadius);
        boolean withinY = (cellY >= centerY - squareRadius) && (cellY <= centerY + squareRadius);
        return withinX && withinY;
    }

    public static boolean isPathHasNoPieces(List<Vector2i> path, ChessGrid grid){
        for (Vector2i cell : path){
            if (grid.safeGetPieceId(cell.x, cell.y) != ChessPieces.EMPTY.id)
                return false;
        }
        return true;
    }

    public static List<Vector2f> toRelative(List<Vector2i> absoluteVertices, int windowWidth, int windowHeight){
        List<Vector2f> relativeVertices = new ArrayList<>();
        for (Vector2i absoluteVertex : absoluteVertices){
            relativeVertices.add(toRelative(absoluteVertex, windowWidth, windowHeight));
        }
        return relativeVertices;
    }

    public static Vector2f toRelative(Vector2i absoluteVertex, int windowWidth, int windowHeight){
        return new Vector2f((float) absoluteVertex.x / windowWidth, (float) absoluteVertex.y / windowHeight);
    }

    public static Vector2i secondsToMinAndSec(int seconds){
        return new Vector2i((int)Math.floor((double) seconds / 60), seconds % 60);
    }

    public static String timeToString(Vector2i time){
        if (time.equals(-1, -1))
            return "——:——";
        return time.x + ":" + (String.valueOf(time.y).length() == 1 ? "0" : "") + time.y;
    }

    public static File getFile(String path){
        return new File(path);
    }

    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int currentX = x1;
        int currentY = y1;

        while (true) {
            context.fill(currentX, currentY, currentX + 1, currentY + 1, color);
            if (currentX == x2 && currentY == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
    }

    public static void advancedFill(DrawContext context, int x1, int y1, int x2, int y2, Vector4i color){
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, (float)x1, (float)y1, (float)0).color(color.x, color.y, color.z, color.w);
        vertexConsumer.vertex(matrix4f, (float)x1, (float)y2, (float)0).color(color.x, color.y, color.z, color.w);
        vertexConsumer.vertex(matrix4f, (float)x2, (float)y2, (float)0).color(color.x, color.y, color.z, color.w);
        vertexConsumer.vertex(matrix4f, (float)x2, (float)y1, (float)0).color(color.x, color.y, color.z, color.w);
        context.draw();
    }

    public static void renderScaledHead(DrawContext context, ItemStack head, float scale, int baseX, int baseY) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(baseX + 8, baseY + 8, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(-8, -8, 0);
        context.drawItem(head, 0, 0);
        matrices.pop();
    }

    public static String orderedTextToString(OrderedText orderedText) {
        StringBuilder stringBuilder = new StringBuilder();
        orderedText.accept((index, style, codePoint) -> {
            stringBuilder.appendCodePoint(codePoint);
            return true;
        });
        return stringBuilder.toString();
    }
}
