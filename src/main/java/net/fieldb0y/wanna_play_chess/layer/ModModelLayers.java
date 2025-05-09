package net.fieldb0y.wanna_play_chess.layer;

import net.fieldb0y.wanna_play_chess.WannaPlayChess;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer PLAYER_COPY = new EntityModelLayer(Identifier.of(WannaPlayChess.MOD_ID, "main"), "player_copy");
    public static final EntityModelLayer CHESS_BOARD = new EntityModelLayer(Identifier.of(WannaPlayChess.MOD_ID, "main"), "chess_board");
}
