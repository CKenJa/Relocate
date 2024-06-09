package mod.ckenja.relocate.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import mod.ckenja.relocate.Relocate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public class RelocateCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Relocate.ID);
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = REGISTER.register("main", ()-> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.relocate"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(RelocateBlocks.RELOCATE_FUNNEL::asStack)
            .displayItems((params,output)-> output.acceptAll(Stream.of(
                    RelocateBlocks.RELOCATE_FUNNEL
            ).map(ItemProviderEntry::asStack).toList()))
            .build());
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
