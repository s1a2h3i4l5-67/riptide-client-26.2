package net.riptide.modules.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.NumberSetting;

/** Walk straight up blocks instead of jumping. */
public class Step extends Module {
    private static final double VANILLA = 0.6;
    private final NumberSetting height = add(new NumberSetting("Height", 1.0, 0.6, 2.5, 0.5));

    public Step() {
        super("Step", "Step up full blocks", Category.MOVEMENT);
    }

    @Override
    public String getSuffix() { return String.valueOf(height.get()); }

    @Override
    public void onTick() {
        AttributeInstance a = attr();
        if (a != null && a.getBaseValue() != height.get()) a.setBaseValue(height.get());
    }

    @Override
    public void onDisable() {
        AttributeInstance a = attr();
        if (a != null) a.setBaseValue(VANILLA);
    }

    private AttributeInstance attr() {
        if (!inGame()) return null;
        LocalPlayer me = mc().player;
        return me.getAttribute(Attributes.STEP_HEIGHT);
    }
}
