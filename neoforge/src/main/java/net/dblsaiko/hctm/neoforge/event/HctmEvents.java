package net.dblsaiko.hctm.neoforge.event;

import net.dblsaiko.hctm.block.BlockCustomBreak;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

@Mod.EventBusSubscriber
public class HctmEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof World world)) return;
        PlayerEntity player = event.getPlayer();
        BlockState state = event.getState();
        Block block = state.getBlock();
        BlockPos pos = event.getPos();

        // BreakEvent is fired before any of these checks, so we have to do them now
        if (!player.getMainHandStack().getItem().canMine(state, world, pos, player)) return;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            GameMode mode = serverPlayer.interactionManager.getGameMode();
            if (player.isBlockBreakingRestricted(world, pos, mode)) return;
        }
        // we *probably* won't have to worry about the OperatorBlock case

        // It would be easier to use IBlockExtension.canHarvestBlock if this were solely a forge mod, but this is not
        if (block instanceof BlockCustomBreak custom) {
            if (!custom.tryBreak(state, pos, world, player, world.getBlockEntity(pos))) {
                event.setCanceled(true);
            }
        }
    }
}
