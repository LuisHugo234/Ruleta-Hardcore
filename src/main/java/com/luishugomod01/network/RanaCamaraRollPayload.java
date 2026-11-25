package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RanaCamaraRollPayload(
        float targetRollDegrees,
        int durationTicks
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RanaCamaraRollPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("rana_camara_roll"));

    public static final StreamCodec<ByteBuf, RanaCamaraRollPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeFloat(payload.targetRollDegrees());
                ByteBufCodecs.VAR_INT.encode(buf, payload.durationTicks());
            },
            buf -> new RanaCamaraRollPayload(
                    buf.readFloat(),
                    ByteBufCodecs.VAR_INT.decode(buf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
