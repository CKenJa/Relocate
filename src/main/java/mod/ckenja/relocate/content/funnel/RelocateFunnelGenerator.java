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

    private final String type;
    private final ResourceLocation blockTexture;

    public RelocateFunnelGenerator(String type, ResourceLocation blockTexture) {
        this.type = type;
        this.blockTexture = blockTexture;
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
        String extracting = s.getValue(RelocateFunnelBlock.EXTRACTING) ? "_push" : "_pull";
        String neutral = s.getValue(RelocateFunnelBlock.NEUTRAL) ? "_neutral" : "_unneutral";
        Direction facing = s.getValue(RelocateFunnelBlock.FACING);
        boolean horizontal = facing.getAxis().isHorizontal();
        String parent = horizontal ? "horizontal" : "vertical";
        BlockModelBuilder model = p.models()
                .withExistingParent("block/" + type + "_funnel_" + parent + extracting + neutral + powered,
                        Create.asResource(prefix + "block_" + parent))
                .texture("particle", blockTexture)
                .texture("base", p.modLoc(prefix + type + "_funnel"))
                .texture("redstone", p.modLoc(prefix + type + "_funnel" + powered))
                .texture("direction", p.modLoc(prefix + type + "_funnel" + "_neutral"));

        if (horizontal)
            return model.texture("block", blockTexture);

        return model.texture("frame", p.modLoc(prefix + type + "_funnel_frame"))
                .texture("open", Create.asResource(prefix + "funnel" + closed));
    }

    public static NonNullBiConsumer<DataGenContext<Item, RelocateFunnelItem>, RegistrateItemModelProvider> itemModel(
            String type, ResourceLocation blockTexture) {
        String prefix = "block/funnel/";
        return (c, p) -> p.withExistingParent("item/" + "relocate" + "_funnel", Create.asResource("block/funnel/item"))
                .texture("particle", blockTexture)
                .texture("block", blockTexture)
                .texture("base", p.modLoc(prefix + type + "_funnel"))
                .texture("direction", p.modLoc(prefix + type + "_funnel_neutral"))
                .texture("redstone", p.modLoc(prefix + type + "_funnel_unpowered"));
    }

}
