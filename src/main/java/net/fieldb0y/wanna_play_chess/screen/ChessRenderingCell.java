package net.fieldb0y.wanna_play_chess.screen;


import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.item.custom.ChessPiece;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Arrays;

import static net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces.*;

public class ChessRenderingCell {
    private static final Identifier WHITE_TEXTURE = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/white_chess_cell.png");
    private static final Identifier BLACK_TEXTURE = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/black_chess_cell.png");

    public static final int COLOR_WHITE = 0;
    public static final int COLOR_BLACK = 1;

    public int heldPieceId;
    public int cellColor;

    public int x, y;
    public int width, height;

    public ChessRenderingCell(int cellColor, int x, int y){
        this(EMPTY.id, cellColor, x, y, 16, 16);
    }

    public ChessRenderingCell(int cellColor, int x, int y, int width, int height){
        this(EMPTY.id, cellColor, x, y, width, height);
    }

    public ChessRenderingCell(int heldPieceId, int cellColor, int x, int y, int width, int height){
        this.heldPieceId = heldPieceId;
        this.cellColor = cellColor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void putPiece(int id){
        this.heldPieceId = id;
    }

    public void render(DrawContext context){
        ChessPiece pieceItem = Arrays.stream(values()).filter((value)->value.id==heldPieceId).toList().getFirst().item;

       context.drawTexture(cellColor == COLOR_WHITE ? WHITE_TEXTURE : BLACK_TEXTURE, getRealX(), getRealY(), 0, 0, 0, width, height, width, height);
       if (pieceItem != null)
            context.drawItem(pieceItem.getDefaultStack(), getRealX(), getRealY());
    }

    public int getOppositeX(){
        return 7 - x;
    }

    public int getOppositeY(){
        return 7 - y;
    }

    public int getRealX(){
        return x * width;
    }

    public int getRealY(){
        return y * height;
    }
}
