package dev.anye.mc.st.helper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.data_type.CommandData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
	private static final String root = CommandConfig.I.getData().commandRoot();
	public List<CommandData> commands;
	private final CommandDispatcher<CommandSourceStack> dispatcher;

	public CommandList(CommandDispatcher<CommandSourceStack> dispatcher) {
		this.dispatcher = dispatcher;
		commands = new ArrayList<>();

		//commands.add(CommandData.create(CommandHelper::login,root,"login"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::reward, root, "reward"));
		commands.add(CommandData.create(CommandHelper::clearAll, root, "clear", "all"));
		commands.add(CommandData.create(CommandHelper::clearItem, root, "clear", "item"));
		commands.add(CommandData.create(CommandHelper::clearEntity, root, "clear", "entity"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::trash, root, "trash"));
		commands.add(CommandData.create(CommandHelper::clearTrash, root, "trash", "clear"));


		commands.add(CommandData.create(CommandHelper::reload, root, "reload", "all"));
		commands.add(CommandData.create(CommandHelper::reloadConfig, root, "reload", "config"));
		commands.add(CommandData.create(CommandHelper::reloadPlayerGroup, root, "reload", "player_group"));
		commands.add(CommandData.create(CommandHelper::reloadClear, root, "reload", "clear"));
		commands.add(CommandData.create(CommandHelper::reloadMsg, root, "reload", "msg"));
		commands.add(CommandData.create(CommandHelper::reloadLanguage, root, "reload", "language"));
		commands.add(CommandData.create(CommandHelper::reloadBlackList, root, "reload", "blacklist"));
		commands.add(CommandData.create(CommandHelper::reloadBanItem, root, "reload", "ban_item"));
		commands.add(CommandData.create(CommandHelper::reloadReward, root, "reload", "reward"));


		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::setHome, "setHome"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::home, "home"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::back, "back"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::tpaAccept, "tpaAccept"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::tpaDeny, "tpaDeny"));


	}

	public void register() {
		for (CommandData commandData : commands) {
			LiteralArgumentBuilder<CommandSourceStack> c = null;
			List<String> cs = commandData.command;
			int size = cs.size() - 1;
			for (int i = size; i >= 0; i--) {
				if (i == size) {
					c = Commands.literal(cs.get(i))
							.requires(Commands.hasPermission(commandData.permission))
							.executes(commandData::code);
				} else {
					c = Commands.literal(cs.get(i)).then(c);
				}
			}
			dispatcher.register(c);
		}
	}
}
