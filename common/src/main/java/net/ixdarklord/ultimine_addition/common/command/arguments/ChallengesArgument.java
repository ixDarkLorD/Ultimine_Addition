package net.ixdarklord.ultimine_addition.common.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import net.ixdarklord.ultimine_addition.common.data.challenge.ChallengesData;
import net.ixdarklord.ultimine_addition.common.data.challenge.ChallengesManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ChallengesArgument implements ArgumentType<Pair<ResourceLocation, ChallengesData>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_CHALLENGE = new DynamicCommandExceptionType((entry) ->
            Component.translatable("argument.ultimine_addition.challenge.unknown", entry));

    public static ChallengesArgument data() {
        return new ChallengesArgument();
    }

    @SuppressWarnings("unchecked")
    public static Pair<ResourceLocation, ChallengesData> getData(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, Pair.class);
    }

    @Override
    public Pair<ResourceLocation, ChallengesData> parse(StringReader reader) throws CommandSyntaxException {
        var id = ResourceLocation.read(reader);
        if (ChallengesManager.INSTANCE.getAllChallenges().containsKey(id)) return Pair.of(id, ChallengesManager.INSTANCE.getAllChallenges().get(id));
        throw ERROR_UNKNOWN_CHALLENGE.create(id.toString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        return SharedSuggestionProvider.suggestResource(ChallengesManager.INSTANCE.getAllChallenges().keySet(), pBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}