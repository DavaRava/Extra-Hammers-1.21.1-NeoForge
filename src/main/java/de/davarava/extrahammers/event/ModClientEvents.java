package de.davarava.extrahammers.event;

import de.davarava.extrahammers.Config;
import de.davarava.extrahammers.ExtraHammers;
import de.davarava.extrahammers.item.HammerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(modid = ExtraHammers.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void hammerBlockOutlineRender(RenderHighlightEvent.Block event){
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        //if hammer in hand
        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof HammerItem hammer)) return;

        //check if block in front
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        //variables
        BlockPos center = blockHit.getBlockPos();

        int radius = hammer.radius;
        int depth = hammer.depth;

        //cam position
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;

        //get blocks
        var blocks = HammerItem.getBlocksToBeDestroyed(
                mc.level,
                center,
                mc.player,
                depth,
                radius
        ); blocks.add(center);

        BlockPos min = center;
        BlockPos max = center;

        //calculate
        for (BlockPos pos : blocks) {
            min = new BlockPos(
                    Math.min(min.getX(), pos.getX()),
                    Math.min(min.getY(), pos.getY()),
                    Math.min(min.getZ(), pos.getZ())
            );

            max = new BlockPos(
                    Math.max(max.getX(), pos.getX()),
                    Math.max(max.getY(), pos.getY()),
                    Math.max(max.getZ(), pos.getZ())
            );
        }

        //create outline box
        AABB box = new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1, max.getY() + 1, max.getZ() + 1
        ).move(-camX, -camY, -camZ);

        MultiBufferSource buffer = mc.renderBuffers().bufferSource();

        //render the box
        boolean isBlack = Config.IS_BLOCK_OUTLINE_BLACK.getAsBoolean();

        if (isBlack){
            LevelRenderer.renderLineBox(
                    event.getPoseStack(),
                    buffer.getBuffer(RenderType.lines()),
                    box, 0f, 0f, 0f, 0.5f
            );
        } else {
            LevelRenderer.renderLineBox(
                    event.getPoseStack(),
                    buffer.getBuffer(RenderType.lines()),
                    box, 1f, 1f, 1f, 0.5f
            );
        }

        //event.setCanceled(true);
    }
}
