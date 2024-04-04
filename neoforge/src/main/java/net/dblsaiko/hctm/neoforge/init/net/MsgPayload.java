package net.dblsaiko.hctm.neoforge.init.net;

import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MsgPayload(Identifier id, NbtElement element) implements CustomPayload {
    public MsgPayload(Identifier id, PacketByteBuf buf) {
        this(id, buf.readNbt());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(element);
    }

    @Override
    public Identifier id() {
        return id;
    }
}
