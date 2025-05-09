package net.fieldb0y.wanna_play_chess.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
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

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.CHESS_BOARD, "Chess Board");
        translationBuilder.add(ModItems.BOX_FOR_CHESS_PIECES, "Box for Chess Pieces");
        translationBuilder.add(ModItems.BLACK_BONE, "Black Bone");

        translationBuilder.add(ModItems.WHITE_PAWN, "White Pawn");
        translationBuilder.add(ModItems.WHITE_KNIGHT, "White Knight");
        translationBuilder.add(ModItems.WHITE_BISHOP, "White Bishop");
        translationBuilder.add(ModItems.WHITE_ROOK, "White Rook");
        translationBuilder.add(ModItems.WHITE_QUEEN, "White Queen");
        translationBuilder.add(ModItems.WHITE_KING, "White King");

        translationBuilder.add(ModItems.BLACK_PAWN, "Black Pawn");
        translationBuilder.add(ModItems.BLACK_KNIGHT, "Black Knight");
        translationBuilder.add(ModItems.BLACK_BISHOP, "Black Bishop");
        translationBuilder.add(ModItems.BLACK_ROOK, "Black Rook");
        translationBuilder.add(ModItems.BLACK_QUEEN, "Black Queen");
        translationBuilder.add(ModItems.BLACK_KING, "Black King");

        translationBuilder.add(ModItemTagProvider.BLACK_BONE_COMPONENTS, "Black Bone Components");

        addText(translationBuilder, ModItemGroups.MOD_GROUP_TITLE, "Wanna Play Chess?");

        addText(translationBuilder, ChessLobbyRenderingState.JOIN_LOBBY_BUTTON_TEXT, "Join Lobby");
        addText(translationBuilder, ChessLobbyRenderingState.LEAVE_LOBBY_BUTTON_TEXT, "Leave Lobby");
        addText(translationBuilder, ChessLobbyRenderingState.START_GAME_BUTTON_TEXT, "Start Game");
        addText(translationBuilder, ChessLobbyRenderingState.NO_TIME_CONTROL_CHECKBOX_TEXT, "No Time Control");

        addText(translationBuilder, ChessLobbyRenderingState.GAME_TIME_TEXT, "Game Time");
        addText(translationBuilder, ChessLobbyRenderingState.SEC_TEXT, "Sec:");
        addText(translationBuilder, ChessLobbyRenderingState.MIN_TEXT, "Min:");
        addText(translationBuilder, ChessLobbyRenderingState.FIRST_PLAYER_ROLE_TEXT, "First Player Role");
        addText(translationBuilder, ChessLobbyRenderingState.MULTIPLAYER_TEXT, "MULTIPLAYER");
        addText(translationBuilder, ChessLobbyRenderingState.SINGLEPLAYER_TEXT, "SINGLEPLAYER");
        addText(translationBuilder, ChessLobbyRenderingState.START_WORD, "Start");
        addText(translationBuilder, ChessLobbyRenderingState.GAME_WORD, "game");
        addText(translationBuilder, ChessLobbyRenderingState.NEITHER_SET_INSERTED_TEXT, "§cPieces sets are not inserted!");
        addText(translationBuilder, ChessLobbyRenderingState.WHITE_SET_ISNT_INSERTED_TEXT, "§cSet of§r White Pieces §cis not inserted!§r");
        addText(translationBuilder, ChessLobbyRenderingState.BLACK_SET_ISNT_INSERTED_TEXT, "§cSet of§r §7Black Pieces§r §cis not inserted!§r");

        addText(translationBuilder, ChessGameRenderingState.DRAW_BUTTON_TEXT, "Draw?");
        addText(translationBuilder, ChessGameRenderingState.RESIGN_BUTTON_TEXT, "Resign");
        addText(translationBuilder, ChessGameRenderingState.DRAW_TEXT, "Accept draw?");
        addText(translationBuilder, ChessGameRenderingState.RESIGN_TEXT, "Resign?");

        addText(translationBuilder, ChessGameOverRenderingState.BACK_TO_LOBBY_BUTTON_TEXT, "Back to Lobby");
        addText(translationBuilder, ChessGameOverRenderingState.DRAW_TEXT, "It's a DRAW!");
        addText(translationBuilder, ChessGameOverRenderingState.WHITE_WORD, "WHITE");
        addText(translationBuilder, ChessGameOverRenderingState.BLACK_WORD, "BLACK");
        addText(translationBuilder, ChessGameOverRenderingState.WON_WORD, "Won");
        addText(translationBuilder, ChessGameOverRenderingState.GAME_OVER_REASON_TEXT, "Game Over Reason:");

        addText(translationBuilder, ChessGameOverReason.NONE.text, "NONE");
        addText(translationBuilder, ChessGameOverReason.CHECKMATE.text, "Checkmate");
        addText(translationBuilder, ChessGameOverReason.IMPOSSIBLE_TO_WIN.text, "Impossible to win");
        addText(translationBuilder, ChessGameOverReason.TIME_IS_UP.text, "Time is up");
        addText(translationBuilder, ChessGameOverReason.STALEMATE.text, "Stalemate");
        addText(translationBuilder, ChessGameOverReason.RESIGN.text, "Player resigned");
        addText(translationBuilder, ChessGameOverReason.AGREED_DRAW.text, "Agreed draw");

        addText(translationBuilder, BoxForPieces.BOX_IS_NOT_FULL_MESSAGE, "Cannot insert a partial set of chess pieces");
        addText(translationBuilder, BoxForPieces.SET_ALREADY_INSERTED_MESSAGE, "This set of chess pieces is already inserted");
        addText(translationBuilder, BoxForPieces.SUCCESSFUL_SET_INSERT, "The chess set has been successfully inserted");
        addText(translationBuilder, BoxForPieces.BOX_IS_EMPTY_TOOLTIP, "Box is Empty!");
        addText(translationBuilder, BoxForPieces.EMPTY_BOX_SHIFT_TOOLTIP, "Take the piece in your other hand and right-click to put it in the box");
        addText(translationBuilder, BoxForPieces.BOX_SHIFT_TOOLTIP, "To empty the box right-click while you sneaking");
        addText(translationBuilder, BoxForPieces.PRESS_SHIFT_TOOLTIP, "§7Press§r §e[shift]§r §7to get more info§r");
    }

    private static void addText(@NotNull TranslationBuilder builder, @NotNull Text text, @NotNull String value){
        if (text.getContent() instanceof  TranslatableTextContent content)
            builder.add(content.getKey(), value);

    }
}
