package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/**
 * Skill guides, character summary, worn equipment, social, and other UI tab
 * interfaces.
 */
public final class UserInterfaceWidgets {
	private UserInterfaceWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			// Skill guides
			ModalWidget.of(InterfaceID.SkillGuide.INFINITY, "Skill guide"),
			ModalWidget.of(InterfaceID.SkillGuideV2.INFINITY, "Skill guide"),

			// Character Summary
			ModalWidget.of(InterfaceID.CaOverview.INFINITY, "Character summary - overview"),
			ModalWidget.of(InterfaceID.CaTasks.INFINITY, "Character summary - combat achievement tasks"),
			ModalWidget.of(InterfaceID.CaBosses.INFINITY, "Character summary - combat achievement bosses"),
			ModalWidget.of(InterfaceID.CaRewards.REWARDS_CONTENT, "Character summary - combat achievement rewards"),
			ModalWidget.of(InterfaceID.CollectionOverview.INFINITY, "Collection log overview tab"),
			ModalWidget.of(InterfaceID.Collection.UNIVERSE, "Collection log"),
			ModalWidget.of(InterfaceID.QuestjournalOverview.INFINITY, "Quest journal overview tab"),
			ModalWidget.of(InterfaceID.Questjournal.INFINITY, "Quest journal"),
			ModalWidget.of(InterfaceID.Journalscroll.UNIVERSE, "Quest journal scroll"),
			ModalWidget.of(InterfaceID.Minigames.INFINITE, "Minigames tab (also the spellbook switcher button)"),

			// Worn Equipment
			ModalWidget.of(InterfaceID.Equipment.FRAME, "Worn equipment screen"),
			ModalWidget.of(InterfaceID.GePricechecker.ITEMS, "Grand Exchange price checker (from equipment screen)"),
			ModalWidget.of(InterfaceID.Deathkeep.ITEMS, "Items kept on death screen"),

			// Social - Clan, Friend, etc
			ModalWidget.of(InterfaceID.ChatchannelSetup.UNIVERSE, "Chat channel setup"),
			ModalWidget.of(InterfaceID.ClansInfo.INFINITE, "Clan info panel"),
			ModalWidget.of(InterfaceID.ClansEvents.INFINITE, "Clan events panel"),
			ModalWidget.of(InterfaceID.ClansMembers.INFINITE, "Clan members panel"),
			ModalWidget.of(InterfaceID.ClansApplicants.INFINITE, "Clan applicants panel (untested)"),
			ModalWidget.of(InterfaceID.ClansHall.INFINITE, "Clan hall panel"),
			ModalWidget.of(InterfaceID.ClansPermissions.INFINITE, "Clan permissions panel"),
			ModalWidget.of(InterfaceID.ClansRanktitles.INFINITE, "Clan rank titles panel"),
			ModalWidget.of(InterfaceID.ClansBanned.INFINITE, "Clan banned users panel"),

			// Account Management
			ModalWidget.of(InterfaceID.Ballot.UNIVERSE, "Poll booth ballot"),
			ModalWidget.of(InterfaceID.BondMain.CONTENTS, "Bond redeem screen"),
			ModalWidget.of(InterfaceID.BondManagement.CONTENTS, "Membership management screen"),

			// Settings
			ModalWidget.of(InterfaceID.Settings.CONTENT, "Settings panel"),
			ModalWidget.of(InterfaceID.PohOptions.VIEWER, "House options panel"),

			// World map
			ModalWidget.of(InterfaceID.Worldmap.UNIVERSE, "World map"),

			// Wilderness stuff
			ModalWidget.of(InterfaceID.WildernessLootingbag.UNIVERSE, "Wilderness looting bag"),
			ModalWidget.of(InterfaceID.WildyLootChest.ITEMS, "Wilderness loot chest (PvP loot keys, untested)"),

			// Other
			ModalWidget.of(InterfaceID.WorldswitcherOptions.INFINITE, "World switcher"),
			ModalWidget.of(InterfaceID.XpdropsSetup.UNIVERSE, "XP drops setup"),

			// Membership benefits interface; untested
			ModalWidget.of(InterfaceID.MembershipBenefits.CONTENT, "Membership benefits screen (untested)"),
	};
}
