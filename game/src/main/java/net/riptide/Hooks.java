package net.riptide;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.riptide.modules.combat.Velocity;
import net.riptide.modules.render.ESP;
import net.riptide.modules.render.Fullbright;

/**
 * Every call from vanilla code into Riptide goes through here, so the vanilla
 * edits stay one-liners and a Riptide bug can never crash the game.
 */
public final class Hooks {
    private Hooks() {}

    private static Riptide rt() { return Riptide.get(); }

    /** Gui.extractRenderState, right after the vanilla HUD. */
    public static void onRender2D(GuiGraphicsExtractor g) {
        try { rt().getModuleManager().onRender2D(g); } catch (Throwable t) { t.printStackTrace(); }
    }

    /** KeyboardHandler.keyPress, key down while in-game (no screen open). Returns true to eat the key. */
    public static boolean onKeyPress(int key) {
        try { return rt().onKeyPress(key); } catch (Throwable t) { t.printStackTrace(); return false; }
    }

    /** Minecraft.shouldEntityAppearGlowing */
    public static boolean shouldGlow(Entity e) {
        try {
            ESP esp = rt().getModuleManager().get(ESP.class);
            return esp != null && esp.isEnabled() && esp.shouldHighlight(e);
        } catch (Throwable t) { return false; }
    }

    /** EntityRenderer outline color. */
    public static int outlineColor(Entity e, int vanilla) {
        try {
            ESP esp = rt().getModuleManager().get(ESP.class);
            if (esp != null && esp.isEnabled() && esp.shouldHighlight(e) && !e.isCurrentlyGlowing()) return esp.colorFor(e);
        } catch (Throwable t) { /* fall through */ }
        return vanilla;
    }

    /** LightmapRenderStateExtractor brightness option. */
    public static float brightness(float vanilla) {
        try {
            Fullbright fb = rt().getModuleManager().get(Fullbright.class);
            return fb == null ? vanilla : fb.brightness(vanilla);
        } catch (Throwable t) { return vanilla; }
    }

    /** ClientPacketListener.handleSetEntityMotion */
    public static Vec3 entityMotion(Entity e, Vec3 motion) {
        try {
            if (e != Minecraft.getInstance().player) return motion;
            Velocity v = rt().getModuleManager().get(Velocity.class);
            return v != null && v.isEnabled() ? v.modify(motion) : motion;
        } catch (Throwable t) { return motion; }
    }

    /** ClientPacketListener.handleExplosion player knockback. */
    public static Vec3 explosionKnockback(Vec3 kb) {
        try {
            Velocity v = rt().getModuleManager().get(Velocity.class);
            return v != null && v.isEnabled() ? v.modifyExplosion(kb) : kb;
        } catch (Throwable t) { return kb; }
    }
}
