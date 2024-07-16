package net.dblsaiko.hctm.init.net;

import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MsgPayload(Identifier id, NbtElement element) implements CustomPayload {
    public static Id<MsgPayload> id(Identifier id) {
        return new Id<>(id);
    }

    public static PacketCodec<PacketByteBuf, MsgPayload> codec(Identifier id) {
        return PacketCodec.ofStatic((buf, payload) -> buf.writeNbt(payload.element()),
            buf -> new MsgPayload(id, buf.readNbt()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return id(id);
    }
}
