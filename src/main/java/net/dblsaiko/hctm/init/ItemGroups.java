package net.dblsaiko.hctm.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.dblsaiko.hctm.HctmBase;

import static net.dblsaiko.hctm.HctmBase.MOD_ID;

public class ItemGroups {
    private final ItemGroupRegistry reg = new ItemGroupRegistry(MOD_ID);

    public final RegistryObject<ItemGroup> all;

    public ItemGroups(Items items) {
        all = reg.create("all", FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.hctm-base.all"))
            .icon(() -> HctmBase.getInstance().items.getScrewdriver().getDefaultStack())
            .entries((displayContext, entries) -> {
                entries.add(items.screwdriver.get());
            })
            .build());
    }

    public void register() {
        reg.register();
    }
}
