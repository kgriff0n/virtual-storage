package kgriffon.virtualstorage.database;

import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.inventory.VirtualInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualInventoryManager {

    private static VirtualInventoryManager instance = null;

    private VirtualStorageDb db;
    private ConcurrentHashMap<UUID, List<VirtualInventory>> loadedInventories;
    private ConcurrentHashMap<UUID, Integer> maxPages;

    public static void initialize(VirtualStorageDb db) {
        new VirtualInventoryManager(db);
        db.init();
    }

    public static VirtualInventoryManager getInstance() {
        if (instance == null) {
            throw new NullPointerException("Inventory manager not initialized!");
        }
        return instance;
    }

    private VirtualInventoryManager(VirtualStorageDb db) {
        if (instance == null) {
            this.db = db;
            this.loadedInventories = new ConcurrentHashMap<>();
            this.maxPages = new ConcurrentHashMap<>();
            instance = this;
        }
    }

    public void unload(UUID uuid) {
        loadedInventories.remove(uuid);
        maxPages.remove(uuid);
    }

    public int getMaxPage(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (maxPages.containsKey(uuid)) {
            return maxPages.get(uuid);
        } else {
            int maxPage = db.getMaxPage(uuid);
            maxPages.put(player.getUuid(), maxPage);
            return maxPage;
        }
    }

    public void setMaxPage(ServerPlayerEntity player, int value) {
        maxPages.put(player.getUuid(), value);
        db.setMaxPage(player.getUuid(), value);
    }

    public List<VirtualInventory> getAll() {
        List<VirtualInventory> list = new ArrayList<>();
        for (Map.Entry<UUID, List<VirtualInventory>> entry : loadedInventories.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    public VirtualInventory getVirtualInventory(ServerPlayerEntity player, int page) throws LoadInventoryException {
        UUID uuid = player.getUuid();
        List<VirtualInventory> inventories = loadedInventories.get(uuid);
        if (inventories == null) {
            inventories = new ArrayList<>();
        }

        VirtualInventory inventory = null;
        for (VirtualInventory inv : inventories) {
            if (inv.getPage() == page) {
                inventory = inv;
            }
        }

        if (inventory == null) {
            inventory = new VirtualInventory(player, page, db.load(uuid, page));
            inventories.add(inventory);
        }

        loadedInventories.put(uuid, inventories);
        return inventory;
    }

    public void saveVirtualInventory(UUID uuid, int page, Map<Integer, ItemStack> inventory) {
        db.save(uuid, page, inventory);
    }

    public void clear(ServerPlayerEntity player) {
        unload(player.getUuid());
        db.clear(player.getUuid());
    }
}
