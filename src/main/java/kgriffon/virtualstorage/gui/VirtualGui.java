package kgriffon.virtualstorage.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.database.VirtualInventoryManager;
import kgriffon.virtualstorage.inventory.VirtualInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VirtualGui extends SimpleGui {

    private final int page;
    private final int maxPage;
    private boolean shouldSave;

    private final VirtualInventory inventory;

    public VirtualGui(VirtualInventory inventory, int maxPage) {
//        super(ScreenHandlerType.GENERIC_9X6, inventory.getPlayer(), false);
//        this.inventory = inventory;
//        this.page = inventory.getPage();
//        this.maxPage = maxPage;
//        shouldSave = false;

//        renderTitle();
//        for (int i = 0; i < 45; i++) {
//            this.setSlotRedirect(i, new Slot(inventory, i, 0, 0));
//        }
//        renderControls();
        this(inventory, maxPage, inventory.getPlayer());
    }

    public VirtualGui(VirtualInventory inventory, int maxPage, ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.inventory = inventory;
        this.page = inventory.getPage();
        this.maxPage = maxPage;
        shouldSave = false;

//        renderTitle();
        this.setTitle(GuiUtils.title(player, Text.literal("Virtual Storage - ").append(Text.translatable("book.pageIndicator", page, maxPage))));
        for (int i = 0; i < 45; i++) {
            this.setSlotRedirect(i, new Slot(inventory, i, 0, 0));
        }
        renderControls();
    }

    protected void renderTitle() {
        this.setTitle(Text.literal("Virtual Storage - ").append(Text.translatable("book.pageIndicator", page, maxPage)));
    }

    protected void renderControls() {
        for (int i = 45; i < 54; i++) {
            this.setSlot(i, GuiUtils.empty(player));
        }

        if (inventory.getPage() > 1) {
            this.setSlot(46, GuiUtils.previousPage(player)
                    .setCallback(() -> {
                        try {
                            new VirtualGui(VirtualInventoryManager.getInstance().getVirtualInventory(inventory.getPlayer(), page - 1), maxPage, player).open();
                        } catch (LoadInventoryException e) {
                            player.sendMessage(Text.translatable("mco.errorMessage.connectionFailure").formatted(Formatting.RED));
                            close();
                        }
                    })
            );
        }

        if (inventory.getPage() < maxPage) {
            this.setSlot(52, GuiUtils.nextPage(player)
                    .setCallback(() -> {
                        try {
                            new VirtualGui(VirtualInventoryManager.getInstance().getVirtualInventory(inventory.getPlayer(), page + 1), maxPage, player).open();
                        } catch (LoadInventoryException e) {
                            player.sendMessage(Text.translatable("mco.errorMessage.connectionFailure").formatted(Formatting.RED));
                            close();
                        }
                    })
            );
        }
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
        if (0 <= index && index < 45 || action == SlotActionType.QUICK_MOVE || action == SlotActionType.PICKUP_ALL) {
            shouldSave = true;
        }
        return super.onAnyClick(index, type, action);
    }

    @Override
    public void onScreenHandlerClosed() {
        if (shouldSave) {
            inventory.save();
        }
        super.onScreenHandlerClosed();
    }
}
