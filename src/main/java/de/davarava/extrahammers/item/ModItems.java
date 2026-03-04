package de.davarava.extrahammers.item;

import de.davarava.extrahammers.ExtraHammers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtraHammers.MODID);

    public static final DeferredItem<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer",
            () -> new HammerItem(Tiers.WOOD, new Item.Properties()
                    .attributes(HammerItem.createAttributes(Tiers.WOOD, 7f, -3.5f)), 1, 1));

    public static final DeferredItem<Item> STONE_HAMMER = ITEMS.register("stone_hammer",
            () -> new HammerItem(Tiers.STONE, new Item.Properties()
                    .attributes(HammerItem.createAttributes(Tiers.STONE, 7f, -3.5f)), 1, 1));

    public static final DeferredItem<Item> IRON_HAMMER = ITEMS.register("iron_hammer",
            () -> new HammerItem(Tiers.IRON, new Item.Properties()
                    .attributes(HammerItem.createAttributes(Tiers.IRON, 7f, -3.5f)), 1, 1));

    public static final DeferredItem<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer",
            () -> new HammerItem(Tiers.GOLD, new Item.Properties()
                    .attributes(HammerItem.createAttributes(Tiers.GOLD, 7f, -3.5f)), 1, 1));

    public static final DeferredItem<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer",
            () -> new HammerItem(Tiers.DIAMOND, new Item.Properties()
                    .attributes(HammerItem.createAttributes(Tiers.DIAMOND, 7f, -3.5f)), 1, 2));

    public static final DeferredItem<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer",
            () -> new HammerItem(Tiers.NETHERITE, new Item.Properties().fireResistant()
                    .attributes(HammerItem.createAttributes(Tiers.NETHERITE, 7f, -3.5f)), 1, 3));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}