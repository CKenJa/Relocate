package mod.ckenja.relocate.infrastructure.networking;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import mod.ckenja.relocate.foundation.block.RelocateFunnelPlacementPacket;
import mod.ckenja.relocate.Relocate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum RelocatePackets {
    //Client to Server
    PLACE_RELOCATE_FUNNEL(RelocateFunnelPlacementPacket.class, RelocateFunnelPlacementPacket::new, PLAY_TO_SERVER),

    //Server to Client
    S_PLACE_EJECTOR(RelocateFunnelPlacementPacket.ClientBoundRequest.class, RelocateFunnelPlacementPacket.ClientBoundRequest::new,
                    PLAY_TO_CLIENT),
    ;

    public static final int NETWORK_VERSION = 1;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    public static SimpleChannel channel;

    private final PacketType<?> packetType;

    <T extends SimplePacketBase> RelocatePackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                                 NetworkDirection direction) {
        packetType = new RelocatePackets.PacketType<>(type, factory, direction);
    }
    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(Relocate.resource("main"))
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        for (RelocatePackets packet : values())
            packet.packetType.register();
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;
        private final NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            channel.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }
}
