package net.dblsaiko.hctm.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public class ItemGroups {

    public final RegistryObject<ItemGroup> all;

    public ItemGroups(Items items) {
        ItemGroupRegistry reg = AbstractPlatform.getInstance().getItemGroupRegistry();
        all = reg.create("all", ItemGroup.create(ItemGroup.Row.TOP, 0)
            .displayName(Text.translatable("itemGroup.hctm-base.all"))
            .icon(() -> items.getScrewdriver().getDefaultStack())
            .entries((displayContext, entries) -> {
                entries.add(items.getScrewdriver());
            })
            .build());
    }
}
