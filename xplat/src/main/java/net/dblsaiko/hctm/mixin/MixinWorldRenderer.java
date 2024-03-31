package net.dblsaiko.hctm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dblsaiko.hctm.block.BlockAdvancedShape;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Shadow private ClientWorld world;

    @Shadow
    public static void drawShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha, boolean colorize) {}

    @Inject(
        method = "drawBlockOutline",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void drawBlockOutline(MatrixStack matrixStack_1, VertexConsumer vertexConsumer_1, Entity entity_1, double double_1, double double_2, double double_3, BlockPos blockPos_1, BlockState blockState_1, CallbackInfo ci) {
        if (!(blockState_1.getBlock() instanceof BlockAdvancedShape)) return;
        BlockAdvancedShape bas = (BlockAdvancedShape) blockState_1.getBlock();

        drawShapeOutline(matrixStack_1, vertexConsumer_1, blockState_1.getOutlineShape(this.world, blockPos_1, ShapeContext.of(entity_1)), (double) blockPos_1.getX() - double_1, (double) blockPos_1.getY() - double_2, (double) blockPos_1.getZ() - double_3, 0.0F, 0.0F, 0.0F, 0.4F, true);
        ci.cancel();
    }

}
