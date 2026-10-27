package net.riptide.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.riptide.module.setting.Setting;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private int keyCode;          // GLFW key code, -1 = unbound
    private boolean enabled;
    protected final List<Setting<?>> settings = new ArrayList<Setting<?>>();

    public Module(String name, String description, Category category) {
        this(name, description, category, -1);
    }
    public Module(String name, String description, Category category, int keyCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyCode = keyCode;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int k) { this.keyCode = k; }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }

    public void setEnabled(boolean e) {
        if (this.enabled == e) return;
        this.enabled = e;
        if (e) onEnable(); else onDisable();
    }
    public void toggle() { setEnabled(!enabled); }

    protected <S extends Setting<?>> S add(S s) { settings.add(s); return s; }

    // lifecycle + event hooks (override as needed)
    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender3D(float partialTicks) {}
    public void onRender2D(net.minecraft.client.gui.GuiGraphicsExtractor g) {}

    protected Minecraft mc() { return Minecraft.getInstance(); }
}
