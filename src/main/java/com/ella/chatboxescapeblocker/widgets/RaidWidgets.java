package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/**
 * Chambers of Xeric, Theatre of Blood, Tombs of Amascut, and the unreleased 4th
 * raid.
 */
public final class RaidWidgets {
	private RaidWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			// Chambers of Xeric
			ModalWidget.of(InterfaceID.RaidsStoragePrivate.UNIVERSE, "COX - private storage"),
			ModalWidget.of(InterfaceID.RaidsStorageShared.UNIVERSE, "COX - shared storage (untested)"),
			ModalWidget.of(InterfaceID.RaidsRewards.CONTENT, "COX - reward chest (untested)"),
			ModalWidget.of(InterfaceID.RaidsChallenge.UNIVERSE, "COX - challenge mode screen"),
			ModalWidget.of(InterfaceID.RaidsLobbyPartylist.UNIVERSE, "COX - party list (lobby)"),
			ModalWidget.of(InterfaceID.RaidsLobbyPartydetails.UNIVERSE, "COX - party details (lobby)"),

			// Theatre of Blood
			ModalWidget.of(InterfaceID.TobPartylist.UNIVERSE, "TOB - party list"),
			ModalWidget.of(InterfaceID.TobPartydetails.UNIVERSE, "TOB - party details"),
			ModalWidget.of(InterfaceID.TobChests.CONTENT, "TOB - reward chest"),
			ModalWidget.of(InterfaceID.TobMidwayStores.UNIVERSE, "TOB - midway storage"),
			ModalWidget.of(InterfaceID.TobInfoboard.UNIVERSE, "TOB - information board"),

			// Tombs of Amascut
			ModalWidget.of(InterfaceID.ToaPartylist.UNIVERSE, "TOA - party list"),
			ModalWidget.of(InterfaceID.ToaPartydetails.UNIVERSE, "TOA - party details"),
			ModalWidget.of(InterfaceID.ToaMidraidlootBag.ITEMS, "TOA - mid-raid loot bag (untested)"),
			ModalWidget.of(InterfaceID.ToaChests.CONTENT, "TOA - reward chest (untested)"),
			ModalWidget.of(InterfaceID.ToaScoreboard.UNIVERSE, "TOA - scoreboard"),

			// Fraccin
			ModalWidget.of(InterfaceID.TfaPartylist.UNIVERSE, "TFA - party list"),
			ModalWidget.of(InterfaceID.TfaChests.UNIVERSE, "TFA - reward chest"),
			ModalWidget.of(InterfaceID.TfaStats.INFINITY, "TFA - stats screen"),
	};
}
