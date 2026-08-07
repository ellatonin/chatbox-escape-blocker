package com.ella.chatboxescapeblocker;

import com.google.inject.Provides;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.annotations.Interface;
import net.runelite.api.gameval.InterfaceID;
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
				message.append(' ').append(widgetName(widgetId)).append(',');
				anyOpen = true;
			}
		}

		int debugWidgetId = config.debugWidgetId();
		if (debugWidgetId != 0 && isVisible(debugWidgetId)) {
			message.append(' ').append(widgetName(debugWidgetId)).append(" (debug),");
			anyOpen = true;
		}

		if (anyOpen) {
			message.setLength(message.length() - 1);
		} else {
			message.append(" none");
		}

		client.addChatMessage(ChatMessageType.CONSOLE, "", message.toString(), null);
	}

	/**
	 * Every {@code InterfaceID} constant (across all its nested per-interface
	 * classes), keyed by
	 * its widget ID, so debug output can show e.g. "Bankmain.ITEMS" instead of a
	 * bare packed int.
	 * Built once via reflection rather than hand-maintained, so it can't drift out
	 * of sync with
	 * the IDs actually referenced in {@link #MODAL_CONTENT_WIDGETS}.
	 */
	private static final Map<Integer, String> WIDGET_NAMES = buildWidgetNames();

	private static Map<Integer, String> buildWidgetNames() {
		Map<Integer, String> names = new HashMap<>();
		for (Class<?> group : InterfaceID.class.getDeclaredClasses()) {
			for (Field field : group.getDeclaredFields()) {
				if (field.getType() != int.class || !Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				try {
					names.put(field.getInt(null), group.getSimpleName() + "." + field.getName());
				} catch (IllegalAccessException e) {
					// unreachable - these fields are all public
				}
			}
		}
		return names;
	}

	private static String widgetName(int widgetId) {
		String name = WIDGET_NAMES.get(widgetId);
		return name != null ? name + " (" + widgetId + ")" : String.valueOf(widgetId);
	}

	/**
	 * Each interface's own content widget - unlike the chatbox/toplevel containers,
	 * these only
	 * exist while that interface is genuinely open (same technique the core Bank
	 * plugin uses
	 * for Bankmain.ITEMS; Toplevel.MAINMODAL, the shared container most of these
	 * load into,
	 * turned out to never actually report itself as hidden). Add more here if
	 * escape needs to
	 * be blocked from closing one.
	 *
	 * Deliberately NOT included: achievement diaries, the quest list/journal,
	 * combat achievement
	 * tasks, the collection log, clan setup, the world switcher, and boss stats
	 * boards (e.g.
	 * Araxxor's). These never reliably report themselves closed - not via
	 * Widget.isSelfHidden()
	 * (stays "open" forever after first shown) and not via
	 * WidgetLoaded/WidgetClosed events
	 * either (most of these live in a tab/sidebar-hosted panel system and get
	 * covered by
	 * switching tabs rather than genuinely closed at the engine level; the world
	 * switcher and
	 * stats boards aren't tab-hosted but show the same symptom regardless). Either
	 * detection
	 * method gets permanently stuck treating them as open, which disables escape
	 * remapping
	 * everywhere until the client restarts - worse than not blocking escape on them
	 * at all.
	 * Don't re-add these without a per-interface way to detect their true state
	 * (e.g. a varbit)
	 * - a widget check alone isn't enough.
	 */
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
			// Collection Log regular doesn't work (very annoying)
			// InterfaceID.Collection.UNIVERSE,
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
			// even once the interface is genuinely gone - same "never hides" issue
			// documented above.
			// Use ITEMS instead (matches the core Bank plugin's own technique).
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

	};

	/**
	 * True everywhere except while the bank, a shop, the trade screen, or a similar
	 * modal
	 * interface (see {@link #MODAL_CONTENT_WIDGETS}) is open - those should keep
	 * their normal
	 * "escape closes interface" behavior untouched.
	 */
	boolean shouldRemapEscape() {
		boolean modalInterfaceOpen = false;
		for (int widgetId : MODAL_CONTENT_WIDGETS) {
			if (isVisible(widgetId)) {
				modalInterfaceOpen = true;
				log.debug("shouldRemapEscape: blocked by {}", widgetName(widgetId));
				break;
			}
		}

		int debugWidgetId = config.debugWidgetId();
		if (debugWidgetId != 0) {
			boolean debugWidgetVisible = isVisible(debugWidgetId);
			log.debug("shouldRemapEscape: debugWidgetId={} visible={}", debugWidgetId, debugWidgetVisible);
			modalInterfaceOpen |= debugWidgetVisible;
		}

		log.debug("shouldRemapEscape: modalInterfaceOpen={}", modalInterfaceOpen);

		return !modalInterfaceOpen;
	}

	private boolean isVisible(int component) {
		Widget w = client.getWidget(component);
		return w != null && !w.isSelfHidden();
	}
}
