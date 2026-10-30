package net.riptide.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;

/** Glowing outlines through walls, colored by entity type. */
public class ESP extends Module {
    private final BooleanSetting players = add(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", true));
    private final BooleanSetting animals = add(new BooleanSetting("Animals", false));
    private final BooleanSetting items = add(new BooleanSetting("Items", false));
    private final BooleanSetting crystals = add(new BooleanSetting("Crystals", true));

    public ESP() {
        super("ESP", "See entities through walls", Category.RENDER);
    }

    public boolean shouldHighlight(Entity e) {
        if (e == Minecraft.getInstance().player) return false;
        if (e instanceof Player) return players.get();
        if (e instanceof Enemy) return mobs.get();
        if (e instanceof Animal) return animals.get();
        if (e instanceof ItemEntity) return items.get();
        if (e instanceof EndCrystal) return crystals.get();
        return false;
    }

    /** RGB (no alpha) outline color. */
    public int colorFor(Entity e) {
        if (e instanceof Player) return 0xFF3B3B;
        if (e instanceof Enemy) return 0xFF9A1F;
        if (e instanceof Animal) return 0x4CFF4C;
        if (e instanceof ItemEntity) return 0xFFFFFF;
        if (e instanceof EndCrystal) return 0xD94CFF;
        return 0x2EC4F1;
    }
}
