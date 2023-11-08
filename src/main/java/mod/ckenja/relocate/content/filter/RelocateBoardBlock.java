package mod.ckenja.relocate.content.filter;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RelocateBoardBlock extends FaceAttachedHorizontalDirectionalBlock
implements IBE<RelocateBoardEntity>, IWrenchable, ProperWaterloggedBlock {
    public RelocateBoardBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<RelocateBoardEntity> getBlockEntityClass() {
        return null;
    }

    @Override
    public BlockEntityType<? extends RelocateBoardEntity> getBlockEntityType() {
        return null;
    }
}
