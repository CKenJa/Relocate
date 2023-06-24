package mod.ckenja.relocate;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import mod.ckenja.relocate.registry.RelocateItems;
import mod.ckenja.relocate.registry.RelocateMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Relocate.ID)
public class Relocate {
    public static final String ID = "relocate";
    public static final String NAME = "Relocate";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = NonNullSupplier.lazy(() -> CreateRegistrate.create(ID));

    public Relocate(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RelocateItems.register();
        RelocateMenuTypes.register();
        registrate().registerEventListeners(modEventBus);
    }
    public static CreateRegistrate registrate(){
        return REGISTRATE.get();
    }
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(ID, path);
    }
}
