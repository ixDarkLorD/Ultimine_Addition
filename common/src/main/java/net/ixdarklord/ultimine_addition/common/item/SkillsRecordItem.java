package net.ixdarklord.ultimine_addition.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import net.ixdarklord.coolcat_lib.util.ScreenUtils;
import net.ixdarklord.ultimine_addition.client.gui.components.SkillsRecordTooltip;
import net.ixdarklord.ultimine_addition.common.container.SkillsRecordContainer;
import net.ixdarklord.ultimine_addition.common.data.challenge.ChallengesManager;
import net.ixdarklord.ultimine_addition.common.data.item.MiningSkillCardData;
import net.ixdarklord.ultimine_addition.common.data.item.SkillsRecordData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class SkillsRecordItem extends DataAbstractItem<SkillsRecordData> {
    private static final Component TITLE = Component.translatable("item.ultimine_addition.skills_record");
    public static final int CONTAINER_SIZE = 6;
    public SkillsRecordItem(Properties properties) {
        super(properties, ComponentType.TOOLS);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        if (player.isShiftKeyDown()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        if (stack.hasTag()) {
            MenuRegistry.openExtendedMenu((ServerPlayer) player,
                    new SimpleMenuProvider((id, inv, p) -> new SkillsRecordContainer(id, inv, p, stack, getData(stack).getContainer()), TITLE),
                    buf -> buf.writeItem(stack));
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotID, boolean isSelected) {
        if (level.isClientSide()) return;
        if (entity instanceof ServerPlayer player) {
            if (!stack.hasTag()) getData(stack).sendToClient(player).saveData(stack);
            if (getData(stack).getCardSlots().stream().filter(s -> !s.isEmpty()).toList().isEmpty() && getData(stack).isConsumeMode()) {
                getData(stack).sendToClient(player).toggleConsumeMode();
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (isShiftButtonNotPressed(tooltipComponents)) return;
        if (!stack.hasTag()) {
            Component component = Component.translatable("tooltip.ultimine_addition.skills_record.info").withStyle(ChatFormatting.GRAY);
            List<Component> components = ScreenUtils.splitComponent(component, getSplitterLength());
            tooltipComponents.addAll(components);
            return;
        }
        if (isConsumeChallengeExists(stack)) {
            Component state = getData(stack).isConsumeMode() ? Component.translatable("options.on").withStyle(ChatFormatting.GREEN) : Component.translatable("options.off").withStyle(ChatFormatting.RED);
            tooltipComponents.add(Component.literal("§8• ").append(Component.translatable("gui.ultimine_addition.skills_record.consume", state).withStyle(ChatFormatting.GRAY)));
        }
        if (!getData(stack).getPenSlot().isEmpty()) {
            tooltipComponents.add(Component.literal("§8• ").append(Component.translatable("tooltip.ultimine_addition.pen.ink_chamber",
                    (getData(stack).getPenSlot().getItem() instanceof PenItem item)
                            ? item.getData(getData(stack).getPenSlot()).getCapacity()
                            : 0
            ).withStyle(ChatFormatting.GRAY)));
        }
        tooltipComponents.add(Component.literal("§8• ").append(Component.translatable("tooltip.ultimine_addition.skills_record.contents").withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        Stream<ItemStack> itemStackStream = getData(stack).getAllSlots().stream();
        Objects.requireNonNull(nonNullList);
        itemStackStream.forEach(nonNullList::add);
        if (stack.hasTag()) {
            if (!isShiftButtonNotPressed(null)) {
                return Optional.of(new SkillsRecordTooltip(nonNullList));
            }
        }
        return Optional.empty();
    }


    public boolean isConsumeChallengeExists(ItemStack stack) {
        AtomicBoolean result = new AtomicBoolean();
        getData(stack).getCardSlots().forEach(itemStack -> {
            MiningSkillCardData cardData = new MiningSkillCardData().loadData(itemStack);
            if (!cardData.getChallenges().keySet().stream().filter(identifier -> ChallengesManager.INSTANCE.getAllChallenges().get(identifier.id()).getChallengeType().isConsuming()).toList().isEmpty())
                result.set(true);
        });
        return result.get();
    }

    @Override
    public SkillsRecordData getData(ItemStack stack) {
        return new SkillsRecordData().loadData(stack);
    }
}