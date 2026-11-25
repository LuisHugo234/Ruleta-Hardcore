package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FrogzillaEscapeTapPayload() implements CustomPacketPayload {

    public static final FrogzillaEscapeTapPayload INSTANCE = new FrogzillaEscapeTapPayload();

    public static final CustomPacketPayload.Type<FrogzillaEscapeTapPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("frogzilla_escape_tap"));

    public static final StreamCodec<ByteBuf, FrogzillaEscapeTapPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {},
            buf -> INSTANCE
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
