package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AbrirMenuRevivirPayload(
        boolean manoPrincipal,
        List<JugadorEliminado> eliminados
) implements CustomPacketPayload {

    public record JugadorEliminado(UUID uuid, String nombre) {}

    public static final CustomPacketPayload.Type<AbrirMenuRevivirPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("abrir_menu_revivir"));

    public static final StreamCodec<ByteBuf, AbrirMenuRevivirPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AbrirMenuRevivirPayload decode(ByteBuf buf) {
            boolean mano = buf.readBoolean();
            int count = ByteBufCodecs.VAR_INT.decode(buf);
            List<JugadorEliminado> lista = new ArrayList<>(Math.max(0, count));
            for (int i = 0; i < count; i++) {
                UUID uuid = UUIDUtil.STREAM_CODEC.decode(buf);
                String nombre = ByteBufCodecs.STRING_UTF8.decode(buf);
                lista.add(new JugadorEliminado(uuid, nombre));
            }
            return new AbrirMenuRevivirPayload(mano, lista);
        }

        @Override
        public void encode(ByteBuf buf, AbrirMenuRevivirPayload value) {
            buf.writeBoolean(value.manoPrincipal());
            List<JugadorEliminado> lista = value.eliminados();
            ByteBufCodecs.VAR_INT.encode(buf, lista.size());
            for (JugadorEliminado entry : lista) {
                UUIDUtil.STREAM_CODEC.encode(buf, entry.uuid());
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.nombre());
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
