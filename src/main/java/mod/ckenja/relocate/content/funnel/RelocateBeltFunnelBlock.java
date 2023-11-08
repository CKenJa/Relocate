package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RelocateBeltFunnelBlock extends BeltFunnelBlock {
    public RelocateBeltFunnelBlock(BlockEntry<? extends FunnelBlock> parent, Properties p_i48377_1_) {
        super(parent, p_i48377_1_);
    }
    public BlockEntityType<? extends RelocateFunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }
}
