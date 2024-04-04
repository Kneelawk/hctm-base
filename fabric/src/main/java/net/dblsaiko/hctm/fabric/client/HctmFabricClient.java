package net.dblsaiko.hctm.fabric.client;

import net.dblsaiko.hctm.client.HctmBaseClient;
import net.dblsaiko.hctm.fabric.HctmFabric;

public class HctmFabricClient {
    public static void initialize() {
        HctmBaseClient.initialize();

        HctmFabric.getInstance().networkRegistry.registerClientHandlers();
    }
}
