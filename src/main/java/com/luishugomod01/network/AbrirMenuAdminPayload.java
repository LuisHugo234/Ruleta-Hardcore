package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AbrirMenuAdminPayload(
        boolean ruletaAutomatica,
        int vidasIniciales
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbrirMenuAdminPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("abrir_menu_admin"));

    public static final StreamCodec<ByteBuf, AbrirMenuAdminPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AbrirMenuAdminPayload::ruletaAutomatica,
            ByteBufCodecs.VAR_INT, AbrirMenuAdminPayload::vidasIniciales,
            AbrirMenuAdminPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
