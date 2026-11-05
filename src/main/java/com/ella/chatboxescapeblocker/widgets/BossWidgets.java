package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/** Boss score/reward screens. */
public final class BossWidgets {
	private BossWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			// Scoreboards
			ModalWidget.of(InterfaceID.AmoxliatlScoreboard.UNIVERSE, "Amoxliatl scoreboard"),
			ModalWidget.of(InterfaceID.AraxxorScoreboard.UNIVERSE, "Araxxor scoreboard"),
			ModalWidget.of(InterfaceID.CowbossScoreboard.UNIVERSE, "Cow-pen scoreboard"),
			ModalWidget.of(InterfaceID.DomScoreboard.UNIVERSE, "Doom of Mokhaiotl scoreboard"),
			ModalWidget.of(InterfaceID.GauntletScoreboard.CONTENT, "Gauntlet scoreboard"),
			ModalWidget.of(InterfaceID.HueyScoreboard.UNIVERSE, "Huey boss scoreboard"),
			ModalWidget.of(InterfaceID.JadChallengeScoreboard.CONTENT, "Jad challenge scoreboard"),
			ModalWidget.of(InterfaceID.MaggotKingScoreboard.UNIVERSE, "Maggot King scoreboard"),
			ModalWidget.of(InterfaceID.MuspahScoreboard.UNIVERSE, "Muspah scoreboard"),
			ModalWidget.of(InterfaceID.NexScoreboard.UNIVERSE, "Nex scoreboard (untested)"),
			ModalWidget.of(InterfaceID.NightmareScoreboard.CONTENT, "The Nightmare scoreboard"),
			ModalWidget.of(InterfaceID.PerilousMoonsScoreboard.UNIVERSE, "Perilous Moons scoreboard"),
			ModalWidget.of(InterfaceID.RoyalTitansScoreboard.UNIVERSE, "Royal Titans scoreboard"),
			ModalWidget.of(InterfaceID.YamaScoreboard.UNIVERSE, "Yama scoreboard"),
			ModalWidget.of(InterfaceID.DukeSucellusScoreboard.UNIVERSE, "Duke Sucellus scoreboard"),
			ModalWidget.of(InterfaceID.WhispererScoreboard.UNIVERSE, "The Whisperer scoreboard"),
			ModalWidget.of(InterfaceID.LeviathanScoreboard.UNIVERSE, "The Leviathan scoreboard"),
			ModalWidget.of(InterfaceID.VardorvisScoreboard.UNIVERSE, "Vardorvis scoreboard"),

			// Non-scoreboards
			ModalWidget.of(InterfaceID.DomEndLevelUi.UNIVERSE, "Doom of Mokhaiotl rewards"),
			ModalWidget.of(InterfaceID.BarrowsReward.UNIVERSE, "Barrows rewards"),

			// Fortis Colosseum
			ModalWidget.of(InterfaceID.ColosseumScoreboard.UNIVERSE, "Colosseum scoreboard"),
			ModalWidget.of(InterfaceID.ColosseumIntermission2.INFINITY,
					"Colosseum modifiers (untested)"),
			ModalWidget.of(InterfaceID.ColosseumRewardChest2.INFINITY, "Fortis reward chest (untested)"),
	};
}
