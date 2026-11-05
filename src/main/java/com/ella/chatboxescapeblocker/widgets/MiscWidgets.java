package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/**
 * Makeover, scrolls/books, Fossil Island, sailing, hunter, and other
 * miscellaneous interfaces.
 */
public final class MiscWidgets {
	private MiscWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			// Character appearance/makeover interface
			ModalWidget.of(InterfaceID.Makeover.INFINITY, "Character makeover screen"),
			ModalWidget.of(InterfaceID.MakeoverMage.FRAME, "Make-over Mage interface"),

			ModalWidget.of(InterfaceID.Fairyrings.ROOT_RECT0, "Fairy ring teleport interface"),
			ModalWidget.of(InterfaceID.Questscroll.UNIVERSE, "Quest completion scroll"),
			ModalWidget.of(InterfaceID.Longscroll.UNIVERSE, "Long scroll reading interface"),
			ModalWidget.of(InterfaceID.Xpreward.UNIVERSE, "XP reward lamp interface"),
			ModalWidget.of(InterfaceID.SailingMenu.UNIVERSE, "Charter ship destination menu"),
			ModalWidget.of(InterfaceID.ItemTransmog.UNIVERSE, "Item transmog (cosmetic override) interface"),
			ModalWidget.of(InterfaceID.SlayerTaskChoice.INFINITY, "Slayer task choice screen"),
			ModalWidget.of(InterfaceID.RunePouch.UNIVERSE, "Rune pouch contents"),
			ModalWidget.of(InterfaceID.Bookofscrolls.UNIVERSE, "Book of scrolls"),
			ModalWidget.of(InterfaceID.GravestoneRetrieval.UNIVERSE, "Gravestone item retrieval screen"),
			ModalWidget.of(InterfaceID.GryphonBoss.UNIVERSE, "Shellbane Gryphon boss stats screen"),
			ModalWidget.of(InterfaceID.Patchy.UNIVERSE, "Patchy (parrot) interface"),
			ModalWidget.of(InterfaceID.Note.UNIVERSE, "Note reading interface"),
			ModalWidget.of(InterfaceID.PrifRecipes.UNIVERSE, "Prifddinas recipe/creation interface"),
			ModalWidget.of(InterfaceID.NtkScores.ROOT_MODEL0, "Pyramid Plunder score screen"),
			ModalWidget.of(InterfaceID.Book.UNIVERSE, "Book reading interface"),
			ModalWidget.of(InterfaceID.Messagescroll2.ROOT_MODEL1, "Message scroll reading interface"),
			ModalWidget.of(InterfaceID.Glidermap.UNIVERSE, "Gnome glider destination map"),
			ModalWidget.of(InterfaceID.Letterscroll.ROOT_MODEL0, "Letter/scroll reading interface"),

			// Fossil island stuff
			ModalWidget.of(InterfaceID.MessagescrollHandwriting.ROOT_MODEL0, "Fossil cleaning note scroll"),
			ModalWidget.of(InterfaceID.FossilMushtrees.UNIVERSE, "Fossil Island mushroom tree interface"),
			ModalWidget.of(InterfaceID.FarmingTools.UNIVERSE, "Farming tool leprechaun interface"),
			ModalWidget.of(InterfaceID.FossilUnderwaterTraining.UNIVERSE,
					"Fossil Island underwater training interface"),

			// Sailing
			ModalWidget.of(InterfaceID.SailingBoatCargohold.UNIVERSE, "Sailing boat cargo hold"),
			ModalWidget.of(InterfaceID.SailingCustomisation.INFINITY, "Sailing boat customisation menu"),
			ModalWidget.of(InterfaceID.SailingBoatSelection.INFINITY, "Sailing boat selection menu"),
			ModalWidget.of(InterfaceID.SailingCrew.INFINITY, "Sailing crew management menu"),
			ModalWidget.of(InterfaceID.PortTaskBoard.INFINITY, "Sailing port task board"),

			// Hunter stuff
			ModalWidget.of(InterfaceID.HuntsmansKit.UNIVERSE, "Huntsman's kit interface"),

			// Currently glitched/escape doesn't close
			// InterfaceID.DisplaynameMenu.INFINITE
			// InterfaceID.Smithing.UNIVERSE
			// InterfaceID.CraftingGold.UNIVERS
	};
}
