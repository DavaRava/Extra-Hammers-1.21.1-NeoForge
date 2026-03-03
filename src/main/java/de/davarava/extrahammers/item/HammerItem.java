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
    //radius
    public final int area;

    public HammerItem(Tier tier, Properties properties, int area) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
        this.area = area;
    }

    //returns a list with all BlockPoses in the area
    public static List<BlockPos> getBlocksToBeDestroyed(Level level, BlockPos initialBlockPos, Player player, int area) {
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
        for (int d = 0; d < area; d++) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {

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

    //tool tip
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("§7Mines in a §a3x3x" + (area) + " §7area"));
            tooltipComponents.add(Component.literal("§9Durability: " + "§r" + (stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage()));
        } else {
            tooltipComponents.add(Component.literal("§7Press §aSHIFT §7to see more"));
        }
    }
}
