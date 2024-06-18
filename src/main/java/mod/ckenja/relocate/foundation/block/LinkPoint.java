package mod.ckenja.relocate.foundation.block;

import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LinkPoint {
    Level level;
    BlockPos pos;
    BlockState cachedState;
    public LinkPoint(Level level, BlockPos pos){
        this.level=level;
        this.pos=pos;
    }

    public void updateCachedState() {
        cachedState = level.getBlockState(pos);
    }

    public final CompoundTag serialize(BlockPos anchor) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Pos", NbtUtils.writeBlockPos(pos.subtract(anchor)));
        return nbt;
    }

    public static LinkPoint deserialize(CompoundTag nbt, Level level, BlockPos anchor) {
        BlockPos pos = NbtUtils.readBlockPos(nbt.getCompound("Pos")).offset(anchor);
        return new LinkPoint(level, pos);
    }


    public boolean isValid() {
        updateCachedState();
        return RelocateBlocks.RELOCATE_FUNNEL.has(cachedState);
    }
}
