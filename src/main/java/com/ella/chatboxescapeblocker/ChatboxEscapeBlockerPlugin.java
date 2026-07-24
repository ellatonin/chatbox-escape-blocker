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
	 * True everywhere except while the bank, bank deposit box, Grand Exchange, or a shop is
	 * open - those should keep their normal "escape closes interface" behavior untouched.
	 * Toplevel.MAINMODAL (the shared container these load into) turned out to never actually
	 * report itself as hidden, so instead this checks each interface's own content widget,
	 * which - unlike the chatbox/toplevel containers - only exists while that interface is
	 * genuinely open (same technique the core Bank plugin uses for Bankmain.ITEMS). Doesn't
	 * cover every possible interface (trade, minigame reward shops, etc.) - add more here if
	 * escape needs to be blocked from closing one.
	 */
	boolean shouldRemapEscape()
	{
		boolean modalInterfaceOpen = isVisible(InterfaceID.Bankmain.ITEMS)
			|| isVisible(InterfaceID.BankDepositbox.CONTENTS)
			|| isVisible(InterfaceID.GeOffers.CONTENTS)
			|| isVisible(InterfaceID.Shopmain.ITEMS);

		log.debug("shouldRemapEscape: modalInterfaceOpen={}", modalInterfaceOpen);

		return !modalInterfaceOpen;
	}

	private boolean isVisible(int component)
	{
		Widget w = client.getWidget(component);
		return w != null && !w.isSelfHidden();
	}
}
