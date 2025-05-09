package net.fieldb0y.wanna_play_chess.chess.utils;

import org.joml.Vector4i;

public enum PieceAction {
    NONE(new Vector4i(1, 1, 1, 1)),
    TAKE(new Vector4i(196, 55, 55, 200)),
    MOVE(new Vector4i(3, 251, 255, 200)),
    EN_PASSANT(new Vector4i(196, 55, 55, 200)),
    CASTLE(new Vector4i(3, 251, 255, 200)),
    OTHER(new Vector4i(1, 1, 1, 1));

    private Vector4i color;

    PieceAction(Vector4i color){
        this.color = color;
    }

    public Vector4i getColor() {
        return color;
    }
}
