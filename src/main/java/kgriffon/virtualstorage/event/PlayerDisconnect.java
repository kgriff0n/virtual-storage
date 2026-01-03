package kgriffon.virtualstorage.event;

import kgriffon.virtualstorage.database.VirtualInventoryManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerDisconnect implements ServerPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        VirtualInventoryManager.getInstance().unload(serverPlayNetworkHandler.player.getUuid());
    }
}
