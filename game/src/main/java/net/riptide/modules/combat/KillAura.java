package net.riptide.modules.combat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;

/** Attacks the best nearby target, timed to the weapon cooldown or a fixed CPS. */
public class KillAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 4.2, 2.0, 6.0, 0.1));
    private final ModeSetting priority = add(new ModeSetting("Priority", "Distance", "Distance", "Health", "Angle"));
    private final BooleanSetting players = add(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", true));
    private final BooleanSetting animals = add(new BooleanSetting("Animals", false));
    private final BooleanSetting cooldown = add(new BooleanSetting("Cooldown", true));
    private final NumberSetting cps = add(new NumberSetting("CPS", 10, 1, 20, 1));
    private final BooleanSetting rotate = add(new BooleanSetting("Rotate", true));
    private final BooleanSetting ignoreInvis = add(new BooleanSetting("Ignore Invisible", false));
    private final BooleanSetting pauseInGui = add(new BooleanSetting("Pause In GUI", true));

    private int ticksSinceAttack = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
    }

    @Override
    public String getSuffix() { return priority.get(); }

    @Override
    public void onEnable() { ticksSinceAttack = 0; }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        ticksSinceAttack++;
        if (!me.isAlive() || me.isSpectator()) return;
        if (pauseInGui.get() && mc().gui.screen() != null) return;

        LivingEntity target = findTarget(me);
        if (target == null) return;

        if (cooldown.get()) {
            if (me.getAttackStrengthScale(0.5F) < 0.95F) return;
        } else {
            int delay = Math.max(1, (int) Math.round(20.0 / cps.get()));
            if (ticksSinceAttack < delay) return;
        }

        if (rotate.get()) {
            float[] rot = rotationsTo(me, target);
            me.connection.send(new ServerboundMovePlayerPacket.Rot(rot[0], rot[1], me.onGround(), me.horizontalCollision));
        }
        mc().gameMode.attack(me, target);
        me.swing(InteractionHand.MAIN_HAND);
        ticksSinceAttack = 0;
    }

    private LivingEntity findTarget(LocalPlayer me) {
        double maxSq = range.get() * range.get();
        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity e : mc().level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity living) || e == me) continue;
            if (!living.isAlive() || living.isRemoved() || living.getHealth() <= 0) continue;
            if (e instanceof ArmorStand) continue;
            if (ignoreInvis.get() && e.isInvisible()) continue;
            if (!isWanted(e)) continue;
            double distSq = me.distanceToSqr(e);
            if (distSq > maxSq) continue;

            double score;
            if (priority.is("Health")) score = living.getHealth();
            else if (priority.is("Angle")) score = angleTo(me, e);
            else score = distSq;
            if (score < bestScore) { bestScore = score; best = living; }
        }
        return best;
    }

    private boolean isWanted(Entity e) {
        if (e instanceof Player p) return players.get() && !p.isSpectator() && !p.isCreative();
        if (e instanceof Enemy) return mobs.get();
        if (e instanceof Animal) return animals.get();
        return false;
    }

    static float[] rotationsTo(LocalPlayer me, Entity e) {
        Vec3 eye = me.getEyePosition();
        Vec3 aim = e.getBoundingBox().getCenter();
        double dx = aim.x - eye.x, dy = aim.y - eye.y, dz = aim.z - eye.z;
        double flat = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, flat));
        return new float[] { Mth.wrapDegrees(yaw), Mth.clamp(pitch, -90.0F, 90.0F) };
    }

    private static double angleTo(LocalPlayer me, Entity e) {
        float yaw = rotationsTo(me, e)[0];
        return Math.abs(Mth.wrapDegrees(yaw - me.getYRot()));
    }
}
