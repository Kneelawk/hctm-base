package net.dblsaiko.hctm.client;

import java.util.Objects;

import net.dblsaiko.hctm.HctmBase;
import net.dblsaiko.hctm.net.ClientNetHandler;

public class HctmBaseClient {
    private static HctmBaseClient INSTANCE;

    public final ClientNetHandler clientNetHandler;

    private HctmBaseClient(HctmBase b) {
        this.clientNetHandler = new ClientNetHandler(b.packets);
    }

    public static void initialize() {
        HctmBaseClient client = new HctmBaseClient(HctmBase.getInstance());
        client.clientNetHandler.register();
        INSTANCE = client;
    }

    public static HctmBaseClient getInstance() {
        return Objects.requireNonNull(INSTANCE);
    }
}
