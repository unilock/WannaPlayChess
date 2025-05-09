package net.fieldb0y.wanna_play_chess.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class RenderingImage {
    public Identifier id;
    public int width, height;
    public int renderX, renderY;
    private boolean renderWithFrame;

    public RenderingImage(Identifier id, int width, int height, boolean frame) {
        this(id, 0, 0, width, height, frame);
    }

    public RenderingImage(Identifier id, int renderX, int renderY, int width, int height, boolean frame) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.renderX = renderX;
        this.renderY = renderY;
        this.renderWithFrame = frame;
    }

    public void render(DrawContext context, int mouseX, int mouseY){
        this.render(context, renderX, renderY, 0, 0, mouseX, mouseY);
    }

    public void render(DrawContext context, int x, int y, int u, int v, int mouseX, int mouseY){
        context.drawTexture(id, x, y, u, v, width, height, width, height);
        if (renderWithFrame){
/*            ClickableRect rect = new ClickableRect(x, y, width, height, Colors.GREEN);
            if (rect.isMouseInRect(mouseX, mouseY))
                rect.render(context);*/
        }
    }
}

