package mod.ckenja.relocate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import mod.ckenja.relocate.content.funnel.RelocateFunnelBlockEntity;
import mod.ckenja.relocate.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.BiConsumer;

@Mod(Relocate.ID)
public class Relocate {
    public static final String ID = "relocate";
    public static final String NAME = "Relocate";
    public static final Logger LOGGER = LogUtils.getLogger();

    //public static final RelocateSavedData RELOCATE_SAVED_DATA = new RelocateSavedData();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE));
    }

    public Relocate(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modEventBus);

        RelocateBlocks.register();
        //RelocateItems.register();
        //RelocateMenuTypes.register();
        RelocateBlockEntityTypes.register();
        RelocateCreativeModeTabs.register(modEventBus);
        MinecraftForge.EVENT_BUS.addGenericListener(RelocateFunnelBlockEntity.class,RelocateFunnelBlockEntity::handleBlockEntityBehaviourEvent);
        REGISTRATE.addDataGenerator(ProviderType.LANG, prov->{
            provideDefaultLang("interface", prov::add);
            provideDefaultLang("tooltips", prov::add);
        });
    }

    public static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer){
        String path = "assets/relocate/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(ID, path);
    }
}
