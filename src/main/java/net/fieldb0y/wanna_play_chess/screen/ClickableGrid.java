package net.fieldb0y.wanna_play_chess.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState;
import net.fieldb0y.wanna_play_chess.network.c2sPayloads.CellClickPayload;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;
import static net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState.*;

public class ClickableGrid {
    private static final String FILE_PATH = "/assets/wanna_play_chess/grid/clickable2.grid";
    public ChessGameRenderingState renderingState;

    public List<ClickableRect> rects = new ArrayList<>();
    private boolean isActive = true;

    public ClickableGrid(ChessGameRenderingState renderingState){
        this.renderingState = renderingState;
        generate();
    }

    public void render(DrawContext context){
        for (ClickableRect rect : rects){
            rect.render(context);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY){
        if (isActive){
            ClickableRect rect = getClickedRect(mouseX, mouseY);
            if (rect == null) return false;

            int x = renderingState.getPlayerRole() == WHITE ? rect.gridX : rect.getOppositeX();
            int y = renderingState.getPlayerRole() == WHITE ? rect.gridY : rect.getOppositeY();

            ClientPlayNetworking.send(new CellClickPayload(renderingState.blockEntity.getPos(), x, y,
                    ((ChessGameState)renderingState.getServerState()).isSingleplayer ? ((ChessGameState)renderingState.getServerState()).currentTurn : renderingState.getPlayerRole()));
        }

        return true;
    }

    private ClickableRect getClickedRect(double mouseX, double mouseY){
        for (ClickableRect rect : rects){
            if (rect.isMouseInRect(mouseX, mouseY))
                return rect;
        }

        return null;
    }

    public void onScreenResize(){
        int x = renderingState.getScreenWidth() / 2 - ((DEF_WINDOW_WIDTH / 2) * renderingState.getScreenHeight() / DEF_WINDOW_HEIGHT);

        for (ClickableRect rect : rects){
            rect.updateVertices(renderingState.getScreenHeight());
            rect.setOffset(x, rect.offset.y);
        }
    }

    public void generate() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                getClass().getResourceAsStream(FILE_PATH))))) {
            String line;
            for (int i = 0; i < ChessGrid.SIZE; i++) {
                for (int j = 0; j < ChessGrid.SIZE; j++) {
                    line = br.readLine();
                    if (line == null) break;
                    rects.add(new ClickableRect(parseLineToVectorList(line), j, i, DEF_WINDOW_WIDTH, DEF_WINDOW_HEIGHT, renderingState.getScreenHeight()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Vector2f> parseLineToVectorList(String line) {
        List<Vector2f> vertices = new ArrayList<>();
        List<Float> numbers = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            numbers.add(Float.parseFloat(matcher.group()));
        }

        if (numbers.size() >= 8) {
            vertices = List.of(
                    new Vector2f(numbers.get(0), numbers.get(1)),
                    new Vector2f(numbers.get(2), numbers.get(3)),
                    new Vector2f(numbers.get(4), numbers.get(5)),
                    new Vector2f(numbers.get(6), numbers.get(7))
            );
        }

        return vertices;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
