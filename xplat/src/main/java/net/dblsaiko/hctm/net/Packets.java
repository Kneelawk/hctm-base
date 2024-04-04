package net.dblsaiko.hctm.net;

import net.dblsaiko.hctm.init.AbstractPlatform;
import net.dblsaiko.hctm.init.NetworkRegistry;

import static net.dblsaiko.hctm.HctmBase.MOD_ID;

public class Packets {
    private final NetworkRegistry reg = AbstractPlatform.getInstance().getNetworkRegistry();

//    public final ServerboundMsgDef<DebugNetRequest> debugNetRequest = this.reg.registerServerbound("debug_net_request", DebugNetRequest.CODEC);
//    public final ClientboundMsgDef<DebugNetResponse> debugNetResponse = this.reg.registerClientbound("debug_net_response", DebugNetResponse.CODEC);
}
