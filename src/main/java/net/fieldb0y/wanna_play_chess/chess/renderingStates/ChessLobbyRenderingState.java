package net.fieldb0y.wanna_play_chess.chess.renderingStates;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessLobbyState;
import net.fieldb0y.wanna_play_chess.chess.gameStates.ChessState;
import net.fieldb0y.wanna_play_chess.entity.ModEntities;
import net.fieldb0y.wanna_play_chess.entity.custom.PlayerCopyEntity;
import net.fieldb0y.wanna_play_chess.CheckBoxAccessor;
import net.fieldb0y.wanna_play_chess.mixin.HandledScreenAccessor;
import net.fieldb0y.wanna_play_chess.mixin.ScreenAccessor;
import net.fieldb0y.wanna_play_chess.network.c2sPayloads.*;
import net.fieldb0y.wanna_play_chess.screen.ChessBoardScreen;
import net.fieldb0y.wanna_play_chess.screen.ClickableChooser;
import net.fieldb0y.wanna_play_chess.screen.NumericTextField;
import net.fieldb0y.wanna_play_chess.utils.GameState;
import net.fieldb0y.wanna_play_chess.utils.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Vector4i;

import static net.fieldb0y.wanna_play_chess.chess.gameStates.ChessGameState.*;

public class ChessLobbyRenderingState extends ChessRenderingState {
    public static final Text JOIN_LOBBY_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.join_lobby_button");
    public static final Text LEAVE_LOBBY_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.leave_lobby_button");
    public static final Text START_GAME_BUTTON_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.start_game_button");
    public static final Text NO_TIME_CONTROL_CHECKBOX_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.no_time_control_checkbox");
    public static final Text GAME_TIME_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.game_time_text");
    public static final Text SEC_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.sec_text");
    public static final Text MIN_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.min_text");
    public static final Text FIRST_PLAYER_ROLE_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.first_player_role_text");
    public static final Text SINGLEPLAYER_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.singleplayer_text").formatted(Formatting.GOLD, Formatting.BOLD, Formatting.ITALIC);
    public static final Text MULTIPLAYER_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.multiplayer_text").formatted(Formatting.GOLD, Formatting.BOLD, Formatting.ITALIC);
    public static final Text START_WORD = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.start_word");
    public static final Text GAME_WORD = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.game_word");
    public static final Text WHITE_SET_ISNT_INSERTED_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.white_set_isnt_inserted_text");
    public static final Text BLACK_SET_ISNT_INSERTED_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.black_set_isnt_inserted_text");
    public static final Text NEITHER_SET_INSERTED_TEXT = Text.translatable(WannaPlayChess.MOD_ID + ".lobby_rs.neither_set_inserted_text");

    public static final int FIRST = 0;
    public static final int SECOND = 1;

    private static final Identifier CHESS_LOBBY = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/chess_lobby.png");
    private static final Identifier WHITE_BLACK_KING_ICON = Identifier.of(WannaPlayChess.MOD_ID, "textures/gui/role_chooser.png");

    private PlayerCopyEntity[] playerCopies = new PlayerCopyEntity[2];
    public int firstPlayerRole;

    private ButtonWidget joinLobbyButton;
    private ButtonWidget leaveLobbyButton;
    private ButtonWidget startGameButton;
    private TextFieldWidget timeInMinsTextField;
    private TextFieldWidget timeInSecTextField;
    private CheckboxWidget noTimeControlCheckbox;
    private ClickableChooser roleClickableChooser;

    private int gameTimeInSec = 0;

    public ChessLobbyRenderingState(ChessState serverState, ChessBoardScreen screen) {
        super(serverState, screen, new RenderingProperties(true, false));

        playerCopies[FIRST] = ModEntities.PLAYER_COPY.create(blockEntity.getWorld());
        playerCopies[SECOND] = ModEntities.PLAYER_COPY.create(blockEntity.getWorld());

        playerCopies[FIRST].idleAnimationState.start(playerCopies[FIRST].age);
        playerCopies[SECOND].idleAnimationState.start(playerCopies[SECOND].age);
    }

    @Override
    public void createWidgets(ChessBoardScreen screen){
        HandledScreenAccessor accessor = (HandledScreenAccessor)screen;
        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        joinLobbyButton = ButtonWidget.builder(JOIN_LOBBY_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new JoinLobbyButtonPayload(blockEntity.getPos()));
        }).dimensions(accessor.getX() - 40, accessor.getY() + 160, 70, 25).build();

        leaveLobbyButton = ButtonWidget.builder(LEAVE_LOBBY_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new LeaveLobbyPayload(blockEntity.getPos()));
        }).dimensions(accessor.getX() + 40, accessor.getY() + 160, 70, 25).build();

        startGameButton = ButtonWidget.builder(START_GAME_BUTTON_TEXT, button->{
            ClientPlayNetworking.send(new StartGameButtonPayload(blockEntity.getPos()));
        }).dimensions(accessor.getX() + 130, accessor.getY() + 160, 90, 25).build();

        timeInMinsTextField = new NumericTextField(((ScreenAccessor)screen).getTextRenderer(), centerX - 30, centerY - 50, 30, 13, Text.literal(""));
        timeInSecTextField = new NumericTextField(((ScreenAccessor)screen).getTextRenderer(), centerX + 30, centerY - 50, 30, 13, Text.literal(""));
        timeInMinsTextField.setMaxLength(3);
        setGameTimeTextFields(((ChessLobbyState)serverState).gameTimeInSec);

        noTimeControlCheckbox = CheckboxWidget.builder(NO_TIME_CONTROL_CHECKBOX_TEXT, ((ScreenAccessor)screen).getTextRenderer()).pos(centerX - 50, centerY - 32)
                .checked(((ChessLobbyState)serverState).noTimeControl).callback((checkbox, checked) -> ClientPlayNetworking.send(new LobbyNoTimeControlCheckboxPayload(blockEntity.getPos(), checked))).build();

        roleClickableChooser = new ClickableChooser(162, 128, 105, 34, 3, this);
        roleClickableChooser.onScreenResize(true);
        this.firstPlayerRole = ((ChessLobbyState)serverState).firstPlayerRole;

        ((ScreenAccessor)screen).addWidget(joinLobbyButton);
        ((ScreenAccessor)screen).addWidget(leaveLobbyButton);
        ((ScreenAccessor)screen).addWidget(startGameButton);
        ((ScreenAccessor)screen).addWidget(timeInMinsTextField);
        ((ScreenAccessor)screen).addWidget(timeInSecTextField);
        ((ScreenAccessor)screen).addWidget(noTimeControlCheckbox);
    }

    @Override
    public void onScreenResize(int width, int height) {
        roleClickableChooser.onScreenResize(true);
    }

    @Override
    public void afterSwitch() {
        joinLobbyButton.visible = false;
        leaveLobbyButton.visible = false;
        startGameButton.visible = false;
        timeInSecTextField.visible = false;
        timeInMinsTextField.visible = false;
        noTimeControlCheckbox.visible = false;

        this.screen.renderingGameStates.get(ChessBoardBlockEntity.GAME_STATE).createWidgets(screen);
        ((ChessGameRenderingState)this.screen.renderingGameStates.get(ChessBoardBlockEntity.GAME_STATE)).init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, ChessBoardScreen screen) {
        roleClickableChooser.mouseClicked(mouseX, mouseY, this::chooseFirstPlayerRole);

        return super.mouseClicked(mouseX, mouseY, button, screen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ChessLobbyState chessLobbyState = (ChessLobbyState)serverState;
        ScreenAccessor accessor = (ScreenAccessor)screen;
        TextRenderer textRenderer = accessor.getTextRenderer();

        manageTimeTextFields();
        ((CheckBoxAccessor)noTimeControlCheckbox).setChecked(chessLobbyState.noTimeControl);

        if(chessLobbyState.isPlayerInLobby(client.player.getUuid())) {
            if ((!blockEntity.blackSetInserted || !blockEntity.whiteSetInsereted) && !client.player.isCreative()) {
                if (!blockEntity.blackSetInserted && !blockEntity.whiteSetInsereted)
                    startGameButton.setTooltip(Tooltip.of(NEITHER_SET_INSERTED_TEXT));
                else if (!blockEntity.whiteSetInsereted)
                    startGameButton.setTooltip(Tooltip.of(WHITE_SET_ISNT_INSERTED_TEXT));
                else startGameButton.setTooltip(Tooltip.of(BLACK_SET_ISNT_INSERTED_TEXT));
                this.startGameButton.active = false;
            } else {
                if (blockEntity.getGameState().equals(GameState.NOT_READY_TO_PLAY)) {
                    this.startGameButton.active = false;
                    if (blockEntity.blackSetInserted && blockEntity.whiteSetInsereted)
                        startGameButton.setTooltip(Tooltip.of(Text.empty()));
                } else {
                    startGameButton.setTooltip(Tooltip.of(START_WORD.copy().formatted(Formatting.ITALIC).append(" ")
                            .append(blockEntity.getGameState().equals(GameState.READY_FOR_SINGLEPLAYER_GAME) ? SINGLEPLAYER_TEXT : MULTIPLAYER_TEXT).append(" ").append(GAME_WORD)));
                    this.startGameButton.active = true;
                }
            }
        } else this.startGameButton.active = false;

        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        renderPlayers(context, mouseX, mouseY);
        renderRoles(context, mouseX, mouseY);
        renderPlayerNames(context, textRenderer, chessLobbyState.getPlayerInLobby(FIRST), chessLobbyState.getPlayerInLobby(SECOND));

        context.drawText(textRenderer, GAME_TIME_TEXT, centerX - textRenderer.getWidth(GAME_TIME_TEXT)/2, centerY - 65, Colors.ALTERNATE_WHITE, true);
        context.drawText(textRenderer, MIN_TEXT, centerX - 50, centerY - 47, Colors.LIGHT_RED, false);
        context.drawText(textRenderer, SEC_TEXT, centerX + 5, centerY - 47, Colors.LIGHT_RED, false);

        context.drawText(textRenderer, FIRST_PLAYER_ROLE_TEXT, centerX - textRenderer.getWidth(FIRST_PLAYER_ROLE_TEXT)/2 + 2, centerY - 5, Colors.ALTERNATE_WHITE, true);
    }

    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x =  getScreenWidth() / 2 - 256 / 2 - 11;
        int y = getScreenHeight() / 2 - 256 / 2 + 15;
        int width = Math.round(256 * 1.3f);
        int height = Math.round(256 * 1.3f);
        context.drawTexture(CHESS_LOBBY, x, y, 0, 0, width, height, width, height);
    }

    public void manageTimeTextFields(){
        int mins = 0, secs = 0;

        try {
            mins = timeInMinsTextField.getText().isEmpty() ? 0 : Integer.parseInt(timeInMinsTextField.getText());
        } catch (NumberFormatException ignored) {}
        try {
            secs = timeInSecTextField.getText().isEmpty() ? 0 : Integer.parseInt(timeInSecTextField.getText());
        } catch (NumberFormatException ignored) {}
        int time = mins * 60 + secs;

        if (time != gameTimeInSec){
            ClientPlayNetworking.send(new LobbyGameTimePayload(blockEntity.getPos(), time));
            this.gameTimeInSec = time;
        }
    }

    public void setGameTimeTextFields(int timeInSec){
        timeInMinsTextField.setText(String.valueOf((int)Math.floor((double) timeInSec / 60)));
        timeInSecTextField.setText(String.valueOf(timeInSec % 60));
    }

    private void renderPlayers(DrawContext context, int mouseX, int mouseY){
        HandledScreenAccessor accessor = (HandledScreenAccessor)screen;
        float fixedMouseY = (float) (screen.height - accessor.getBackgroundHeight()) / 2 + Math.clamp(mouseY / 2f, 55, 105) + 10;
        PlayerEntity player1 = ((ChessLobbyState)getServerState()).getPlayerInLobby(FIRST);
        PlayerEntity player2 = ((ChessLobbyState)getServerState()).getPlayerInLobby(SECOND);

        playerCopies[FIRST].setSkin(player1);
        playerCopies[SECOND].setSkin(player2);

        InventoryScreen.drawEntity(context, accessor.getX() - 53, accessor.getY() - 30, accessor.getX() + 25, accessor.getY() + 150, 45, 0.0625F, mouseX, fixedMouseY, playerCopies[FIRST]);
        InventoryScreen.drawEntity(context, accessor.getX() + 92, accessor.getY() - 30, accessor.getX() + 295, accessor.getY() + 150, 45, 0.0625F, mouseX, fixedMouseY, playerCopies[SECOND]);
    }

    private void renderPlayerNames(DrawContext context, TextRenderer textRenderer, PlayerEntity firstPlayer, PlayerEntity secondPlayer){
        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;

        if (firstPlayer != null){
            Text text = firstPlayer.getDisplayName();
            int textWidth = textRenderer.getWidth(text);
            int textFrameCenterX = centerX - 87;
            context.drawText(textRenderer, text, textFrameCenterX - textWidth / 2, centerY + 53, Colors.GREEN, false);
        }
        if(secondPlayer != null){
            Text text = secondPlayer.getDisplayName();
            int textWidth = textRenderer.getWidth(text);
            int textFrameCenterX = centerX + 90;
            context.drawText(textRenderer, text, textFrameCenterX - textWidth / 2, centerY + 53, Colors.GREEN, false);
        }
    }

    private void renderRoles(DrawContext context, int mouseX, int mouseY){
        int centerX = getScreenWidth()/2;
        int centerY = getScreenHeight()/2;
        this.firstPlayerRole = ((ChessLobbyState)serverState).firstPlayerRole;

        context.drawTexture(WHITE_BLACK_KING_ICON, centerX - 408/8 + 2, centerY + 10, 0, 0, 408/4, 127/4, 408/4, 127/4);

        switch (firstPlayerRole){
            case WHITE: roleClickableChooser.renderRect(context, 0, Colors.ALTERNATE_WHITE); break;
            case BLACK: roleClickableChooser.renderRect(context, 2, Colors.ALTERNATE_WHITE); break;
            case 2: roleClickableChooser.renderRect(context, 1, Colors.ALTERNATE_WHITE); break;
        }

        int intersectedRect = roleClickableChooser.getMouseIntersectedRect(mouseX, mouseY);
        switch (intersectedRect){
            case 0: Utils.advancedFill(context, centerX - 50, centerY + 9, centerX - 16, centerY + 42, new Vector4i(1, 1, 1, 80)); break;
            case 1: Utils.advancedFill(context, centerX - 15, centerY + 9, centerX + 19, centerY + 42, new Vector4i(1, 1, 1, 80)); break;
            case 2: Utils.advancedFill(context, centerX + 20, centerY + 9, centerX + 55, centerY + 42, new Vector4i(1, 1, 1, 80)); break;
        }
    }

    private void chooseFirstPlayerRole(int clickedRectId){
        switch (clickedRectId){
            case 0: this.firstPlayerRole = WHITE; break;
            case 1: this.firstPlayerRole = 2; break;
            case 2: this.firstPlayerRole = BLACK; break;
        }
        ClientPlayNetworking.send(new LobbyFirstPlayerRolePayload(blockEntity.getPos(), firstPlayerRole));
    }
}
