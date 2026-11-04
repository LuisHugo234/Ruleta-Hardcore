package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GuardarConfigAdminPayload(
        boolean ruletaAutomatica,
        int vidasIniciales
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GuardarConfigAdminPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("guardar_config_admin"));

    public static final StreamCodec<ByteBuf, GuardarConfigAdminPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, GuardarConfigAdminPayload::ruletaAutomatica,
            ByteBufCodecs.VAR_INT, GuardarConfigAdminPayload::vidasIniciales,
            GuardarConfigAdminPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
