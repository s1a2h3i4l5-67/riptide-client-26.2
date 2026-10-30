package net.riptide.modules.combat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.riptide.module.Category;
import net.riptide.module.Module;
import net.riptide.module.setting.BooleanSetting;
import net.riptide.module.setting.NumberSetting;

/** Keeps a Totem of Undying in your offhand. */
public class AutoTotem extends Module {
    private final NumberSetting health = add(new NumberSetting("Health", 36, 1, 36, 1));
    private final NumberSetting delay = add(new NumberSetting("Delay", 2, 0, 10, 1));
    private final BooleanSetting hotbar = add(new BooleanSetting("Use Hotbar", true));

    private int wait = 0;

    public AutoTotem() {
        super("AutoTotem", "Puts a totem in your offhand (Health 36 = always)", Category.COMBAT);
    }

    @Override
    public String getSuffix() {
        if (!inGame()) return null;
        return String.valueOf(countTotems(mc().player));
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        if (wait > 0) { wait--; return; }
        // only act in the plain inventory menu, so we never click into a chest
        if (me.containerMenu != me.inventoryMenu) return;
        if (!me.inventoryMenu.getCarried().isEmpty()) return;
        if (me.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) return;
        if (me.getHealth() + me.getAbsorptionAmount() > health.get()) return;

        int slot = findTotem(me);
        if (slot < 0) return;
        // inventory index -> InventoryMenu slot (hotbar 0-8 sits at 36-44; main 9-35 maps 1:1)
        int menuSlot = slot < 9 ? slot + 36 : slot;
        mc().gameMode.handleContainerInput(me.inventoryMenu.containerId, menuSlot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, me);
        wait = delay.getI();
    }

    private int findTotem(LocalPlayer me) {
        Inventory inv = me.getInventory();
        for (int i = 9; i < 36; i++) if (isTotem(inv.getItem(i))) return i;
        if (hotbar.get()) for (int i = 0; i < 9; i++) if (isTotem(inv.getItem(i))) return i;
        return -1;
    }

    private static int countTotems(LocalPlayer me) {
        Inventory inv = me.getInventory();
        int n = 0;
        for (int i = 0; i < 36; i++) if (isTotem(inv.getItem(i))) n += inv.getItem(i).getCount();
        if (isTotem(me.getOffhandItem())) n += me.getOffhandItem().getCount();
        return n;
    }

    private static boolean isTotem(ItemStack s) {
        return !s.isEmpty() && s.getItem() == Items.TOTEM_OF_UNDYING;
    }
}
