package com.ella.chatboxescapeblocker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Chatbox Escape Blocker",
	description = "Remaps the Escape key to another key, except while the bank, Grand Exchange, or a similar "
		+ "interface is open (so it can still close those normally)",
	tags = {"escape", "chatbox", "dialogue", "chat", "interface", "inventory", "remap"}
)
public class ChatboxEscapeBlockerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatboxEscapeBlockerInputListener inputListener;

	@Provides
	ChatboxEscapeBlockerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChatboxEscapeBlockerConfig.class);
	}

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(inputListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(inputListener);
	}

	/**
	 * Each interface's own content widget - unlike the chatbox/toplevel containers, these only
	 * exist while that interface is genuinely open (same technique the core Bank plugin uses
	 * for Bankmain.ITEMS; Toplevel.MAINMODAL, the shared container most of these load into,
	 * turned out to never actually report itself as hidden). Add more here if escape needs to
	 * be blocked from closing one.
	 *
	 * Deliberately NOT included: achievement diaries, the quest list/journal, combat achievement
	 * tasks, the collection log, clan setup, and the world switcher. These never reliably report
	 * themselves closed - not via Widget.isSelfHidden() (stays "open" forever after first shown)
	 * and not via WidgetLoaded/WidgetClosed events either (most of these live in a tab/sidebar-
	 * hosted panel system and get covered by switching tabs rather than genuinely closed at the
	 * engine level; the world switcher isn't tab-hosted but shows the same symptom regardless).
	 * Either detection method gets permanently stuck treating them as open, which disables escape
	 * remapping everywhere until the client restarts - worse than not blocking escape on them
	 * at all. Don't re-add these without a per-interface way to detect their true state (e.g. a
	 * varbit) - a widget check alone isn't enough.
	 */
	private static final int[] MODAL_CONTENT_WIDGETS = {
		InterfaceID.Bankmain.ITEMS,
		InterfaceID.BankDepositbox.CONTENTS,
		InterfaceID.BankpinKeypad.FRAME,
		InterfaceID.GeOffers.CONTENTS,
		InterfaceID.GeCollect.COLLECT_INV,
		InterfaceID.Shopmain.ITEMS,
		InterfaceID.Trademain.YOUR_OFFER,
		InterfaceID.Tradeconfirm.YOUR_OFFER,
		InterfaceID.CastlewarsTrade.ITEMLIST,
		InterfaceID.BarbassaultRewardShop.BARBASSAULT_REWARDS,
		InterfaceID.PestRewardshop.OPTIONS,
		InterfaceID.GiantsFoundryRewardShop.LIST,
		InterfaceID.NzoneRewards.CONTENTS,
		InterfaceID.SoulWarsRewards.CONTENTS,
		InterfaceID.PvpArenaRewards.OPTIONS,
		InterfaceID.SlayerRewards.CONTENTS,
		InterfaceID.CaRewards.REWARDS_CONTENT,
		InterfaceID.AgilityarenaRewards.CONTENTS,
		InterfaceID.LeagueRewards.CONTENT,
		InterfaceID.SpeedrunningRewards.CONTENT,
		InterfaceID.TrekRewards.CONTENTS,
		InterfaceID.ColosseumRewardChest.ITEMS,
		InterfaceID.ColosseumRewardChest2.CONTENT,
		InterfaceID.PohOptions.VIEWER,
		InterfaceID.BondMain.CONTENTS,
		InterfaceID.BondManagement.CONTENTS,
		InterfaceID.Settings.CONTENT,
		InterfaceID.Equipment.FRAME,
		InterfaceID.GePricechecker.ITEMS,
		InterfaceID.GePricelist.LIST,
		InterfaceID.Deathkeep.ITEMS,
		InterfaceID.MembershipBenefits.CONTENT,
		InterfaceID.DeathOffice.ITEMS,
		InterfaceID.DeathCoffer.CONTENTS,
		InterfaceID.ToaChests.CONTENT,
		InterfaceID.TobChests.CONTENT,
		InterfaceID.RaidsRewards.CONTENT,
		InterfaceID.WildyLootChest.ITEMS,
		InterfaceID.Makeover.CONTENT,
		InterfaceID.MakeoverMage.FRAME,
		InterfaceID.MiscCollection.ITEMS,
	};

	/**
	 * True everywhere except while the bank, a shop, the trade screen, or a similar modal
	 * interface (see {@link #MODAL_CONTENT_WIDGETS}) is open - those should keep their normal
	 * "escape closes interface" behavior untouched.
	 */
	boolean shouldRemapEscape()
	{
		boolean modalInterfaceOpen = false;
		for (int widgetId : MODAL_CONTENT_WIDGETS)
		{
			if (isVisible(widgetId))
			{
				modalInterfaceOpen = true;
				break;
			}
		}

		log.debug("shouldRemapEscape: modalInterfaceOpen={}", modalInterfaceOpen);

		return !modalInterfaceOpen;
	}

	private boolean isVisible(int component)
	{
		Widget w = client.getWidget(component);
		return w != null && !w.isSelfHidden();
	}
}
