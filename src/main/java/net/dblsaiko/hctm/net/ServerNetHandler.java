package net.dblsaiko.hctm.net;

public class ServerNetHandler {
    private final Packets packets;

//    public final ClientboundMsgSender<DebugNetResponse> debugNetResponse;

    public ServerNetHandler(Packets packets) {
        this.packets = packets;

//        packets.debugNetRequest.bind(this::handleDebugNetRequest);
//        this.debugNetResponse = packets.debugNetResponse.sender();
    }

//    private void handleDebugNetRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, DebugNetRequest msg, PacketSender responseSender) {
//        var worldKey = RegistryKey.of(RegistryKeys.WORLD, msg.world());
//        var world = server.getWorld(worldKey);
//
//        if (world == null) {
//            return;
//        }
//
//        var wns = WireNetworkKt.getWireNetworkState(world);
//        var nbt = wns.getController().toTag(world);
//
//        this.debugNetResponse.send(responseSender, new DebugNetResponse(msg.world(), nbt));
//    }

    public void register() {
        this.packets.registerServerHandlers();
    }
}
