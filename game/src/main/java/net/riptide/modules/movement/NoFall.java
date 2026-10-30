package net.riptide.modules.movement;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.riptide.module.Category;
import net.riptide.module.Module;

/** Tells the server you're on the ground while falling, so you take no fall damage. */
public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        LocalPlayer me = mc().player;
        if (me.isCreative() || me.isSpectator() || me.isFallFlying()) return;
        if (me.fallDistance > 2.0 && me.getDeltaMovement().y < 0) {
            me.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, me.horizontalCollision));
        }
    }
}
