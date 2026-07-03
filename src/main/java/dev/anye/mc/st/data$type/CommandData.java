package dev.anye.mc.st.data$type;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;

import java.util.List;

public class CommandData {
    public static final PermissionCheck Permission_OP = Commands.LEVEL_ADMINS;
    public static final PermissionCheck Permission_Player = Commands.LEVEL_ALL;
    public final List<String> command;
    public final PermissionCheck permission;
    public final Code code;

    public CommandData(PermissionCheck permission, Code code, String... command) {
        this.code = code;
        this.command = List.of(command);
        this.permission = permission;
    }
    public CommandData(Code code, String... command) {
        this.code = code;
        this.command = List.of(command);
        this.permission = Permission_Player;
    }

    public int code(CommandContext<CommandSourceStack> context) {
        return code.code(context);
    }

    public interface Code {
        int code(CommandContext<CommandSourceStack> context);
    }
    public static CommandData create(PermissionCheck permission, Code code, String... command) {
        return new CommandData(permission, code, command);
    }
    public static CommandData create(Code code, String... command) {
        return new CommandData(Permission_OP, code, command);
    }
}
