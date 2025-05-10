package net.fieldb0y.wanna_play_chess.chess.renderingStates;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.CameraAnimationPlayable;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.ChessGrid;
import net.fieldb0y.wanna_play_chess.chess.PiecesData;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessState;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessPieces;
import net.fieldb0y.wanna_play_chess.network.c2sPayloads.*;
import net.fieldb0y.wanna_play_chess.screen.ClickableChooser;
import net.fieldb0y.wanna_play_chess.screen.ClickableGrid;
import net.fieldb0y.wanna_play_chess.mixin.ScreenAccessor;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.joml.Vector4i;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.BLACK;
import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.WHITE;


public class ChessGameRenderingState extends ChessRenderingState {
    public static final int DEF_WINDOW_WIDTH = 427, DEF_WINDOW_HEIGHT = 240;
    private static final Identifier PIECES_ATLAS = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/chess_pieces_atlas.png");
    private static final Identifier PIECES_CHOOSER_BG = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/pieces_chooser_bg.png");
    private static final Identifier CHESS_GAME_TEXTURE = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/chess_game_stats.png");

    public static final Text DRAW_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_rs.draw_button_text");
    public static final Text RESIGN_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_rs.resign_button_text");
    public static final Text RESIGN_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_rs.resign_text");
    public static final Text DRAW_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_rs.draw_text");

    public ButtonWidget offerDrawButton;
    public ButtonWidget resignButton;
    public ButtonWidget yesButton, noButton;

    private PlayerEntity player;
    public ClickableGrid clickableGrid;
    private ClickableChooser pieceChooser;

    private float confirmationWindowY = -39;
    private float piecesChooserY = -49;
    private float gameStatsY = -87;

    private int whiteTimeLeftInSec = -1;
    private int blackTimeLeftInSec = -1;

    public ChessGameRenderingState(ChessState serverState, ChessBoardScreen screen) {
        super(serverState, screen, new RenderingProperties(false, false));
        ChessGameState gameState = (ChessGameState)serverState;
        this.player = client.player;
        this.focusCamera();
        this.whiteTimeLeftInSec = gameState.whiteTimeLeft;
        this.blackTimeLeftInSec = gameState.blackTimeLeft;

        if (serverState.shouldUse()) init();
    }

    public void init(){
        clickableGrid = new ClickableGrid(this);
        clickableGrid.onScreenResize();

        pieceChooser = new ClickableChooser(148, 10, 135, 30, 4,this);
        pieceChooser.setActive(false);
    }

    @Override
    public void createWidgets(ChessBoardScreen screen) {
        pieceChooser = new ClickableChooser(148, 10, 135, 30, 4,this);
        pieceChooser.onScreenResize(false);

        offerDrawButton = ButtonWidget.builder(DRAW_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new DrawOfferButtonPayload(blockEntity.getPos(), getPlayerRole()));
        }).dimensions(getScreenWidth() - 88, 59, 40, 18).build();

        resignButton = ButtonWidget.builder(RESIGN_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new ResignButtonPayload(blockEntity.getPos(), getPlayerRole()));
        }).dimensions(getScreenWidth() - 45, 59, 40, 18).build();

        yesButton = ButtonWidget.builder(Text.literal("Yes"), button->{
            ClientPlayNetworking.send(new YesButtonPayload(blockEntity.getPos()));
        }).dimensions(getScreenWidth()/2 - 50, 18, 40, 13).build();

        noButton = ButtonWidget.builder(Text.literal("No"), button->{
            ClientPlayNetworking.send(new NoButtonPayload(blockEntity.getPos()));
        }).dimensions(getScreenWidth()/2 + 10, 18, 40, 13).build();


        ((ScreenAccessor)screen).addWidget(offerDrawButton);
        ((ScreenAccessor)screen).addWidget(resignButton);
        ((ScreenAccessor)screen).addWidget(yesButton);
        ((ScreenAccessor)screen).addWidget(noButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ChessGameState chessGameState = (ChessGameState)serverState;

        renderChessGameStats(context, delta);

        Vector2i  pawnCell = chessGameState.findPieceCellWithTag(PiecesData.DataTag.SHOULD_TURN);
        pieceChooser.setActive(!pawnCell.equals(-1, -1) && (chessGameState.isSingleplayer || ChessGrid.getPieceRole(chessGameState.getGrid().safeGetPieceId(pawnCell.x, pawnCell.y)) == getPlayerRole()));
        if (pieceChooser.active)
            renderPieceChooser(context, mouseX, mouseY, chessGameState.isSingleplayer ? ChessGrid.getPieceRole(chessGameState.getGrid().safeGetPieceId(pawnCell.x, pawnCell.y)) : getPlayerRole(), delta);
        else piecesChooserY = -49;
    }

    float whiteSkullScale = 1.0f;
    float blackSkullScale = 1.0f;
    float maxSkullScale = 1.5f;

    private void renderChessGameStats(DrawContext context, float delta){
        ChessGameState gameState = (ChessGameState)serverState;
        TextRenderer textRenderer = ((ScreenAccessor)screen).getTextRenderer();
        MatrixStack matrices = context.getMatrices();

        ItemStack whitePlayerHead = new ItemStack(Items.PLAYER_HEAD);
        ItemStack blackPlayerHead = new ItemStack(Items.PLAYER_HEAD);

        if (gameState.getPlayers()[WHITE] != null && gameState.getPlayers()[BLACK] != null){
            int currentTurn = gameState.currentTurn;
            Vector2i whiteTime = Utils.secondsToMinAndSec(whiteTimeLeftInSec);
            Vector2i blackTime = Utils.secondsToMinAndSec(blackTimeLeftInSec);
            float textScale = 1.0f;

            matrices.push();
            matrices.scale(textScale, textScale, textScale);
            context.drawText(textRenderer, Utils.timeToString(whiteTime), getScreenWidth() - textRenderer.getWidth(Utils.timeToString(whiteTime)) - 25, (int)(gameStatsY + 9), currentTurn == WHITE ? Colors.LIGHT_RED : Colors.GRAY, true);
            context.drawText(textRenderer, Utils.timeToString(blackTime), getScreenWidth() - textRenderer.getWidth(Utils.timeToString(blackTime)) - 25, (int)(gameStatsY + 34), currentTurn == BLACK ? Colors.LIGHT_RED : Colors.GRAY, true);
            matrices.pop();

            PlayerEntity whitePlayer = blockEntity.getWorld().getPlayerByUuid(gameState.getPlayers()[WHITE]);
            PlayerEntity blackPlayer = blockEntity.getWorld().getPlayerByUuid(gameState.getPlayers()[BLACK]);

            if (whitePlayer != null)
                whitePlayerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(whitePlayer.getGameProfile()));
            if (blackPlayer != null)
                blackPlayerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(blackPlayer.getGameProfile()));

            if (currentTurn == WHITE){
                whiteSkullScale = MathHelper.lerp(delta, whiteSkullScale, maxSkullScale);
                blackSkullScale = MathHelper.lerp(delta, blackSkullScale, 1.0f);
            } else {
                blackSkullScale = MathHelper.lerp(delta, blackSkullScale, maxSkullScale);
                whiteSkullScale = MathHelper.lerp(delta, whiteSkullScale, 1.0f);
            }

            Utils.renderScaledHead(context, whitePlayerHead, whiteSkullScale, getScreenWidth() - 86, (int)(gameStatsY + 3));
            Utils.renderScaledHead(context, blackPlayerHead, blackSkullScale, getScreenWidth() - 86, (int)(gameStatsY + 28));
        }
    }

    public void updateTimer(int whiteTimeLeftInSec, int blackTimeLeftInSec){
        this.whiteTimeLeftInSec = whiteTimeLeftInSec;
        this.blackTimeLeftInSec = blackTimeLeftInSec;
    }

    public void renderPieceChooser(DrawContext context, int mouseX, int mouseY, int role, float delta){
        int width = 126, height = 61;
        int x = context.getScaledWindowWidth() / 2 - width / 2;
        int centerX = getScreenWidth()/2;

        piecesChooserY = MathHelper.lerp(delta * 0.5f, piecesChooserY, 0);

        context.drawTexture(PIECES_CHOOSER_BG, x - 5, (int) piecesChooserY, 0, 0, 140, 98/2, 140, 98/2);
        context.drawTexture(PIECES_ATLAS, x, (int) (piecesChooserY + 10), 0, (role == BLACK ? (float) height / 2 : 0), width, height/2, width, height);

        int intersectedRect = pieceChooser.getMouseIntersectedRect(mouseX, mouseY);
        switch (intersectedRect){
            case 0: Utils.advancedFill(context, centerX - 65, 9, centerX - 31, 40, new Vector4i(1, 1, 1, 80)); break;
            case 1: Utils.advancedFill(context, centerX - 30, 9, centerX, 40, new Vector4i(1, 1, 1, 80)); break;
            case 2: Utils.advancedFill(context, centerX, 9, centerX + 35, 40, new Vector4i(1, 1, 1, 80)); break;
            case 3: Utils.advancedFill(context, centerX + 35, 9, centerX + 69, 40, new Vector4i(1, 1, 1, 80)); break;
        }
    }

    public void renderConfirmationWindow(DrawContext context, Text text, float delta){
        ScreenAccessor accessor = (ScreenAccessor)screen;
        TextRenderer textRenderer = accessor.getTextRenderer();
        int width = 126, height = 40;
        int x = getScreenWidth() / 2 - width / 2;
        confirmationWindowY = MathHelper.lerp(delta * 0.5f, confirmationWindowY, 0);
        yesButton.setPosition(getScreenWidth()/2 - 50, (int) (confirmationWindowY + 18));
        noButton.setPosition(getScreenWidth()/2 + 10, (int)(confirmationWindowY + 18));

        context.drawTexture(PIECES_CHOOSER_BG, x, (int) confirmationWindowY, 0, 0, width, height, width, height);
        context.drawText(textRenderer, text.copy().formatted(Formatting.BOLD), getScreenWidth()/2 - textRenderer.getWidth(text.copy().formatted(Formatting.BOLD))/2, (int)(confirmationWindowY + 7), Colors.LIGHT_RED, true);
    }

    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int width = Math.round(167/1.7f);
        int height = Math.round(148/1.7f);

        gameStatsY = MathHelper.lerp(0.4f * delta, gameStatsY, 0);
        context.drawTexture(CHESS_GAME_TEXTURE, getScreenWidth() - width, (int)gameStatsY, 0, 0, width, height, width, height);

        manageButtons(context, delta);
    }

    private void manageButtons(DrawContext context, float delta){
        ChessGameState gameState = (ChessGameState)serverState;

        offerDrawButton.setY((int) gameStatsY + 59);
        resignButton.setY((int) gameStatsY + 59);

        if ((gameState.drawOfferRole != getPlayerRole() && gameState.drawOfferRole != -1) || (gameState.drawOfferRole != -1 && gameState.isSingleplayer)){
            if (context != null) renderConfirmationWindow(context, DRAW_TEXT, delta);
            yesButton.visible = true;
            noButton.visible = true;
        } else if (gameState.resignOfferRole == getPlayerRole()){
            if(context != null) renderConfirmationWindow(context, RESIGN_TEXT, delta);
            yesButton.visible = true;
            noButton.visible = true;
        } else {
            yesButton.visible = false;
            noButton.visible = false;
            confirmationWindowY = -39;
        }

        if (gameState.drawOfferRole != -1 || gameState.resignOfferRole != -1 || pieceChooser.active){
            resignButton.active = false;
            offerDrawButton.active = false;
        } else {
            resignButton.active = true;
            offerDrawButton.active = true;
        }
    }

    @Override
    public void onScreenResize(int width, int height) {
        clickableGrid.onScreenResize();
        pieceChooser.onScreenResize(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, ChessBoardScreen screen) {
        clickableGrid.onScreenResize();
        clickableGrid.mouseClicked(mouseX, mouseY);
        pieceChooser.mouseClicked(mouseX, mouseY, this::pieceChooserClick);
        return true;
    }

    public void pieceChooserClick(int cellId){
        ChessGameState chessGameState = (ChessGameState)serverState;
        int whitePieceId = cellId == 0 ? ChessPieces.WHITE_QUEEN.id : cellId == 1 ? ChessPieces.WHITE_BISHOP.id : cellId == 2 ? ChessPieces.WHITE_KNIGHT.id : ChessPieces.WHITE_ROOK.id;
        ClientPlayNetworking.send(new TurnPawnIntoPayload(blockEntity.getPos(), (chessGameState.isSingleplayer
                ? (chessGameState.currentTurn == WHITE ? whitePieceId : whitePieceId + 6) : (getPlayerRole() == WHITE ? whitePieceId : whitePieceId + 6))));
    }

    public int getPlayerRole(){
        if (((ChessGameState)serverState).getPlayers()[WHITE] == null || ((ChessGameState)serverState).getPlayers()[BLACK] == null) return -1;

        int whiteResult = this.client.player.getUuid().compareTo(((ChessGameState)serverState).getPlayers()[WHITE]);
        int blackResult = this.client.player.getUuid().compareTo(((ChessGameState)serverState).getPlayers()[BLACK]);
        if (!serverState.shouldUse() || (whiteResult != 0 && blackResult != 0)) return -1;
        return whiteResult == 0 ? WHITE : BLACK;
    }

    public void focusCamera(){
        if (this.shouldRender()){
            Camera camera = client.gameRenderer.getCamera();
            ((CameraAnimationPlayable)camera).focusOnBlockEntity(blockEntity, player);
        }
    }

    @Override
    public void afterSwitch() {
        screen.renderingGameStates.get(ChessBoardBlockEntity.GAME_OVER_STATE).createWidgets(screen);

        offerDrawButton.visible = false;
        resignButton.visible = false;
        yesButton.visible = false;
        noButton.visible = false;
    }
}
