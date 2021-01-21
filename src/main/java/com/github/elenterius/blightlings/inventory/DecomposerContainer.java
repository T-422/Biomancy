package com.github.elenterius.blightlings.inventory;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModContainerTypes;
import com.github.elenterius.blightlings.tileentity.DecomposerTileEntity;
import com.github.elenterius.blightlings.tileentity.state.DecomposerStateData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class DecomposerContainer extends Container {

	protected final SimpleInvContents fuelContents;
	protected final SimpleInvContents inputContents;
	protected final SimpleInvContents outputContents;
	private final DecomposerStateData decomposerState;
	private World world;

	private DecomposerContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents fuelContents, SimpleInvContents inputContents, SimpleInvContents outputContents, DecomposerStateData decomposerState) {
		super(ModContainerTypes.DECOMPOSER.get(), screenId);
		this.fuelContents = fuelContents;
		this.inputContents = inputContents;
		this.outputContents = outputContents;
		this.decomposerState = decomposerState;
		world = playerInventory.player.world;

		trackIntArray(decomposerState);

		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int HOT_BAR_SIZE = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < HOT_BAR_SIZE; idx++) { //hotbar
			addSlot(new SlotItemHandler(playerInventoryForge, idx, 8 + SLOT_X_SPACING * idx, 142));
		}

		// Add the players main inventory
		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 84;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = HOT_BAR_SIZE + y * 9 + x;
				int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, xpos, ypos));
			}
		}

		int posX = 17;
		int posY = 17;
		addSlot(new Slot(fuelContents, 0, posX, posY) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return DecomposerTileEntity.isItemValidFuel(stack);
			}
		});

		int inputPosX = posX + 18 * 2;
		addSlot(new Slot(inputContents, 0, inputPosX, posY));
		addSlot(new Slot(inputContents, 1, inputPosX + 18, posY));
		addSlot(new Slot(inputContents, 2, inputPosX + 18 * 2, posY));
		addSlot(new Slot(inputContents, 3, inputPosX, posY + 18));
		addSlot(new Slot(inputContents, 4, inputPosX + 18, posY + 18));
		addSlot(new Slot(inputContents, 5, inputPosX + 18 * 2, posY + 18));

		int outputPosX = inputPosX + 18 * 4;
		addSlot(new OutputSlot(outputContents, 0, outputPosX, posY));
		addSlot(new OutputSlot(outputContents, 1, outputPosX + 18, posY));
		addSlot(new OutputSlot(outputContents, 2, outputPosX, posY + 18));
		addSlot(new OutputSlot(outputContents, 3, outputPosX + 18, posY + 18));
	}

	public static DecomposerContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInvContents fuelContents, SimpleInvContents inputContents, SimpleInvContents outputContents, DecomposerStateData decomposerState) {
		return new DecomposerContainer(screenId, playerInventory, fuelContents, inputContents, outputContents, decomposerState);
	}

	public static DecomposerContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInvContents fuelContents = SimpleInvContents.createClientContents(DecomposerTileEntity.FUEL_SLOTS_COUNT);
		SimpleInvContents inputContents = SimpleInvContents.createClientContents(DecomposerTileEntity.INPUT_SLOTS_COUNT);
		SimpleInvContents outputContents = SimpleInvContents.createClientContents(DecomposerTileEntity.OUTPUT_SLOTS_COUNT);
		DecomposerStateData decomposerState = new DecomposerStateData();
		return new DecomposerContainer(screenId, playerInventory, fuelContents, inputContents, outputContents, decomposerState);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputContents.isUsableByPlayer(playerIn);
	}

	public float getCraftingProgressNormalized() {
		if (decomposerState.timeForCompletion == 0) return 0f;
		return MathHelper.clamp(decomposerState.timeElapsed / (float) decomposerState.timeForCompletion, 0f, 1f);
	}

	public float getFuelNormalized() {
		return MathHelper.clamp(decomposerState.mainFuel / (float) DecomposerTileEntity.MAX_FUEL, 0f, 1f);
	}

	public float getSpeedFuelNormalized() {
		return MathHelper.clamp(decomposerState.speedFuel / (float) DecomposerTileEntity.MAX_FUEL, 0f, 1f);
	}

	/**
	 * copied from: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/ContainerFurnace.java
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = inventorySlots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case OUTPUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, true);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, true);
				if (successfulTransfer) sourceSlot.onSlotChange(sourceStack, copyOfSourceStack);
				break;

			case INPUT_ZONE:
			case FUEL_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (DecomposerTileEntity.getRecipeForItem(world, sourceStack).isPresent()) {
					successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceStack, false);
				}
				if (!successfulTransfer && DecomposerTileEntity.isItemValidFuel(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceStack, true);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.putStack(ItemStack.EMPTY);
		else sourceSlot.onSlotChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BlightlingsMod.LOGGER.warn(MarkerManager.getMarker("DECOMPOSER_CONTAINER"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeInto(SlotZone destinationZone, ItemStack sourceStack, boolean fillFromEnd) {
		return mergeItemStack(sourceStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
	}

	private enum SlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(9, 3 * 9),
		FUEL_ZONE(9 + 3 * 9, DecomposerTileEntity.FUEL_SLOTS_COUNT),
		INPUT_ZONE(9 + 3 * 9 + DecomposerTileEntity.FUEL_SLOTS_COUNT, DecomposerTileEntity.INPUT_SLOTS_COUNT),
		OUTPUT_ZONE(9 + 3 * 9 + DecomposerTileEntity.FUEL_SLOTS_COUNT + DecomposerTileEntity.INPUT_SLOTS_COUNT, DecomposerTileEntity.OUTPUT_SLOTS_COUNT);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			this.slotCount = numberOfSlots;
			this.lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}
	}
}