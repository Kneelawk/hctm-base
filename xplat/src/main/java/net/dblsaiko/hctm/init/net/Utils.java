package net.dblsaiko.hctm.init.net;

import io.netty.buffer.Unpooled;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;

public class Utils {
    public static <T> NbtElement toNbt(Codec<T> codec, T message) {
        DataResult<NbtElement> res = codec.encodeStart(NbtOps.INSTANCE, message);
        return res.getPartialOrThrow();
    }

    public static <T> PacketByteBuf prepareBuffer(Codec<T> codec, T message) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtElement el = toNbt(codec, message);
        buf.writeNbt(el);

        return buf;
    }

    public static <T> T fromNbt(Codec<T> codec, NbtElement element) {
        return codec.parse(NbtOps.INSTANCE, element).getPartialOrThrow();
    }

    public static <T> T readBuffer(Codec<T> codec, PacketByteBuf buf) {
        NbtElement read = buf.readNbt();

        return fromNbt(codec, read);
    }
}
