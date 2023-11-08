package mod.ckenja.relocate.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.logistics.funnel.BeltFunnelGenerator;
import com.simibubi.create.content.logistics.funnel.FunnelGenerator;
import com.simibubi.create.content.logistics.funnel.FunnelItem;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import mod.ckenja.relocate.content.filter.RelocateBoardBlock;
import mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock;
import mod.ckenja.relocate.content.funnel.RelocateFunnelBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static mod.ckenja.relocate.Relocate.REGISTRATE;
public class RelocateBlocks {
    public static final BlockEntry<RelocateBoardBlock> RELOCATE_BOARD = REGISTRATE.block("relocate_board", RelocateBoardBlock::new)
            .lang("Relocate Board")
            .register();
    public static final BlockEntry<RelocateFunnelBlock> RELOCATE_FUNNEL =
            REGISTRATE.block("relocate_funnel", RelocateFunnelBlock::new)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.color(MaterialColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .blockstate(new FunnelGenerator("industrial_iron", true)::generate)
                    .item(FunnelItem::new)
                    .model(FunnelGenerator.itemModel("industrial_iron"))
                    .build()
                    .register();

    public static final BlockEntry<RelocateBeltFunnelBlock> RELOCATE_BELT_FUNNEL =
            REGISTRATE.block("relocate_belt_funnel", p -> new RelocateBeltFunnelBlock(RelocateBlocks.RELOCATE_FUNNEL, p))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.color(MaterialColor.COLOR_GRAY)
                            .sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .blockstate(new BeltFunnelGenerator("industrial_iron")::generate)
                    .loot((p, b) -> p.dropOther(b, RELOCATE_FUNNEL.get()))
                    .lang("Relocate Belt Funnel")
                    .register();
    public static void register(){
        REGISTRATE.creativeModeTab(()-> AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }
}
