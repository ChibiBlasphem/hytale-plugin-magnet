package com.chibikookie.magnet.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.validator.RangeValidator;

public class MagnetConfig {
    public static final BuilderCodec<MagnetConfig> CODEC = BuilderCodec.<MagnetConfig>builder(MagnetConfig.class, MagnetConfig::new)
            .append(new KeyedCodec<>("PickupRadius", Codec.DOUBLE), MagnetConfig::setPickupRadius, MagnetConfig::getPickupRadius)
            .addValidator(new RangeValidator<>(0.1, Double.MAX_VALUE, true))
            .documentation("The radius in which the items will be picked")
            .add()
            .append(new KeyedCodec<>("AllowUnequippedUse", Codec.BOOLEAN), MagnetConfig::setAllowUnequippedUse, MagnetConfig::getAllowUnequippedUse)
            .documentation("Allow the magnet to pull item even if it's not equipped in hotbar or utilities")
            .add()
            .append(new KeyedCodec<>("AllowUseFromBackpack", Codec.BOOLEAN), MagnetConfig::setAllowUseFromBackpack, MagnetConfig::getAllowUseFromBackpack)
            .documentation("Allow the magnet to pull item even if it's stored in the player backpack")
            .add()
            .build();

    private double pickupRadius = 10;
    private boolean allowUnequippedUse = false;
    private boolean allowUseFromBackpack = false;


    public void setPickupRadius(double pickupRadius) {
        this.pickupRadius = pickupRadius;
    }
    public double getPickupRadius() {
        return pickupRadius;
    }

    public void setAllowUnequippedUse(boolean allowUnequippedUse) {
        this.allowUnequippedUse = allowUnequippedUse;
    }
    public boolean getAllowUnequippedUse() { return allowUnequippedUse; }

    public void setAllowUseFromBackpack(boolean allowUseFromBackpack) {
        this.allowUseFromBackpack = allowUseFromBackpack;
    }
    public boolean getAllowUseFromBackpack() { return allowUseFromBackpack; }

    public void setConfig(double pickupRadius, boolean allowUnequippedUse, boolean allowUseFromBackpack) {
        this.pickupRadius = pickupRadius;
        this.allowUnequippedUse = allowUnequippedUse;
        this.allowUseFromBackpack = allowUseFromBackpack;
    }
}
