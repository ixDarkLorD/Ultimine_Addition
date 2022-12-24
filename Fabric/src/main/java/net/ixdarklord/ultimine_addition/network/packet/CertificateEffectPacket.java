package net.ixdarklord.ultimine_addition.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.ixdarklord.ultimine_addition.item.MinerCertificate;
import net.ixdarklord.ultimine_addition.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class CertificateEffectPacket {
    public static void receive(MinecraftServer server, ServerPlayer player,
                               ServerGamePacketListenerImpl ignored1,
                               FriendlyByteBuf buf, PacketSender ignored2) {

        ItemStack stack = buf.readItem();
        Minecraft MC = Minecraft.getInstance();
        assert MC.level != null;
        Entity entity = MC.level.getEntity(buf.readInt());
        assert entity != null;
        server.execute(() -> {
            MinerCertificate.playParticleAndSound(player);

            var buffer = PacketByteBufs.create();
            buffer.writeItem(stack);
            buffer.writeInt(entity.getId());
            ServerPlayNetworking.send(player, PacketHandler.CERTIFICATE_EFFECT_SYNC_ID, buffer);
        });
    }

    public static class Play2Client {
        public static void receive(Minecraft client, ClientPacketListener ignored1,
                                   FriendlyByteBuf buf, PacketSender ignored2) {

            ItemStack stack = buf.readItem();
            assert client.level != null;
            Entity entity = client.level.getEntity(buf.readInt());
            client.execute(() -> {
                MinerCertificate.playAnimation(stack, entity);
            });
        }
    }
}
