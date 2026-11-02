package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RuletaAnimacionPayload(
        String nivelNombre,
        String castigoNombre,
        String explicacion,
        float targetAngle,
        int duracionTicks
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RuletaAnimacionPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("animacion_ruleta"));

    public static final StreamCodec<ByteBuf, RuletaAnimacionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RuletaAnimacionPayload::nivelNombre,
            ByteBufCodecs.STRING_UTF8, RuletaAnimacionPayload::castigoNombre,
            ByteBufCodecs.STRING_UTF8, RuletaAnimacionPayload::explicacion,
            ByteBufCodecs.FLOAT, RuletaAnimacionPayload::targetAngle,
            ByteBufCodecs.VAR_INT, RuletaAnimacionPayload::duracionTicks,
            RuletaAnimacionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
