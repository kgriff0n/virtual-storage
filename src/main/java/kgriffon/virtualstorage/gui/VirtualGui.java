package kgriffon.virtualstorage.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.SimpleGui;
import kgriffon.virtualstorage.LoadInventoryException;
import kgriffon.virtualstorage.database.VirtualInventoryManager;
import kgriffon.virtualstorage.inventory.VirtualInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

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

    public VirtualGui(VirtualInventory inventory, int maxPage, ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);
        this.inventory = inventory;
        this.page = inventory.getPage();
        this.maxPage = maxPage;
        shouldSave = false;

//        renderTitle();
        this.setTitle(GuiUtils.title(player, Component.literal("Virtual Storage - ").append(Component.translatable("book.pageIndicator", page, maxPage))));
        for (int i = 0; i < 45; i++) {
            this.setSlot(i, new Slot(inventory, i, 0, 0));
        }
        renderControls();
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
                            player.sendSystemMessage(Component.translatable("mco.errorMessage.connectionFailure").withStyle(ChatFormatting.RED));
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
                            player.sendSystemMessage(Component.translatable("mco.errorMessage.connectionFailure").withStyle(ChatFormatting.RED));
                            close();
                        }
                    })
            );
        }
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, ContainerInput action) {
        if (0 <= index && index < 45 || action == ContainerInput.QUICK_MOVE || action == ContainerInput.PICKUP_ALL) {
            shouldSave = true;
        }
        return super.onAnyClick(index, type, action);
    }

    @Override
    public void onRemoved() {
        if (shouldSave) {
            inventory.save();
        }
        super.onRemoved();
    }
}
