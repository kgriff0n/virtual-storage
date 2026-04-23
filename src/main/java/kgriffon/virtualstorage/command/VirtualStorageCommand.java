package kgriffon.virtualstorage.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import kgriffon.virtualstorage.api.VirtualStorageApi;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class VirtualStorageCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) ->
                dispatcher.register(literal("virtual-storage")
                        .requires(Permissions.require("virtualstorage", PermissionLevel.GAMEMASTERS))
                        .then(literal("open")
                                .requires(Permissions.require("virtualstorage.open", PermissionLevel.GAMEMASTERS))
                                .executes(context -> open(context.getSource(), 1))
                                .then(argument("page", IntegerArgumentType.integer(1))
                                        .requires(Permissions.require("virtualstorage.open.page", PermissionLevel.GAMEMASTERS))
                                        .executes(context -> open(context.getSource(), IntegerArgumentType.getInteger(context, "page")))
                                )
                        )
                        .then(literal("admin")
                                .requires(Permissions.require("virtualstorage.admin", PermissionLevel.ADMINS))
                                .then(literal("open")
                                        .then(argument("player", EntityArgument.player())
                                                .executes(context -> preview(context.getSource(), EntityArgument.getPlayer(context, "player")))
                                        )
                                )
                                .then(literal("clear")
                                        .then(argument("player", EntityArgument.player())
                                                .executes(context -> clear(EntityArgument.getPlayer(context, "player")))
                                        )
                                )
                                .then(literal("max_page")
                                        .then(argument("player", EntityArgument.player())
                                                .then(literal("set")
                                                        .then(argument("number", IntegerArgumentType.integer(1))
                                                                .executes(context -> setPage(EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "number")))
                                                        )
                                                )
                                                .then(literal("increment")
                                                        .executes(context -> setPage(EntityArgument.getPlayer(context, "player"), VirtualStorageApi.getMaxPage(EntityArgument.getPlayer(context, "player")) + 1))
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int open(CommandSourceStack source, int page) {
        VirtualStorageApi.open(source.getPlayer(), page);
        return Command.SINGLE_SUCCESS;
    }

    private static int preview(CommandSourceStack source, ServerPlayer player) {
        ServerPlayer admin = source.getPlayer();
        if (admin != null) {
            VirtualStorageApi.view(admin, player);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clear(ServerPlayer player) {
        VirtualStorageApi.clear(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int setPage(ServerPlayer player, int value) {
        VirtualStorageApi.setMaxPage(player, value);
        return Command.SINGLE_SUCCESS;
    }
}
