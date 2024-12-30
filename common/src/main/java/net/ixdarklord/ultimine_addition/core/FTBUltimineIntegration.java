package net.ixdarklord.ultimine_addition.core;

import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import dev.ftb.mods.ftbultimine.integration.FTBRanksIntegration;
import dev.ftb.mods.ftbultimine.integration.FTBUltiminePlugin;
import net.ixdarklord.ultimine_addition.common.effect.MineGoJuiceEffect;
import net.ixdarklord.ultimine_addition.common.item.MiningSkillCardItem;
import net.ixdarklord.ultimine_addition.config.ConfigHandler;
import net.ixdarklord.ultimine_addition.config.PlaystyleMode;
import net.ixdarklord.ultimine_addition.util.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static dev.ftb.mods.ftbultimine.config.FTBUltimineServerConfig.MAX_BLOCKS;
import static net.ixdarklord.ultimine_addition.config.ConfigHandler.COMMON.PLAYSTYLE_MODE;

public class FTBUltimineIntegration implements FTBUltiminePlugin {
    private static boolean isButtonPressed;

    @Override
    public void init() {
        UltimineAddition.LOGGER.info("Registering plugin to FTBUltimine!");
    }

    @Override
    public boolean canUltimine(Player player) {
        boolean result = ServicePlatform.Players.isPlayerUltimineCapable(player);
        if (result) return true;
        if (PLAYSTYLE_MODE.get() == PlaystyleMode.LEGACY) return result;

        if (isPlayerHasCustomCardValidEffect(player)) {
            if (ItemUtils.isItemInHandCustomCardValid(player)) result = true;
        }

        if (!ItemUtils.checkTargetedBlock(player)) return false;

        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.getHolder(Registration.MINE_GO_JUICE_PICKAXE.getId()).orElseThrow())) {
            if (ItemUtils.isItemInHandPickaxe(player)) result = true;
        }
        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.getHolder(Registration.MINE_GO_JUICE_AXE.getId()).orElseThrow())) {
            if (ItemUtils.isItemInHandAxe(player)) result = true;
        }
        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.getHolder(Registration.MINE_GO_JUICE_SHOVEL.getId()).orElseThrow())) {
            if (ItemUtils.isItemInHandShovel(player)) result = true;
        }
        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.getHolder(Registration.MINE_GO_JUICE_HOE.getId()).orElseThrow())) {
            if (ItemUtils.isItemInHandHoe(player)) result = true;
        }
        return result;
    }

    public static void keyEvent(Player player) {
        if (FTBUltimineClient.keyBinding.isDown()) {
            if (!isButtonPressed) {
                MutableComponent MSG = Component.translatable("info.ultimine_addition.incapable");
                Component requiredTool = null;

                if (!ServicePlatform.Players.isPlayerUltimineCapable(player)) {
                    if (ItemUtils.isItemInHandCustomCardValid(player)) {
                        if (!isPlayerHasCustomCardValidEffect(player)) {
                            String[] toolNames = getCustomCardTypes(player).stream()
                                    .map(MiningSkillCardItem.Type::getId)
                                    .toArray(String[]::new);
                            String toolPrefix = toolNames.length > 1 ? "many_tools" : toolNames[0];

                            MutableComponent toolsList = Component.empty();
                            for (int i = 0; i < toolNames.length; i++) {
                                toolsList.append("[").append(Component.translatable("info.ultimine_addition.required_skill.%s".formatted(toolNames[i]))).append("]");
                                if (i != toolNames.length-1)
                                    toolsList.append(", ");
                            }
                            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, toolsList);
                            Style style = toolNames.length > 1 ? Style.EMPTY.withUnderlined(true).withHoverEvent(event) : Style.EMPTY;
                            requiredTool = Component.translatable("info.ultimine_addition.required_skill.%s".formatted(toolPrefix)).withStyle(style);
                        }
                    } else if (ItemUtils.isItemInHandPickaxe(player)) {
                        if (!player.hasEffect(Registration.MINE_GO_JUICE_PICKAXE)) {
                            requiredTool = Component.translatable("info.ultimine_addition.required_skill.pickaxe");
                        }
                    } else if (ItemUtils.isItemInHandAxe(player)) {
                        if (!player.hasEffect(Registration.MINE_GO_JUICE_AXE)) {
                            requiredTool = Component.translatable("info.ultimine_addition.required_skill.axe");
                        }
                    } else if (ItemUtils.isItemInHandShovel(player)) {
                        if (!player.hasEffect(Registration.MINE_GO_JUICE_SHOVEL)) {
                            requiredTool = Component.translatable("info.ultimine_addition.required_skill.shovel");
                        }
                    } else if (ItemUtils.isItemInHandHoe(player)) {
                        if (!player.hasEffect(Registration.MINE_GO_JUICE_HOE)) {
                            requiredTool = Component.translatable("info.ultimine_addition.required_skill.hoe");
                        }
                    } else if (ItemUtils.isItemInHandNotTools(player)) {
                        requiredTool = Component.translatable("info.ultimine_addition.required_skill.all");
                    }

                    if (PLAYSTYLE_MODE.get() != PlaystyleMode.LEGACY) {
                        if (requiredTool != null) {
                            player.displayClientMessage(MSG.withStyle(ChatFormatting.RED), false);
                            player.displayClientMessage(Component.literal("✖ ")
                                    .append(Component.translatable("info.ultimine_addition.required_skill", requiredTool))
                                    .withStyle(ChatFormatting.GRAY), false);
                        }
                    } else player.displayClientMessage(MSG.withStyle(ChatFormatting.RED), false);
                }
                isButtonPressed = true;
            }
        } else {
            isButtonPressed = false;
        }
    }

    public static List<MiningSkillCardItem.Type> getCustomCardTypes(Player player) {
        ItemStack stack = ItemUtils.getItemInHand(player, true);
        return MiningSkillCardItem.Type.TYPES.stream()
                .filter(MiningSkillCardItem.Type::isCustomType)
                .filter(type -> type.utilizeRequiredTools().contains(stack.getItem()))
                .toList();
    }

    private static boolean isPlayerHasCustomCardValidEffect(Player player) {
        List<MiningSkillCardItem.Type> types = getCustomCardTypes(player);
        for (MiningSkillCardItem.Type type : types) {
            Optional<Holder.Reference<MobEffect>> mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(MineGoJuiceEffect.getId(type));
            if (mobEffect.isEmpty()) continue;
            if (player.hasEffect(mobEffect.get()))
                return true;
        }
        return false;
    }

    public static int getMaxBlocks(ServerPlayer player) {
        if (!ConfigHandler.COMMON.TIER_BASED_MAX_BLOCKS.get())
            return FTBUltimine.ranksMod ? FTBRanksIntegration.getMaxBlocks(player) : MAX_BLOCKS.get();

        List<MobEffectInstance> instances = new ArrayList<>(player.getActiveEffects().stream().filter(mobEffectInstance -> mobEffectInstance.getEffect() instanceof MineGoJuiceEffect).toList());
        if (!ServicePlatform.Players.isPlayerUltimineCapable(player) && !instances.isEmpty()) {
            instances.sort(Comparator.comparingInt(MobEffectInstance::getAmplifier).reversed());

            if (ItemUtils.isItemInHandCustomCardValid(player)) {
                instances.removeIf(instance -> {
                    ItemStack stack = ItemUtils.getItemInHand(player, true);
                    if (stack.getItem() instanceof MiningSkillCardItem cardItem)
                        return ((MineGoJuiceEffect) instance.getEffect()).getType() != cardItem.getType();
                    return false;
                });
            } else if (ItemUtils.isItemInHandPickaxe(player)) {
                instances.removeIf(instance -> ((MineGoJuiceEffect) instance.getEffect()).getType() != MiningSkillCardItem.Type.PICKAXE);
            } else if (ItemUtils.isItemInHandAxe(player)) {
                instances.removeIf(instance -> ((MineGoJuiceEffect) instance.getEffect()).getType() != MiningSkillCardItem.Type.AXE);
            } else if (ItemUtils.isItemInHandShovel(player)) {
                instances.removeIf(instance -> ((MineGoJuiceEffect) instance.getEffect()).getType() != MiningSkillCardItem.Type.SHOVEL);
            } else if (ItemUtils.isItemInHandHoe(player)) {
                instances.removeIf(instance -> ((MineGoJuiceEffect) instance.getEffect()).getType() != MiningSkillCardItem.Type.HOE);
            }

            if (!instances.isEmpty()) {
                return switch (instances.getFirst().getAmplifier()) {
                    case 0 -> ConfigHandler.COMMON.TIER_1_MAX_BLOCKS.get();
                    case 1 -> ConfigHandler.COMMON.TIER_2_MAX_BLOCKS.get();
                    case 2 -> ConfigHandler.COMMON.TIER_3_MAX_BLOCKS.get();
                    default -> MAX_BLOCKS.get();
                };
            }
        }
        return FTBUltimine.ranksMod ? FTBRanksIntegration.getMaxBlocks(player) : MAX_BLOCKS.get();
    }
}
