package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.SoftOverride;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.ref.WeakReference;

import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.SHAPE;
import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.Shape;

@Mixin(FunnelBlockEntity.class)
public class RelocateFunnelBlockEntity extends FunnelBlockEntity {

    @Invoker("activateExtractor")
    void activateExtractor() {}

    @Invoker("activateExtractingBeltFunnel")
    void activateExtractingBeltFunnel() {}

    @Invoker("handleDirectBeltInput")
    ItemStack handleDirectBeltInput(TransportedItemStack transportedItemStack, Direction direction, boolean b){return null;};

    private FilteringBehaviour filtering;
    private InvManipulationBehaviour invManipulation;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private int extractionCooldown;

    private WeakReference<ItemEntity> lastObserved; // In-world Extractors only

    //独自Mode
    public enum Mode {
        INVALID, PAUSED, PUSHING_TO_BELT, TAKING_FROM_BELT, NEUTRAL_WITH_BELT, COLLECT, EXTRACT, NEUTRAL
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
            if (shape == Shape.PULLING)
                return Mode.TAKING_FROM_BELT;
            if (shape == Shape.PUSHING)
                return Mode.PUSHING_TO_BELT;
            if(shape == Shape.NEUTRAL) //change point, have NEUTRAL shape in belt funnel
                return Mode.NEUTRAL_WITH_BELT;

            BeltBlockEntity belt = BeltHelper.getSegmentBE(level, worldPosition.below());
            if (belt != null)
                return belt.getMovementFacing() == state.getValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING)
                        ? Mode.PUSHING_TO_BELT
                        : Mode.TAKING_FROM_BELT;
            return Mode.INVALID;
        }
        if (state.getBlock() instanceof RelocateFunnelBlock)
            return Mode.NEUTRAL;//TODO: ほかのモードも
        return Mode.INVALID;
    }

    @Override
    public void tick() {
        super.tick();
        Mode mode = determineCurrentRelocateMode();
        if (level.isClientSide)
            return;

        // Redstone resets the extraction cooldown
        if (mode == Mode.PAUSED)
            extractionCooldown = 0;
        if (mode == Mode.TAKING_FROM_BELT)
            return;

        if (extractionCooldown > 0) {
            extractionCooldown--;
            return;
        }

		if (mode == Mode.PUSHING_TO_BELT || mode == Mode.NEUTRAL_WITH_BELT)//change point
			activateExtractingBeltFunnel();
		if (mode == Mode.NEUTRAL)//change point
			activateExtractor();
    }

    private boolean supportsDirectBeltInput(Direction side) {
        BlockState blockState = getBlockState();
        if (blockState == null)
            return false;
        if (!(blockState.getBlock() instanceof RelocateFunnelBlock))
            return false;
        if (blockState.getValue(RelocateFunnelBlock.SHAPE) != RelocateFunnelBlock.Shape.EXTRACT)
            return false;
        return RelocateFunnelBlock.getFunnelFacing(blockState) == Direction.UP;
    }
}
