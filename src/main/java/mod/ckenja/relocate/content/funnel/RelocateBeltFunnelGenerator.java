package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.Create;
import mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.Shape;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;

public class RelocateBeltFunnelGenerator extends SpecialBlockStateGen {

    private String type;
    private ResourceLocation materialBlockTexture;

    public RelocateBeltFunnelGenerator(String type) {
        this.type = type;
        this.materialBlockTexture = Create.asResource("block/" + type + "_block");
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        String prefix = "block/funnel/";
        Shape shape = state.getValue(RelocateBeltFunnelBlock.SHAPE);
        String serializedShape = shape.getSerializedName();
        String shapeName = "perpendicular".equals(serializedShape) ? "pulling" : serializedShape;
        boolean powered = state.getOptionalValue(BlockStateProperties.POWERED)
                .orElse(false);
        String poweredSuffix = powered ? "_powered" : "_unpowered";
        String shapeSuffix = "_neutral";
        String name = ctx.getName() + "_" + serializedShape + poweredSuffix;

        return prov.models()
                .withExistingParent(name, Create.asResource("block/belt_funnel/block_" + shapeName))
                .texture("particle", materialBlockTexture)
                .texture("block", materialBlockTexture)
                .texture("direction", Create.asResource(prefix + type + "_funnel" + shapeSuffix))
                .texture("redstone", Create.asResource(prefix + type + "_funnel" + poweredSuffix))
                .texture("base", Create.asResource(prefix + type + "_funnel"));
    }

}
