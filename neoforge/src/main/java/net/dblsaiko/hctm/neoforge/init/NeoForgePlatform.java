package net.dblsaiko.hctm.neoforge.init;

import net.dblsaiko.hctm.init.AbstractPlatform;
import net.dblsaiko.hctm.init.ItemGroupRegistry;
import net.dblsaiko.hctm.init.ItemRegistry;
import net.dblsaiko.hctm.neoforge.HctmNeoForge;

public class NeoForgePlatform extends AbstractPlatform {
    @Override
    public ItemRegistry getItemRegistry() {
        return HctmNeoForge.ITEMS;
    }

    @Override
    public ItemGroupRegistry getItemGroupRegistry() {
        return HctmNeoForge.ITEM_GROUPS;
    }
}
