package net.ixdarklord.ultimine_addition.common.item;

import net.ixdarklord.coolcat_lib.common.item.ComponentItem;
import net.ixdarklord.ultimine_addition.common.data.DataHandler;
import net.minecraft.world.item.ItemStack;

public abstract class DataAbstractItem<T extends DataHandler<?, ?>> extends ComponentItem {
    public DataAbstractItem(Properties properties, ComponentType componentType) {
        super(properties, componentType);
    }

    public abstract T getData(ItemStack stack);
}