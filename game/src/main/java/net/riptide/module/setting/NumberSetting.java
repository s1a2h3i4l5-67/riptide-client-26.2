package net.riptide.module.setting;

public class NumberSetting extends Setting<Double> {
    private final double min, max, inc;
    public NumberSetting(String name, double def, double min, double max, double inc) {
        super(name, def);
        this.min = min; this.max = max; this.inc = inc;
    }
    public double get() { return value; }
    public float getF() { return value.floatValue(); }
    public int getI() { return (int) Math.round(value); }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getInc() { return inc; }
    public void set(double v) {
        if (v < min) v = min;
        if (v > max) v = max;
        this.value = v;
    }
}
