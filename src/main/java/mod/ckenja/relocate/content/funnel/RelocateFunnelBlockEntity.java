package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.SHAPE;
import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.Shape;

@Mixin(FunnelBlockEntity.class)
public class RelocateFunnelBlockEntity extends FunnelBlockEntity {

    @Invoker("activateExtractor")
    void activateExtractor() {}

    @Invoker("activateExtractingBeltFunnel")
    void activateExtractingBeltFunnel() {}

    private int extractionCooldown;

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
}
