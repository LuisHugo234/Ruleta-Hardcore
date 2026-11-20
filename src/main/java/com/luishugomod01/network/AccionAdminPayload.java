package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Paquete enviado desde la pestaña de Control Rápido de /ruleta config hacia el servidor
 * para ejecutar acciones administrativas con un clic (verificadas por permisos de admin en servidor).
 */
public record AccionAdminPayload(String accion) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AccionAdminPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("accion_admin"));

    public static final StreamCodec<ByteBuf, AccionAdminPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AccionAdminPayload::accion,
            AccionAdminPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
