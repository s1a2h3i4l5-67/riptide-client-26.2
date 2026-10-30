package net.riptide.modules.player;

import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.NumberSetting;

/** Removes the 4-tick delay between right clicks (placing blocks, crystals, etc). */
public class FastPlace extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay", 0, 0, 4, 1));

    public FastPlace() {
        super("FastPlace", "Place blocks faster", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (mc().rightClickDelay > delay.getI()) mc().rightClickDelay = delay.getI();
    }
}
