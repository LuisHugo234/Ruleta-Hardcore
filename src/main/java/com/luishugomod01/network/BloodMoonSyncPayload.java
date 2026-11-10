package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BloodMoonSyncPayload(
        boolean active,
        boolean eclipseActive,
        boolean contaminacionActive,
        boolean cuevasTenebrosasActive,
        boolean maresAcidosActive,
        boolean gritoPrimalActive,
        boolean sombraParpadeanteActive,
        int sorderaTicks
) implements CustomPacketPayload {

    public BloodMoonSyncPayload(boolean active) {
        this(active, false, false, false, false, false, false, 0);
    }

    public BloodMoonSyncPayload(
            boolean active,
            boolean eclipseActive,
            boolean contaminacionActive,
            boolean cuevasTenebrosasActive,
            boolean maresAcidosActive
    ) {
        this(active, eclipseActive, contaminacionActive, cuevasTenebrosasActive, maresAcidosActive, false, false, 0);
    }

    public static final CustomPacketPayload.Type<BloodMoonSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("blood_moon_sync"));

    public static final StreamCodec<ByteBuf, BloodMoonSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.active());
                buf.writeBoolean(payload.eclipseActive());
                buf.writeBoolean(payload.contaminacionActive());
                buf.writeBoolean(payload.cuevasTenebrosasActive());
                buf.writeBoolean(payload.maresAcidosActive());
                buf.writeBoolean(payload.gritoPrimalActive());
                buf.writeBoolean(payload.sombraParpadeanteActive());
                buf.writeInt(payload.sorderaTicks());
            },
            buf -> new BloodMoonSyncPayload(
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readInt()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
