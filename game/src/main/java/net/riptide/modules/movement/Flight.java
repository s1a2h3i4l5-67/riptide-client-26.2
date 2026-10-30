package net.riptide.modules.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;
import net.riptide.util.MoveUtil;

/** Lets you fly. Vanilla = creative-style flying, Motion = sets your velocity directly. */
public class Flight extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Motion", "Motion", "Vanilla"));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1));
    private final NumberSetting vSpeed = add(new NumberSetting("Vertical", 0.6, 0.1, 3.0, 0.1));
    private final BooleanSetting antiKick = add(new BooleanSetting("Anti-Kick", true));

    private int ticks = 0;

    public Flight() {
        super("Flight", "Lets you fly", Category.MOVEMENT);
    }

    @Override
    public String getSuffix() { return mode.get(); }

    @Override
    public void onEnable() { ticks = 0; }

    @Override
    public void onDisable() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        Abilities a = me.getAbilities();
        if (!me.isCreative() && !me.isSpectator()) {
            a.flying = false;
            a.mayfly = false;
        }
        a.setFlyingSpeed(0.05F);
        me.onUpdateAbilities();
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        ticks++;

        if (mode.is("Vanilla")) {
            Abilities a = me.getAbilities();
            a.mayfly = true;
            a.flying = true;
            a.setFlyingSpeed((float) (0.05 * speed.get()));
            if (antiKick.get() && ticks % 40 == 0) me.setDeltaMovement(me.getDeltaMovement().x, -0.04, me.getDeltaMovement().z);
            return;
        }

        // Motion
        double[] dir = MoveUtil.direction(me, speed.get());
        double y = 0.0;
        if (mc().options.keyJump.isDown()) y += vSpeed.get();
        if (mc().options.keyShift.isDown()) y -= vSpeed.get();
        if (antiKick.get() && ticks % 40 == 0 && y >= 0) y = -0.04;
        me.setDeltaMovement(dir[0], y, dir[1]);
        me.fallDistance = 0;
    }
}
