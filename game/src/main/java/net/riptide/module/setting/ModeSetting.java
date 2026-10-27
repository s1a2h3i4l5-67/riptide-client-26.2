package net.riptide.module.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    public ModeSetting(String name, String def, String... modes) {
        super(name, def);
        this.modes = Arrays.asList(modes);
    }
    public String get() { return value; }
    public boolean is(String m) { return value.equals(m); }
    public List<String> getModes() { return modes; }
    public void cycle() {
        int i = modes.indexOf(value);
        this.value = modes.get((i + 1) % modes.size());
    }
}
