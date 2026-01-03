package kgriffon.virtualstorage.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import kgriffon.virtualstorage.api.VirtualStorageApi;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VirtualStorageCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("virtual-storage")
                        .requires(Permissions.require("virtualstorage", 2))
                        .then(literal("open")
                                .requires(Permissions.require("virtualstorage.open", 2))
                                .executes(context -> open(context.getSource(), 1))
                                .then(argument("page", IntegerArgumentType.integer(1))
                                        .requires(Permissions.require("virtualstorage.open.page", 2))
                                        .executes(context -> open(context.getSource(), IntegerArgumentType.getInteger(context, "page")))
                                )
                        )
                        .then(literal("admin")
                                .requires(Permissions.require("virtualstorage.admin", 4))
                                .then(literal("open")
                                        .then(argument("player", EntityArgumentType.player())
                                                .executes(context -> preview(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                                        )
                                )
                                .then(literal("clear")
                                        .then(argument("player", EntityArgumentType.player())
                                                .executes(context -> clear(EntityArgumentType.getPlayer(context, "player")))
                                        )
                                )
                                .then(literal("max_page")
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(literal("set")
                                                        .then(argument("number", IntegerArgumentType.integer(1))
                                                                .executes(context -> setPage(EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "number")))
                                                        )
                                                )
                                                .then(literal("increment")
                                                        .executes(context -> setPage(EntityArgumentType.getPlayer(context, "player"), VirtualStorageApi.getMaxPage(EntityArgumentType.getPlayer(context, "player")) + 1))
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int open(ServerCommandSource source, int page) {
        VirtualStorageApi.open(source.getPlayer(), page);
        return Command.SINGLE_SUCCESS;
    }

    private static int preview(ServerCommandSource source, ServerPlayerEntity player) {
        ServerPlayerEntity admin = source.getPlayer();
        if (admin != null) {
            VirtualStorageApi.view(admin, player);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clear(ServerPlayerEntity player) {
        VirtualStorageApi.clear(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int setPage(ServerPlayerEntity player, int value) {
        VirtualStorageApi.setMaxPage(player, value);
        return Command.SINGLE_SUCCESS;
    }
}
