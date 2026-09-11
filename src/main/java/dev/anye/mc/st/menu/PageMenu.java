package dev.anye.mc.st.menu;

import dev.anye.mc.st.config.Language;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class PageMenu extends AbstractContainerMenu {
	protected static final Map<Integer,Vec2> ITEM_SLOTS_INDEX = slotsIndex();

	protected final ItemStacksResourceHandler pageItem = new ItemStacksResourceHandler(4);
	protected final Inventory playerInventory;

	protected final ItemStacksResourceHandler itemHandler;

	protected int pageIndex = 0;
	protected int maxPage;

	protected final int pageItemNumber;
	protected final boolean border;
	protected final int pageButtonStartIndex;
	protected final Map<Integer,Slot> itemSlots = new HashMap<>();

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




	protected PageMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,int allItemNumber,int pageItemNumber,boolean border,int pageButtonStartIndex) {
		super(menuType, containerId);
		this.border = border;
		this.playerInventory = playerInventory;
		this.pageButtonStartIndex = pageButtonStartIndex;
		this.pageItemNumber = pageItemNumber;
		this.maxPage = getMaxPage(allItemNumber,pageItemNumber);
		itemHandler = new ItemStacksResourceHandler(this.pageItemNumber);
		initItem();
		initSlots();
		pageRefresh();
		setAllSlots();
	}

	public void addItemSlot(int index, ISlot slot){
		Vec2 vec2 = ITEM_SLOTS_INDEX.get(index);
		if (vec2 != null){
			itemSlots.put(index,slot.slot(vec2.x, vec2.y));
		}
	}
	public void initSlots() {
		/*
		0  1  2  3  4  5  6  7  8
		9  10 11 12 13 14 15 16 17
		18 19 20 21 22 23 24 25 26

		27 28 29 30 31 32 33 34 35
		36 37 38 39 40 41 42 43 44
		45 46 47 48 49 50 51 52 53
		 */
		addItemSlot(pageButtonStartIndex,this::xButton);
		addItemSlot(pageButtonStartIndex + 1,this::xButton);
		addItemSlot(pageButtonStartIndex + 2,this::prevButton);
		addItemSlot(pageButtonStartIndex + 3,this::xButton);
		addItemSlot(pageButtonStartIndex + 4,this::homeButton);
		addItemSlot(pageButtonStartIndex + 5,this::xButton);
		addItemSlot(pageButtonStartIndex + 6,this::nextButton);
		addItemSlot(pageButtonStartIndex + 7,this::xButton);
		addItemSlot(pageButtonStartIndex + 8,this::xButton);
		if (border) {
			addItemSlot(0, this::xButton);
			addItemSlot(1, this::xButton);
			addItemSlot(2, this::xButton);
			addItemSlot(3, this::xButton);
			addItemSlot(4, this::xButton);
			addItemSlot(5, this::xButton);
			addItemSlot(6, this::xButton);
			addItemSlot(7, this::xButton);
			addItemSlot(8, this::xButton);

			addItemSlot(9, this::xButton);
			addItemSlot(17, this::xButton);

			addItemSlot(18, this::xButton);
			addItemSlot(26, this::xButton);

			addItemSlot(27, this::xButton);
			addItemSlot(35, this::xButton);

			addItemSlot(36, this::xButton);
			addItemSlot(44, this::xButton);
		}
		addInventory();
	}



	public void initItem(){
		ItemStack itemStack = new ItemStack(Items.ECHO_SHARD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"trash.menu.button.previous_page"));
		pageItem.set(1, ItemResource.of(itemStack), 1);

		itemStack = new ItemStack(Items.NETHER_STAR);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"trash.menu.button.home_page"));
		pageItem.set(2, ItemResource.of(itemStack), 1);

		itemStack = new ItemStack(Items.AMETHYST_SHARD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"trash.menu.button.next_page"));
		pageItem.set(3, ItemResource.of(itemStack), 1);


		itemStack = new ItemStack(Items.BARRIER);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent((ServerPlayer) playerInventory.player,"trash.menu.button.tips"));
		pageItem.set(0, ItemResource.of(itemStack), 1);
	}


	public void setAllSlots() {
		List<Slot> slotList = new ArrayList<>(Collections.nCopies(itemSlots.size(), null));
		itemSlots.forEach(slotList::set);
		slotList.forEach(slot -> {
			if (slot != null) this.addSlot(slot);
		});

		//itemSlots.forEach(this.slots::set);

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

	public SlotButton<?> xButton(int xp,int yp){
		return new SlotButton<>(pageItem, pageItem::set, 0, xp, yp, null,null);
	}
	public SlotButton<?> prevButton(int xp,int yp){
		return new SlotButton<>(pageItem, pageItem::set, 1,  xp, yp, _ -> prevPage(),null);
	}
	public SlotButton<?> homeButton(int xp, int yp){
		return new SlotButton<>(pageItem, pageItem::set, 2, xp, yp,  _ -> homePage(),null);
	}
	public SlotButton<?> nextButton(int xp,int yp){
		return new SlotButton<>(pageItem, pageItem::set, 3, xp, yp,_ -> nextPage(),null);
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
}
