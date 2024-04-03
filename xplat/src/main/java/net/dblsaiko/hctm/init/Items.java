package net.dblsaiko.hctm.init;

import net.minecraft.item.Item;

public class Items {

    public final RegistryObject<Item> screwdriver;

    public Items() {
        ItemRegistry reg = AbstractPlatform.getInstance().getItemRegistry();
        this.screwdriver = reg.create("screwdriver", new Item(new Item.Settings()));
    }

    public Item getScrewdriver() {
        return this.screwdriver.get();
    }
}
