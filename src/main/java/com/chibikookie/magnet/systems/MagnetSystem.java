package com.chibikookie.magnet.systems;

import com.chibikookie.magnet.components.MagnetPickupItemComponent;
import com.chibikookie.magnet.data.MagnetMetadata;
import com.chibikookie.magnet.MagnetPlugin;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MagnetSystem extends DelayedEntitySystem<EntityStore> {
    public static final float DELAY = 0.5F;

    @Nonnull
    private final Query<EntityStore> query;

    public MagnetSystem() {
        super(DELAY);
        this.query = Query.and(Player.getComponentType());
    }

    private boolean isMagnet(@Nullable ItemStack stack) {
        return stack != null && stack.getItemId().equals(MagnetPlugin.MAGNET_ITEM_ID);
    }

    private boolean isMagnetActivated(@NonNull ItemStack stack) {
        var metadata = stack.getFromMetadataOrDefault("MagnetStatus", MagnetMetadata.CODEC);
        return metadata.getActivated();
    }

    private boolean hasMagnetActivatedInInventory(ItemContainer container) {
        for (short i = 0; i < container.getCapacity(); ++i) {
            var stack = container.getItemStack(i);
            if (isMagnet(stack)) {
                return isMagnetActivated(stack);
            }
        }
        return false;
    }

    private boolean isMagnetActivable(Inventory inventory) {
        if (inventory != null) {
            ItemStack activeHotbarItem = inventory.getActiveHotbarItem();
            ItemStack utilityItem = inventory.getUtilityItem();

            if (isMagnet(activeHotbarItem)) {
                return isMagnetActivated(activeHotbarItem);
            }
            if (isMagnet(utilityItem)) {
                return isMagnetActivated(utilityItem);
            }

            if (MagnetPlugin.get().config.get().getAllowUnequippedUse()) {
                var container = !MagnetPlugin.get().config.get().getAllowUseFromBackpack()
                        ? inventory.getCombinedHotbarUtilityConsumableStorage()
                        : new CombinedItemContainer(inventory.getCombinedHotbarUtilityConsumableStorage(),
                        inventory.getBackpack());
                return hasMagnetActivatedInInventory(container);
            }
        }

        return false;
    }

    @Override
    public void tick(float dt, int index, @NonNull ArchetypeChunk<EntityStore> chunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> cmd) {
        Ref<EntityStore> playerRef = chunk.getReferenceTo(index);

        Player player = EntityUtils.toHolder(index, chunk).getComponent(Player.getComponentType());
        if (player != null && isMagnetActivable(player.getInventory())) {
            TransformComponent playerTransform = cmd.getComponent(playerRef, TransformComponent.getComponentType());
            ModelComponent modelComponent = cmd.getComponent(playerRef, ModelComponent.getComponentType());
            if (playerTransform == null || modelComponent == null) {
                return;
            }

            Vector3d playerPos = playerTransform.getPosition().clone().add(0, modelComponent.getModel().getEyeHeight(), 0);
            List<Ref<EntityStore>> nearby = new ArrayList<>();
            SpatialResource<Ref<EntityStore>, EntityStore> itemSpacialResource = cmd.getResource(EntityModule.get().getItemSpatialResourceType());

            itemSpacialResource.getSpatialStructure().collect(playerPos, MagnetPlugin.get().config.get().getPickupRadius(), nearby);
            for (Ref<EntityStore> itemRef : nearby) {
                TransformComponent entityPos = cmd.getComponent(itemRef, TransformComponent.getComponentType());
                MagnetPickupItemComponent pickupComp = cmd.getComponent(itemRef, MagnetPickupItemComponent.getComponentType());

                if (pickupComp == null) {
                    cmd.addComponent(itemRef, MagnetPickupItemComponent.getComponentType(), new MagnetPickupItemComponent(playerRef, entityPos.getPosition()));
                }
            }
        }
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
