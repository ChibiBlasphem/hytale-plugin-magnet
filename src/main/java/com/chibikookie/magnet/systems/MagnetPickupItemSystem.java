package com.chibikookie.magnet.systems;

import com.chibikookie.magnet.components.MagnetPickupItemComponent;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

public class MagnetPickupItemSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    private final Query<EntityStore> query;

    public MagnetPickupItemSystem() {
        this.query = Query.and(MagnetPickupItemComponent.getComponentType());
    }

    public void tick(float dt, int index, @NonNull ArchetypeChunk<EntityStore> chunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> cmd) {
        MagnetPickupItemComponent component = chunk.getComponent(index, MagnetPickupItemComponent.getComponentType());
        assert component != null;

        Ref<EntityStore> itemRef = chunk.getReferenceTo(index);

        if (component.hasFinished()) {
            cmd.removeComponent(itemRef, MagnetPickupItemComponent.getComponentType());
        } else {
            Ref<EntityStore> targetRef = component.getTargetRef();
            if (targetRef != null && targetRef.isValid()) {
                TransformComponent transformComponent = chunk.getComponent(index, TransformComponent.getComponentType());
                assert transformComponent != null;

                Vector3d position = transformComponent.getPosition();
                TransformComponent targetTransformComponent = cmd.getComponent(targetRef, TransformComponent.getComponentType());
                assert targetTransformComponent != null;

                Vector3d targetPosition = targetTransformComponent.getPosition().clone();
                ModelComponent targetModelComponent = cmd.getComponent(targetRef, ModelComponent.getComponentType());
                if (targetModelComponent != null) {
                    float targetModelEyeHeight = targetModelComponent.getModel().getEyeHeight(targetRef, cmd);
                    targetPosition.add(0.0, targetModelEyeHeight / 5.0F, 0.0);
                }

                if (updateMovement(component, position, targetPosition, dt)) {
                    component.setFinished(true);
                }
            } else {
                cmd.removeComponent(itemRef, MagnetPickupItemComponent.getComponentType());
            }
        }
    }

    private static boolean updateMovement(@Nonnull MagnetPickupItemComponent pickupItemComponent, @Nonnull Vector3d current, @Nonnull Vector3d target, float dt) {
        float remainingTime = pickupItemComponent.getLifeTime();
        float originalLifeTime = pickupItemComponent.getOriginalLifeTime();
        float progress = 1.0F - remainingTime / originalLifeTime;
        if (progress >= 1.0F) {
            current.assign(target);
            return true;
        } else {
            current.assign(Vector3d.lerp(pickupItemComponent.getStartPosition(), target, progress));
            pickupItemComponent.decreaseLifetime(dt);
            return false;
        }
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }
}
