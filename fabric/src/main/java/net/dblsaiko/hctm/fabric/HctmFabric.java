package net.dblsaiko.hctm.fabric;

import net.dblsaiko.hctm.HctmBase;
import net.dblsaiko.hctm.fabric.init.ItemGroupRegistryFabric;
import net.dblsaiko.hctm.fabric.init.ItemRegistryFabric;

public class HctmFabric {
    private static HctmFabric INSTANCE;

    public final ItemRegistryFabric items;
    public final ItemGroupRegistryFabric itemGroups;

    public HctmFabric() {
        items = new ItemRegistryFabric(HctmBase.MOD_ID);
        itemGroups = new ItemGroupRegistryFabric(HctmBase.MOD_ID);
    }

    private void register() {
        items.register();
        itemGroups.register();
    }

    public static void initialize() {
        INSTANCE = new HctmFabric();

        // need to have INSTANCE set before calling into platform-independent code
        HctmBase.initialize();
        HctmBase.initializeWireNetwork();

        // need to have initialized platform-independent code before performing registration
        getInstance().register();
    }

    public static HctmFabric getInstance() {
        return INSTANCE;
    }
}
