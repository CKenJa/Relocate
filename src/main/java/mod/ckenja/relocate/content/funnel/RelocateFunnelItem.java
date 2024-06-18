package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.FunnelItem;
import mod.ckenja.relocate.foundation.block.RelocateFunnelPlacementPacket;
import mod.ckenja.relocate.infrastructure.networking.RelocatePackets;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class RelocateFunnelItem extends FunnelItem {
    public RelocateFunnelItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @SubscribeEvent
    public static void funnelItemAlwaysPlacesWhenUsed(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack()
                .getItem() instanceof RelocateFunnelItem)
            event.setUseBlock(Event.Result.DENY);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        if (isInteractable(world.getBlockState(pos)))
            return InteractionResult.SUCCESS;
        return super.useOn(ctx);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, Level world, Player player, @NotNull ItemStack p_195943_4_,
                                                 @NotNull BlockState p_195943_5_) {
        if (!world.isClientSide && player instanceof ServerPlayer sp)
            RelocatePackets.channel.send(PacketDistributor.PLAYER.with(()->sp), new RelocateFunnelPlacementPacket.ClientBoundRequest(pos));
        return super.updateCustomBlockEntityTag(pos, world, player, p_195943_4_, p_195943_5_);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player p_195938_4_) {
        return !isInteractable(state);
    }

    public static boolean isInteractable(BlockState state){
        return isRelocateFunnelBLockEntity(state);
    }

    public static boolean isRelocateFunnelBLockEntity(BlockState blockState) {
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.has(blockState) || RelocateBlocks.RELOCATE_FUNNEL.has(blockState);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = super.getPlacementState(ctx);
        if (state == null)
            return state;
        if (!(state.getBlock() instanceof RelocateFunnelBlock))
            return state;
        if (state.getValue(RelocateFunnelBlock.FACING)
                .getAxis()
                .isVertical())
            return state;

        Direction direction = state.getValue(RelocateFunnelBlock.FACING);
        RelocateFunnelBlock block = (RelocateFunnelBlock) getBlock();
        Block beltFunnelBlock = block.getEquivalentBeltFunnel(world, pos, state)
                .getBlock();
        BlockState equivalentBeltFunnel = beltFunnelBlock.getStateForPlacement(ctx)
                .setValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING, direction);
        if (RelocateBeltFunnelBlock.isOnValidBelt(equivalentBeltFunnel, world, pos))
            return equivalentBeltFunnel;

        return state;
    }

}
