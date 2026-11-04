package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record VidasSyncPayload(
        int vidasRestantes,
        int vidasMaximas
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<VidasSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("vidas_sync"));

    public static final StreamCodec<ByteBuf, VidasSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, VidasSyncPayload::vidasRestantes,
            ByteBufCodecs.VAR_INT, VidasSyncPayload::vidasMaximas,
            VidasSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
