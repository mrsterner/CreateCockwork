package dev.sterner.createcockwork;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mod(CreateCockwork.MODID)
public class CreateCockwork {

    public static final String MODID = "createcockwork";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


    public CreateCockwork() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;


        MinecraftForge.EVENT_BUS.register(this);
    }
}
