package mod.ckenja.relocate;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import mod.ckenja.relocate.content.funnel.RelocateFunnelBlockEntity;
import mod.ckenja.relocate.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Relocate.ID)
public class Relocate {
    public static final String ID = "relocate";
    public static final String NAME = "Relocate";
    public static final Logger LOGGER = LogUtils.getLogger();

    //public static final RelocateSavedData RELOCATE_SAVED_DATA = new RelocateSavedData();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

    public Relocate(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RelocateItems.register();
        RelocateMenuTypes.register();
        RelocateBlocks.register();
        RelocateBlockEntityTypes.register();
        REGISTRATE.registerEventListeners(modEventBus);
        /*MinecraftForge.EVENT_BUS.addGenericListener(RelocateFunnelBlockEntity.class,
                (BlockEntityBehaviourEvent<RelocateFunnelBlockEntity> event) ->
                        event.attach(new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, event.getBlockEntity(), 2, 1000, true)));*/
    }
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(ID, path);
    }
}
