package kgriffon.virtualstorage.gui;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import kgriffon.virtualstorage.VirtualStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class GuiUtils {

    public static GuiElement empty(ServerPlayer player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
                .setName(Component.empty())
                .hideTooltip();
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.parse("air"));
        }
        return builder.build();
    }

    public static GuiElementBuilder nextPage(ServerPlayer player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ARROW)
                .setName(Component.translatable("book.page_button.next"));
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.fromNamespaceAndPath(VirtualStorage.MOD_ID, "next_page"));
        }
        return builder;
    }

    public static GuiElementBuilder previousPage(ServerPlayer player) {
        GuiElementBuilder builder = new GuiElementBuilder(Items.ARROW)
                .setName(Component.translatable("book.page_button.previous"));
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            builder.model(Identifier.fromNamespaceAndPath(VirtualStorage.MOD_ID, "previous_page"));
        }
        return builder;
    }

    public static Component title(ServerPlayer player, @Nullable Component input) {
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            MutableComponent text = Component.empty();
            MutableComponent textTexture = Component.empty().setStyle(Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath(VirtualStorage.MOD_ID, "gui"))).withColor(ChatFormatting.WHITE));

            textTexture.append("a").append("e").append("b");

            if (!textTexture.getSiblings().isEmpty()) {
                text.append(textTexture);
            }

            if (input != null) {
                text.append(input);
            }
            return text;
        } else {
            return input != null ? input : Component.empty();
        }
    }
}
