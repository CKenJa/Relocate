package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.funnel.AbstractDirectionalFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RelocateFunnelBlock extends FunnelBlock {

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public enum Shape implements StringRepresentable {
        NEUTRAL,
        EXTRACT,
        COLLECT;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

    public RelocateFunnelBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SHAPE, Shape.NEUTRAL));
    }

    public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
        Direction facing = getFacing(state);
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.getDefaultState()
                .setValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING, facing)
                .setValue(POWERED, state.getValue(POWERED));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate = state.setValue(FACING, direction.getOpposite());
            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos()))
                return blockstate.setValue(POWERED, state.getValue(POWERED));
        }

        return state;
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
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        ItemEntity itemEntity = (ItemEntity) entityIn;

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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return facing == Direction.DOWN ? AllShapes.FUNNEL_CEILING
                : facing == Direction.UP ? AllShapes.FUNNEL_FLOOR : AllShapes.FUNNEL_WALL.get(facing);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext
                && ((EntityCollisionContext) context).getEntity() instanceof ItemEntity && getFacing(state).getAxis()
                .isHorizontal())
            return AllShapes.FUNNEL_COLLISION.get(getFacing(state));
        return getShape(state, world, pos, context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState p_196271_3_, LevelAccessor world,
                                  BlockPos pos, BlockPos p_196271_6_) {
        updateWater(world, state, pos);
        if (getFacing(state).getAxis()
                .isVertical() || direction != Direction.DOWN)
            return state;
        BlockState equivalentFunnel =
                ProperWaterloggedBlock.withWater(world, getEquivalentBeltFunnel(null, null, state), pos);
        if (RelocateBeltFunnelBlock.isOnValidBelt(equivalentFunnel, world, pos))
            return equivalentFunnel.setValue(RelocateBeltFunnelBlock.SHAPE,
                    RelocateBeltFunnelBlock.getShapeForPosition(world, pos, getFacing(state), state.getValue(RelocateBeltFunnelBlock.SHAPE)));
        return state;
    }

    public BlockEntityType<? extends FunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }
}
