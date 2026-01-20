package com.chibikookie.magnet.interactions;

import com.chibikookie.magnet.data.MagnetMetadata;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import org.jspecify.annotations.NonNull;

public class MagnetInteraction extends SimpleInstantInteraction {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final BuilderCodec<MagnetInteraction> CODEC = BuilderCodec.builder(MagnetInteraction.class, MagnetInteraction::new, SimpleInstantInteraction.CODEC)
            .build();

    @Override
    protected void firstRun(@NonNull InteractionType interactionType, @NonNull InteractionContext interactionContext, @NonNull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> cmd = interactionContext.getCommandBuffer();
        if (cmd == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("CommandBuffer is null");
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = cmd.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = cmd.getComponent(ref, PlayerRef.getComponentType());
        if (player == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Failed to retrieve player");
            return;
        }

        ItemStack stack = interactionContext.getHeldItem();
        if (stack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Failed to retrieve item stack");
            return;
        }

        var metadata = stack.getFromMetadataOrDefault("MagnetStatus", MagnetMetadata.CODEC);
        var newActivatedState = !metadata.getActivated();

        metadata.setActivated(newActivatedState);
        ItemStack newStack = stack.withMetadata("MagnetStatus", MagnetMetadata.CODEC, metadata);

        var container = interactionContext.getHeldItemContainer();
        if (container == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Failed to retrieve held item container");
            return;
        }
        container.setItemStackForSlot(interactionContext.getHeldItemSlot(), newStack);

        if (playerRef != null) {
            PacketHandler packetHandler = playerRef.getPacketHandler();
            var primaryMessage = Message.join(
                    Message.raw("Your magnet is now "),
                    Message.raw(newActivatedState ? "ON" : "OFF").color(newActivatedState ? "#00ff00" : "#ff0000")
            );
            NotificationUtil.sendNotification(packetHandler, primaryMessage, null, stack.toPacket());
        }

        interactionContext.getState().state = InteractionState.Finished;
    }
}
