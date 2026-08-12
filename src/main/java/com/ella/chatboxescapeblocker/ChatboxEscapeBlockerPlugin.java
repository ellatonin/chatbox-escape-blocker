package com.ella.chatboxescapeblocker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.annotations.Interface;
import net.runelite.api.events.ScriptPostFired;
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
	 * The collection log never reliably reports itself closed via any widget (see
	 * the doc
	 * comment on {@link #MODAL_CONTENT_WIDGETS} - both Collection.UNIVERSE and
	 * Collection.CONTENT
	 * get permanently stuck reporting themselves open), so it's handled separately
	 * here with a
	 * timeout heuristic instead: script 7797 ("collection log setup", per
	 * TempleOSRS's own use of
	 * it to know when to re-add its sync button) reliably fires whenever the log's
	 * content is
	 * (re)built - on open, and again on switching tabs/searching within it. Treat
	 * it as open for
	 * a grace period after that script last fired, rather than trying to detect a
	 * real close.
	 */
	private static final int COLLECTION_LOG_SETUP_SCRIPT_ID = 7797;
	private static final int COLLECTION_LOG_GRACE_PERIOD_TICKS = 15;
	private int collectionLogOpenUntilTick = -1;

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event) {
		if (event.getScriptId() == COLLECTION_LOG_SETUP_SCRIPT_ID) {
			collectionLogOpenUntilTick = client.getTickCount() + COLLECTION_LOG_GRACE_PERIOD_TICKS;
			log.debug("onScriptPostFired: collection log setup fired, treating as open until tick {}",
					collectionLogOpenUntilTick);
		}
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
			if (isVisible(widgetId)) {
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

			/////////////////////////////////////////////////////////
			// User Interface Tabs //////////////////////////////////
			/////////////////////////////////////////////////////////

			// Skill guides
			InterfaceID.SkillGuide.INFINITY,
			InterfaceID.SkillGuideV2.INFINITY,

			// Character Summary
			InterfaceID.CaOverview.INFINITY,
			InterfaceID.CaTasks.INFINITY,
			InterfaceID.CaBosses.INFINITY,
			InterfaceID.CaRewards.REWARDS_CONTENT,
			InterfaceID.CollectionOverview.INFINITY,
			// Collection Log doesn't work (very annoying) - both UNIVERSE and CONTENT get
			// permanently stuck reporting themselves open, not a wrong-widget-choice
			// problem.
			// Would need a varbit-based detection signal instead of a widget check.
			// InterfaceID.Collection.UNIVERSE,
			// InterfaceID.Collection.CONTENT,
			InterfaceID.QuestjournalOverview.INFINITY,
			InterfaceID.Journalscroll.UNIVERSE,

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
			InterfaceID.Displayname.UNIVERSE,

			// Settings
			InterfaceID.Settings.CONTENT,
			InterfaceID.PohOptions.VIEWER,

			// World map
			InterfaceID.Worldmap.UNIVERSE,

			/////////////////////////////////////////////////////////
			// Player House /////////////////////////////////////////
			/////////////////////////////////////////////////////////
			InterfaceID.Telenexus.UNIVERSE,
			InterfaceID.TelenexusTeleport.UNIVERSE,
			InterfaceID.TeletabsCraftIf.UNIVERSE,
			InterfaceID.PohBookcase.INFINITE,
			InterfaceID.LeagueTrophies.INFINITY,
			InterfaceID.PohCostumes.INFINITE,
			InterfaceID.PohMenagerie.UNIVERSE,
			InterfaceID.PohFurnitureCreationMenu.UNIVERSE,
			InterfaceID.Fairyrings.ROOT_RECT0,
			InterfaceID.KillLog.UNIVERSE,
			InterfaceID.ChampionsLog.UNIVERSE,
			InterfaceID.Questdisplay.UNIVERSE,
			InterfaceID.PohJewelleryBox.UNIVERSE,
			InterfaceID.PohFurnitureCreation.UNIVERSE,

			/////////////////////////////////////////////////////////
			// World UI /////////////////////////////////////////////
			/////////////////////////////////////////////////////////

			// Bank
			// Bankmain.UNIVERSE (the shared root container) intermittently stays reported
			// as open
			// even once the interface is genuinely gone
			InterfaceID.Bankmain.ITEMS,
			InterfaceID.BankDepositbox.UNIVERSE,
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

			// Brimhaven Agility Arena
			// Castle Wars
			// Deadman rewards
			InterfaceID.OmnishopMain.INFINITY,

			// Fortis Colosseum
			InterfaceID.ColosseumScoreboard.UNIVERSE,
			InterfaceID.ColosseumIntermission2.INFINITY, // untested
			InterfaceID.ColosseumRewardChest2.INFINITY, // untested

			// Membership benefits interface; untested
			InterfaceID.MembershipBenefits.CONTENT,

			// Wilderness Looting bag
			InterfaceID.WildernessLootingbag.UNIVERSE,

			// Chambers of Xeris
			InterfaceID.RaidsStoragePrivate.UNIVERSE,
			InterfaceID.RaidsStorageShared.UNIVERSE, // untested
			InterfaceID.RaidsRewards.CONTENT, // untested

			// Theatre of Blood
			InterfaceID.TobPartylist.UNIVERSE,
			InterfaceID.TobPartydetails.UNIVERSE,
			InterfaceID.TobChests.CONTENT, // untested

			// Tombs of Depression
			InterfaceID.ToaPartylist.UNIVERSE,
			InterfaceID.ToaPartydetails.UNIVERSE,
			InterfaceID.ToaMidraidlootBag.ITEMS, // untested
			InterfaceID.ToaChests.CONTENT, // untested

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

			InterfaceID.DukeSucellusScoreboard.UNIVERSE,
			InterfaceID.WhispererScoreboard.UNIVERSE, // untested
			InterfaceID.LeviathanScoreboard.UNIVERSE, // untested
			InterfaceID.VardorvisScoreboard.UNIVERSE, // untested

			// Misc
			InterfaceID.Questscroll.UNIVERSE,
			InterfaceID.Longscroll.UNIVERSE,

			// Sailing
			InterfaceID.SailingBoatCargohold.UNIVERSE,
			InterfaceID.SailingCustomisation.INFINITY,
			InterfaceID.SailingBoatSelection.INFINITY,
			InterfaceID.SailingCrew.INFINITY,

			// Clue scrolls
			InterfaceID.TrailCluetext.ROOT_MODEL0,
			InterfaceID.TrailRewardscreen.ITEMS, // untested
			InterfaceID.TrailSlidepuzzle.PIECES, // untested
			InterfaceID.LightPuzzle.LIGHTS, // untested
			InterfaceID.TrailSextant.SEXTANT_BACKING, // untested

			InterfaceID.TrailMap01.SCROLLMODEL_MAP01, // untested
			InterfaceID.TrailMap02.SCROLLMODEL_MAP02, // untested
			InterfaceID.TrailMap03.SCROLLMODEL_MAP03, // untested
			InterfaceID.TrailMap04.SCROLLMODEL_MAP04, // untested
			InterfaceID.TrailMap05.SCROLLMODEL_MAP05, // untested
			InterfaceID.TrailMap06.SCROLLMODEL_MAP06, // untested
			InterfaceID.TrailMap07.BG_SCROLL, // untested
			InterfaceID.TrailMap08.BG_SCROLL, // untested
			InterfaceID.TrailMap09.BG_SCROLL,
			InterfaceID.TrailMap10.BG_SCROLL, // untested
			InterfaceID.TrailMap11.BG_SCROLL, // untested
			InterfaceID.TrailMap12.BG_SCROLL, // untested
			InterfaceID.TrailMap13.BG_SCROLL, // untested
			InterfaceID.TrailMap14.BG_SCROLL, // untested
			InterfaceID.TrailMap15.BG_SCROLL, // untested
			InterfaceID.TrailMap16.BG_SCROLL, // untested
			InterfaceID.TrailMap17.BG_SCROLL, // untested
			InterfaceID.TrailMap18.BG_SCROLL, // untested
			InterfaceID.TrailMap19.BG_SCROLL, // untested
			InterfaceID.TrailMap20.ROOT_MODEL0, // untested
			InterfaceID.TrailMap21.ROOT_MODEL0, // untested
			InterfaceID.TrailMap22.ROOT_MODEL0, // untested
			InterfaceID.TrailMap23.ROOT_MODEL0, // untested
			InterfaceID.TrailMap24.ROOT_MODEL0, // untested

			// A few map clues live outside the TrailMap01-24 block at their own group IDs
			InterfaceID.TrailClueEasyMap006.BG_SCROLL, // untested
			InterfaceID.TrailClueHardMap006.BG_SCROLL, // untested
			InterfaceID.TrailClueHardMap007.BG_SCROLL, // untested
			InterfaceID.TrailClueMediumMap008.ROOT_MODEL0, // untested
			InterfaceID.TrailClueMediumMap009.ROOT_MODEL0, // untested
			InterfaceID.TrailClueMediumMap010.BG_SCROLL, // untested
			InterfaceID.TrailClueMediumMap011.BG_SCROLL, // untested
			InterfaceID.TrailClueMediumMap012.BG_SCROLL, // untested

	};

	/**
	 * True everywhere except while the bank, a shop, the trade screen, or a similar
	 * modal
	 */
	boolean shouldRemapEscape() {
		boolean modalInterfaceOpen = false;
		for (int widgetId : MODAL_CONTENT_WIDGETS) {
			if (isVisible(widgetId)) {
				modalInterfaceOpen = true;
				log.debug("shouldRemapEscape: blocked by widget {}", widgetId);
				break;
			}
		}

		int debugWidgetId = config.debugWidgetId();
		if (debugWidgetId != 0) {
			boolean debugWidgetVisible = isVisible(debugWidgetId);
			log.debug("shouldRemapEscape: debugWidgetId={} visible={}", debugWidgetId, debugWidgetVisible);
			modalInterfaceOpen |= debugWidgetVisible;
		}

		if (client.getTickCount() <= collectionLogOpenUntilTick) {
			log.debug("shouldRemapEscape: blocked by collection log grace period (until tick {})",
					collectionLogOpenUntilTick);
			modalInterfaceOpen = true;
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
}
