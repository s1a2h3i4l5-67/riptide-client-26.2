package net.riptide.modules.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.riptide.Riptide;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;

/** Watermark, active module list, coords and FPS. */
public class HUD extends Module {
    private final BooleanSetting watermark = add(new BooleanSetting("Watermark", true));
    private final BooleanSetting arrayList = add(new BooleanSetting("Module List", true));
    private final BooleanSetting coords = add(new BooleanSetting("Coords", true));
    private final BooleanSetting fps = add(new BooleanSetting("FPS", true));

    public HUD() {
        super("HUD", "Shows client info on screen", Category.RENDER);
    }

    @Override
    public boolean isHidden() { return true; }

    @Override
    public void onRender2D(GuiGraphicsExtractor g) {
        Font font = mc().font;
        int w = g.guiWidth(), h = g.guiHeight();

        if (watermark.get()) {
            String name = Riptide.NAME;
            String ver = " v" + Riptide.VERSION;
            g.fill(2, 2, 6 + font.width(name + ver) + 4, 4 + font.lineHeight + 3, 0x90000000);
            g.fill(2, 2, 4, 4 + font.lineHeight + 3, Riptide.ACCENT);
            g.text(font, name, 7, 5, Riptide.ACCENT, true);
            g.text(font, ver, 7 + font.width(name), 5, 0xFFAAAAAA, true);
        }

        if (arrayList.get()) {
            List<String> lines = new ArrayList<String>();
            List<String> names = new ArrayList<String>();
            for (Module m : Riptide.get().getModuleManager().getEnabled()) {
                if (m.isHidden()) continue;
                String suf = null;
                try { suf = m.getSuffix(); } catch (Throwable ignored) { }
                names.add(m.getName());
                lines.add(suf == null ? "" : " " + suf);
            }
            // sort longest first
            Integer[] order = new Integer[names.size()];
            for (int i = 0; i < order.length; i++) order[i] = i;
            final List<String> fn = names, fl = lines;
            java.util.Arrays.sort(order, (a, b) -> font.width(fn.get(b) + fl.get(b)) - font.width(fn.get(a) + fl.get(a)));
            int y = 2;
            for (int idx : order) {
                String n = names.get(idx), s = lines.get(idx);
                int tw = font.width(n + s);
                int x = w - tw - 4;
                g.fill(x - 2, y, w, y + font.lineHeight + 2, 0x90000000);
                g.fill(w - 1, y, w, y + font.lineHeight + 2, Riptide.ACCENT);
                g.text(font, n, x, y + 1, Riptide.ACCENT, true);
                if (!s.isEmpty()) g.text(font, s, x + font.width(n), y + 1, 0xFFAAAAAA, true);
                y += font.lineHeight + 2;
            }
        }

        int by = h - font.lineHeight - 2;
        if (coords.get() && inGame()) {
            LocalPlayer p = mc().player;
            String c = String.format("XYZ %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ());
            g.text(font, c, 2, by, 0xFFFFFFFF, true);
            by -= font.lineHeight + 1;
        }
        if (fps.get()) {
            g.text(font, mc().getFps() + " FPS", 2, by, 0xFFFFFFFF, true);
        }
    }
}
