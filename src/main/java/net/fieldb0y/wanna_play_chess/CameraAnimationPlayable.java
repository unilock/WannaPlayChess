package net.fieldb0y.wanna_play_chess;

import net.fieldb0y.wanna_play_chess.block.entity.ChessBoardBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface CameraAnimationPlayable {
    void focusOnBlockEntity(ChessBoardBlockEntity blockEntity, PlayerEntity player);
    void focusOnBlockEntity(ChessBoardBlockEntity blockEntity, int playerRole);
    void stopFocusing();
    default boolean isCameraFocused(){
        return false;
    }
}
