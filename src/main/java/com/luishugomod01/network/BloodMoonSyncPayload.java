package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BloodMoonSyncPayload(
        boolean active,
        boolean eclipseActive,
        boolean contaminacionActive,
        boolean cuevasTenebrosasActive,
        boolean maresAcidosActive
) implements CustomPacketPayload {

    public BloodMoonSyncPayload(boolean active) {
        this(active, false, false, false, false);
    }

    public static final CustomPacketPayload.Type<BloodMoonSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("blood_moon_sync"));

    public static final StreamCodec<ByteBuf, BloodMoonSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::active,
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::eclipseActive,
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::contaminacionActive,
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::cuevasTenebrosasActive,
            ByteBufCodecs.BOOL, BloodMoonSyncPayload::maresAcidosActive,
            BloodMoonSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
