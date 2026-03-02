package de.davarava.extrahammers.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem {
    // Radius
    public final int radius;

    public HammerItem(Tier tier, Properties properties, int radius) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
        this.radius = radius;
    }

    //Returns a List with all BLockPoses in the radius
    public static List<BlockPos> getBlocksToBeDestroyed(Level level, BlockPos initialBlockPos, Player player, int radius) {
        List<BlockPos> positions = new ArrayList<>();

        // Eyes Direction
        BlockHitResult traceResult = level.clip(new ClipContext(
                player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if(traceResult.getType() == HitResult.Type.MISS) return positions;

        Direction face = traceResult.getDirection();

        // Put each BlockPos with the right orientation in the list
        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    BlockPos pos = switch(face) {
                        case UP, DOWN -> initialBlockPos.offset(x, 0, z);
                        case NORTH, SOUTH -> initialBlockPos.offset(x, y, 0);
                        case EAST, WEST -> initialBlockPos.offset(0, y, z);
                    };
                    if(!pos.equals(initialBlockPos)) positions.add(pos);
                }
            }
        }

        return positions;
    }

    //Tool Tip
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("§7Mines in a §a" + (radius * 2 + 1) + "x" + (radius * 2 + 1) + " §7area"));
            tooltipComponents.add(Component.literal("§9Durability: " + "§r" + (stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage()));
        } else {
            tooltipComponents.add(Component.literal("§7Press §aSHIFT §7to see more"));
        }
    }
}
