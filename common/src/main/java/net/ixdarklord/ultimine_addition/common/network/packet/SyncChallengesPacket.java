package net.ixdarklord.ultimine_addition.common.network.packet;

import dev.architectury.networking.NetworkManager;
import net.ixdarklord.ultimine_addition.common.data.challenge.ChallengesData;
import net.ixdarklord.ultimine_addition.common.data.challenge.ChallengesManager;
import net.ixdarklord.ultimine_addition.core.UltimineAddition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record SyncChallengesPacket(Map<ResourceLocation, ChallengesData> dataMap) implements CustomPacketPayload {
    public static final Type<SyncChallengesPacket> TYPE = new Type<>(UltimineAddition.getLocation("sync_challenges"));
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, ChallengesData>> CHALLENGES_STREAM_CODEC =
            ByteBufCodecs.map(i -> new HashMap<>(), ResourceLocation.STREAM_CODEC, ChallengesData.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncChallengesPacket> STREAM_CODEC = StreamCodec.composite(
            CHALLENGES_STREAM_CODEC, SyncChallengesPacket::dataMap,
            SyncChallengesPacket::new);

    public static void handle(SyncChallengesPacket message, NetworkManager.PacketContext context) {
        context.queue(() -> ChallengesManager.INSTANCE.setChallenges(message.dataMap));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
