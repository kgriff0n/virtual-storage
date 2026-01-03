package kgriffon.virtualstorage.inventory;

import kgriffon.virtualstorage.database.VirtualInventoryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public class VirtualInventory implements Inventory {

    private final ServerPlayerEntity player;
    private final int page;
    private final Map<Integer, ItemStack> content;

    public VirtualInventory(ServerPlayerEntity player, int page, Map<Integer, ItemStack> content) {
        this.player = player;
        this.page = page;
        this.content = content;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public void save() {
        VirtualInventoryManager.getInstance().saveVirtualInventory(player.getUuid(), page, content);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public int size() {
        return 45;
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        ItemStack stack = content.get(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return content.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = content.get(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return stack.split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = content.remove(slot);
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        content.put(slot, stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
