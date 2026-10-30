package net.riptide.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<Module>();

    public void register(Module m) { modules.add(m); }
    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category c) {
        List<Module> l = new ArrayList<Module>();
        for (Module m : modules) if (m.getCategory() == c) l.add(m);
        return l;
    }
    public Module get(String name) {
        for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }
    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (m.getClass() == type) return (T) m;
        return null;
    }
    public List<Module> getEnabled() {
        List<Module> l = new ArrayList<Module>();
        for (Module m : modules) if (m.isEnabled()) l.add(m);
        return l;
    }

    // dispatch from game hooks (guarded so a buggy module never crashes the game)
    public void onTick() {
        for (Module m : modules) if (m.isEnabled()) {
            try { m.onTick(); } catch (Throwable t) { t.printStackTrace(); }
        }
    }
    public void onRender3D(float partial) {
        for (Module m : modules) if (m.isEnabled()) {
            try { m.onRender3D(partial); } catch (Throwable t) { t.printStackTrace(); }
        }
    }
    public void onRender2D(GuiGraphicsExtractor g) {
        for (Module m : modules) if (m.isEnabled()) {
            try { m.onRender2D(g); } catch (Throwable t) { t.printStackTrace(); }
        }
    }
    /** Toggles every module bound to this key. Returns true if any was bound. */
    public boolean onKey(int key) {
        if (key == -1) return false;
        boolean hit = false;
        for (Module m : modules) if (m.getKeyCode() == key) {
            m.toggle();
            hit = true;
        }
        return hit;
    }
}
