package net.fieldb0y.wanna_play_chess.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NumericTextField extends TextFieldWidget {
    public NumericTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        this.setTextPredicate(s -> s.matches("\\d*"));
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return Character.isDigit(chr) && super.charTyped(chr, modifiers);
    }
}
