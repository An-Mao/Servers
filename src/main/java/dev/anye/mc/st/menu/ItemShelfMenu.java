package dev.anye.mc.st.menu;

/*
public class ItemShelfMenu extends BorderPageMenu {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final boolean system;
	private List<ItemStack> stacks ;

	public ItemShelfMenu(int id, Inventory playerInventory,boolean system) {
		super(id,playerInventory,getCount(system));
		this.system = system;
		pageRefresh();
	}

	public static int getCount(boolean system){
		if (system) return Currency.I.systemItemShelf().count();
		return Currency.I.playerItemShelf().count();
	}



	public ItemStack getShelfItem(int index) {
		if (index < 0 || index > stacks.size() - 1) return ItemStack.EMPTY;
		return stacks.get(index);
	}


	@Override
	protected void pageInitLoad() {

	}

	@Override
	public void addInventory(){
		int ii = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 7; col++) {
				int i = 10 + row * 9 + col;
				int iii = ii;
				this.addItemSlot(i,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,iii,x,y, player -> this.playerClick(player,iii)));
				ii++;
			}
		}
	}

	public void playerClick(Player player,int index){
		if (player instanceof ServerPlayer player1) {
			LOGGER.debug("playerClick");
			ItemStack stack = this.itemHandler.getResource(index).toStack();
			if (stack.isEmpty()){
				return;
			}
			stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(compoundTag -> {
				String uuid = compoundTag.getString(system ? "shelf.st.system.item.uuid":"shelf.st.item.uuid").orElse("");
				if (uuid.isBlank())return;
				MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.buy." + (Currency.I.buy(player1,uuid,system) ? "success":"failed"));
			});
		}
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public void pageRefresh() {
		stacks = Currency.I.getShelfItems(this.serverPlayer, system);

		int pageOffset = this.pageItemNumber * this.pageIndex;
		for (int i = 0; i < itemHandler.size(); i++) {
			ItemStack itemStack = getShelfItem(i + pageOffset);
			if (itemStack.isEmpty()) continue;
			itemHandler.set(i, ItemResource.of(getShelfItem(i + pageOffset)), 1);
		}
	}

	private int getItemIndex(int i){
		int pageOffset = this.pageItemNumber * this.pageIndex;
		return i + pageOffset;
	}

	public static void open(ServerPlayer serverPlayer,boolean system){
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, _) -> new ItemShelfMenu(id, playerInventory,system),
				Language.getComponent(serverPlayer,"menu.st.shelf.item.title")
		));
	}
}

 */
