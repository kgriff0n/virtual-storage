package kgriffon.virtualstorage.inventory;

import kgriffon.virtualstorage.database.VirtualInventoryManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VirtualInventory implements Container {

    private final ServerPlayer player;
    private final int page;
    private final Map<Integer, ItemStack> content;

    public VirtualInventory(ServerPlayer player, int page, Map<Integer, ItemStack> content) {
        this.player = player;
        this.page = page;
        this.content = content;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public void save() {
        VirtualInventoryManager.getInstance().saveVirtualInventory(player.getUUID(), page, content);
    }

    @Override
    public void clearContent() {
        content.clear();
    }

    @Override
    public int getContainerSize() {
        return 45;
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        ItemStack stack = content.get(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return content.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = content.get(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return stack.split(amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = content.remove(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        content.put(slot, stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
