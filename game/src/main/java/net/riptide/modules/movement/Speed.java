package net.riptide.modules.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;
import net.riptide.util.MoveUtil;
import net.riptide.modules.movement.Flight;

/** Moves faster on the ground. Strafe = constant speed, Hop = auto-jump bhop. */
public class Speed extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Strafe", "Strafe", "Hop"));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.4, 0.2, 1.5, 0.05));
    private final BooleanSetting inWater = add(new BooleanSetting("In Water", false));

    public Speed() {
        super("Speed", "Move faster", Category.MOVEMENT);
    }

    @Override
    public String getSuffix() { return mode.get(); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        Flight flight = net.riptide.Riptide.get().getModuleManager().get(Flight.class);
        if (flight != null && flight.isEnabled()) return;
        if (me.getAbilities().flying || me.isFallFlying() || me.isPassenger()) return;
        if (!inWater.get() && (me.isInWater() || me.isInLava())) return;
        if (me.isShiftKeyDown() || !MoveUtil.isMoving(me)) return;

        Vec3 v = me.getDeltaMovement();
        double[] dir = MoveUtil.direction(me, speed.get());
        if (mode.is("Hop")) {
            if (me.onGround()) {
                me.jumpFromGround();
                v = me.getDeltaMovement();
            }
            me.setDeltaMovement(dir[0], v.y, dir[1]);
        } else if (me.onGround()) {
            me.setDeltaMovement(dir[0], v.y, dir[1]);
        }
    }
}
