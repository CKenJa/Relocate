package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class RelocateFunnelGenerator extends SpecialBlockStateGen {

    private String type;
    private ResourceLocation blockTexture;
    private boolean hasFilter;

    public RelocateFunnelGenerator(String type, boolean hasFilter) {
        this.type = type;
        this.hasFilter = hasFilter;
        this.blockTexture = Create.asResource("block/" + type + "_block");
    }

    @Override
    protected int getXRotation(BlockState state) {
        return state.getValue(RelocateFunnelBlock.FACING) == Direction.DOWN ? 180 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(RelocateFunnelBlock.FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> c, RegistrateBlockstateProvider p,
                                                BlockState s) {
        String prefix = "block/funnel/";
        String powered = s.getValue(RelocateFunnelBlock.POWERED) ? "_powered" : "_unpowered";
        String closed = s.getValue(RelocateFunnelBlock.POWERED) ? "_closed" : "_open";
        Direction facing = s.getValue(RelocateFunnelBlock.FACING);
        boolean horizontal = facing.getAxis()
                .isHorizontal();
        String parent = horizontal ? "horizontal" : hasFilter ? "vertical" : "vertical_filterless";

        BlockModelBuilder model = p.models()
                .withExistingParent("block/" + type + "_funnel_" + parent + powered,
                        Create.asResource(prefix + "block_" + parent))
                .texture("particle", blockTexture)
                .texture("base", Create.asResource(prefix + type + "_funnel"))
                .texture("redstone", Create.asResource(prefix + type + "_funnel" + powered))
                .texture("direction", Create.asResource(prefix + type + "_funnel" + "_neutral"));

        if (horizontal)
            return model.texture("block", blockTexture);

        return model.texture("frame", Create.asResource(prefix + type + "_funnel_frame"))
                .texture("open", Create.asResource(prefix + "funnel" + closed));
    }

    public static NonNullBiConsumer<DataGenContext<Item, RelocateFunnelItem>, RegistrateItemModelProvider> itemModel(
            String type) {
        String prefix = "block/funnel/";
        ResourceLocation blockTexture = Create.asResource("block/" + type + "_block");
        return (c, p) -> {
            p.withExistingParent("item/" + "relocate" + "_funnel", Create.asResource("block/funnel/item"))
                    .texture("particle", blockTexture)
                    .texture("block", blockTexture)
                    .texture("base", Create.asResource(prefix + type + "_funnel"))
                    .texture("direction", Create.asResource(prefix + type + "_funnel_neutral"))
                    .texture("redstone", Create.asResource(prefix + type + "_funnel_unpowered"));
        };
    }

}
