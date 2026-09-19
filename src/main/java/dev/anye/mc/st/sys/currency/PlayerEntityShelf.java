package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.exception._IOException;
import dev.anye.core.system._File;
import dev.anye.core.time.FastDateTime;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.shelf.entity.ShelfEntity;
import dev.anye.mc.st.config.currency.shelf.entity.ShelfEntityConfig;
import dev.anye.mc.st.config.currency.shelf.entity.ShelfEntityData;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.data_type.IShelf;
import dev.anye.mc.st.helper.EntityHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class PlayerEntityShelf implements IShelf<Entity> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ShelfEntityConfig config = new ShelfEntityConfig();
	private final Map<String, ShelfEntityData> entities = new HashMap<>();
	public PlayerEntityShelf(){
		loadEntities();
	}

	public void reload(){
		config.reload();
		loadEntities();
	}



	public void loadEntities(){
		entities.clear();
		_File.getFiles(ConfigDir.CURRENCY_SHELF_ENTITY, _SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			ShelfEntity item = new ShelfEntity(uuid,null);
			if (item.data() != null){
				entities.put(uuid,item.data());
			}
		});
		LOGGER.debug("load shelf entity count : {}",entities.size());
	}

	public boolean itemConfigIsLoad(){
		if (config.data() == null) {
			LOGGER.debug("Entity Shelf Config is NULL");
			return false;
		}
		return true;
	}

	@Override
	public int count() {
		return entities.size();
	}

	@Override
	public List<ItemStack> all(ServerPlayer serverPlayer) {

		if (itemConfigIsLoad()){
			FastDateTime fastDateTime = new FastDateTime();
			List<ItemStack> itemStacks = new ArrayList<>();
			entities.forEach((s, shelfEntityData) -> {
				EntityType<?> entityType = shelfEntityData.getEntityType();
				ItemStack item = EntityHelper.getSpawnEgg(entityType,new ItemStack(Items.EGG));
				CustomData.update(DataComponents.CUSTOM_DATA,item, compoundTag -> compoundTag.putString(idKey(),s));
				ItemLore itemLore = item.getOrDefault(DataComponents.LORE,ItemLore.EMPTY)
						.withLineAdded(entityType.getDescription()
						)
						.withLineAdded(Component.literal(shelfEntityData.price() + " ")
								.append(Language.getComponent(null,"currency.st.name"))
								.withColor(TextColor.GOLD)
						)
						.withLineAdded(Component.literal(fastDateTime.setEpochMillis(shelfEntityData.time()).toDateString("-"))
								.append(" ")
								.append(fastDateTime.toTimeString(":"))
								.withColor(TextColor.BLUE)
						);
				item.set(DataComponents.LORE,itemLore);

				itemStacks.add(item);
			});
			return itemStacks;
		}
		return List.of();
	}

	public boolean checkType(ServerPlayer serverPlayer, Entity entity){
		if (config.data().type() == 0) return true;
		if (config.data().type() == 1){
			return entity instanceof Animal;
		}else if (config.data().type() == 2) {
			return entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() instanceof Player player && player.getStringUUID().equals(serverPlayer.getStringUUID());
		}
		return false;
	}

	@Override
	public boolean sell(@NotNull ServerPlayer serverPlayer, Entity entity, double price){
		if (itemConfigIsLoad() && checkType(serverPlayer,entity)) {
			if (!config.data().checkPrice(price)) {
				MsgHelper.sendMsgToPlayerF(serverPlayer, "shelf.st.entity.player.error.price");
			}
			double fee = config.data().getFee(price);
			if (PlayerCurrency.getPlayerCurrency(serverPlayer).sub(fee, "entity sell fee")) {
				String uuid = System.currentTimeMillis() + "_" + serverPlayer.getStringUUID();
				String fileTmp = uuid;
				int i = 0;
				while (entities.containsKey(fileTmp)) {
					i++;
					fileTmp = uuid + "_" + i;
				}
				if (entity instanceof Leashable leashable) {
					leashable.dropLeash();
				}
				ShelfEntityData data = new ShelfEntityData(serverPlayer, price, entity);
				new ShelfEntity(fileTmp, data);
				entities.put(fileTmp, data);
				entity.discard();
				return true;
			}
			MsgHelper.sendMsgToPlayerF(serverPlayer, "shelf.st.entity.player.error.insufficient_funds");
			return false;
		}
		return false;
	}


	@Override
	public boolean buy(ServerPlayer serverPlayer, String key){
		if (itemConfigIsLoad() && entities.containsKey(key)) {
			ShelfEntityData data = entities.get(key);
			PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
			String u = data.playerUUID();
			if (playerCurrency.sub(data.price(), "buy entity '" + key + "'")) {
				Entity entity = data.getEntity(serverPlayer);
				if (entity == null) return false;
				if (entity instanceof OwnableEntity){
					entity.getEntityData().set(TamableAnimal.DATA_OWNERUUID_ID, Optional.of(serverPlayer).map(EntityReference::of));
				}
				entity.setPos(serverPlayer.getX(),serverPlayer.getY(),serverPlayer.getZ());
				serverPlayer.level().addFreshEntity(entity);
				remove(key);

				playerCurrency = PlayerCurrency.getPlayerCurrency(u, serverPlayer);
				return playerCurrency.add(data.price() - config.data().getTax(data.price()), "sell entity '" + key + "'");
			}
		} else {
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.entity.buy.error.not_have");
		}
		return false;
	}


	@Override
	public void remove(String key){
		entities.remove(key);
		try {
			Files.move(Paths.get(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ENTITY, key + _SuffixCDT.JSON_SUFFIX)),Paths.get(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ENTITY_LOG, key + _SuffixCDT.JSON_SUFFIX)));
		} catch (IOException e) {
			throw new _IOException(e);
		}
	}
}
