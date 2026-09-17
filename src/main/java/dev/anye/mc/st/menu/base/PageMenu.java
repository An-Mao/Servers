package dev.anye.mc.st.menu.base;

import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.menu.STMenu;
import dev.anye.mc.st.menu.button.SlotButton;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class PageMenu extends AbstractContainerMenu {
	protected static final Map<Integer,Vec2> ITEM_SLOTS_INDEX = slotsIndex();
	private static final Logger LOGGER = LogUtils.getLogger();

	protected final ItemStacksResourceHandler pageItem = new ItemStacksResourceHandler(6);
	protected final Inventory playerInventory;

	protected final ItemStacksResourceHandler itemHandler;

	protected int pageIndex = 0;
	protected int maxPage;

	protected final int pageItemNumber;
	/**
	 * 初始物品缓存
	 */
	protected final Map<Integer,Slot> itemSlots = new HashMap<>();

	protected final ServerPlayer serverPlayer;

	public static Map<Integer, Vec2> slotsIndex() {
		Map<Integer,Vec2> map = new HashMap<>();
		int i = -1;
		for (int x = 0; x < 9; x++){
			for (int y = 0; y < 6; y++) {
				i++;
				map.put(i, new Vec2(8 + x * 18,18 + y * 18));
			}
		}
		return map;
	}


	protected PageMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,int allItemNumber,int pageItemNumber) {
		super(menuType, containerId);
		this.playerInventory = playerInventory;
		this.serverPlayer = (ServerPlayer) playerInventory.player;

		this.pageItemNumber = pageItemNumber;

		this.maxPage = getMaxPage(allItemNumber,pageItemNumber);

		itemHandler = new ItemStacksResourceHandler(this.pageItemNumber);
		//用空白(Air)物品填充
		fillWithEmpty();


		//初始化物品
		initPageItem();
		//----------------------------------------------
		//添加静态按钮
		addPageButton();
		//添加库存按钮
		addInventory();
		//填充未进行添加的slot
		int maxIndex = getItemSlotsMaxIndex();
		for (int i = 0; i < maxIndex; i ++){
			if (!itemSlots.containsKey(i)) {
				addItemSlot(i,this::emptyButton);
			}
		}
		//触发一次页面刷新
		pageRefresh();

		//----------------------------------------------
		//将缓存的itemSlot应用
		setAllSlots();
	}

	/**
	 * 用EMPTY填充itemHandler，可以解决一些超限问题。
	 */
	public void fillWithEmpty(){
		for (int i = 0; i < this.itemHandler.size(); i++){
			itemHandler.set(i,ItemResource.EMPTY,0);
		}
	}

	/**
	 * 添加物品到物品缓存
	 * @param index 索引
	 * @param slot 要执行的操作
	 */
	public void addItemSlot(int index, ISlot slot){
		Vec2 vec2 = ITEM_SLOTS_INDEX.get(index);
		if (vec2 != null){
			itemSlots.put(index,slot.slot(vec2.x, vec2.y));
		}
	}
	public abstract void addPageButton();



	public void initPageItem(){
		//空白
		pageItem.set(0, ItemResource.EMPTY, 0);
		//禁用按钮
		ItemStack itemStack = new ItemStack(Items.BARRIER);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"menu.st.button.tips"));
		pageItem.set(1, ItemResource.of(itemStack), 1);
		//上一页
		itemStack = new ItemStack(Items.ECHO_SHARD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"menu.st.button.previous_page"));
		pageItem.set(2, ItemResource.of(itemStack), 1);

		//第一页
		itemStack = new ItemStack(Items.NETHER_STAR);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"menu.st.button.first_page"));
		pageItem.set(3, ItemResource.of(itemStack), 1);

		//下一页
		itemStack = new ItemStack(Items.AMETHYST_SHARD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"menu.st.button.next_page"));
		pageItem.set(4, ItemResource.of(itemStack), 1);

		//主页
		itemStack = new ItemStack(Items.ENDER_EYE);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"menu.st.button.home_page"));
		pageItem.set(5, ItemResource.of(itemStack), 1);

	}

	public int getItemSlotsMaxIndex(){
		int[] d ={
				0
		};
		itemSlots.keySet().forEach(integer -> {
			if (d[0] < integer) d[0] = integer;
		});
		return d[0] + 1;
	}

	public void setAllSlots() {
		/*
		List<Slot> slotList = new ArrayList<>(itemSlots.values());
		itemSlots.forEach(slotList::set);
		slotList.forEach(this::addSlot);
		*/
		int maxIndex = getItemSlotsMaxIndex();
		LOGGER.debug("max index {}",maxIndex);
		for (int i = 0; i < maxIndex; i++){
			Slot s = itemSlots.get(i);

			if (s != null){
				this.addSlot(s);
			}else {
				LOGGER.warn("The index has a gap, which should not normally occur. Check whether the index of the added items is continuous => itemSlots index {}",i);
				Vec2 index = ITEM_SLOTS_INDEX.getOrDefault(i,new Vec2(0,0));
				this.addSlot(this.emptyButton(index.x,index.y));
			}
		}


		addPlayerInventory();
		addPlayerHotBar();
	}


	public void addPlayerInventory(){
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
	}


	public void addPlayerHotBar(){
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}


	public abstract void addInventory();

	public SlotButton emptyButton(int xp, int yp){
		return new SlotButton(pageItem, pageItem::set, 0, xp, yp, null);
	}
	public SlotButton xButton(int xp,int yp){
		return new SlotButton(pageItem, pageItem::set, 1, xp, yp, null);
	}
	public SlotButton prevButton(int xp,int yp){
		return new SlotButton(pageItem, pageItem::set, 2,  xp, yp, _ -> prevPage());
	}
	public SlotButton firstButton(int xp, int yp){
		return new SlotButton(pageItem, pageItem::set, 3, xp, yp,  _ -> homePage());
	}
	public SlotButton nextButton(int xp,int yp){
		return new SlotButton(pageItem, pageItem::set, 4, xp, yp,_ -> nextPage());
	}

	public SlotButton homeButton(int xp,int yp){
		return new SlotButton(pageItem, pageItem::set, 5, xp, yp, _ -> STMenu.open(serverPlayer));
	}

	public void prevPage(){
		switchPage(-1);
	}
	public void homePage(){
		switchPageTo(0);
	}

	public void nextPage(){
		switchPage(1);
	}


	public void switchPage(int offset) {
		pageIndex += offset;
		if (pageIndex < 0) pageIndex = maxPage;
		if (pageIndex > maxPage) pageIndex = 0;
		pageRefresh();
	}

	public void switchPageTo(int index) {
		pageIndex = index;
		pageRefresh();
	}

	public abstract void pageRefresh();

	public void setMaxPage(int allItemNumber){
		this.maxPage = getMaxPage(allItemNumber,pageItemNumber);
		if (this.pageIndex > this.maxPage){
			homePage();
		}
	}

	public static int getMaxPage(int allItemNumber,int pageItemNumber){
		int maxPage = (allItemNumber / pageItemNumber) - 1;
		if (allItemNumber % pageItemNumber != 0) maxPage++;
		return Math.max(0, maxPage);
	}
	public record Vec2(int x,int y){

	}
	public interface ISlot{
		Slot slot(int x,int y);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}
}
