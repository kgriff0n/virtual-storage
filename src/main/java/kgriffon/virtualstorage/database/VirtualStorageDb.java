package kgriffon.virtualstorage.database;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.kgriffon.databaseutils.DatabaseLinkException;
import dev.kgriffon.databaseutils.DatabaseQueries;
import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.VirtualStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VirtualStorageDb extends DatabaseQueries {

    @Override
    public void init() {
        try (Connection connection = getConnection()) {
            PreparedStatement storage = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS storage (
                        uuid CHAR(36) NOT NULL,
                        page INT NOT NULL,
                        slot INT NOT NULL,
                        item TEXT NOT NULL,
                        PRIMARY KEY (uuid, page, slot)
                    )""");
            storage.execute();

            PreparedStatement maxStorage = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS max_storage (
                        uuid CHAR(36) PRIMARY KEY,
                        number INT
                    )""");
            maxStorage.execute();
        } catch (SQLException | DatabaseLinkException e) {
            VirtualStorage.LOGGER.error("Unable to initialize tables {}", e.getMessage());
        }
    }

    public Map<Integer, ItemStack> load(UUID uuid, int page) throws LoadInventoryException {
        HashMap<Integer, ItemStack> inventory = new HashMap<>();
        try (Connection connection = getConnection()) {
            String sql = """
                SELECT slot, item
                FROM storage
                WHERE uuid = ?
                AND page = ?;
            """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, page);

            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                int slot = result.getInt("slot");
                String item = result.getString("item");
                try {
                    NbtElement nbt = StringNbtReader.readCompound(item);
                    ItemStack stack = ItemStack.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
                    inventory.put(slot, stack);
                } catch (CommandSyntaxException e) {
                    VirtualStorage.LOGGER.error("Unable to parse item {}", item);
                }
            }
            return inventory;
        } catch (SQLException | DatabaseLinkException e) {
            VirtualStorage.LOGGER.error("Unable to fetch inventory for {} {}", uuid, e.getMessage());
            throw new LoadInventoryException();
        }
    }

    public void save(UUID uuid, int page, Map<Integer, ItemStack> inventory) {
        getExecutor().submit(() -> {
            try (Connection connection = getConnection()) {
                String sql = """
                    DELETE FROM storage
                    WHERE uuid = ?
                    and page = ?
                """;
                PreparedStatement delete = connection.prepareStatement(sql);
                delete.setString(1, uuid.toString());
                delete.setInt(2, page);
                delete.execute();

                sql = "INSERT INTO storage (uuid, page, slot, item) VALUES (?, ?, ?, ?)";
                PreparedStatement insert = connection.prepareStatement(sql);
                for (Map.Entry<Integer, ItemStack> item : inventory.entrySet()) {
                    if (!item.getValue().isEmpty()) {
                        insert.setString(1, uuid.toString());
                        insert.setInt(2, page);
                        insert.setInt(3, item.getKey());
                        insert.setString(4, ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, item.getValue()).getOrThrow().toString());
                        insert.executeUpdate();
                    }
                }
            } catch (SQLException | DatabaseLinkException e) {
                VirtualStorage.LOGGER.error("Unable to save page {} for {} {}", page, uuid, e.getMessage());
            }
        });
    }

    public void clear(UUID uuid) {
        getExecutor().submit(() -> {
            try (Connection connection = getConnection()) {
                String sql = """
                    DELETE FROM storage
                    WHERE uuid = ?
                """;
                PreparedStatement delete = connection.prepareStatement(sql);
                delete.setString(1, uuid.toString());
                delete.execute();
            } catch (SQLException | DatabaseLinkException e) {
                VirtualStorage.LOGGER.error("Unable to clear {} {}", uuid, e.getMessage());
            }
        });
    }

    public int getMaxPage(UUID uuid) {
        try (Connection connection = getConnection()) {
            String sql = """
                    SELECT number
                    FROM max_storage
                    WHERE uuid = ?
                """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("number");
            }
        } catch (SQLException | DatabaseLinkException e) {
            VirtualStorage.LOGGER.error("Unable to read max storage for {} {}", uuid, e.getMessage());
        }
        return 1; // default value
    }

    public void setMaxPage(UUID uuid, int value) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM max_storage WHERE uuid = ?";
            PreparedStatement delete = connection.prepareStatement(sql);
            delete.setString(1, uuid.toString());
            delete.execute();
            delete.close();

            sql = "INSERT INTO max_storage (uuid, number) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, value);
            stmt.execute();
            stmt.close();
        }  catch (SQLException | DatabaseLinkException e) {
            VirtualStorage.LOGGER.error("Unable to save max storage for {} {}", uuid, e.getMessage());
        }
    }
}
