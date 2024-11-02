package nws.mc.servers.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import nws.mc.servers.config.Language;

import java.util.HashMap;

public class TrashBinContainer extends AbstractContainerMenu {
    public static ServerPlayer nowPlayer = null;
    public static final HashMap<Integer, ItemStack> slots = new HashMap<>();
    private final ItemStackHandler PREV_PAGE_ITEM = new ItemStackHandler();
    private final ItemStackHandler HOME_PAGE_ITEM = new ItemStackHandler();
    private final ItemStackHandler NEXT_PAGE_ITEM = new ItemStackHandler();
    private final ItemStackHandler X = new ItemStackHandler();
    private final ItemStackHandler itemHandler;
    private int nowPage;
    public TrashBinContainer(int id, Inventory playerInventory) {
        super(MenuType.GENERIC_9x6, id);
        nowPage = 0;
        this.itemHandler = new ItemStackHandler(45);
        ItemStack itemStack = new ItemStack(Items.ARROW);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("trash.menu.button.previous_page"));
        PREV_PAGE_ITEM.setStackInSlot(0, itemStack);
        itemStack = new ItemStack(Items.BOOK);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("trash.menu.button.home_page"));
        HOME_PAGE_ITEM.setStackInSlot(0, itemStack);
        itemStack = new ItemStack(Items.ARROW);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("trash.menu.button.next_page"));
        NEXT_PAGE_ITEM.setStackInSlot(0, itemStack);
        itemStack = new ItemStack(Items.BARRIER);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("trash.menu.button.tips"));
        X.setStackInSlot(0, itemStack);
        loadItems();
        setupSlots(playerInventory);
    }

    private void setupSlots(Inventory playerInventory) {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(itemHandler, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }
        this.addSlot(new SlotButton(PREV_PAGE_ITEM, 0, 8, 142, player -> switchPage(-1)));
        this.addSlot(new SlotButton(X, 0, 26, 142, null));
        this.addSlot(new SlotButton(X, 0, 44, 142, null));
        this.addSlot(new SlotButton(X, 0, 62, 142, null));
        this.addSlot(new SlotButton(HOME_PAGE_ITEM, 0, 80, 142, player -> switchPageTo(0)));
        this.addSlot(new SlotButton(X, 0, 98, 142, null));
        this.addSlot(new SlotButton(X, 0, 116, 142, null));
        this.addSlot(new SlotButton(X, 0, 134, 142, null));
        this.addSlot(new SlotButton(NEXT_PAGE_ITEM, 0, 152, 142, player -> switchPage(1)));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    private void switchPage(int offset) {
        saveItems();
        nowPage += offset;
        int maxPage = (slots.size() / 45)-1;
        if (slots.size() % 45 != 0) maxPage++;
        maxPage = Math.max(0,maxPage);
        if (nowPage < 0) nowPage = maxPage;
        if (nowPage > maxPage) nowPage = 0;
        refreshPage();
    }
    private void switchPageTo(int page) {
        saveItems();
        nowPage = page;
        refreshPage();
    }
    private void refreshPage() {
        int num = itemHandler.getSlots();
        int pageIndex = nowPage * 45;
        for (int i = 0; i < num; i++) {
            itemHandler.setStackInSlot(i,slots.getOrDefault(pageIndex+i, ItemStack.EMPTY));
        }
        /*
        int start = currentPage * 45;
        for (int i = 0; i < 45; i++) {
            if (start + i < itemHandler.getSlots()) {
                setItem(i,0, itemHandler.getStackInSlot(start + i));
            } else {
                setItem(i,0, ItemStack.EMPTY); // 清空多余槽位
            }
        }

         */
    }
    private void loadItems() {
        refreshPage();
    }
    public void saveItems() {
        int num = itemHandler.getSlots();
        int pageIndex = nowPage * 45;
        for (int i = 0; i < num; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) slots.put(pageIndex + i, itemHandler.getStackInSlot(i));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
    @Override
    public void removed(Player player) {
        super.removed(player);
        saveItems();
        nowPlayer = null;
    }
}