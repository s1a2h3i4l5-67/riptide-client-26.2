package net.riptide.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc");

    private final String display;
    Category(String display) { this.display = display; }
    public String getDisplay() { return display; }
}
