package net.riptide.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.NumberSetting;

/** Scales knockback from hits and explosions. 0% = no knockback. */
public class Velocity extends Module {
    private final NumberSetting horizontal = add(new NumberSetting("Horizontal %", 0, 0, 100, 5));
    private final NumberSetting vertical = add(new NumberSetting("Vertical %", 0, 0, 100, 5));
    private final BooleanSetting explosions = add(new BooleanSetting("Explosions", true));

    public Velocity() {
        super("Velocity", "Reduces knockback", Category.COMBAT);
    }

    @Override
    public String getSuffix() { return horizontal.getI() + "% " + vertical.getI() + "%"; }

    /** Server-set motion packet: blend between the current motion and the new one. */
    public Vec3 modify(Vec3 incoming) {
        LocalPlayer me = Minecraft.getInstance().player;
        Vec3 cur = me == null ? Vec3.ZERO : me.getDeltaMovement();
        double h = horizontal.get() / 100.0, v = vertical.get() / 100.0;
        return new Vec3(
                cur.x + (incoming.x - cur.x) * h,
                cur.y + (incoming.y - cur.y) * v,
                cur.z + (incoming.z - cur.z) * h);
    }

    /** Explosion knockback is added on top of current motion, so just scale it. */
    public Vec3 modifyExplosion(Vec3 kb) {
        if (!explosions.get()) return kb;
        double h = horizontal.get() / 100.0, v = vertical.get() / 100.0;
        return new Vec3(kb.x * h, kb.y * v, kb.z * h);
    }
}
