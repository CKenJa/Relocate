package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.foundation.networking.BlockEntityDataPacket;

import net.minecraft.network.FriendlyByteBuf;

public class RelocateFunnelFlapPacket extends BlockEntityDataPacket<RelocateFunnelBlockEntity> {

    private final boolean inwards;

    public RelocateFunnelFlapPacket(FriendlyByteBuf buffer) {
        super(buffer);

        inwards = buffer.readBoolean();
    }

    public RelocateFunnelFlapPacket(RelocateFunnelBlockEntity blockEntity, boolean inwards) {
        super(blockEntity.getBlockPos());
        this.inwards = inwards;
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(inwards);
    }

    @Override
    protected void handlePacket(RelocateFunnelBlockEntity blockEntity) {
        blockEntity.flap(inwards);
    }
}
