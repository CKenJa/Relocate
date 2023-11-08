package mod.ckenja.relocate.registry;

import com.simibubi.create.content.logistics.funnel.FunnelInstance;
import com.simibubi.create.content.logistics.funnel.FunnelRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import mod.ckenja.relocate.content.funnel.RelocateFunnelBlockEntity;

import static mod.ckenja.relocate.Relocate.REGISTRATE;

public class RelocateBlockEntityTypes {
    public static final BlockEntityEntry<RelocateFunnelBlockEntity> RELOCATE_FUNNEL = REGISTRATE
            .blockEntity("relocate_funnel", RelocateFunnelBlockEntity::new)
            .instance(() -> FunnelInstance::new)
            .validBlocks(RelocateBlocks.RELOCATE_FUNNEL, RelocateBlocks.RELOCATE_BELT_FUNNEL)
            .renderer(() -> FunnelRenderer::new)
            .register();
    public static void register() {
    }
}
