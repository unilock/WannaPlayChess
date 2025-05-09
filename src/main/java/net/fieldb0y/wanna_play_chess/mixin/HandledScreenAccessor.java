package net.fieldb0y.wanna_play_chess.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x") int getX();
    @Accessor("y") int getY();
    @Accessor("backgroundWidth") int getBackgroundWidth();
    @Accessor("backgroundHeight") int getBackgroundHeight();
    @Accessor("handler") ScreenHandler getHandler();
}
