package net.fieldb0y.wanna_play_chess.chess.utils;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.minecraft.text.Text;

import java.util.Arrays;

public enum ChessGameOverReason {
    NONE(0, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.none")),
    CHECKMATE(1, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.checkmate")),
    STALEMATE(2, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.stalemate")),
    IMPOSSIBLE_TO_WIN(3, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.impossible_to_win")),
    TIME_IS_UP(4, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.time_is_up")),
    RESIGN(5, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.resign")),
    AGREED_DRAW(6, Text.translatable(WannaPlayChess.MOD_ID + ".game_over_reason.agreed_draw"));

    public int nbtValue;
    public Text text;

    ChessGameOverReason(int nbtValue, Text text){
        this.nbtValue = nbtValue;
        this.text = text;
    }

    public static ChessGameOverReason getWithNbtValue(int nbtValue){
        return Arrays.stream(ChessGameOverReason.values()).filter((value)->value.nbtValue == nbtValue).toList().getFirst();
    }
}
