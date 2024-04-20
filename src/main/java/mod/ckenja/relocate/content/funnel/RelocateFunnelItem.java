package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.FunnelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
