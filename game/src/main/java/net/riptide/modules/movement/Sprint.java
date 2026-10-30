package net.riptide.modules.movement;

import net.minecraft.client.player.LocalPlayer;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.util.MoveUtil;

/** Always sprint. Legit = only when walking forward, Omni = any direction. */
public class Sprint extends Module {
    private final BooleanSetting omni = add(new BooleanSetting("Omni", false));

    public Sprint() {
        super("Sprint", "Sprints for you", Category.MOVEMENT);
    }

    @Override
    public String getSuffix() { return omni.get() ? "Omni" : "Legit"; }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        if (me.isShiftKeyDown() || me.isUsingItem() || me.horizontalCollision) return;
        if (me.getFoodData().getFoodLevel() <= 6 && !me.getAbilities().mayfly) return;
        boolean moving = omni.get() ? MoveUtil.isMoving(me) : me.input.hasForwardImpulse();
        if (moving && !me.isSprinting()) me.setSprinting(true);
    }
}
