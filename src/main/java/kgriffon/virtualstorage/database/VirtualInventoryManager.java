package kgriffon.virtualstorage.database;

import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.inventory.VirtualInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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

    public int getMaxPage(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (maxPages.containsKey(uuid)) {
            return maxPages.get(uuid);
        } else {
            int maxPage = db.getMaxPage(uuid);
            maxPages.put(player.getUUID(), maxPage);
            return maxPage;
        }
    }

    public void setMaxPage(ServerPlayer player, int value) {
        maxPages.put(player.getUUID(), value);
        db.setMaxPage(player.getUUID(), value);
    }

    public List<VirtualInventory> getAll() {
        List<VirtualInventory> list = new ArrayList<>();
        for (Map.Entry<UUID, List<VirtualInventory>> entry : loadedInventories.entrySet()) {
            list.addAll(entry.getValue());
        }
        return list;
    }

    public VirtualInventory getVirtualInventory(ServerPlayer player, int page) throws LoadInventoryException {
        UUID uuid = player.getUUID();
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

    public void clear(ServerPlayer player) {
        unload(player.getUUID());
        db.clear(player.getUUID());
    }
}
