package net.dblsaiko.hctm.net;

public class ClientNetHandler {
    private final Packets packets;

//    public final ServerboundMsgSender<DebugNetRequest> debugNetRequest;

    public ClientNetHandler(Packets packets) {
        this.packets = packets;

//        this.debugNetRequest = packets.debugNetRequest.sender();
//        packets.debugNetResponse.bind(this::handleDebugNetResponse);
    }

//    private void handleDebugNetResponse(MinecraftClient client, ClientPlayNetworkHandler handler, DebugNetResponse msg, PacketSender responseSender) {
//        var world = RegistryKey.of(RegistryKeys.WORLD, msg.world());
//        client.execute(() -> ClientNetworkState.INSTANCE.update(world, msg.data()));
//    }

    public void register() {
        this.packets.registerClientHandlers();
    }
}
