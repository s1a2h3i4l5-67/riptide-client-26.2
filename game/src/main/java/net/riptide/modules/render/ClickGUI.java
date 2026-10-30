package net.riptide.modules.render;

import com.mojang.blaze3d.platform.InputConstants;
import net.riptide.gui.ClickGuiScreen;
import net.riptide.module.Category;
import net.riptide.module.Module;

/** Opens the module menu. Default key: Right Shift. */
public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "Opens this menu", Category.RENDER, InputConstants.KEY_RSHIFT);
    }

    @Override
    public boolean isHidden() { return true; }

    @Override
    public void onEnable() {
        if (!(mc().gui.screen() instanceof ClickGuiScreen)) {
            mc().gui.setScreen(ClickGuiScreen.get());
        }
    }

    @Override
    public void onTick() {
        // something else replaced the menu (death screen, chat...) -> reset so the key works next press
        if (!(mc().gui.screen() instanceof ClickGuiScreen)) setEnabled(false);
    }

    @Override
    public void onDisable() {
        if (mc().gui.screen() instanceof ClickGuiScreen) mc().gui.setScreen(null);
    }
}
