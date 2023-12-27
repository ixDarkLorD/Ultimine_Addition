package net.ixdarklord.ultimine_addition.datagen.advancement;

import net.ixdarklord.ultimine_addition.common.advancement.UltimineAbilityTrigger;
import net.ixdarklord.ultimine_addition.common.data.item.MinerCertificateData;
import net.ixdarklord.ultimine_addition.common.item.ModItems;
import net.ixdarklord.ultimine_addition.core.Constants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static net.ixdarklord.ultimine_addition.common.advancement.AdvancementTriggers.*;

public class AdvancementGenerator extends AdvancementProvider {
    public AdvancementGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.advancement().display(
                        ModItems.MINER_CERTIFICATE,
                        new TranslatableComponent(String.format("itemGroup.%s.tab", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.root.desc", Constants.MOD_ID)),
                        Constants.getGuiTexture("adv_background", "png"),
                        FrameType.TASK,
                        false, false, false)
                .addCriterion("has_crafting_table", KilledTrigger.TriggerInstance.entityKilledPlayer())
                .addCriterion("killed_by_something", KilledTrigger.TriggerInstance.entityKilledPlayer())
                .addCriterion("killed_something", KilledTrigger.TriggerInstance.playerKilledEntity())
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("root").toString());

        Advancement amethyst = Advancement.Builder.advancement().parent(root).display(
                        Items.AMETHYST_SHARD,
                        new TranslatableComponent(String.format("advancement.%s.amethyst_gathering", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.obtain", Constants.MOD_ID), Items.AMETHYST_SHARD.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_amethyst", inventoryHas(Items.AMETHYST_SHARD))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("gathering_amethyst").toString());

        Advancement cardBlueprint = Advancement.Builder.advancement().parent(amethyst).display(
                        ModItems.CARD_BLUEPRINT,
                        new TranslatableComponent(String.format("advancement.%s.craft.card.blueprint", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.CARD_BLUEPRINT.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("amethyst_adv", advancementTrigger(amethyst))
                .addCriterion("has_card_blueprint", inventoryHas(ModItems.CARD_BLUEPRINT))
                .requirements(RequirementsStrategy.AND)
                .save(consumer, Constants.getLocation("card_blueprint").toString());

        Advancement slime = Advancement.Builder.advancement().parent(root).display(
                        Items.SLIME_BALL,
                        new TranslatableComponent(String.format("advancement.%s.obtain.slime_balls", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.obtain", Constants.MOD_ID), Items.SLIME_BALL.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_slime_balls", inventoryHas(Items.SLIME_BALL))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("slime_balls").toString());

        Advancement pen = Advancement.Builder.advancement().parent(slime).display(
                        ModItems.PEN,
                        new TranslatableComponent(String.format("advancement.%s.craft.pen", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.PEN.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("slime_adv", advancementTrigger(slime))
                .addCriterion("has_pen", inventoryHas(ModItems.PEN))
                .requirements(RequirementsStrategy.AND)
                .save(consumer, Constants.getLocation("pen").toString());

        Advancement emptyCard = Advancement.Builder.advancement().parent(root).display(
                        ModItems.MINING_SKILL_CARD_EMPTY,
                        new TranslatableComponent(String.format("advancement.%s.obtain.card.empty", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.obtain", Constants.MOD_ID), ModItems.MINING_SKILL_CARD_EMPTY.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("trade_for_empty_card", tradedWithVillager(ItemPredicate.Builder.item().of(ModItems.MINING_SKILL_CARD_EMPTY)))
                .addCriterion("has_empty_card", inventoryHas(ModItems.MINING_SKILL_CARD_EMPTY))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("empty_card").toString());

        Advancement skillsRecord = Advancement.Builder.advancement().parent(emptyCard).display(
                        ModItems.SKILLS_RECORD,
                        new TranslatableComponent(String.format("advancement.%s.craft.skills_record", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.SKILLS_RECORD.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.GOAL,
                        true, true, false)
                .addCriterion("has_skills_record", inventoryHas(ModItems.SKILLS_RECORD))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("skills_record").toString());

        Advancement pickaxeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                        ModItems.MINING_SKILL_CARD_PICKAXE,
                        new TranslatableComponent(String.format("advancement.%s.craft.card.pickaxe", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.MINING_SKILL_CARD_PICKAXE.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_pickaxe_card", inventoryHas(ModItems.MINING_SKILL_CARD_PICKAXE))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("pickaxe_card").toString());

        Advancement axeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                        ModItems.MINING_SKILL_CARD_AXE,
                        new TranslatableComponent(String.format("advancement.%s.craft.card.axe", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.MINING_SKILL_CARD_AXE.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_axe_card", inventoryHas(ModItems.MINING_SKILL_CARD_AXE))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("axe_card").toString());

        Advancement shovelCard = Advancement.Builder.advancement().parent(emptyCard).display(
                        ModItems.MINING_SKILL_CARD_SHOVEL,
                        new TranslatableComponent(String.format("advancement.%s.craft.card.shovel", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.MINING_SKILL_CARD_SHOVEL.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_shovel_card", inventoryHas(ModItems.MINING_SKILL_CARD_SHOVEL))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("shovel_card").toString());

        Advancement hoeCard = Advancement.Builder.advancement().parent(emptyCard).display(
                        ModItems.MINING_SKILL_CARD_HOE,
                        new TranslatableComponent(String.format("advancement.%s.craft.card.hoe", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.craft", Constants.MOD_ID), ModItems.MINING_SKILL_CARD_HOE.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_hoe_card", inventoryHas(ModItems.MINING_SKILL_CARD_HOE))
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("hoe_card").toString());

        Advancement ultiminePower = Advancement.Builder.advancement().parent(skillsRecord).display(
                        Util.make(() -> {
                            ItemStack stack = ModItems.MINER_CERTIFICATE.getDefaultInstance();
                            new MinerCertificateData().loadData(stack).setAccomplished(true).saveData(stack);
                            return stack;
                        }),
                        new TranslatableComponent(String.format("advancement.%s.ultimine_ability", Constants.MOD_ID)),
                        new TranslatableComponent(String.format("advancement.%s.ultimine_ability.desc", Constants.MOD_ID), ModItems.MINER_CERTIFICATE.getDefaultInstance().getHoverName()),
                        null,
                        FrameType.CHALLENGE,
                        true, true, false)
                .addCriterion("has_ultimine_ability", UltimineAbilityTrigger.Instance.obtain())
                .requirements(RequirementsStrategy.OR)
                .save(consumer, Constants.getLocation("ultimine_ability").toString());
    }

    @Override
    public @NotNull String getName() {
        return String.format("%s %s", Constants.MOD_NAME, super.getName());
    }
}