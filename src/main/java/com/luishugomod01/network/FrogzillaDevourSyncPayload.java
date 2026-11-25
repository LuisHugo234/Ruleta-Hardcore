package com.luishugomod01.network;

import com.luishugomod01.Ruleta_Hardcore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FrogzillaDevourSyncPayload(
        boolean active,
        int progressPct,
        int ticksRemaining,
        int frogEntityId
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FrogzillaDevourSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Ruleta_Hardcore.id("frogzilla_devour_sync"));

    public static final StreamCodec<ByteBuf, FrogzillaDevourSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.active());
                ByteBufCodecs.VAR_INT.encode(buf, payload.progressPct());
                ByteBufCodecs.VAR_INT.encode(buf, payload.ticksRemaining());
                ByteBufCodecs.VAR_INT.encode(buf, payload.frogEntityId());
            },
            buf -> new FrogzillaDevourSyncPayload(
                    buf.readBoolean(),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
