package net.riptide.module.setting;

public abstract class Setting<T> {
    private final String name;
    protected T value;
    public Setting(String name, T def) { this.name = name; this.value = def; }
    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T v) { this.value = v; }
}
