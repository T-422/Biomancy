package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableTileEntity extends SimpleSyncedTileEntity implements IOwnableTile, INameable {

	private final HashMap<UUID, UserAuthorization.AuthorityLevel> userAuthorityLevelMap = new HashMap<>(8); // 8 * 0.75 = 6 -> we naively assume that the average player won't add more than 6 users
	private ITextComponent customName = null;
	@Nullable
	private UUID owner;

	public OwnableTileEntity(TileEntityType<?> entityType) {
		super(entityType);
	}

	public boolean canPlayerOpenInv(PlayerEntity player) {
		if (world == null || world.getTileEntity(pos) != this) return false;
		return player.getDistanceSq(Vector3d.copyCentered(pos)) < 8d * 8d && isUserAuthorized(player);
	}

	public boolean canPlayerUse(PlayerEntity player) {
		if (!player.isSpectator() && !isUserAuthorized(player)) {
			if (!player.world.isRemote()) {
				player.sendStatusMessage(new TranslationTextComponent("container.isLocked", getDefaultName()).mergeStyle(TextFormatting.RED), true);
				player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public HashMap<UUID, UserAuthorization.AuthorityLevel> getUserAuthorityLevelMap() {
		return userAuthorityLevelMap;
	}

	@Override
	public Optional<UUID> getOwner() {
		return Optional.ofNullable(owner);
	}

	@Override
	public void setOwner(UUID uuid) {
		owner = uuid;
		markDirty();
	}

	@Override
	public void removeOwner() {
		owner = null;
		markDirty();
	}

	@Override
	public boolean hasOwner() {
		return owner != null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return getName();
	}

	@Override
	public ITextComponent getName() {
		return customName != null ? customName : getDefaultName();
	}

	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	public void setCustomName(ITextComponent name) {
		customName = name;
		markDirty();
	}

	protected abstract ITextComponent getDefaultName();

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
			customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
		}
		else {
			customName = null;
		}

		if (nbt.hasUniqueId("OwnerUUID")) owner = nbt.getUniqueId("OwnerUUID");
		else owner = null;

		userAuthorityLevelMap.clear();
		if (nbt.contains("UserList")) {
			ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < nbtList.size(); i++) {
				CompoundNBT nbtEntry = nbtList.getCompound(i);
				UUID userUUID = nbtEntry.getUniqueId("UserUUID");
				UserAuthorization.AuthorityLevel authority = UserAuthorization.AuthorityLevel.deserialize(nbtEntry);
				userAuthorityLevelMap.put(userUUID, authority);
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if (customName != null) nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));

		if (owner != null) nbt.putUniqueId("OwnerUUID", owner);

		if (userAuthorityLevelMap.size() > 0) {
			ListNBT listNBT = new ListNBT();
			for (Map.Entry<UUID, UserAuthorization.AuthorityLevel> entry : userAuthorityLevelMap.entrySet()) {
				CompoundNBT nbtEntry = new CompoundNBT();
				nbtEntry.putUniqueId("UserUUID", entry.getKey());
				entry.getValue().serialize(nbtEntry);
				listNBT.add(nbtEntry);
			}
			nbt.put("UserList", listNBT);
		}
		return nbt;
	}

	public CompoundNBT writeToItemBlockEntityTag(CompoundNBT nbt) {

		if (userAuthorityLevelMap.size() > 0) {
			if (owner != null) nbt.putUniqueId("OwnerUUID", owner);

			ListNBT listNBT = new ListNBT();
			for (Map.Entry<UUID, UserAuthorization.AuthorityLevel> entry : userAuthorityLevelMap.entrySet()) {
				CompoundNBT nbtEntry = new CompoundNBT();
				nbtEntry.putUniqueId("UserUUID", entry.getKey());
				entry.getValue().serialize(nbtEntry);
				listNBT.add(nbtEntry);
			}
			nbt.put("UserList", listNBT);
		}

		return nbt;
	}
}
