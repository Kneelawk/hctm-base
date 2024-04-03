package net.dblsaiko.hctm.fabric.init;

import net.dblsaiko.hctm.fabric.HctmFabric;
import net.dblsaiko.hctm.init.AbstractPlatform;
import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.ItemRegistry;

public class FabricPlatform extends AbstractPlatform {
    @Override
    public ItemRegistry getItemRegistry() {
        return HctmFabric.getInstance().items;
    }

    @Override
    public ItemGroupRegistry getItemGroupRegistry() {
        return HctmFabric.getInstance().itemGroups;
    }
}
