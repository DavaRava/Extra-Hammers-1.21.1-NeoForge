package de.davarava.extrahammers.event;

import de.davarava.extrahammers.ExtraHammers;
import de.davarava.extrahammers.item.HammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = ExtraHammers.MODID)
public class ModEvents {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onHammerUsage(BlockEvent.BreakEvent event) {
        //if hammer in hand
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof HammerItem hammer)) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        //get blocks to be destroyed
        BlockPos mainPos = event.getPos();
        if (HARVESTED_BLOCKS.contains(mainPos)) return;

        var blocks = HammerItem.getBlocksToBeDestroyed(player.level(), mainPos, serverPlayer, hammer.depth, hammer.radius);
        blocks.add(mainPos);

        //destroy them
        for (BlockPos pos : blocks) {
            if (!hammer.isCorrectToolForDrops(stack, player.level().getBlockState(pos))) continue;
            if (pos == mainPos) continue;

            HARVESTED_BLOCKS.add(pos);
            serverPlayer.gameMode.destroyBlock(pos);
            HARVESTED_BLOCKS.remove(pos);
        }
    }
}
