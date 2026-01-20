package com.chibikookie.magnet.components;

import com.chibikookie.magnet.MagnetPlugin;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// This is a rewrite of com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent
// To create a system which doesn't remove the entity and not excluded by PlayerItemEntityPickupComponent
public class MagnetPickupItemComponent implements Component<EntityStore> {
    public static final float PICKUP_TRAVEL_TIME_DEFAULT = 0.15F;
    @Nonnull
    public static final BuilderCodec<MagnetPickupItemComponent> CODEC = BuilderCodec.builder(MagnetPickupItemComponent.class, MagnetPickupItemComponent::new).build();
    private Ref<EntityStore> targetRef;
    private Vector3d startPosition;
    private float originalLifeTime;
    private float lifeTime;
    private boolean finished;

    @Nonnull
    public static ComponentType<EntityStore, MagnetPickupItemComponent> getComponentType() {
        return MagnetPlugin.get().getMagnetPickupItemComponentType();
    }

    public MagnetPickupItemComponent() {
        this.lifeTime = PICKUP_TRAVEL_TIME_DEFAULT;
        this.finished = false;
    }

    public MagnetPickupItemComponent(@Nonnull Ref<EntityStore> targetRef, @Nonnull Vector3d startPosition) {
        this(targetRef, startPosition, PICKUP_TRAVEL_TIME_DEFAULT);
    }

    public MagnetPickupItemComponent(@Nonnull Ref<EntityStore> targetRef, @Nonnull Vector3d startPosition, float lifeTime) {
        this.lifeTime = PICKUP_TRAVEL_TIME_DEFAULT;
        this.finished = false;
        this.targetRef = targetRef;
        this.startPosition = startPosition;
        this.lifeTime = lifeTime;
        this.originalLifeTime = lifeTime;
    }

    public MagnetPickupItemComponent(@Nonnull MagnetPickupItemComponent pickupItemComponent) {
        this.lifeTime = PICKUP_TRAVEL_TIME_DEFAULT;
        this.finished = false;
        this.targetRef = pickupItemComponent.targetRef;
        this.lifeTime = pickupItemComponent.lifeTime;
        this.startPosition = pickupItemComponent.startPosition;
        this.originalLifeTime = pickupItemComponent.originalLifeTime;
        this.finished = pickupItemComponent.finished;
    }

    public boolean hasFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void decreaseLifetime(float amount) {
        this.lifeTime -= amount;
    }

    public float getLifeTime() {
        return this.lifeTime;
    }

    public float getOriginalLifeTime() {
        return this.originalLifeTime;
    }

    @Nonnull
    public Vector3d getStartPosition() {
        return this.startPosition;
    }

    @Nullable
    public Ref<EntityStore> getTargetRef() {
        return this.targetRef;
    }

    @Nonnull
    public MagnetPickupItemComponent clone() {
        return new MagnetPickupItemComponent(this);
    }
}
