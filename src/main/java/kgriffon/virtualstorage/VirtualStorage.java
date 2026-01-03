package kgriffon.virtualstorage;

import dev.kgriffon.databaseutils.DatabaseUtils;
import dev.kgriffon.databaseutils.Type;
import dev.kgriffon.databaseutils.database.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kgriffon.virtualstorage.command.VirtualStorageCommand;
import kgriffon.virtualstorage.database.VirtualInventoryManager;
import kgriffon.virtualstorage.database.VirtualStorageDb;
import kgriffon.virtualstorage.event.PlayerDisconnect;
import kgriffon.virtualstorage.inventory.VirtualInventory;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualStorage implements ModInitializer {
	public static final String MOD_ID = "virtual-storage";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer SERVER;

	@Override
	public void onInitialize() {

        PolymerResourcePackUtils.addModAssets(MOD_ID);

		if (!Config.exist()) {
			Config.createConfigFile();
			Config.writeDefaultConfig();
		}

		Config.loadFile();

        Type type = DatabaseUtils.getType(Config.storage);
        Database db = getDatabase(type);

        VirtualStorageDb virtualStorage = new VirtualStorageDb();
        virtualStorage.link(db);
		VirtualInventoryManager.initialize(virtualStorage);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			for (VirtualInventory inventory : VirtualInventoryManager.getInstance().getAll()) {
				inventory.save();
			}
            db.disconnect();
        });
		ServerPlayConnectionEvents.DISCONNECT.register(new PlayerDisconnect());

		VirtualStorageCommand.register();
		LOGGER.info("Virtual Storage Loaded");
	}

    private static Database getDatabase(Type type) {
        Database db = null;
        switch (type) {
            case MYSQL -> db = new MySQL(Config.host, Config.port, Config.database, Config.user, Config.password);
            case MARIADB -> db = new MariaDB(Config.host, Config.port, Config.database, Config.user, Config.password);
            case POSTGRESQL -> db = new PostgreSQL(Config.host, Config.port, Config.database, Config.user, Config.password);
            case SQLITE -> db = new SQLite(Config.database);
            case TEST -> db = new TestDB();
        }
        return db;
    }
}