package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/**
 * Minigame reward shops, Fortis Colosseum, membership benefits, and Wilderness
 * loot interfaces.
 */
public final class MinigameWidgets {
	private MinigameWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			// Minigames
			ModalWidget.of(InterfaceID.BarbassaultRewardShop.BARBASSAULT_REWARDS, "Barbarian Assault reward shop"),
			ModalWidget.of(InterfaceID.PestRewardshop.OPTIONS, "Pest Control reward shop"),
			ModalWidget.of(InterfaceID.GiantsFoundryRewardShop.LIST, "Giants' Foundry reward shop"),
			ModalWidget.of(InterfaceID.NzoneRewards.CONTENTS, "Nightmare Zone reward shop"),
			ModalWidget.of(InterfaceID.SoulWarsRewards.CONTENTS, "Soul Wars reward shop"),
			ModalWidget.of(InterfaceID.PvpArenaRewards.OPTIONS, "PvP Arena reward shop"),
			ModalWidget.of(InterfaceID.SlayerRewards.CONTENTS, "Slayer reward shop"),
			ModalWidget.of(InterfaceID.LeagueRewards.CONTENT, "Leagues reward shop"),
			ModalWidget.of(InterfaceID.SpeedrunningRewards.CONTENT, "Speedrunning event reward shop"),
			ModalWidget.of(InterfaceID.TrekRewards.UNIVERSE, "Trekking reward shop"),
			ModalWidget.of(InterfaceID.FossilVolcanicShop.UNIVERSE, "Volcanic Mine reward shop (Fossil Island)"),
			ModalWidget.of(InterfaceID.BarbassaultPlayerstat.ROOT_MODEL0, "Barbarian Assault personal stats screen"),
			ModalWidget.of(InterfaceID.OmnishopMain.INFINITY, "Generic reward shop"),

	};
}
