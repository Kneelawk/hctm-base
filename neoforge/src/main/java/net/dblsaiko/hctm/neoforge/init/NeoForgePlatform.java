package net.dblsaiko.hctm.neoforge.init;

import net.dblsaiko.hctm.init.AbstractPlatform;
import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.ItemRegistry;
import net.dblsaiko.hctm.init.NetworkRegistry;
import net.dblsaiko.hctm.neoforge.HctmNeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class NeoForgePlatform extends AbstractPlatform {
    @Override
    public boolean isClientEnvironment() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return HctmNeoForge.ITEMS;
    }

    @Override
    public ItemGroupRegistry getItemGroupRegistry() {
        return HctmNeoForge.ITEM_GROUPS;
    }

    @Override
    public NetworkRegistry getNetworkRegistry() {
        return HctmNeoForge.NETWORK_REGISTRY;
    }
}
