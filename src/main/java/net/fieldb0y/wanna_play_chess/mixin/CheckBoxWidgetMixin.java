package net.fieldb0y.wanna_play_chess.mixin;

import net.fieldb0y.wanna_play_chess.CheckBoxAccessor;
import net.minecraft.client.gui.widget.CheckboxWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CheckboxWidget.class)
public class CheckBoxWidgetMixin implements CheckBoxAccessor {
    @Shadow private boolean checked;

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
