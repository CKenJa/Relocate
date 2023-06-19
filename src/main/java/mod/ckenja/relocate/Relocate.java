package mod.ckenja.relocate;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Relocate.ID)
public class Relocate {
    public static final String ID = "relocate";
    public static final String NAME = "Relocate";
    public static final String VERSION = "0.0.1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Relocate(){
    }
}
