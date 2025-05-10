package net.fieldb0y.wanna_play_chess.chess.renderingStates;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.CameraAnimationPlayable;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameOverState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessState;
import net.fieldb0y.wanna_play_chess.mixin.ScreenAccessor;
import net.fieldb0y.wanna_play_chess.network.c2sPayloads.BackToLobbyButtonPayload;
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

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class ChessGameOverRenderingState extends ChessRenderingState {
    public static final Text BACK_TO_LOBBY_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.back_to_lobby");
    public static final Text WHITE_WORD = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.white_word");
    public static final Text BLACK_WORD = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.black");
    public static final Text WON_WORD = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.won_word");
    public static final Text DRAW_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.draw_text");
    public static final Text GAME_OVER_REASON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".game_over_rs.game_over_reason_text");

    private static final Identifier CHESS_GAME_OVER_TEXTURE = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/chess_game_over.png");

    public PlayerEntity player;
    private ButtonWidget backToLobbyButton;

    private float whiteSkullScale = -1;
    private float blackSkullScale = -1;
    private float winnerSkullScale = 2f;
    private float looserSkullScale = 1.5f;

    public ChessGameOverRenderingState(ChessState serverState, ChessBoardScreen screen) {
        super(serverState, screen, new RenderingProperties(false, false));
        this.player = client.player;
        this.focusCamera();
    }

    @Override
    public void createWidgets(ChessBoardScreen screen) {
        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        backToLobbyButton = ButtonWidget.builder(BACK_TO_LOBBY_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new BackToLobbyButtonPayload(blockEntity.getPos()));
        }).dimensions(centerX - 39, centerY + 52, 80, 18).build();

        ((ScreenAccessor)screen).addWidget(backToLobbyButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ScreenAccessor accessor = (ScreenAccessor)screen;
        TextRenderer textRenderer = accessor.getTextRenderer();
        MatrixStack matrices = context.getMatrices();
        ChessGameOverState gameOverState = (ChessGameOverState)serverState;

        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        renderSkulls(context);

        matrices.push();

        if (gameOverState.winnerRole == WHITE || gameOverState.winnerRole == BLACK) {
            Text text = Text.literal(gameOverState.winnerRole == WHITE ? WHITE_WORD.getString() : BLACK_WORD.getString())
                    .formatted(gameOverState.winnerRole == WHITE ? Formatting.WHITE : Formatting.DARK_GRAY, Formatting.BOLD)
                    .append(" ")
                    .append(WON_WORD.copy().formatted(Formatting.RED, Formatting.BOLD));

            int textWidth = textRenderer.getWidth(text);
            float maxScale = Math.min(1.3f, 110f / textWidth);

            matrices.scale(maxScale, maxScale, 1);
            int x = (int) (centerX / maxScale - (float) textWidth / 2);
            int y = (int) ((centerY - 53) / maxScale);

            context.drawText(textRenderer, text, x, y, Colors.GRAY, true);
        } else {
            Text text = DRAW_TEXT.copy().formatted(Formatting.AQUA, Formatting.BOLD);
            int textWidth = textRenderer.getWidth(text);
            float maxScale = Math.min(1.3f, 110f / textWidth);

            matrices.scale(maxScale, maxScale, 1);
            int x = (int) (centerX / maxScale - (float) textWidth / 2);
            int y = (int) ((centerY - 53) / maxScale);

            context.drawText(textRenderer, text, x, y, Colors.GRAY, true);
        }
        matrices.pop();

        context.drawText(textRenderer, GAME_OVER_REASON_TEXT.copy().formatted(Formatting.UNDERLINE),
                centerX - 45, centerY + 10, Colors.ALTERNATE_WHITE, false);
        if (gameOverState.gameOverReason != null){
            context.drawText(textRenderer, gameOverState.gameOverReason.text.copy().formatted(Formatting.ITALIC),
                    centerX - textRenderer.getWidth(gameOverState.gameOverReason.text)/2, centerY + 25, Colors.YELLOW, false);
        }
    }

    private void renderSkulls(DrawContext context){
        ChessGameOverState gameOverState = (ChessGameOverState)serverState;

        if (gameOverState.players[WHITE] != null && gameOverState.players[BLACK] != null){
            ItemStack whitePlayerHead = new ItemStack(Items.PLAYER_HEAD);
            ItemStack blackPlayerHead = new ItemStack(Items.PLAYER_HEAD);

            PlayerEntity whitePlayer = blockEntity.getWorld().getPlayerByUuid(gameOverState.players[WHITE]);
            PlayerEntity blackPlayer = blockEntity.getWorld().getPlayerByUuid(gameOverState.players[BLACK]);

            if (whitePlayer != null)
                whitePlayerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(whitePlayer.getGameProfile()));
            if (blackPlayer != null)
                blackPlayerHead.set(DataComponentTypes.PROFILE, new ProfileComponent(blackPlayer.getGameProfile()));

            if (gameOverState.winnerRole == WHITE){
                if (whiteSkullScale == -1) whiteSkullScale = looserSkullScale;
                if (blackSkullScale == -1) blackSkullScale = winnerSkullScale;
                whiteSkullScale = MathHelper.lerp(0.02f, whiteSkullScale, winnerSkullScale);
                blackSkullScale = MathHelper.lerp(0.02f, blackSkullScale, looserSkullScale);
            } else if (gameOverState.winnerRole == BLACK){
                if (whiteSkullScale == -1) whiteSkullScale = winnerSkullScale;
                if (blackSkullScale == -1) blackSkullScale = looserSkullScale;
                whiteSkullScale = MathHelper.lerp(0.02f, whiteSkullScale, looserSkullScale);
                blackSkullScale = MathHelper.lerp(0.02f, blackSkullScale, winnerSkullScale);
            } else {
                if (whiteSkullScale == -1) whiteSkullScale = looserSkullScale;
                if (blackSkullScale == -1) blackSkullScale = looserSkullScale;
                whiteSkullScale = MathHelper.lerp(0.02f, whiteSkullScale, winnerSkullScale);
                blackSkullScale = MathHelper.lerp(0.02f, blackSkullScale, winnerSkullScale);
            }

            Utils.renderScaledHead(context, whitePlayerHead, whiteSkullScale, getScreenWidth()/2 - 40, getScreenHeight()/2 - 25);
            Utils.renderScaledHead(context, blackPlayerHead, blackSkullScale, getScreenWidth()/2 + 25, getScreenHeight()/2 - 25);
        }
    }

    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        context.drawTexture(CHESS_GAME_OVER_TEXTURE, centerX - 119/2, centerY - 119/2, 0, 0, 256, 256, 256, 256);
    }

    @Override
    public void afterSwitch() {
        backToLobbyButton.visible = false;
        screen.renderingGameStates.get(ChessBoardBlockEntity.LOBBY_STATE).createWidgets(screen);
    }

    private void focusCamera(){
        if (this.shouldRender()){
            Camera camera = client.gameRenderer.getCamera();
            ((CameraAnimationPlayable)camera).focusOnBlockEntity(blockEntity, WHITE);
        }
    }
}
