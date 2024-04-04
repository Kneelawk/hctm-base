package net.dblsaiko.hctm.init;

import net.dblsaiko.hctm.init.net.ClientboundMsgDef;
import net.dblsaiko.hctm.init.net.ServerboundMsgDef;

import com.mojang.serialization.Codec;

public interface NetworkRegistry {
    <T> ServerboundMsgDef<T> registerServerbound(String name, Codec<T> codec);

    <T> ClientboundMsgDef<T> registerClientbound(String name, Codec<T> codec);
}
