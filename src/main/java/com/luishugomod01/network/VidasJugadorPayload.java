package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record VidasJugadorPayload(
        UUID playerUuid,
        int vidasRestantes,
        int vidasMaximas
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<VidasJugadorPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("vidas_jugador_sync"));

    public static final StreamCodec<ByteBuf, VidasJugadorPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, VidasJugadorPayload::playerUuid,
            ByteBufCodecs.VAR_INT, VidasJugadorPayload::vidasRestantes,
            ByteBufCodecs.VAR_INT, VidasJugadorPayload::vidasMaximas,
            VidasJugadorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
