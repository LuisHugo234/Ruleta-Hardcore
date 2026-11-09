package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record AbrirMenuEstadoPayload(
        List<MaldicionInfo> maldiciones,
        boolean bloodMoon,
        boolean eclipse
) implements CustomPacketPayload {

    public record MaldicionInfo(int nivel, String nombre, String descripcion) {
        public static final StreamCodec<ByteBuf, MaldicionInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, MaldicionInfo::nivel,
                ByteBufCodecs.STRING_UTF8, MaldicionInfo::nombre,
                ByteBufCodecs.STRING_UTF8, MaldicionInfo::descripcion,
                MaldicionInfo::new
        );
    }

    public static final CustomPacketPayload.Type<AbrirMenuEstadoPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("abrir_menu_estado"));

    public static final StreamCodec<ByteBuf, AbrirMenuEstadoPayload> STREAM_CODEC = StreamCodec.composite(
            MaldicionInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), AbrirMenuEstadoPayload::maldiciones,
            ByteBufCodecs.BOOL, AbrirMenuEstadoPayload::bloodMoon,
            ByteBufCodecs.BOOL, AbrirMenuEstadoPayload::eclipse,
            AbrirMenuEstadoPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
