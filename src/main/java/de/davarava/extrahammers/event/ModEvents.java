package de.davarava.extrahammers.event;

import com.ibm.icu.text.MessagePattern;
import de.davarava.extrahammers.Config;
import de.davarava.extrahammers.ExtraHammers;
import de.davarava.extrahammers.item.HammerItem;
import de.davarava.extrahammers.other.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
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
        ServerLevel level = (ServerLevel) event.getLevel();

        if (!(stack.getItem() instanceof HammerItem hammer)) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if(level.isClientSide) return;

        //get blocks to be destroyed
        BlockPos mainPos = event.getPos();
        if (HARVESTED_BLOCKS.contains(mainPos)) return;

        var blocks = HammerItem.getBlocksToBeDestroyed(player.level(), mainPos, serverPlayer, hammer.depth, hammer.radius);
        blocks.add(mainPos);

        int blocks_mined = 0;

        //destroy them
        for (BlockPos pos : blocks) {
            //if not correct tool but main pos also add to blocks_mined
            if (!hammer.isCorrectToolForDrops(stack, player.level().getBlockState(pos)) && pos == mainPos) blocks_mined++;
            //if not correct tool continue
            if (!hammer.isCorrectToolForDrops(stack, player.level().getBlockState(pos))) continue;
            //if stack has 1 durability left then continue to avoid bugs
            if(stack.getDamageValue() == (stack.getMaxDamage() - 1)) continue;

            //particles
            if(Config.PARTICLES.getAsBoolean()) {
                level.sendParticles(ParticleTypes.COMPOSTER, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, 1, 0,0,0, 1);
            }

            //calculate blocks_mined
            blocks_mined++;

            //don't break mainPos because event already doing it for us
            if (pos.equals(mainPos)) continue;

            //break + add to block list to avoid endless breaking
            HARVESTED_BLOCKS.add(pos);
            serverPlayer.gameMode.destroyBlock(pos);
            HARVESTED_BLOCKS.remove(pos);
        }

        //set blocks_mined
        var previous = stack.get(ModDataComponents.BLOCKS_MINED);
        if(previous == null){
            stack.set(ModDataComponents.BLOCKS_MINED, blocks_mined);
        } else {
            stack.set(ModDataComponents.BLOCKS_MINED, previous + blocks_mined);
        }

    }
}
