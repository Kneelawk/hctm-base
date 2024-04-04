package net.dblsaiko.hctm.init;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractPlatform {
    private static final AbstractPlatform INSTANCE;

    static {
        AbstractPlatform instance = null;

        try {
            instance = (AbstractPlatform) Class.forName("net.dblsaiko.hctm.fabric.init.FabricPlatform").getConstructor()
                .newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException ignored) {
        }

        if (instance == null) {
            try {
                instance = (AbstractPlatform) Class.forName("net.dblsaiko.hctm.neoforge.init.NeoForgePlatform")
                    .getConstructor().newInstance();
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException ignored) {
            }
        }

        if (instance == null) {
            throw new RuntimeException("Unable to find HCTM-Base platform class");
        }

        INSTANCE = instance;
    }

    public static AbstractPlatform getInstance() {
        return INSTANCE;
    }

    public abstract boolean isClientEnvironment();

    public abstract ItemRegistry getItemRegistry();

    public abstract ItemGroupRegistry getItemGroupRegistry();
    
    public abstract NetworkRegistry getNetworkRegistry();
}
