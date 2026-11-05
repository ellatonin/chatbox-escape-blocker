package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/** Player-owned house interfaces. */
public final class PlayerHouseWidgets {
	private PlayerHouseWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {
			ModalWidget.of(InterfaceID.Telenexus.UNIVERSE, "POH - Nexus Teleport config"),
			ModalWidget.of(InterfaceID.TelenexusTeleport.UNIVERSE, "POH - Nexus Teleport list"),
			ModalWidget.of(InterfaceID.TeletabsCraftIf.UNIVERSE, "POH - Teleport tablet crafting"),
			ModalWidget.of(InterfaceID.PohBookcase.INFINITE, "POH - Bookcase"),
			ModalWidget.of(InterfaceID.LeagueTrophies.INFINITY, "POH - League trophy case"),
			ModalWidget.of(InterfaceID.PohCostumes.ITEMS, "POH - Costume room"),
			ModalWidget.of(InterfaceID.PohMenagerie.UNIVERSE, "POH - Menagerie"),
			ModalWidget.of(InterfaceID.PohFurnitureCreationMenu.UNIVERSE, "POH - Furniture creation menu"),
			ModalWidget.of(InterfaceID.KillLog.UNIVERSE, "POH - Kill log"),
			ModalWidget.of(InterfaceID.ChampionsLog.UNIVERSE, "POH - Champion's challenge log"),
			ModalWidget.of(InterfaceID.Questdisplay.UNIVERSE, "POH - Quest display case)"),
			ModalWidget.of(InterfaceID.PohJewelleryBox.UNIVERSE, "POH - Jewellery box teleports"),
			ModalWidget.of(InterfaceID.PohFurnitureCreation.UNIVERSE, "POH - Furniture building menu"),
	};
}
