package net.ixdarklord.ultimine_addition.core.plugin;

import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import dev.ftb.mods.ftbultimine.integration.FTBUltiminePlugin;
import net.ixdarklord.ultimine_addition.core.Constants;
import net.ixdarklord.ultimine_addition.helper.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class FTBUltimatePlugin implements FTBUltiminePlugin {
    @Override
    public void init() {
        Constants.LOGGER.info("Registering plugin to FTBUltimine!");
        FTBUltiminePlugin.super.init();
    }

    @Override
    public boolean canUltimine(Player player) {
        return Services.PLATFORM.isPlayerCapable(player);
    }

    private static boolean isButtonPressed;
    public static void keyEvent(Player player) {
        if (FTBUltimineClient.keyBinding.isDown()) {
            if (!isButtonPressed) {
                String MSG = "\u2716"+ Component.translatable("info.ultimine_addition.incapable").getString();
                if (!Services.PLATFORM.isPlayerCapable(player)) {
                    player.displayClientMessage(Component.literal(MSG).withStyle(ChatFormatting.RED), false);
                }
            }
            isButtonPressed = true;
        } else {
            isButtonPressed = false;
        }
    }
}
