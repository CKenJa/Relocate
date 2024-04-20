package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.*;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.lang.ref.WeakReference;
import java.util.List;

import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.SHAPE;
import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.Shape;

public class RelocateFunnelBlockEntity extends FunnelBlockEntity {
    private int extractionCooldown;
    private InvManipulationBehaviour invManipulation;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private FilteringBehaviour filtering;

    private WeakReference<ItemEntity> lastObserved; // In-world Extractors only

    //独自Mode
    enum Mode {
        INVALID, PAUSED, PUSHING_TO_BELT, TAKING_FROM_BELT, NEUTRAL_WITH_BELT, NEUTRAL
    }

    public RelocateFunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        extractionCooldown = 0;
    }

    public Mode determineCurrentRelocateMode(){
        BlockState state = getBlockState();
        if (!RelocateFunnelBlock.isFunnel(state))
            return Mode.INVALID;
        if (state.getOptionalValue(BlockStateProperties.POWERED)
                .orElse(false))
            return Mode.PAUSED;
        if (state.getBlock() instanceof RelocateBeltFunnelBlock) {
            Shape shape = state.getValue(SHAPE);
            if(shape == Shape.PERPENDICULAR)
                return Mode.NEUTRAL_WITH_BELT;

            BeltBlockEntity belt = BeltHelper.getSegmentBE(level, worldPosition.below());
            if (belt != null)
                return belt.getMovementFacing() == state.getValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING)
                        ? Mode.PUSHING_TO_BELT
                        : Mode.TAKING_FROM_BELT;
            return Mode.INVALID;
        }
        if (state.getBlock() instanceof RelocateFunnelBlock)
            return Mode.NEUTRAL;
        return Mode.INVALID;
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            return;
        Mode mode = determineCurrentRelocateMode();
		if (level.isClientSide)
			return;
		if (mode == Mode.PAUSED)
			extractionCooldown = 0;
		if (mode == Mode.TAKING_FROM_BELT)
			return;

		if (extractionCooldown > 0) {
			extractionCooldown--;
			return;
		}
		if (mode == Mode.PUSHING_TO_BELT || mode == Mode.NEUTRAL_WITH_BELT)
			activateExtractingBeltFunnel();
		if (mode == Mode.NEUTRAL)
			activateExtractor();
    }
    //Behavioursいじり

    private void activateExtractor() {
        if (invVersionTracker.stillWaiting(invManipulation))
            return;

        BlockState blockState = getBlockState();
        Direction facing = AbstractFunnelBlock.getFunnelFacing(blockState);

        if (facing == null)
            return;

        boolean trackingEntityPresent = true;
        AABB area = getEntityOverflowScanningArea();

        // Check if last item is still blocking the extractor
        if (lastObserved == null) {
            trackingEntityPresent = false;
        } else {
            ItemEntity lastEntity = lastObserved.get();
            if (lastEntity == null || !lastEntity.isAlive() || !lastEntity.getBoundingBox()
                    .intersects(area)) {
                trackingEntityPresent = false;
                lastObserved = null;
            }
        }

        if (trackingEntityPresent)
            return;

        // Find other entities blocking the extract (only if necessary)
        int amountToExtract = getAmountToExtract();
        ItemHelper.ExtractionCountMode mode = getModeToExtract();
        ItemStack stack = invManipulation.simulate()
                .extract(mode, amountToExtract);
        if (stack.isEmpty()) {
            invVersionTracker.awaitNewVersion(invManipulation);
            return;
        }
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, area)) {
            lastObserved = new WeakReference<>(itemEntity);
            return;
        }

        // Extract
        stack = invManipulation.extract(mode, amountToExtract);
        if (stack.isEmpty())
            return;

        flap(false);
        onTransfer(stack);

        Vec3 outputPos = VecHelper.getCenterOf(worldPosition);
        boolean vertical = facing.getAxis()
                .isVertical();
        boolean up = facing == Direction.UP;

        outputPos = outputPos.add(Vec3.atLowerCornerOf(facing.getNormal())
                .scale(vertical ? up ? .15f : .5f : .25f));
        if (!vertical)
            outputPos = outputPos.subtract(0, .45f, 0);

        Vec3 motion = Vec3.ZERO;
        if (up)
            motion = new Vec3(0, 4 / 16f, 0);

        ItemEntity item = new ItemEntity(level, outputPos.x, outputPos.y, outputPos.z, stack.copy());
        item.setDefaultPickUpDelay();
        item.setDeltaMovement(motion);
        level.addFreshEntity(item);
        lastObserved = new WeakReference<>(item);

        startCooldown();
    }

    static final AABB coreBB = new AABB(VecHelper.CENTER_OF_ORIGIN, VecHelper.CENTER_OF_ORIGIN).inflate(.75f);

    private AABB getEntityOverflowScanningArea() {
        Direction facing = AbstractFunnelBlock.getFunnelFacing(getBlockState());
        AABB bb = coreBB.move(worldPosition);
        if (facing == null || facing == Direction.UP)
            return bb;
        return bb.expandTowards(0, -1, 0);
    }

    private void activateExtractingBeltFunnel() {
        if (invVersionTracker.stillWaiting(invManipulation))
            return;

        BlockState blockState = getBlockState();
        Direction facing = blockState.getValue(BeltFunnelBlock.HORIZONTAL_FACING);
        DirectBeltInputBehaviour inputBehaviour =
                BlockEntityBehaviour.get(level, worldPosition.below(), DirectBeltInputBehaviour.TYPE);

        if (inputBehaviour == null)
            return;
        if (!inputBehaviour.canInsertFromSide(facing))
            return;
        if (inputBehaviour.isOccupied(facing))
            return;

        int amountToExtract = getAmountToExtract();
        ItemHelper.ExtractionCountMode mode = getModeToExtract();
        MutableBoolean deniedByInsertion = new MutableBoolean(false);
        ItemStack stack = invManipulation.extract(mode, amountToExtract, s -> {
            ItemStack handleInsertion = inputBehaviour.handleInsertion(s, facing, true);
            if (handleInsertion.isEmpty())
                return true;
            deniedByInsertion.setTrue();
            return false;
        });
        if (stack.isEmpty()) {
            if (deniedByInsertion.isFalse())
                invVersionTracker.awaitNewVersion(invManipulation.getInventory());
            return;
        }
        flap(false);
        onTransfer(stack);
        inputBehaviour.handleInsertion(stack, facing, false);
        startCooldown();
    }

    private int startCooldown() {
        return extractionCooldown = AllConfigs.server().logistics.defaultExtractionTimer.get();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        invManipulation =
                new InvManipulationBehaviour(this, (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s)
                        .getOpposite()));
        behaviours.add(invManipulation);

        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));

        filtering = new FilteringBehaviour(this, new FunnelFilterSlotPositioning());
        filtering.showCountWhen(()->true);
        filtering.onlyActiveWhen(()->true);
        filtering.withCallback($ -> invVersionTracker.reset());
        behaviours.add(filtering);

        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput)
                .setInsertionHandler(this::handleDirectBeltInput));
        registerAwardables(behaviours, AllAdvancements.FUNNEL);
    }

    private boolean supportsDirectBeltInput(Direction side) {
        BlockState blockState = getBlockState();
        if (blockState == null)
            return false;
        if (!(blockState.getBlock() instanceof FunnelBlock))
            return false;
        if (blockState.getValue(FunnelBlock.EXTRACTING))
            return false;
        return FunnelBlock.getFunnelFacing(blockState) == Direction.UP;
    }

    private ItemStack handleDirectBeltInput(TransportedItemStack stack, Direction side, boolean simulate) {
        ItemStack inserted = stack.stack;
        if (!filtering.test(inserted))
            return inserted;
        if (determineCurrentRelocateMode() == Mode.PAUSED)
            return inserted;
        if (simulate)
            invManipulation.simulate();
        if (!simulate)
            onTransfer(inserted);
        return invManipulation.insert(inserted);
    }
}
