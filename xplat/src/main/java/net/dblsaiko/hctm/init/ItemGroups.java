package net.dblsaiko.hctm.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

import net.dblsaiko.hctm.HctmBase;

import static net.dblsaiko.hctm.HctmBase.MOD_ID;

public class ItemGroups {
    private final ItemGroupRegistry reg = new ItemGroupRegistry(MOD_ID);

    public final RegistryObject<ItemGroup> all;

    public ItemGroups(Items items) {
        all = reg.create("all", ItemGroup.create(ItemGroup.Row.TOP, 0)
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
