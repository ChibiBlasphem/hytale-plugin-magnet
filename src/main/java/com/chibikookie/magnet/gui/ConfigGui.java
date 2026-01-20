package com.chibikookie.magnet.gui;

import com.chibikookie.magnet.MagnetPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

public class ConfigGui extends InteractiveCustomUIPage<ConfigGui.ConfigGuiData> {
    public ConfigGui(@NonNull PlayerRef playerRef, @NonNull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, ConfigGuiData.CODEC);
    }

    @Override
    public void build(@NonNull Ref<EntityStore> ref, @NonNull UICommandBuilder uiCommandBuilder, @NonNull UIEventBuilder uiEventBuilder, @NonNull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/ChibiKookie_Magnet_Config.ui");

        var saveEventData = new EventData()
                .append("Button", "Save")
                .append("@PickupRadius", "#PickupRadius #Input.Value")
                .append("@AllowUnequippedUse", "#AllowUnequippedUse #Input.Value")
                .append("@AllowUseFromBackpack", "#AllowUseFromBackpack #Input.Value");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", EventData.of("Button", "Cancel"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", saveEventData, false);

        var config = MagnetPlugin.get().config.get();

        uiCommandBuilder.set("#PickupRadius #Input.Value", config.getPickupRadius());
        uiCommandBuilder.set("#AllowUnequippedUse #Input.Value", config.getAllowUnequippedUse());
        uiCommandBuilder.set("#AllowUseFromBackpack #Input.Value", config.getAllowUseFromBackpack());
    }

    @Override
    public void handleDataEvent(@NonNull Ref<EntityStore> ref, @NonNull Store<EntityStore> store, @NonNull ConfigGuiData data) {
        super.handleDataEvent(ref, store, data);

        if (data.button.equals("Cancel")) {
            close();
            return;
        }

        if (data.button.equals("Save")) {
            close();

            var config = MagnetPlugin.get().config;
            config.get().setConfig(data.pickupRadius, data.allowUnequippedUse, data.allowUseFromBackpack);
            config.save();
        }
    }

    public static class ConfigGuiData {
        public static final BuilderCodec<ConfigGuiData> CODEC = BuilderCodec.<ConfigGuiData>builder(ConfigGuiData.class, ConfigGuiData::new)
                .append(new KeyedCodec<>("@PickupRadius", Codec.DOUBLE), (guiData, v) -> guiData.pickupRadius = v, guiData -> guiData.pickupRadius).add()
                .append(new KeyedCodec<>("@AllowUnequippedUse", Codec.BOOLEAN), (guiData, v) -> guiData.allowUnequippedUse = v, guiData -> guiData.allowUnequippedUse).add()
                .append(new KeyedCodec<>("@AllowUseFromBackpack", Codec.BOOLEAN), (guiData, v) -> guiData.allowUseFromBackpack = v, guiData -> guiData.allowUseFromBackpack).add()
                .append(new KeyedCodec<>("Button", Codec.STRING), (guiData, s) -> guiData.button = s, guiData -> guiData.button).add()
                .build();

        private Double pickupRadius;
        private Boolean allowUnequippedUse;
        private Boolean allowUseFromBackpack;
        private String button;
    }
}
