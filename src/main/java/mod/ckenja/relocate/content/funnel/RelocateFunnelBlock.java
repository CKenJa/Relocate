package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RelocateFunnelBlock extends FunnelBlock {

    public static final BooleanProperty NEUTRAL = BooleanProperty.create("neutral");

    public RelocateFunnelBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(NEUTRAL, true));
    }

    public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
        Direction facing = getFacing(state);
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.getDefaultState()
                .setValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING, facing)
                .setValue(POWERED, state.getValue(POWERED));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NEUTRAL));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof RelocateBeltFunnelBlock bfb && bfb.isOfSameType(this))
            return;
        super.onRemove(state, world, pos, newState, isMoving);

    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                          BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(handIn);
        boolean shouldntInsertItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

        if (AllItems.WRENCH.isIn(heldItem))
            return InteractionResult.PASS;

        if (hit.getDirection() == getFunnelFacing(state) && !shouldntInsertItem) {
            if (!worldIn.isClientSide)
                withBlockEntityDo(worldIn, pos, be -> {
                    ItemStack toInsert = heldItem.copy();
                    ItemStack remainder = tryInsert(worldIn, pos, toInsert, false);
                    if (!ItemStack.matches(remainder, toInsert))
                        player.setItemInHand(handIn, remainder);
                });
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide)
            return;
        if (!(entityIn instanceof ItemEntity itemEntity))
            return;
        if (!entityIn.isAlive())
            return;

        Direction direction = getFunnelFacing(state);
        Vec3 diff = entityIn.position()
                .subtract(VecHelper.getCenterOf(pos)
                        .add(Vec3.atLowerCornerOf(direction.getNormal())
                                .scale(-.325f)));
        double projectedDiff = direction.getAxis()
                .choose(diff.x, diff.y, diff.z);
        if (projectedDiff < 0 == (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE))
            return;

        ItemStack toInsert = itemEntity.getItem();
        ItemStack remainder = tryInsert(worldIn, pos, toInsert, false);

        if (remainder.isEmpty())
            itemEntity.discard();
        if (remainder.getCount() < toInsert.getCount())
            itemEntity.setItem(remainder);
    }

    public BlockEntityType<? extends FunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }
}
