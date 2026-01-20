package com.chibikookie.magnet;

import com.chibikookie.magnet.commands.MagnetConfigCommand;
import com.chibikookie.magnet.components.MagnetPickupItemComponent;
import com.chibikookie.magnet.data.MagnetConfig;
import com.chibikookie.magnet.interactions.MagnetInteraction;
import com.chibikookie.magnet.systems.MagnetPickupItemSystem;
import com.chibikookie.magnet.systems.MagnetSystem;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;

public class MagnetPlugin extends JavaPlugin {
    private static MagnetPlugin INSTANCE;
    public static MagnetPlugin get() {
        return INSTANCE;
    }

    public static final String MAGNET_ITEM_ID = "ChibiKookie_Magnet_Magnet";

    public final Config<MagnetConfig> config;

    private ComponentType<EntityStore, MagnetPickupItemComponent> magnetPickupItemComponentType;

    public MagnetPlugin(JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        config = this.withConfig(MagnetConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        this.config.save();

        magnetPickupItemComponentType = this.getEntityStoreRegistry().registerComponent(MagnetPickupItemComponent.class, MagnetPickupItemComponent::new);

        this.getEntityStoreRegistry().registerSystem(new MagnetSystem());
        this.getEntityStoreRegistry().registerSystem(new MagnetPickupItemSystem());

        this.getCommandRegistry().registerCommand(new MagnetConfigCommand());
        this.getCodecRegistry(Interaction.CODEC).register("chibikookie_magnet_interaction", MagnetInteraction.class, MagnetInteraction.CODEC);
    }

    public ComponentType<EntityStore, MagnetPickupItemComponent> getMagnetPickupItemComponentType() {
        return magnetPickupItemComponentType;
    }
}
