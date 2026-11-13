package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record VidasSyncPayload(
        int vidasRestantes,
        int vidasMaximas,
        int insolacionPct,
        int congelacionPct,
        int corduraPct,
        int avisoRayoSegundos
) implements CustomPacketPayload {

    public VidasSyncPayload(int vidasRestantes, int vidasMaximas) {
        this(vidasRestantes, vidasMaximas, 0, 0, -1, 0);
    }

    public static final CustomPacketPayload.Type<VidasSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("vidas_sync"));

    public static final StreamCodec<ByteBuf, VidasSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                ByteBufCodecs.VAR_INT.encode(buf, payload.vidasRestantes());
                ByteBufCodecs.VAR_INT.encode(buf, payload.vidasMaximas());
                ByteBufCodecs.VAR_INT.encode(buf, payload.insolacionPct());
                ByteBufCodecs.VAR_INT.encode(buf, payload.congelacionPct());
                ByteBufCodecs.VAR_INT.encode(buf, payload.corduraPct());
                ByteBufCodecs.VAR_INT.encode(buf, payload.avisoRayoSegundos());
            },
            buf -> new VidasSyncPayload(
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
