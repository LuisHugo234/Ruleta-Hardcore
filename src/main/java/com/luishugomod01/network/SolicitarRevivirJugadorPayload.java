package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record SolicitarRevivirJugadorPayload(
        UUID objetivoUuid,
        boolean manoPrincipal
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SolicitarRevivirJugadorPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("solicitar_revivir_jugador"));

    public static final StreamCodec<ByteBuf, SolicitarRevivirJugadorPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SolicitarRevivirJugadorPayload decode(ByteBuf buf) {
            UUID uuid = UUIDUtil.STREAM_CODEC.decode(buf);
            boolean mano = buf.readBoolean();
            return new SolicitarRevivirJugadorPayload(uuid, mano);
        }

        @Override
        public void encode(ByteBuf buf, SolicitarRevivirJugadorPayload value) {
            UUIDUtil.STREAM_CODEC.encode(buf, value.objetivoUuid());
            buf.writeBoolean(value.manoPrincipal());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
