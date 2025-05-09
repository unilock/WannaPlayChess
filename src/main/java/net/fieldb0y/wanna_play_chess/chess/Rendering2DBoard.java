package net.fieldb0y.wanna_play_chess.chess;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState;
import net.fieldb0y.wanna_play_chess.network.payloads.CellClickPayload;
import net.fieldb0y.wanna_play_chess.screen.ChessRenderingCell;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.WHITE;

public class Rendering2DBoard {
    private final Vector2i offset;
    public ChessGameRenderingState renderingState;
    public ChessRenderingCell[][] cells = new ChessRenderingCell[8][8];
    public boolean isActive;

    public Rendering2DBoard(ChessGameRenderingState renderingState){
        this(new Vector2i(0, 0), false, renderingState);
    }

    public Rendering2DBoard(Vector2i offset, boolean isActive, ChessGameRenderingState renderingState){
        this.offset = offset;
        this.renderingState = renderingState;
        this.isActive = isActive;

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new ChessRenderingCell((i + j) % 2 == 0 ? ChessRenderingCell.COLOR_WHITE : ChessRenderingCell.COLOR_BLACK, i + offset.x, j + offset.y,  20, 20);
            }
        }
    }

    public void render(DrawContext context){
        if (isActive){
            for (int i = 0; i < cells.length; i++){
                for (int j = 0; j < cells.length; j++){
                    int[][] gridData = ((ChessGameState)renderingState.getServerState()).getGrid().data;

                    int x = renderingState.getPlayerRole() == WHITE ? i : 7 - i;
                    int y = renderingState.getPlayerRole() == WHITE ? j : 7 - j;

                    cells[x][y].putPiece(gridData[i][j]);
                    cells[x][y].render(context);
                }
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY){
        if (isActive){
            ChessRenderingCell clickedCell = getClickedCell(mouseX, mouseY);
            if (clickedCell == null) return false;

            int x = renderingState.getPlayerRole() == WHITE ? clickedCell.x : clickedCell.getOppositeX();
            int y = renderingState.getPlayerRole() == WHITE ? clickedCell.y : clickedCell.getOppositeY();

            ClientPlayNetworking.send(new CellClickPayload(renderingState.blockEntity.getPos(), x - offset.x, y - offset.y, renderingState.getPlayerRole()));
        }
        return true;
    }

    private ChessRenderingCell getClickedCell(double mouseX, double mouseY){
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells.length; j++){
                ChessRenderingCell cell = cells[i][j];
                if (Utils.isMouseInBounds(cell.getRealX(), cell.getRealY(), cell.width, cell.height, mouseX, mouseY)){
                    return cell;
                }
            }
        }
        return null;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
