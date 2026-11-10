package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MuerteFantasmaPayload(
        String nombreVictima,
        int vidasRestantes,
        int vidasMaximas,
        boolean esEliminado
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MuerteFantasmaPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("muerte_fantasma"));

    public static final StreamCodec<ByteBuf, MuerteFantasmaPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MuerteFantasmaPayload::nombreVictima,
            ByteBufCodecs.VAR_INT, MuerteFantasmaPayload::vidasRestantes,
            ByteBufCodecs.VAR_INT, MuerteFantasmaPayload::vidasMaximas,
            ByteBufCodecs.BOOL, MuerteFantasmaPayload::esEliminado,
            MuerteFantasmaPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
