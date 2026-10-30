package net.riptide.modules.render;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;

/** See in the dark. Gamma boosts the lightmap brightness, Night Vision gives a client-side effect. */
public class Fullbright extends Module {
    private static final float MAX_BOOST = 15.0F;
    private final ModeSetting mode = add(new ModeSetting("Mode", "Gamma", "Gamma", "Night Vision"));
    private final NumberSetting strength = add(new NumberSetting("Strength", 1.0, 0.0, 1.0, 0.1));
    private final BooleanSetting smooth = add(new BooleanSetting("Smooth", true));

    /** 0..1 how far the gamma boost is faded in. */
    private float fade = 0.0F;
    private boolean gaveEffect = false;

    public Fullbright() {
        super("Fullbright", "Makes everything bright", Category.RENDER);
    }

    @Override
    public String getSuffix() { return mode.get(); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (mode.is("Night Vision")) {
            // long enough that it never flickers, refreshed every tick while on
            mc().player.forceAddEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false), null);
            gaveEffect = true;
        } else {
            clearEffect();
        }
    }

    @Override
    public void onDisable() { clearEffect(); }

    private void clearEffect() {
        if (gaveEffect && inGame()) mc().player.removeEffect(MobEffects.NIGHT_VISION);
        gaveEffect = false;
    }

    /** Called from the lightmap every frame, enabled or not, so it can fade out smoothly. */
    public float brightness(float vanilla) {
        float target = isEnabled() && mode.is("Gamma") ? 1.0F : 0.0F;
        if (smooth.get()) fade += (target - fade) * 0.08F;
        else fade = target;
        if (fade < 0.001F) { fade = 0.0F; return vanilla; }
        float boosted = 1.0F + strength.getF() * (MAX_BOOST - 1.0F);
        return vanilla + (Math.max(vanilla, boosted) - vanilla) * fade;
    }
}
