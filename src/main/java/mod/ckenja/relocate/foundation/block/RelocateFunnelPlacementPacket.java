package mod.ckenja.relocate.foundation.block;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import mod.ckenja.relocate.content.funnel.RelocateFunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;

public class RelocateFunnelPlacementPacket extends SimplePacketBase {
    private Collection<LinkPoint> points;
    private ListTag receivedTag;
    private final BlockPos pos;

    public RelocateFunnelPlacementPacket(Collection<LinkPoint> points, BlockPos pos) {
        this.points = points;
        this.pos = pos;
    }

    public RelocateFunnelPlacementPacket(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        receivedTag = nbt.getList("Points", Tag.TAG_COMPOUND);
        pos = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        CompoundTag nbt = new CompoundTag();
        ListTag pointsNBT = new ListTag();
        points.stream()
                .map(point -> point.serialize(pos))
                .forEach(pointsNBT::add);
        nbt.put("Points", pointsNBT);
        buffer.writeNbt(nbt);
        buffer.writeBlockPos(pos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            Level world = player.level();
            if (world == null || !world.isLoaded(pos))
                return;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof RelocateFunnelBlockEntity funnel))
                return;
            funnel.linkPointTag = receivedTag;
        });
        return true;
    }

    public static class ClientBoundRequest extends SimplePacketBase {

        BlockPos pos;

        public ClientBoundRequest(BlockPos pos) {
            this.pos = pos;
        }

        public ClientBoundRequest(FriendlyByteBuf buffer) {
            this.pos = buffer.readBlockPos();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeBlockPos(pos);
        }

        @Override
        public boolean handle(NetworkEvent.Context context) {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> LinkHandler.flushSettings(pos)));
            return true;
        }

    }


}
