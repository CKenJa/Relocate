package mod.ckenja.relocate.content.filter;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelocateBoardEntity extends SmartBlockEntity {
    public ItemStack dataContainer;
    public RelocateBoardEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        dataContainer = RelocateBlocks.RELOCATE_BOARD.asStack();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
}
