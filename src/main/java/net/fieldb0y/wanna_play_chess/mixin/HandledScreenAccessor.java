package net.fieldb0y.wanna_play_chess.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x") int getX();
    @Accessor("y") int getY();
    @Accessor("backgroundWidth") int getBackgroundWidth();
    @Accessor("backgroundHeight") int getBackgroundHeight();
}
