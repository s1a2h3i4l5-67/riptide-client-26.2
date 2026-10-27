package net.riptide.module.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean def) { super(name, def); }
    public boolean get() { return value; }
    public void set(boolean v) { this.value = v; }
    public void toggle() { this.value = !this.value; }
}
