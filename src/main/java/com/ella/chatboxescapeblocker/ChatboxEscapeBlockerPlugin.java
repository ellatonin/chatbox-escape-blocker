package com.ella.chatboxescapeblocker;

import com.ella.chatboxescapeblocker.widgets.BankingWidgets;
import com.ella.chatboxescapeblocker.widgets.BossWidgets;
import com.ella.chatboxescapeblocker.widgets.ClueScrollWidgets;
import com.ella.chatboxescapeblocker.widgets.GroupIronmanWidgets;
import com.ella.chatboxescapeblocker.widgets.MinigameWidgets;
import com.ella.chatboxescapeblocker.widgets.MiscWidgets;
import com.ella.chatboxescapeblocker.widgets.ModalWidget;
import com.ella.chatboxescapeblocker.widgets.PlayerHouseWidgets;
import com.ella.chatboxescapeblocker.widgets.RaidWidgets;
import com.ella.chatboxescapeblocker.widgets.UserInterfaceWidgets;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
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

	private volatile boolean modalWidgetOpen = false;

	@Subscribe
	public void onGameTick(GameTick event) {
		for (ModalWidget widget : MODAL_CONTENT_WIDGETS) {
			if (isVisibleIncludingAncestors(widget.id)) {
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

		// Reset immediately so this acts as a momentary button, not a toggle.
		configManager.setConfiguration(ChatboxEscapeBlockerConfig.GROUP, "printOpenInterfaces", false);

		// Runs on the Swing EDT, not the client thread - touching widgets/chat here is
		// unsafe.
		clientThread.invoke(this::printOpenInterfaces);
	}

	private void printOpenInterfaces() {
		StringBuilder message = new StringBuilder("Open modal interface widgets:");
		boolean anyOpen = false;

		for (ModalWidget widget : MODAL_CONTENT_WIDGETS) {
			if (isVisibleIncludingAncestors(widget.id)) {
				message.append(' ').append(widget.id).append(" (").append(widget.description).append("),");
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

	private static final ModalWidget[] MODAL_CONTENT_WIDGETS = Stream.of(
			UserInterfaceWidgets.WIDGETS,
			PlayerHouseWidgets.WIDGETS,
			BankingWidgets.WIDGETS,
			MinigameWidgets.WIDGETS,
			RaidWidgets.WIDGETS,
			BossWidgets.WIDGETS,
			ClueScrollWidgets.WIDGETS,
			MiscWidgets.WIDGETS,
			GroupIronmanWidgets.WIDGETS)
			.flatMap(Arrays::stream)
			.toArray(ModalWidget[]::new);

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

		log.debug("shouldRemapEscape: modalInterfaceOpen={}", modalInterfaceOpen);

		return !modalInterfaceOpen;
	}

	private boolean isVisible(int component) {
		Widget w = client.getWidget(component);
		return w != null && !w.isSelfHidden();
	}

	/**
	 * Like {@link #isVisible(int)}, but via {@link Widget#isHidden()}, which also
	 * accounts for ancestors - some widgets (e.g. PohCostumes.ITEMS,
	 * GeOffers.CONTENTS) only ever get hidden through an ancestor, never their
	 * own flag. Requires the client thread, so only call from
	 * {@link #onGameTick(GameTick)} or similar - never from
	 * {@link #shouldRemapEscape()}, which reads the cached
	 * {@link #modalWidgetOpen} instead.
	 */
	private boolean isVisibleIncludingAncestors(int component) {
		Widget w = client.getWidget(component);
		return w != null && !w.isHidden();
	}
}
