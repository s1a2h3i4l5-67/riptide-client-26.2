package net.riptide;

import net.riptide.module.ModuleManager;
import net.riptide.modules.render.Fullbright;

/** Riptide client core. Single entry point, held as a lazy singleton. */
public class Riptide {
    public static final String NAME = "Riptide";
    public static final String VERSION = "1.0";

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
        moduleManager.register(new Fullbright());
        // more modules land in the next pass
    }
}
