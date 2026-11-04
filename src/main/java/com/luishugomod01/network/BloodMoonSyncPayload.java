package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BloodMoonSyncPayload(boolean active) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BloodMoonSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("blood_moon_sync"));

    public static final StreamCodec<ByteBuf, BloodMoonSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::active,
            BloodMoonSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
