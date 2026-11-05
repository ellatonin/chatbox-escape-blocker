package com.ella.chatboxescapeblocker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "Chatbox Escape Blocker", description = "Remaps the Escape key to another key, except while the bank, Grand Exchange, or a similar "
		+ "interface is open (so it can still close those normally)", tags = { "escape", "chatbox", "dialogue", "chat",
				"interface", "inventory", "remap" })
public class ChatboxEscapeBlockerPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatboxEscapeBlockerInputListener inputListener;

	@Inject
	private ChatboxEscapeBlockerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientThread clientThread;

	/**
	 * {@link Widget#isHidden()} asserts it's called on the client thread, but
	 * {@link #shouldRemapEscape()} runs on the AWT thread and needs a synchronous
	 * answer, so {@link #MODAL_CONTENT_WIDGETS} visibility is instead recomputed
	 * here once per game tick and cached for {@link #shouldRemapEscape()} to read.
	 */
	private volatile boolean modalWidgetOpen = false;

	@Subscribe
	public void onGameTick(GameTick event) {
		for (int widgetId : MODAL_CONTENT_WIDGETS) {
			if (isVisibleIncludingAncestors(widgetId)) {
				modalWidgetOpen = true;
				return;
			}
		}

		modalWidgetOpen = false;
	}

	@Provides
	ChatboxEscapeBlockerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ChatboxEscapeBlockerConfig.class);
	}

	@Override
	protected void startUp() {
		keyManager.registerKeyListener(inputListener);
	}

	@Override
	protected void shutDown() {
		keyManager.unregisterKeyListener(inputListener);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!ChatboxEscapeBlockerConfig.GROUP.equals(event.getGroup()) || !"printOpenInterfaces".equals(event.getKey())
				|| !"true".equals(event.getNewValue())) {
			return;
		}

		// Reset immediately so this behaves like a momentary button rather than a
		// persistent toggle.
		configManager.setConfiguration(ChatboxEscapeBlockerConfig.GROUP, "printOpenInterfaces", false);

		// This listener runs on the Swing EDT (the config panel calls
		// setConfiguration() directly from the
		// checkbox's ActionListener), not the client thread - touching widgets/chat
		// from here is unsafe.
		clientThread.invoke(this::printOpenInterfaces);
	}

	private void printOpenInterfaces() {
		StringBuilder message = new StringBuilder("Open modal interface widgets:");
		boolean anyOpen = false;

		for (int widgetId : MODAL_CONTENT_WIDGETS) {
			if (isVisibleIncludingAncestors(widgetId)) {
				message.append(' ').append(widgetId).append(',');
				anyOpen = true;
			}
		}

		int debugWidgetId = config.debugWidgetId();
		if (debugWidgetId != 0 && isVisible(debugWidgetId)) {
			message.append(' ').append(debugWidgetId).append(" (debug),");
			anyOpen = true;
		}

		if (anyOpen) {
			message.setLength(message.length() - 1);
		} else {
			message.append(" none");
		}

		client.addChatMessage(ChatMessageType.CONSOLE, "", message.toString(), null);
	}

	private static final int[] MODAL_CONTENT_WIDGETS = {

			////////////////////////////////////////////////////
			// User Interface Tabs /////////////////////////////
			////////////////////////////////////////////////////

			// Skill guides
			InterfaceID.SkillGuide.INFINITY,
			InterfaceID.SkillGuideV2.INFINITY,

			// Character Summary
			InterfaceID.CaOverview.INFINITY,
			InterfaceID.CaTasks.INFINITY,
			InterfaceID.CaBosses.INFINITY,
			InterfaceID.CaRewards.REWARDS_CONTENT,
			InterfaceID.CollectionOverview.INFINITY,
			InterfaceID.Collection.UNIVERSE,
			InterfaceID.Collection.CONTENT,
			InterfaceID.QuestjournalOverview.INFINITY,
			InterfaceID.Questjournal.INFINITY,
			InterfaceID.Journalscroll.UNIVERSE,
			InterfaceID.Minigames.INFINITE, // spellbook button

			// Worn Equipment
			InterfaceID.Equipment.FRAME,
			InterfaceID.GePricechecker.ITEMS,
			InterfaceID.Deathkeep.ITEMS,

			// Social - Clan, Friend, etc
			InterfaceID.ChatchannelSetup.UNIVERSE,
			InterfaceID.ClansInfo.INFINITE,
			InterfaceID.ClansEvents.INFINITE,
			InterfaceID.ClansMembers.INFINITE,
			InterfaceID.ClansApplicants.INFINITE, // untested
			InterfaceID.ClansHall.INFINITE,
			InterfaceID.ClansPermissions.INFINITE,
			InterfaceID.ClansRanktitles.INFINITE,
			InterfaceID.ClansBanned.INFINITE,

			// Account Management
			InterfaceID.Ballot.UNIVERSE,
			InterfaceID.BondMain.CONTENTS,
			InterfaceID.BondManagement.CONTENTS,

			// Settings
			InterfaceID.Settings.CONTENT,
			InterfaceID.PohOptions.VIEWER,

			// World map
			InterfaceID.Worldmap.UNIVERSE,

			// Other
			InterfaceID.WorldswitcherOptions.INFINITE,
			InterfaceID.XpdropsSetup.UNIVERSE,

			////////////////////////////////////////////////////
			// Player House ////////////////////////////////////
			////////////////////////////////////////////////////
			InterfaceID.Telenexus.UNIVERSE,
			InterfaceID.TelenexusTeleport.UNIVERSE,
			InterfaceID.TeletabsCraftIf.UNIVERSE,
			InterfaceID.PohBookcase.INFINITE,
			InterfaceID.LeagueTrophies.INFINITY,
			InterfaceID.PohCostumes.ITEMS,
			InterfaceID.PohMenagerie.UNIVERSE,
			InterfaceID.PohFurnitureCreationMenu.UNIVERSE,
			InterfaceID.Fairyrings.ROOT_RECT0,
			InterfaceID.KillLog.UNIVERSE,
			InterfaceID.ChampionsLog.UNIVERSE,
			InterfaceID.Questdisplay.UNIVERSE,
			InterfaceID.PohJewelleryBox.UNIVERSE,
			InterfaceID.PohFurnitureCreation.UNIVERSE,

			////////////////////////////////////////////////////
			// World UI ////////////////////////////////////////
			////////////////////////////////////////////////////

			// Bank
			// Bankmain.UNIVERSE (the shared root container) intermittently stays reported
			// as open
			// even once the interface is genuinely gone
			InterfaceID.Bankmain.ITEMS,
			InterfaceID.BankDepositbox.UNIVERSE,
			InterfaceID.BankDepositImp.INVENTORY,
			InterfaceID.BankpinKeypad.UNIVERSE,
			InterfaceID.BankpinSettings.SETTINGS,
			InterfaceID.GeCollect.UNIVERSE,
			InterfaceID.Menu.LJ_LAYER2, // purchase more slots

			// Grand Exchange
			InterfaceID.GeOffers.CONTENTS,
			InterfaceID.GePricelist.LIST,
			InterfaceID.GeHistory.LIST,
			InterfaceID.GeCollect.UNIVERSE,
			InterfaceID.Itemsets.ITEMLIST,

			// Trade
			InterfaceID.Trademain.YOUR_OFFER,
			InterfaceID.Tradeconfirm.YOUR_OFFER,

			// Death's Office
			InterfaceID.DeathOffice.ITEMS,
			InterfaceID.DeathCoffer.CONTENTS,

			// Shop
			InterfaceID.Shopmain.ITEMS,

			// Minigames
			InterfaceID.BarbassaultRewardShop.BARBASSAULT_REWARDS,
			InterfaceID.PestRewardshop.OPTIONS,
			InterfaceID.GiantsFoundryRewardShop.LIST,
			InterfaceID.NzoneRewards.CONTENTS,
			InterfaceID.SoulWarsRewards.CONTENTS,
			InterfaceID.PvpArenaRewards.OPTIONS,
			InterfaceID.SlayerRewards.CONTENTS,
			InterfaceID.LeagueRewards.CONTENT,
			InterfaceID.SpeedrunningRewards.CONTENT,
			InterfaceID.TrekRewards.UNIVERSE,
			InterfaceID.FossilVolcanicShop.UNIVERSE,
			InterfaceID.BarbassaultPlayerstat.ROOT_MODEL0,
			InterfaceID.OmnishopMain.INFINITY, // generic reward shops

			// Fortis Colosseum
			InterfaceID.ColosseumScoreboard.UNIVERSE,
			InterfaceID.ColosseumIntermission2.INFINITY, // untested
			InterfaceID.ColosseumRewardChest2.INFINITY, // untested

			// Membership benefits interface; untested
			InterfaceID.MembershipBenefits.CONTENT,

			// Wilderness Looting bag
			InterfaceID.WildernessLootingbag.UNIVERSE,

			// Chambers of Xeric
			InterfaceID.RaidsStoragePrivate.UNIVERSE,
			InterfaceID.RaidsStorageShared.UNIVERSE, // untested
			InterfaceID.RaidsRewards.CONTENT, // untested
			InterfaceID.RaidsChallenge.UNIVERSE,
			InterfaceID.RaidsLobbyPartylist.UNIVERSE,
			InterfaceID.RaidsLobbyPartydetails.UNIVERSE,

			// Theatre of Blood
			InterfaceID.TobPartylist.UNIVERSE,
			InterfaceID.TobPartydetails.UNIVERSE,
			InterfaceID.TobChests.CONTENT,
			InterfaceID.TobMidwayStores.UNIVERSE,
			InterfaceID.TobInfoboard.UNIVERSE,

			// Tombs of Depression
			InterfaceID.ToaPartylist.UNIVERSE,
			InterfaceID.ToaPartydetails.UNIVERSE,
			InterfaceID.ToaMidraidlootBag.ITEMS, // untested
			InterfaceID.ToaChests.CONTENT, // untested
			InterfaceID.ToaScoreboard.UNIVERSE,

			// Fraccin
			InterfaceID.TfaPartylist.UNIVERSE,
			InterfaceID.TfaChests.UNIVERSE,
			InterfaceID.TfaStats.INFINITY,

			// Wilderness loot chest
			InterfaceID.WildyLootChest.ITEMS, // untested

			// Character appearance/makeover interface
			InterfaceID.Makeover.INFINITY,
			InterfaceID.MakeoverMage.FRAME,

			// Bosses
			InterfaceID.AmoxliatlScoreboard.UNIVERSE,
			InterfaceID.AraxxorScoreboard.UNIVERSE,
			InterfaceID.CowbossScoreboard.UNIVERSE,
			InterfaceID.DomScoreboard.UNIVERSE,
			InterfaceID.DomEndLevelUi.UNIVERSE,
			InterfaceID.GauntletScoreboard.CONTENT,
			InterfaceID.HueyScoreboard.UNIVERSE,
			InterfaceID.JadChallengeScoreboard.CONTENT,
			InterfaceID.MaggotKingScoreboard.UNIVERSE,
			InterfaceID.MuspahScoreboard.UNIVERSE,
			InterfaceID.NexScoreboard.UNIVERSE, // untested
			InterfaceID.NightmareScoreboard.CONTENT,
			InterfaceID.PerilousMoonsScoreboard.UNIVERSE,
			InterfaceID.RoyalTitansScoreboard.UNIVERSE,
			InterfaceID.YamaScoreboard.UNIVERSE,
			InterfaceID.BarrowsReward.UNIVERSE,

			InterfaceID.DukeSucellusScoreboard.UNIVERSE,
			InterfaceID.WhispererScoreboard.UNIVERSE,
			InterfaceID.LeviathanScoreboard.UNIVERSE,
			InterfaceID.VardorvisScoreboard.UNIVERSE,

			// Misc
			InterfaceID.Questscroll.UNIVERSE,
			InterfaceID.Longscroll.UNIVERSE,
			InterfaceID.Xpreward.UNIVERSE,
			InterfaceID.SailingMenu.UNIVERSE, // charter
			InterfaceID.ItemTransmog.UNIVERSE,
			InterfaceID.SlayerTaskChoice.INFINITY,
			InterfaceID.RunePouch.UNIVERSE,
			InterfaceID.Bookofscrolls.UNIVERSE,
			InterfaceID.GravestoneRetrieval.UNIVERSE,
			InterfaceID.GryphonBoss.UNIVERSE,
			InterfaceID.Patchy.UNIVERSE,
			InterfaceID.Note.UNIVERSE,
			InterfaceID.PrifRecipes.UNIVERSE,
			InterfaceID.NtkScores.ROOT_MODEL0, // pyramid plunder
			InterfaceID.Book.UNIVERSE,
			InterfaceID.Messagescroll2.ROOT_MODEL1,
			InterfaceID.Glidermap.UNIVERSE,
			InterfaceID.Letterscroll.ROOT_MODEL0,

			// Fossil island stuff
			InterfaceID.MessagescrollHandwriting.ROOT_MODEL0, // fossil island fossil cleaning notes
			InterfaceID.FossilMushtrees.UNIVERSE,
			InterfaceID.FarmingTools.UNIVERSE,
			InterfaceID.FossilUnderwaterTraining.UNIVERSE,

			// Sailing
			InterfaceID.SailingBoatCargohold.UNIVERSE,
			InterfaceID.SailingCustomisation.INFINITY,
			InterfaceID.SailingBoatSelection.INFINITY,
			InterfaceID.SailingCrew.INFINITY,
			InterfaceID.PortTaskBoard.INFINITY,

			// Hunter stuff
			InterfaceID.HuntsmansKit.UNIVERSE,

			// Clue scrolls
			InterfaceID.TrailCluetext.ROOT_MODEL0,
			InterfaceID.TrailRewardscreen.ITEMS,
			InterfaceID.TrailSlidepuzzle.PIECES,
			InterfaceID.LightPuzzle.LIGHTS,
			InterfaceID.TrailSextant.SEXTANT_BACKING,
			InterfaceID.TrailMap01.SCROLLMODEL_MAP01,
			InterfaceID.TrailMap02.SCROLLMODEL_MAP02,
			InterfaceID.TrailMap03.SCROLLMODEL_MAP03,
			InterfaceID.TrailMap04.SCROLLMODEL_MAP04,
			InterfaceID.TrailMap05.SCROLLMODEL_MAP05,
			InterfaceID.TrailMap06.SCROLLMODEL_MAP06,
			InterfaceID.TrailMap07.BG_SCROLL,
			InterfaceID.TrailMap08.BG_SCROLL,
			InterfaceID.TrailMap09.BG_SCROLL,
			InterfaceID.TrailMap10.BG_SCROLL,
			InterfaceID.TrailMap11.BG_SCROLL,
			InterfaceID.TrailMap12.BG_SCROLL,
			InterfaceID.TrailMap13.BG_SCROLL,
			InterfaceID.TrailMap14.BG_SCROLL,
			InterfaceID.TrailMap15.BG_SCROLL,
			InterfaceID.TrailMap16.BG_SCROLL,
			InterfaceID.TrailMap17.BG_SCROLL,
			InterfaceID.TrailMap18.BG_SCROLL,
			InterfaceID.TrailMap19.BG_SCROLL,
			InterfaceID.TrailMap20.ROOT_MODEL0,
			InterfaceID.TrailMap21.ROOT_MODEL0,
			InterfaceID.TrailMap22.ROOT_MODEL0,
			InterfaceID.TrailMap23.ROOT_MODEL0,
			InterfaceID.TrailMap24.ROOT_MODEL0,
			InterfaceID.TrailClueEasyMap006.BG_SCROLL,
			InterfaceID.TrailClueHardMap006.BG_SCROLL,
			InterfaceID.TrailClueHardMap007.BG_SCROLL,
			InterfaceID.TrailClueMediumMap008.ROOT_MODEL0,
			InterfaceID.TrailClueMediumMap009.ROOT_MODEL0,
			InterfaceID.TrailClueMediumMap010.BG_SCROLL,
			InterfaceID.TrailClueMediumMap011.BG_SCROLL,
			InterfaceID.TrailClueMediumMap012.BG_SCROLL,

			// Group Ironman, probably more cant verify rn
			InterfaceID.GimSettings.INFINITE,
			InterfaceID.GimOptions.INFINITE,
			InterfaceID.SharedBank.INFINITE, // GIM bank

			// escape doesn't close these
			// InterfaceID.DisplaynameMenu.INFINITE,
			// InterfaceID.Smithing.UNIVERSE,
			// InterfaceID.CraftingGold.UNIVERSE,

	};

	/**
	 * True everywhere except while the bank, a shop, the trade screen, or a similar
	 * modal
	 */
	boolean shouldRemapEscape() {
		boolean modalInterfaceOpen = modalWidgetOpen;
		if (modalInterfaceOpen) {
			log.debug("shouldRemapEscape: blocked by cached modal widget state");
		}

		int debugWidgetId = config.debugWidgetId();
		if (debugWidgetId != 0) {
			boolean debugWidgetVisible = isVisible(debugWidgetId);
			log.debug("shouldRemapEscape: debugWidgetId={} visible={}", debugWidgetId, debugWidgetVisible);
			modalInterfaceOpen |= debugWidgetVisible;
		}

		if (modalInterfaceOpen) {
			forceClearStuckBankSearch();
		}

		log.debug("shouldRemapEscape: modalInterfaceOpen={}", modalInterfaceOpen);

		return !modalInterfaceOpen;
	}

	/**
	 * Works around a client bug: closing the bank while its search box is still
	 * active leaves
	 * the chatbox message layer stuck in {@link InputType#SEARCH}, which in turn
	 * leaves the
	 * whole Bankmain interface group permanently reporting itself as visible -
	 * confirmed by
	 * testing both Bankmain.ITEMS and Bankmain.FRAME, which get stuck identically,
	 * so this
	 * isn't fixable by picking a different child widget. Forcibly clears that
	 * leftover chatbox
	 * state with the same script the core Bank plugin's own search reset uses
	 * (BankSearch#reset), letting the interface's hidden state catch up on a later
	 * tick.
	 * Harmless if the bank is genuinely open and being searched right now - it just
	 * mirrors
	 * what native Escape already does in that case (cancel the search, not close
	 * the bank).
	 */
	private void forceClearStuckBankSearch() {
		if (client.getVarcIntValue(VarClientID.MESLAYERMODE) != InputType.SEARCH.getType()) {
			return;
		}

		log.debug("shouldRemapEscape: forcing stuck bank search closed");
		clientThread.invoke(() -> client.runScript(ScriptID.MESSAGE_LAYER_CLOSE, 1, 1, 0));
	}

	private boolean isVisible(int component) {
		Widget w = client.getWidget(component);
		return w != null && !w.isSelfHidden();
	}

	/**
	 * Like {@link #isVisible(int)}, but uses {@link Widget#isHidden()} - which
	 * also accounts for
	 * ancestors, unlike {@link Widget#isSelfHidden()}. Several widgets in
	 * {@link #MODAL_CONTENT_WIDGETS} (e.g. PohCostumes.ITEMS, GeOffers.CONTENTS)
	 * only ever get hidden via an ancestor in the client's interface-layer
	 * system rather than flipping their own hidden flag, so this is the check
	 * used for all of them. isHidden() asserts it's called on the client
	 * thread, so this may only be called from {@link #onGameTick(GameTick)} or
	 * other client-thread callbacks - never directly from
	 * {@link #shouldRemapEscape()}, which runs on the AWT thread and reads the
	 * cached {@link #modalWidgetOpen} instead.
	 */
	private boolean isVisibleIncludingAncestors(int component) {
		Widget w = client.getWidget(component);
		return w != null && !w.isHidden();
	}
}
