package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.drawable.FluidTankBar;
import com.github.elenterius.biomancy.inventory.ChewerContainer;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChewerContainerScreen extends ContainerScreen<ChewerContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/chewer_gui.png");
	private final FluidTankBar fuelTankBar = new FluidTankBar(39, 17, 5, 60 - 17);

	public ChewerContainerScreen(ChewerContainer container, PlayerInventory inv, ITextComponent titleIn) {
		super(container, inv, titleIn);
		//texture size
		xSize = 176;
		ySize = 166;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float FONT_Y_SPACING = 12;
		font.drawText(matrixStack, title, 10, 18 - FONT_Y_SPACING, 0xFFFFFF);

		String craftingProgress = (int) (container.getCraftingProgressNormalized() * 100) + "%";
		font.drawString(matrixStack, craftingProgress, 155 - font.getStringWidth(craftingProgress), 52 + 6, 0xFFFFFF);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//noinspection ConstantConditions
		minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int edgeSpacingX = (width - xSize) / 2;
		int edgeSpacingY = (height - ySize) / 2;
		blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, xSize, ySize);

		fuelTankBar.update(container.getFuelTank());
		fuelTankBar.draw(minecraft, matrixStack, guiLeft, guiTop, mouseX, mouseY);
	}

	@Override
	protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		//noinspection ConstantConditions
		if (!minecraft.player.inventory.getItemStack().isEmpty()) return;

		List<ITextComponent> hoveringText = new ArrayList<>();

		if (fuelTankBar.isMouseInside(guiLeft, guiTop, mouseX, mouseY)) {
			int amount = container.getFuelTank().getFluidAmount();
			if (amount > 0) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				hoveringText.add(new TranslationTextComponent(container.getFuelTank().getFluid().getTranslationKey()).appendString(": " + df.format(amount) + " mb"));
			}
			else {
				hoveringText.add(ClientTextUtil.getTooltipText("empty"));
			}
		}

		if (!hoveringText.isEmpty()) {
			func_243308_b(matrixStack, hoveringText, mouseX, mouseY);
		}
		else {
			super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
		}
	}
}
