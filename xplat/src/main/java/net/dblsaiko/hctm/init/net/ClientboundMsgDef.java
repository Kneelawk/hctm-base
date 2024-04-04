package net.dblsaiko.hctm.init.net;

public interface ClientboundMsgDef<T> {
    void bind(ClientMessageHandler<T> handler);

    ClientboundMsgSender<T> sender();
}
