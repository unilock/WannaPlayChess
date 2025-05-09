package net.fieldb0y.wanna_play_chess.screen;

import net.minecraft.client.gui.DrawContext;

public interface ClickableWidget {
    void render(DrawContext context);
    void onResize();
    boolean mouseClicked(double mouseX, double mouseY);
}
