package net.fieldb0y.wanna_play_chess.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameOverRenderingState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessGameRenderingState;
import net.fieldb0y.wanna_play_chess.chess.renderingStates.ChessLobbyRenderingState;
import net.fieldb0y.wanna_play_chess.chess.utils.ChessGameOverReason;
import net.fieldb0y.wanna_play_chess.datagen.ModItemTagProvider;
import net.fieldb0y.wanna_play_chess.item.ModItemGroups;
import net.fieldb0y.wanna_play_chess.item.ModItems;
import net.fieldb0y.wanna_play_chess.item.custom.BoxForPieces;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRussianLangProvider extends FabricLanguageProvider {
    public ModRussianLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "ru_ru", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.CHESS_BOARD, "Шахматная доска");
        translationBuilder.add(ModItems.BOX_FOR_CHESS_PIECES, "Коробка для фигур");
        translationBuilder.add(ModItems.BLACK_BONE, "Чёрная кость");

        translationBuilder.add(ModItems.WHITE_PAWN, "Белая пешка");
        translationBuilder.add(ModItems.WHITE_KNIGHT, "Белый конь");
        translationBuilder.add(ModItems.WHITE_BISHOP, "Белый слон");
        translationBuilder.add(ModItems.WHITE_ROOK, "Белая ладья");
        translationBuilder.add(ModItems.WHITE_QUEEN, "Белый ферзь");
        translationBuilder.add(ModItems.WHITE_KING, "Белый король");

        translationBuilder.add(ModItems.BLACK_PAWN, "Чёрная пешка");
        translationBuilder.add(ModItems.BLACK_KNIGHT, "Чёрный конь");
        translationBuilder.add(ModItems.BLACK_BISHOP, "Чёрный слон");
        translationBuilder.add(ModItems.BLACK_ROOK, "Чёрная ладья");
        translationBuilder.add(ModItems.BLACK_QUEEN, "Чёрный ферзь");
        translationBuilder.add(ModItems.BLACK_KING, "Чёрный король");

        translationBuilder.add(ModItemTagProvider.BLACK_BONE_COMPONENTS, "Black Bone Components");

        addText(translationBuilder, ModItemGroups.MOD_GROUP_TITLE, "Wanna Play Chess?");

        addText(translationBuilder, ChessLobbyRenderingState.JOIN_LOBBY_BUTTON_TEXT, "Присоединиться");
        addText(translationBuilder, ChessLobbyRenderingState.LEAVE_LOBBY_BUTTON_TEXT, "Выйти");
        addText(translationBuilder, ChessLobbyRenderingState.START_GAME_BUTTON_TEXT, "Начать игру");
        addText(translationBuilder, ChessLobbyRenderingState.NO_TIME_CONTROL_CHECKBOX_TEXT, "Без контроля времени");

        addText(translationBuilder, ChessLobbyRenderingState.GAME_TIME_TEXT, "Контроль времени");
        addText(translationBuilder, ChessLobbyRenderingState.SEC_TEXT, "Сек:");
        addText(translationBuilder, ChessLobbyRenderingState.MIN_TEXT, "Мин:");
        addText(translationBuilder, ChessLobbyRenderingState.FIRST_PLAYER_ROLE_TEXT, "Роль первого игрока");
        addText(translationBuilder, ChessLobbyRenderingState.MULTIPLAYER_TEXT, "МНОГОПОЛЬЗОВАТЕЛЬСКУЮ");
        addText(translationBuilder, ChessLobbyRenderingState.SINGLEPLAYER_TEXT, "ОДИНОЧНУЮ");
        addText(translationBuilder, ChessLobbyRenderingState.START_WORD, "Начать");
        addText(translationBuilder, ChessLobbyRenderingState.GAME_WORD, "игру");
        addText(translationBuilder, ChessLobbyRenderingState.NEITHER_SET_INSERTED_TEXT, "§cСет фигур не вставлен!");
        addText(translationBuilder, ChessLobbyRenderingState.WHITE_SET_ISNT_INSERTED_TEXT, "§cСет§r белых фигур §cне вставлен!§r");
        addText(translationBuilder, ChessLobbyRenderingState.BLACK_SET_ISNT_INSERTED_TEXT, "§cСет§r §7чёрных фигур§r §cне вставлен!§r");

        addText(translationBuilder, ChessGameRenderingState.DRAW_BUTTON_TEXT, "Ничья?");
        addText(translationBuilder, ChessGameRenderingState.RESIGN_BUTTON_TEXT, "Сдаться");
        addText(translationBuilder, ChessGameRenderingState.DRAW_TEXT, "Согласиться на ничью?");
        addText(translationBuilder, ChessGameRenderingState.RESIGN_TEXT, "Сдаться?");

        addText(translationBuilder, ChessGameOverRenderingState.BACK_TO_LOBBY_BUTTON_TEXT, "Назад в лобби");
        addText(translationBuilder, ChessGameOverRenderingState.DRAW_TEXT, "Это НИЧЬЯ!");
        addText(translationBuilder, ChessGameOverRenderingState.WHITE_WORD, "БЕЛЫЕ");
        addText(translationBuilder, ChessGameOverRenderingState.BLACK_WORD, "ЧЁРНЫЕ");
        addText(translationBuilder, ChessGameOverRenderingState.WON_WORD, "ПОБЕДИЛИ");
        addText(translationBuilder, ChessGameOverRenderingState.GAME_OVER_REASON_TEXT, "Причина:");

        addText(translationBuilder, ChessGameOverReason.NONE.text, "NONE");
        addText(translationBuilder, ChessGameOverReason.CHECKMATE.text, "Мат");
        addText(translationBuilder, ChessGameOverReason.IMPOSSIBLE_TO_WIN.text, "Теоритическая ничья");
        addText(translationBuilder, ChessGameOverReason.TIME_IS_UP.text, "Время вышло");
        addText(translationBuilder, ChessGameOverReason.STALEMATE.text, "Пат");
        addText(translationBuilder, ChessGameOverReason.RESIGN.text, "Игрок сдался");
        addText(translationBuilder, ChessGameOverReason.AGREED_DRAW.text, "Согласованная ничья");

        addText(translationBuilder, BoxForPieces.BOX_IS_NOT_FULL_MESSAGE, "Невозможно вставить частичный набор шахматных фигур");
        addText(translationBuilder, BoxForPieces.SET_ALREADY_INSERTED_MESSAGE, "Этот набор шахматных фигур уже вставлен");
        addText(translationBuilder, BoxForPieces.SUCCESSFUL_SET_INSERT, "Шахматный набор успешно вставлен");
        addText(translationBuilder, BoxForPieces.BOX_IS_EMPTY_TOOLTIP, "Коробка пуста!");
        addText(translationBuilder, BoxForPieces.EMPTY_BOX_SHIFT_TOOLTIP, "Возьми фигуру в другую руку и щелкни правой кнопкой мыши, чтобы положить ее в коробку");
        addText(translationBuilder, BoxForPieces.BOX_SHIFT_TOOLTIP, "Чтобы опустошить коробку  щёлкни правой кнопкой мыши по крадёшся");
        addText(translationBuilder, BoxForPieces.PRESS_SHIFT_TOOLTIP, "§7Нажми§r §e[shift]§r §7чтобы получить больше информации§r");

        addText(translationBuilder, WannaPlayChess.CANT_TAKE_OUT_SETS_MESSAGE, "Ты не можешь вытащить наборы фигур во время игры!");
    }

    private static void addText(@NotNull TranslationBuilder builder, @NotNull Text text, @NotNull String value){
        if (text.getContent() instanceof  TranslatableTextContent content)
            builder.add(content.getKey(), value);

    }
}
