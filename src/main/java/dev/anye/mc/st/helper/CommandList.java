package dev.anye.mc.st.helper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.data_type.CommandData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandList {
	public List<CommandData> commands;
	private final CommandDispatcher<CommandSourceStack> dispatcher;

	public CommandList(CommandDispatcher<CommandSourceStack> dispatcher) {
		this.dispatcher = dispatcher;
		commands = new ArrayList<>();

		//commands.add(CommandData.create(CommandHelper::login,root,"login"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::reward, "reward"));
		commands.add(CommandData.create(CommandHelper::clearAll, "clear", "all"));
		commands.add(CommandData.create(CommandHelper::clearItem, "clear", "item"));
		commands.add(CommandData.create(CommandHelper::clearEntity, "clear", "entity"));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::trash,"trash"));
		commands.add(CommandData.create(CommandHelper::clearTrash, "trash", "clear"));


		commands.add(CommandData.create(CommandHelper::reload, "reload", "all"));
		commands.add(CommandData.create(CommandHelper::reloadConfig, "reload", "config"));
		commands.add(CommandData.create(CommandHelper::reloadPlayerGroup, "reload", "player_group"));
		commands.add(CommandData.create(CommandHelper::reloadClear, "reload", "clear"));
		commands.add(CommandData.create(CommandHelper::reloadMsg, "reload", "msg"));
		commands.add(CommandData.create(CommandHelper::reloadLanguage, "reload", "language"));
		commands.add(CommandData.create(CommandHelper::reloadBlackList, "reload", "blacklist"));
		commands.add(CommandData.create(CommandHelper::reloadBanItem, "reload", "ban_item"));
		commands.add(CommandData.create(CommandHelper::reloadReward, "reload", "reward"));


		commands.add(CommandData.create(CommandData.Permission_Player, "setHome", CommandHelper::setHome));
		commands.add(CommandData.create(CommandData.Permission_Player, "home", CommandHelper::home));
		commands.add(CommandData.create(CommandData.Permission_Player, "back", CommandHelper::back));
		commands.add(CommandData.create(CommandData.Permission_Player, "tpaAccept", CommandHelper::tpaAccept));
		commands.add(CommandData.create(CommandData.Permission_Player, "tpaDeny", CommandHelper::tpaDeny));
		commands.add(CommandData.create(CommandData.Permission_Player, CommandHelper::my,"my"));


		commands.add(CommandData.create(CommandHelper::shelf,"shelf"));
	}

	public void register(){
		for (CommandData commandData : commands) {
			LiteralArgumentBuilder<CommandSourceStack> c = null;
			List<String> cs = commandData.command;
			int size = cs.size() - 1;
			for (int i = size; i >= 0; i--) {
				if (i == size) {
					c = Commands.literal(cs.get(i))
							.requires(Commands.hasPermission(commandData.permission))
							.executes(commandData::code);
				}else {
					c = Commands.literal(cs.get(i)).then(c);
				}
			}


			if (c != null) dispatcher.register(Commands.literal(commandData.root).then(c));
			else {
				dispatcher.register(Commands.literal(commandData.root)
						.requires(Commands.hasPermission(commandData.permission))
						.executes(commandData::code));
			}
		}
	}
}
