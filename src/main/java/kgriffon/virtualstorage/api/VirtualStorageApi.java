package kgriffon.virtualstorage.api;

import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.database.VirtualInventoryManager;
import kgriffon.virtualstorage.gui.VirtualGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VirtualStorageApi {

    public static void open(ServerPlayer player) {
        open(player, 1);
    }

    public static void open(ServerPlayer player, int page) {
        VirtualInventoryManager manager = VirtualInventoryManager.getInstance();
        int maxPage = manager.getMaxPage(player);
        if (page > maxPage) {
            page = maxPage;
        }
        try {
            new VirtualGui(manager.getVirtualInventory(player, page), maxPage).open();
        } catch (LoadInventoryException e) {
            player.sendSystemMessage(Component.translatable("mco.errorMessage.connectionFailure").withStyle(ChatFormatting.RED));
        }
    }

    public static void view(ServerPlayer viewer, ServerPlayer player) {
        VirtualInventoryManager manager = VirtualInventoryManager.getInstance();
        try {
            new VirtualGui(manager.getVirtualInventory(player, 1), manager.getMaxPage(player), viewer).open();
        } catch (LoadInventoryException e) {
            player.sendSystemMessage(Component.translatable("mco.errorMessage.connectionFailure").withStyle(ChatFormatting.RED));
        }
    }

    public static int getMaxPage(ServerPlayer player) {
        return VirtualInventoryManager.getInstance().getMaxPage(player);
    }

    public static void setMaxPage(ServerPlayer player, int value) {
        VirtualInventoryManager.getInstance().setMaxPage(player, value);
    }

    public static void clear(ServerPlayer player) {
        VirtualInventoryManager.getInstance().clear(player);
    }

}
