package net.riptide;

import net.riptide.module.Module;
import net.riptide.module.ModuleManager;
import net.riptide.modules.combat.AutoTotem;
import net.riptide.modules.combat.KillAura;
import net.riptide.modules.combat.Velocity;
import net.riptide.modules.movement.Flight;
import net.riptide.modules.movement.NoFall;
import net.riptide.modules.movement.Speed;
import net.riptide.modules.movement.Sprint;
import net.riptide.modules.movement.Step;
import net.riptide.modules.player.FastPlace;
import net.riptide.modules.render.ClickGUI;
import net.riptide.modules.render.ESP;
import net.riptide.modules.render.Fullbright;
import net.riptide.modules.render.HUD;

/** Riptide client core. Single entry point, held as a lazy singleton. */
public class Riptide {
    public static final String NAME = "Riptide";
    public static final String VERSION = "1.1";

    /** Accent color used across the ClickGUI and HUD (ARGB). */
    public static final int ACCENT = 0xFF2EC4F1;

    private static Riptide INSTANCE;
    private final ModuleManager moduleManager = new ModuleManager();
    private boolean initialized = false;

    public static Riptide get() {
        if (INSTANCE == null) INSTANCE = new Riptide();
        return INSTANCE;
    }

    public ModuleManager getModuleManager() { return moduleManager; }

    public void init() {
        if (initialized) return;
        initialized = true;
        registerModules();
        System.out.println("[" + NAME + "] v" + VERSION + " initialized with "
                + moduleManager.getModules().size() + " module(s)");
    }

    private void registerModules() {
        // combat
        moduleManager.register(new KillAura());
        moduleManager.register(new AutoTotem());
        moduleManager.register(new Velocity());
        // movement
        moduleManager.register(new Sprint());
        moduleManager.register(new Flight());
        moduleManager.register(new Speed());
        moduleManager.register(new NoFall());
        moduleManager.register(new Step());
        // player
        moduleManager.register(new FastPlace());
        // render
        moduleManager.register(new Fullbright());
        moduleManager.register(new ESP());
        moduleManager.register(new HUD());
        moduleManager.register(new ClickGUI());

        // HUD on by default so people can see what's active
        Module hud = moduleManager.get(HUD.class);
        if (hud != null) hud.setEnabled(true);
    }

    /** Called on key down while no screen is open. */
    public boolean onKeyPress(int key) {
        if (!initialized || key < 0) return false;
        return moduleManager.onKey(key);
    }
}
