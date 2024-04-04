package net.dblsaiko.hctm.fabric.init;

import net.dblsaiko.hctm.fabric.HctmFabric;
import net.dblsaiko.hctm.init.AbstractPlatform;
import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.ItemRegistry;
import net.dblsaiko.hctm.init.NetworkRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform extends AbstractPlatform {
    @Override
    public boolean isClientEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return HctmFabric.getInstance().items;
    }

    @Override
    public ItemGroupRegistry getItemGroupRegistry() {
        return HctmFabric.getInstance().itemGroups;
    }

    @Override
    public NetworkRegistry getNetworkRegistry() {
        return HctmFabric.getInstance().networkRegistry;
    }
}
