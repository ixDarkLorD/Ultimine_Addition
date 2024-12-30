package net.ixdarklord.ultimine_addition.datagen.advancement;

import net.ixdarklord.ultimine_addition.common.advancement.UltimineObtainTrigger;
import net.ixdarklord.ultimine_addition.common.data.item.MinerCertificateData;
import net.ixdarklord.ultimine_addition.common.item.ModItems;
import net.ixdarklord.ultimine_addition.core.UltimineAddition;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.ixdarklord.ultimine_addition.common.advancement.AdvancementTriggers.*;

public class AdvancementGenerator extends AdvancementProvider {

    public AdvancementGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new Contents()));
    }

    public static class Contents implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
            AdvancementHolder root = Advancement.Builder.advancement().display(
                            ModItems.MINER_CERTIFICATE,
                            Component.translatable(String.format("itemGroup.%s.tab", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.root.desc", UltimineAddition.MOD_ID)),
                            UltimineAddition.getGuiTexture("adv_background", "png"),
                            AdvancementType.TASK,
                            false, false, false)
                    .addCriterion("has_early_items", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Items.DIRT, Items.STONE).of(ItemTags.LOGS).build()))
                    .addCriterion("killed_by_something", KilledTrigger.TriggerInstance.entityKilledPlayer())
                    .addCriterion("killed_something", KilledTrigger.TriggerInstance.playerKilledEntity())
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("root").toString());

            AdvancementHolder amethyst = Advancement.Builder.advancement().parent(root).display(
                            Items.AMETHYST_SHARD,
                            Component.translatable(String.format("advancement.%s.amethyst_gathering", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.obtain", UltimineAddition.MOD_ID), Items.AMETHYST_SHARD.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_amethyst", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AMETHYST_SHARD))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("gathering_amethyst").toString());

            AdvancementHolder cardBlueprint = Advancement.Builder.advancement().parent(amethyst).display(
                            ModItems.CARD_BLUEPRINT,
                            Component.translatable(String.format("advancement.%s.craft.card.blueprint", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.CARD_BLUEPRINT.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("amethyst_adv", advancementTrigger(amethyst))
                    .addCriterion("has_card_blueprint", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CARD_BLUEPRINT))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, UltimineAddition.getLocation("card_blueprint").toString());

            AdvancementHolder slime = Advancement.Builder.advancement().parent(root).display(
                            Items.SLIME_BALL,
                            Component.translatable(String.format("advancement.%s.obtain.slime_balls", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.obtain", UltimineAddition.MOD_ID), Items.SLIME_BALL.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_slime_balls", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SLIME_BALL))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("slime_balls").toString());

            AdvancementHolder pen = Advancement.Builder.advancement().parent(slime).display(
                            ModItems.PEN,
                            Component.translatable(String.format("advancement.%s.craft.pen", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.PEN.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("slime_adv", advancementTrigger(slime))
                    .addCriterion("has_pen", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PEN))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, UltimineAddition.getLocation("pen").toString());

            AdvancementHolder emptyCard = Advancement.Builder.advancement().parent(root).display(
                            ModItems.MINING_SKILL_CARD_EMPTY,
                            Component.translatable(String.format("advancement.%s.obtain.card.empty", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.obtain", UltimineAddition.MOD_ID), ModItems.MINING_SKILL_CARD_EMPTY.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("trade_for_empty_card", tradedWithVillager(ItemPredicate.Builder.item().of(ModItems.MINING_SKILL_CARD_EMPTY).build()))
                    .addCriterion("has_empty_card", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MINING_SKILL_CARD_EMPTY))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("empty_card").toString());

            AdvancementHolder skillsRecord = Advancement.Builder.advancement().parent(emptyCard).display(
                            ModItems.SKILLS_RECORD,
                            Component.translatable(String.format("advancement.%s.craft.skills_record", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.SKILLS_RECORD.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.GOAL,
                            true, true, false)
                    .addCriterion("has_skills_record", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SKILLS_RECORD))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("skills_record").toString());

            AdvancementHolder pickaxeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                            ModItems.MINING_SKILL_CARD_PICKAXE,
                            Component.translatable(String.format("advancement.%s.craft.card.pickaxe", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.MINING_SKILL_CARD_PICKAXE.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_pickaxe_card", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MINING_SKILL_CARD_PICKAXE))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("pickaxe_card").toString());

            AdvancementHolder axeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                            ModItems.MINING_SKILL_CARD_AXE,
                            Component.translatable(String.format("advancement.%s.craft.card.axe", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.MINING_SKILL_CARD_AXE.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_axe_card", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MINING_SKILL_CARD_AXE))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("axe_card").toString());

            AdvancementHolder shovelCard = Advancement.Builder.advancement().parent(emptyCard).display(
                            ModItems.MINING_SKILL_CARD_SHOVEL,
                            Component.translatable(String.format("advancement.%s.craft.card.shovel", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.MINING_SKILL_CARD_SHOVEL.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_shovel_card", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MINING_SKILL_CARD_SHOVEL))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("shovel_card").toString());

            AdvancementHolder hoeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                            ModItems.MINING_SKILL_CARD_HOE,
                            Component.translatable(String.format("advancement.%s.craft.card.hoe", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.craft", UltimineAddition.MOD_ID), ModItems.MINING_SKILL_CARD_HOE.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion("has_hoe_card", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MINING_SKILL_CARD_HOE))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("hoe_card").toString());

            AdvancementHolder ultiminePower = Advancement.Builder.advancement().parent(skillsRecord).display(
                            Util.make(() -> {
                                ItemStack stack = ModItems.MINER_CERTIFICATE.getDefaultInstance();
                                MinerCertificateData.loadData(stack).setAccomplished(true).saveData(stack);
                                return stack;
                            }),
                            Component.translatable(String.format("advancement.%s.ultimine_ability", UltimineAddition.MOD_ID)),
                            Component.translatable(String.format("advancement.%s.ultimine_ability.desc", UltimineAddition.MOD_ID), ModItems.MINER_CERTIFICATE.getDefaultInstance().getHoverName()),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion("has_ultimine_ability", UltimineObtainTrigger.Instance.obtain())
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, UltimineAddition.getLocation("ultimine_ability").toString());
        }
    }
}