package com.chibikookie.magnet.commands;

import com.chibikookie.magnet.gui.ConfigGui;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

public class MagnetConfigCommand extends AbstractPlayerCommand {
    public MagnetConfigCommand() {
        super("magnet-config", "Display UI to configure magnets");
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        player.getPageManager().openCustomPage(ref, store, new ConfigGui(playerRef, CustomPageLifetime.CanDismiss));
    }
}
