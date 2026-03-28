package de.davarava.extrahammers.item;

import de.davarava.extrahammers.other.ModDataComponents;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem {
    //radius
    public final int depth;
    public final int radius;

    public HammerItem(Tier tier, Properties properties, int radius, int depth) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
        this.radius = radius;
        this.depth = depth;
    }

    //returns a list with all BlockPoses in the area
    public static List<BlockPos> getBlocksToBeDestroyed(Level level, BlockPos initialBlockPos, Player player, int depth, int radius) {
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

        //put each BlockPos with the right orientation in the list
        for (int d = 0; d < depth; d++) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {

                    BlockPos pos = switch (face) {
                        case UP -> initialBlockPos.offset(x, -d, y);
                        case DOWN -> initialBlockPos.offset(x, d, y);

                        case NORTH -> initialBlockPos.offset(x, y, d);
                        case SOUTH -> initialBlockPos.offset(x, y, -d);

                        case EAST -> initialBlockPos.offset(-d, y, x);
                        case WEST -> initialBlockPos.offset(d, y, x);
                    };

                    if (!pos.equals(initialBlockPos)) {
                        positions.add(pos);
                    }
                }
            }
        }

        return positions;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return super.isValidRepairItem(toRepair, repair);
    }

    //tool tip
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        //radius info
        tooltipComponents.add(Component.literal("§7Mines stone type blocks and ores"));
        tooltipComponents.add(Component.literal(""));

        if (Screen.hasShiftDown()) {
            //extra info
            tooltipComponents.add(Component.literal("§9Mode: §r" + (radius*2+1) + "x" + (radius*2+1) + "x" + (depth)));
            tooltipComponents.add(Component.literal("§9Blocks mined: §r" + stack.getOrDefault(ModDataComponents.BLOCKS_MINED, 0)));
            tooltipComponents.add(Component.literal("§9Durability: §r" + (stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage()));
        } else {
            tooltipComponents.add(Component.literal("§7Press §aSHIFT §7to see more"));
        }
    }
}
