package dev.anye.mc.st.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import dev.anye.mc.st.config.Language;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TrashBinContainer extends AbstractContainerMenu {
	private static final int SLOT_NUM = 45;
    public  static ServerPlayer nowPlayer = null;
    public  static final HashMap<Integer,ItemStack> SLOTS = new HashMap<>();
    private final ItemStackHandler PREV_PAGE_ITEM          = new ItemStackHandler(1);
    private final ItemStackHandler HOME_PAGE_ITEM = new ItemStackHandler(1);
    private final ItemStackHandler NEXT_PAGE_ITEM = new ItemStackHandler(1);
    private final ItemStackHandler X              = new ItemStackHandler(1);
    private final ItemStackHandler itemHandler;
    private int nowPage;
    public TrashBinContainer(int id, Inventory playerInventory) {
        super(MenuType.GENERIC_9x6, id);
        nowPage = 0;
        this.itemHandler = new ItemStackHandler(SLOT_NUM);
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
        this.addSlot(new SlotButton(PREV_PAGE_ITEM, 0,8, 142, player -> switchPage(-1)));
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
        int maxPage = (SLOTS.size() / SLOT_NUM)-1;
        if (SLOTS.size() % SLOT_NUM != 0) maxPage++;
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
        int pageIndex = nowPage * SLOT_NUM;
        for (int i = 0; i < num; i++) {
            ItemStack itemStack = SLOTS.getOrDefault(pageIndex+i, ItemStack.EMPTY);
            itemHandler.setStackInSlot(i, itemStack);
        }
    }
    private void loadItems() {
        refreshPage();
    }
    public void saveItems() {
        int pageIndex = nowPage * SLOT_NUM;
        for (int i = 0; i < SLOT_NUM; i++) {
            //ItemStack stack = itemHandler.getStackInSlot(i);
            //System.out.println(i+ " stack::"+stack);
            SLOTS.put(pageIndex + i, itemHandler.getStackInSlot(i));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int ind) {
        //System.out.println("Inventory slot index:"+ind);
        Slot sourceSlot = slots.get(ind);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        if (ind < 45){
            if (!moveItemStackTo(sourceStack,54,90,true)){
                return ItemStack.EMPTY;
            }
        }else if (ind < 90){
            if (!moveItemStackTo(sourceStack,0,45,false)){
                return ItemStack.EMPTY;
            }
        }else {
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0){
            sourceSlot.set(ItemStack.EMPTY);
        }else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player,sourceStack);
        return copyOfSourceStack;
    }
    @Override
    public void removed(Player player) {
        super.removed(player);
        saveItems();
        nowPlayer = null;
    }
}
