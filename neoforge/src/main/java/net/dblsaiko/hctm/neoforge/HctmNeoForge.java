package net.dblsaiko.hctm.neoforge;

import net.dblsaiko.hctm.HctmBase;
import net.dblsaiko.hctm.neoforge.init.ItemGroupRegistryNeoForge;
import net.dblsaiko.hctm.neoforge.init.ItemRegistryNeoForge;
import net.dblsaiko.hctm.neoforge.init.NetworkRegistryNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(HctmBase.MOD_ID)
public class HctmNeoForge {
    // static registries, as per forge recommendations
    public static final ItemRegistryNeoForge ITEMS = new ItemRegistryNeoForge(HctmBase.MOD_ID);
    public static final ItemGroupRegistryNeoForge ITEM_GROUPS = new ItemGroupRegistryNeoForge(HctmBase.MOD_ID);
    public static final NetworkRegistryNeoForge NETWORK_REGISTRY = new NetworkRegistryNeoForge(HctmBase.MOD_ID);

    public HctmNeoForge(IEventBus modBus) {
        // it's all deferred, so we can call register in whatever order we want
        ITEMS.register(modBus);
        ITEM_GROUPS.register(modBus);
        NETWORK_REGISTRY.register(modBus);

        HctmBase.initialize();

        modBus.addListener(this::onInit);
    }

    private void onInit(FMLCommonSetupEvent event) {
        // event fired after Registry-based registration is complete

        // GraphUniverses should really be registered on the main thread
        event.enqueueWork(HctmBase::initializeWireNetwork);
    }
}
