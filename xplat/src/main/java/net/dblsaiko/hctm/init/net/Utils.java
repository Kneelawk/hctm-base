package net.dblsaiko.hctm.init.net;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.network.PacketByteBuf;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

public class Utils {
    public static <T> NbtElement toNbt(Codec<T> codec, T message) {
        DataResult<NbtElement> res = codec.encodeStart(NbtOps.INSTANCE, message);
        return res.getOrThrow(false, r -> {});
    }
    
    public static <T> PacketByteBuf prepareBuffer(Codec<T> codec, T message) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtElement el = toNbt(codec, message);
        buf.writeNbt(el);

        return buf;
    }
    
    public static <T> T fromNbt(Codec<T> codec, NbtElement element) {
        return codec.parse(NbtOps.INSTANCE, element).getOrThrow(false, e -> {});
    }

    public static <T> T readBuffer(Codec<T> codec, PacketByteBuf buf) {
        NbtElement read = buf.readNbt();

        return fromNbt(codec, read);
    }
}
