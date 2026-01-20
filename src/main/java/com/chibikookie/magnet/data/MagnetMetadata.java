package com.chibikookie.magnet.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MagnetMetadata {
    public static final BuilderCodec<MagnetMetadata> CODEC = BuilderCodec.<MagnetMetadata>builder(MagnetMetadata.class, MagnetMetadata::new)
            .append(new KeyedCodec<>("Activated", Codec.BOOLEAN), MagnetMetadata::setActivated, MagnetMetadata::getActivated)
            .add()
            .build();

    private boolean activated = false;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean getActivated() {
        return this.activated;
    }
}
