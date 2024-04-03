package net.dblsaiko.hctm.fabric.init;

import net.dblsaiko.hctm.init.RegistryObject;

public interface InternalRegistryObject<T> extends RegistryObject<T> {
    void register();

    void unregister();
}
