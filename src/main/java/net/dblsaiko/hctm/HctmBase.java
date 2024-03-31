package net.dblsaiko.hctm;

import java.util.Objects;

import net.dblsaiko.hctm.common.wire.WireNetworkKt;
import net.dblsaiko.hctm.init.ItemGroups;
import net.dblsaiko.hctm.init.Items;
import net.dblsaiko.hctm.net.Packets;
import net.dblsaiko.hctm.net.ServerNetHandler;

public class HctmBase {
    public static final String MOD_ID = "hctm-base";

    private static HctmBase INSTANCE;

    public final Items items = new Items();
    public final ItemGroups itemGroups = new ItemGroups(this.items);

    public final Packets packets = new Packets();
    public final ServerNetHandler serverNetHandler = new ServerNetHandler(this.packets);

    private HctmBase() {}

    public static void initialize() {
        HctmBase mod = new HctmBase();
        mod.items.register();
        mod.itemGroups.register();
        mod.serverNetHandler.register();

        WireNetworkKt.getWIRE_NETWORK().register();

        INSTANCE = mod;
    }

    public static HctmBase getInstance() {
        return Objects.requireNonNull(INSTANCE);
    }
}
