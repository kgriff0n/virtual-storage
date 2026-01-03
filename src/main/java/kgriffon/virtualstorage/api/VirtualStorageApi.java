package kgriffon.virtualstorage.api;

import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.database.VirtualInventoryManager;
import kgriffon.virtualstorage.gui.VirtualGui;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VirtualStorageApi {

    public static void open(ServerPlayerEntity player) {
        open(player, 1);
    }

    public static void open(ServerPlayerEntity player, int page) {
        VirtualInventoryManager manager = VirtualInventoryManager.getInstance();
        int maxPage = manager.getMaxPage(player);
        if (page > maxPage) {
            page = maxPage;
        }
        try {
            new VirtualGui(manager.getVirtualInventory(player, page), maxPage).open();
        } catch (LoadInventoryException e) {
            player.sendMessage(Text.translatable("mco.errorMessage.connectionFailure").formatted(Formatting.RED));
        }
    }

    public static void view(ServerPlayerEntity viewer, ServerPlayerEntity player) {
        VirtualInventoryManager manager = VirtualInventoryManager.getInstance();
        try {
            new VirtualGui(manager.getVirtualInventory(player, 1), manager.getMaxPage(player), viewer).open();
        } catch (LoadInventoryException e) {
            player.sendMessage(Text.translatable("mco.errorMessage.connectionFailure").formatted(Formatting.RED));
        }
    }

    public static int getMaxPage(ServerPlayerEntity player) {
        return VirtualInventoryManager.getInstance().getMaxPage(player);
    }

    public static void setMaxPage(ServerPlayerEntity player, int value) {
        VirtualInventoryManager.getInstance().setMaxPage(player, value);
    }

    public static void clear(ServerPlayerEntity player) {
        VirtualInventoryManager.getInstance().clear(player);
    }

}
