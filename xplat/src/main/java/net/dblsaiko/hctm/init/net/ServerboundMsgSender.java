package net.dblsaiko.hctm.init.net;

public interface ServerboundMsgSender<T> {
    void send(T message);

    void send(PacketSender sender, T message);
}
