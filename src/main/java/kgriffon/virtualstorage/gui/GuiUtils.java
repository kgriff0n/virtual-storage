package kgriffon.virtualstorage.gui;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import kgriffon.virtualstorage.VirtualStorage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GuiUtils {

    public static GuiElement empty(ServerPlayerEntity player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
                .setName(Text.empty())
                .hideTooltip();
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.of("air"));
        }
        return builder.build();
    }

    public static GuiElementBuilder nextPage(ServerPlayerEntity player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ARROW)
                .noDefaults()
                .setName(Text.translatable("book.page_button.next"));
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.of(VirtualStorage.MOD_ID, "next_page"));
        }
        return builder;
    }

    public static GuiElementBuilder previousPage(ServerPlayerEntity player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ARROW)
                .noDefaults()
                .setName(Text.translatable("book.page_button.previous"));
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.of(VirtualStorage.MOD_ID, "previous_page"));
        }
        return builder;
    }

    public static Text title(ServerPlayerEntity player, @Nullable Text input) {
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            MutableText text = Text.empty();
            MutableText textTexture = Text.empty().setStyle(Style.EMPTY.withFont(Identifier.of(VirtualStorage.MOD_ID, "gui")).withColor(Formatting.WHITE));

            textTexture.append("a").append("e").append("b");

            if (!textTexture.getSiblings().isEmpty()) {
                text.append(textTexture);
            }

            if (input != null) {
                text.append(input);
            }
            return text;
        } else {
            return input != null ? input : Text.empty();
        }
    }
}
