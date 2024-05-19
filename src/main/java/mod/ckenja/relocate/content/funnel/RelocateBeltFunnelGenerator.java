package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;
import static com.simibubi.create.content.logistics.funnel.BeltFunnelBlock.Shape;

public class RelocateBeltFunnelGenerator extends SpecialBlockStateGen {

    private final String type;
    private final ResourceLocation materialBlockTexture;

    public RelocateBeltFunnelGenerator(String type, ResourceLocation materialBlockTexture) {
        this.type = type;
        this.materialBlockTexture = materialBlockTexture;
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
        String neutral = state.getValue(RelocateFunnelBlock.NEUTRAL) ? "_neutral" : "_unneutral";
        String shapeName = shape.getSerializedName();
        boolean powered = state.getOptionalValue(BlockStateProperties.POWERED)
                .orElse(false);
        String poweredSuffix = powered ? "_powered" : "_unpowered";
        String shapeSuffix = state.getValue(RelocateFunnelBlock.NEUTRAL) ? "_dual" : shape == Shape.PULLING ? "_pull" : shape == Shape.PUSHING ? "_push" : "_neutral";
        String name = ctx.getName() + "_" + shapeName + neutral + poweredSuffix;

        return prov.models()
                .withExistingParent(name, Create.asResource("block/belt_funnel/block_" + shapeName))
                .texture("particle", materialBlockTexture)
                .texture("block", materialBlockTexture)
                .texture("direction", prov.modLoc(prefix + type + "_funnel" + shapeSuffix))
                .texture("redstone", prov.modLoc(prefix + type + "_funnel" + poweredSuffix))
                .texture("base", prov.modLoc(prefix + type + "_funnel"));
    }

}
