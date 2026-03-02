package de.davarava.extrahammers.event;

import de.davarava.extrahammers.ExtraHammers;
import de.davarava.extrahammers.item.HammerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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

        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof HammerItem hammer)) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        BlockPos center = blockHit.getBlockPos();
        Direction face = blockHit.getDirection();

        int radius = hammer.radius;

        // Kamera Offset
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;

        // Bounding Box abhängig von der Face berechnen
        BlockPos min;
        BlockPos max;

        switch (face) {
            case UP, DOWN -> {
                min = center.offset(-radius, 0, -radius);
                max = center.offset(radius, 0, radius);
            }
            case NORTH, SOUTH -> {
                min = center.offset(-radius, -radius, 0);
                max = center.offset(radius, radius, 0);
            }
            case EAST, WEST -> {
                min = center.offset(0, -radius, -radius);
                max = center.offset(0, radius, radius);
            }
            default -> {
                return;
            }
        }

        // +1 weil Block-AABB von min bis max+1 geht
        AABB box = new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1, max.getY() + 1, max.getZ() + 1
        ).move(-camX, -camY, -camZ);

        MultiBufferSource buffer = mc.renderBuffers().bufferSource();

        LevelRenderer.renderLineBox(
                event.getPoseStack(),
                buffer.getBuffer(RenderType.lines()),
                box, 1f, 1f, 1f, 0.5f
        );



        event.setCanceled(true);
    }
}
