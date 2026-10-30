package net.riptide.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;

/** Movement helpers shared by Flight and Speed. */
public final class MoveUtil {
    private MoveUtil() {}

    public static boolean isMoving(LocalPlayer p) {
        Vec2 mv = p.input.getMoveVector();
        return Math.abs(mv.x) > 1.0E-5F || Math.abs(mv.y) > 1.0E-5F;
    }

    /**
     * Horizontal velocity {x, z} in the direction the player is pressing (WASD relative to yaw),
     * scaled to the given blocks/tick. Returns {0, 0} when no keys are held.
     */
    public static double[] direction(LocalPlayer p, double speed) {
        Vec2 mv = p.input.getMoveVector(); // x = left impulse, y = forward impulse
        double strafe = mv.x, forward = mv.y;
        double len = Math.sqrt(strafe * strafe + forward * forward);
        if (len < 1.0E-5) return new double[] { 0.0, 0.0 };
        strafe /= len;
        forward /= len;
        double yaw = Math.toRadians(p.getYRot());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        return new double[] {
                (strafe * cos - forward * sin) * speed,
                (forward * cos + strafe * sin) * speed };
    }
}
