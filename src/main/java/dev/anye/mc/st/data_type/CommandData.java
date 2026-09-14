package dev.anye.mc.st.data_type;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.command.CommandConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandData {
	private static final String ROOT = CommandConfig.I.read(commandData -> commandData.commandRoot(),ST.MOD_ID);
	public static final PermissionCheck Permission_OP = Commands.LEVEL_ADMINS;
	public static final PermissionCheck Permission_Player = Commands.LEVEL_ALL;
	public final String root;
	public final List<String> command;
	public final PermissionCheck permission;
	public final Code code;

	public CommandData(PermissionCheck permission, Code code,String root, String... command) {
		this.code = code;
		this.root = root;
		this.command = List.of(command);
		this.permission = permission;
	}

	private static LiteralArgumentBuilder<CommandSourceStack> formatCommand(String... command) {
		LiteralArgumentBuilder<CommandSourceStack> l = null;
		for (String c : command){
			if (l == null) l = Commands.literal(c);
			else {
				l.then(Commands.literal(c));
			}
		}
		return l;
	}

	public CommandData(Code code, String root,String... command) {
		this(Permission_OP,code,root,command);
	}

	public int code(CommandContext<CommandSourceStack> context) {
		return code.code(context);
	}

	public interface Code {
		int code(CommandContext<CommandSourceStack> context);
	}

	public static CommandData create(PermissionCheck permission,String root, Code code,String... command) {
		return new CommandData(permission, code,root, command);
	}

	public static CommandData create(PermissionCheck permission, Code code,String... command) {
		return new CommandData(permission, code,ROOT, command);
	}

	public static CommandData create(String root,Code code, String... command) {
		return new CommandData(Permission_OP, code,root, command);
	}
	public static CommandData create(Code code,String... command) {
		return new CommandData(Permission_OP, code, ROOT, command);
	}
}
