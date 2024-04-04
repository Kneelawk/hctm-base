package net.dblsaiko.hctm.init.net;

public interface ServerboundMsgDef<T> {
    void bind(ServerMessageHandler<T> handler);

    ServerboundMsgSender<T> sender();
}
