package net.riptide.gui;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.riptide.Riptide;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.ModeSetting;
import net.riptide.module.setting.NumberSetting;
import net.riptide.module.setting.Setting;
import net.riptide.modules.render.ClickGUI;

/**
 * Category panels you can drag around.
 * Left click a module = toggle, right click = settings, middle click = bind a key.
 */
public class ClickGuiScreen extends Screen {
    private static final int PANEL_W = 112;
    private static final int HEADER_H = 15;
    private static final int ROW_H = 13;

    private static final int BG = 0xE0101418;
    private static final int ROW = 0xC0181E24;
    private static final int ROW_HOVER = 0xC0232B33;
    private static final int TEXT = 0xFFE6E6E6;
    private static final int DIM = 0xFF9AA4AE;

    private static ClickGuiScreen INSTANCE;

    public static ClickGuiScreen get() {
        if (INSTANCE == null) INSTANCE = new ClickGuiScreen();
        return INSTANCE;
    }

    private final List<Panel> panels = new ArrayList<Panel>();
    private final Set<Module> expanded = new HashSet<Module>();
    private Module binding = null;
    private Panel dragging = null;
    private int dragDX, dragDY;
    private NumberSetting slider = null;
    private int sliderX, sliderW;

    private ClickGuiScreen() {
        super(Component.literal(Riptide.NAME));
        int x = 8;
        for (Category c : Category.values()) {
            panels.add(new Panel(c, x, 24));
            x += PANEL_W + 6;
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() {
        binding = null;
        slider = null;
        dragging = null;
        Module m = Riptide.get().getModuleManager().get(ClickGUI.class);
        super.onClose();
        if (m != null && m.isEnabled()) m.setEnabled(false);
    }

    // ---------- drawing ----------

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
        super.extractRenderState(g, mouseX, mouseY, a);
        Font font = this.font;
        for (Panel p : panels) {
            List<Module> mods = Riptide.get().getModuleManager().getByCategory(p.category);
            if (mods.isEmpty()) continue;

            // header
            g.fill(p.x, p.y, p.x + PANEL_W, p.y + HEADER_H, Riptide.ACCENT);
            g.text(font, p.category.getDisplay(), p.x + 5, p.y + 4, 0xFF0B1A20, false);
            String arrow = p.open ? "-" : "+";
            g.text(font, arrow, p.x + PANEL_W - 9, p.y + 4, 0xFF0B1A20, false);
            if (!p.open) continue;

            int y = p.y + HEADER_H;
            for (Module m : mods) {
                boolean hover = in(mouseX, mouseY, p.x, y, PANEL_W, ROW_H);
                g.fill(p.x, y, p.x + PANEL_W, y + ROW_H, hover ? ROW_HOVER : ROW);
                if (m.isEnabled()) g.fill(p.x, y, p.x + 2, y + ROW_H, Riptide.ACCENT);
                String label = binding == m ? "Press a key..." : m.getName();
                g.text(font, label, p.x + 6, y + 3, m.isEnabled() ? Riptide.ACCENT : TEXT, false);
                if (!m.getSettings().isEmpty() || binding != m) {
                    String bind = keyName(m.getKeyCode());
                    if (bind != null && binding != m) {
                        g.text(font, bind, p.x + PANEL_W - 6 - font.width(bind), y + 3, DIM, false);
                    } else if (!m.getSettings().isEmpty()) {
                        g.text(font, expanded.contains(m) ? "v" : ">", p.x + PANEL_W - 10, y + 3, DIM, false);
                    }
                }
                if (hover) hoverDesc = m.getDescription();
                y += ROW_H;

                if (expanded.contains(m)) {
                    for (Setting<?> s : m.getSettings()) {
                        g.fill(p.x, y, p.x + PANEL_W, y + ROW_H, BG);
                        drawSetting(g, font, s, p.x + 6, y, PANEL_W - 12);
                        y += ROW_H;
                    }
                }
            }
            g.fill(p.x, y, p.x + PANEL_W, y + 1, Riptide.ACCENT);
        }

        // footer help + hovered description
        String help = "LMB toggle  RMB settings  MMB bind  ESC close";
        g.text(font, help, 6, this.height - font.lineHeight - 4, DIM, true);
        if (hoverDesc != null) {
            g.text(font, hoverDesc, 6, this.height - font.lineHeight * 2 - 6, TEXT, true);
            hoverDesc = null;
        }
    }

    private String hoverDesc;

    private void drawSetting(GuiGraphicsExtractor g, Font font, Setting<?> s, int x, int y, int w) {
        if (s instanceof BooleanSetting b) {
            g.text(font, s.getName(), x, y + 3, TEXT, false);
            int bx = x + w - 9;
            g.fill(bx, y + 3, bx + 8, y + 11, 0xFF2A323A);
            if (b.get()) g.fill(bx + 1, y + 4, bx + 7, y + 10, Riptide.ACCENT);
        } else if (s instanceof ModeSetting m) {
            g.text(font, s.getName(), x, y + 3, TEXT, false);
            String v = m.get();
            g.text(font, v, x + w - font.width(v), y + 3, Riptide.ACCENT, false);
        } else if (s instanceof NumberSetting n) {
            double pct = (n.get() - n.getMin()) / (n.getMax() - n.getMin());
            g.fill(x, y + 10, x + w, y + 12, 0xFF2A323A);
            g.fill(x, y + 10, x + (int) Math.round(w * pct), y + 12, Riptide.ACCENT);
            g.text(font, s.getName(), x, y + 1, TEXT, false);
            String v = fmt(n);
            g.text(font, v, x + w - font.width(v), y + 1, DIM, false);
        }
    }

    // ---------- input ----------

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        int mx = (int) e.x(), my = (int) e.y(), button = e.button();
        if (binding != null) { binding = null; return true; }

        // iterate topmost (last drawn) first
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            List<Module> mods = Riptide.get().getModuleManager().getByCategory(p.category);
            if (mods.isEmpty()) continue;
            if (in(mx, my, p.x, p.y, PANEL_W, HEADER_H)) {
                if (button == 0) {
                    dragging = p;
                    dragDX = mx - p.x;
                    dragDY = my - p.y;
                    panels.remove(i);
                    panels.add(p); // bring to front
                } else if (button == 1) {
                    p.open = !p.open;
                }
                return true;
            }
            if (!p.open) continue;
            int y = p.y + HEADER_H;
            for (Module m : mods) {
                if (in(mx, my, p.x, y, PANEL_W, ROW_H)) {
                    if (button == 0) m.toggle();
                    else if (button == 1 && !m.getSettings().isEmpty()) {
                        if (!expanded.remove(m)) expanded.add(m);
                    } else if (button == 2) binding = m;
                    return true;
                }
                y += ROW_H;
                if (expanded.contains(m)) {
                    for (Setting<?> s : m.getSettings()) {
                        if (in(mx, my, p.x, y, PANEL_W, ROW_H)) {
                            clickSetting(s, mx, p.x + 6, PANEL_W - 12, button);
                            return true;
                        }
                        y += ROW_H;
                    }
                }
            }
        }
        return super.mouseClicked(e, doubleClick);
    }

    private void clickSetting(Setting<?> s, int mx, int x, int w, int button) {
        if (s instanceof BooleanSetting b) {
            b.toggle();
        } else if (s instanceof ModeSetting m) {
            if (button == 1) { for (int i = 0; i < m.getModes().size() - 1; i++) m.cycle(); }
            else m.cycle();
        } else if (s instanceof NumberSetting n) {
            slider = n;
            sliderX = x;
            sliderW = w;
            updateSlider(mx);
        }
    }

    private void updateSlider(int mx) {
        if (slider == null) return;
        double pct = Math.max(0.0, Math.min(1.0, (mx - sliderX) / (double) sliderW));
        double raw = slider.getMin() + (slider.getMax() - slider.getMin()) * pct;
        double inc = slider.getInc();
        double snapped = Math.round(raw / inc) * inc;
        slider.set(Math.round(snapped * 1000.0) / 1000.0);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging != null) {
            dragging.x = (int) e.x() - dragDX;
            dragging.y = Math.max(0, (int) e.y() - dragDY);
            return true;
        }
        if (slider != null) {
            updateSlider((int) e.x());
            return true;
        }
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        dragging = null;
        slider = null;
        return super.mouseReleased(e);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        int d = (int) Math.signum(scrollY) * 12;
        for (Panel p : panels) p.y += d;
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (binding != null) {
            int k = event.key();
            if (k == InputConstants.KEY_ESCAPE || k == InputConstants.KEY_DELETE || k == InputConstants.KEY_BACKSPACE) {
                binding.setKeyCode(-1);
            } else {
                binding.setKeyCode(k);
            }
            binding = null;
            return true;
        }
        Module gui = Riptide.get().getModuleManager().get(ClickGUI.class);
        if (gui != null && event.key() == gui.getKeyCode()) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    // ---------- helpers ----------

    private static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static String fmt(NumberSetting n) {
        double v = n.get();
        if (n.getInc() >= 1.0) return String.valueOf((long) Math.round(v));
        return String.format("%.2f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    static String keyName(int key) {
        if (key < 0) return null;
        try {
            String s = InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
            return s.length() > 8 ? s.substring(0, 8) : s;
        } catch (Throwable t) {
            return "#" + key;
        }
    }

    private static final class Panel {
        final Category category;
        int x, y;
        boolean open = true;
        Panel(Category c, int x, int y) { this.category = c; this.x = x; this.y = y; }
    }

    @SuppressWarnings("unused")
    private static Minecraft mc() { return Minecraft.getInstance(); }
}
