package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

/**
 * Paquete enviado del servidor a todos los clientes cuando se inicia un Ritual de Revivir.
 * Todos los jugadores reciben el título global cinematográfico; quienes estén en la misma dimensión
 * ven la grieta en el cielo, el descenso del ángel, el pilar de luz, anillos y cadenas rompiéndose;
 * y el jugador revivido vive la secuencia completa desde una cámara cinematográfica dedicada.
 */
public record RitualRevivirPayload(
        UUID jugadorUuid,
        String nombreJugador,
        String dimensionId,
        double x,
        double y,
        double z,
        int vidasNuevas
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RitualRevivirPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("ritual_revivir"));

    public static final StreamCodec<ByteBuf, RitualRevivirPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public RitualRevivirPayload decode(ByteBuf buf) {
            UUID uuid = UUIDUtil.STREAM_CODEC.decode(buf);
            String nombre = net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.decode(buf);
            String dim = net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.decode(buf);
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            int vidas = net.minecraft.network.codec.ByteBufCodecs.VAR_INT.decode(buf);
            return new RitualRevivirPayload(uuid, nombre, dim, x, y, z, vidas);
        }

        @Override
        public void encode(ByteBuf buf, RitualRevivirPayload value) {
            UUIDUtil.STREAM_CODEC.encode(buf, value.jugadorUuid());
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.encode(buf, value.nombreJugador());
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.encode(buf, value.dimensionId());
            buf.writeDouble(value.x());
            buf.writeDouble(value.y());
            buf.writeDouble(value.z());
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT.encode(buf, value.vidasNuevas());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
