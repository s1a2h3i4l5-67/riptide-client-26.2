package net.riptide.modules.render;

import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;

/**
 * First test module: proves the framework (settings, lifecycle, dispatch) compiles
 * and registers. Lighting effect is wired in a later pass once the render hooks land.
 */
public class Fullbright extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Gamma", "Gamma", "Night Vision"));
    private final NumberSetting strength = add(new NumberSetting("Strength", 1.0, 0.0, 1.0, 0.1));
    private final BooleanSetting smooth = add(new BooleanSetting("Smooth", true));

    public Fullbright() {
        super("Fullbright", "Makes everything bright", Category.RENDER);
    }

    @Override
    public void onEnable() {
        System.out.println("[Riptide] Fullbright on (mode=" + mode.get()
                + ", strength=" + strength.get() + ", smooth=" + smooth.get() + ")");
    }

    @Override
    public void onDisable() {
        System.out.println("[Riptide] Fullbright off");
    }
}
