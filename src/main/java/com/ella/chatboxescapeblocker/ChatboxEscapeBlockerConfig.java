package com.ella.chatboxescapeblocker;

import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;

@ConfigGroup(ChatboxEscapeBlockerConfig.GROUP)
public interface ChatboxEscapeBlockerConfig extends Config
{
	String GROUP = "chatboxescapeblocker";

	@ConfigItem(
		keyName = "remapTo",
		name = "Remap Escape to",
		description = "Whenever Escape is pressed, send this key to the game instead - except while the bank, "
			+ "Grand Exchange, or a similar interface is open, where Escape keeps closing it normally. Whatever "
			+ "this key normally does happens instead of Escape's usual action (e.g. set it to your Inventory "
			+ "tab hotkey). Leave not set to just block Escape."
	)
	default ModifierlessKeybind remapTo()
	{
		return new ModifierlessKeybind(KeyEvent.VK_UNDEFINED, 0);
	}

	@ConfigItem(
		keyName = "debugWidgetId",
		name = "Debug widget ID",
		description = "For testing: also treat this widget ID (from the RuneLite Developer Tools widget "
			+ "inspector, e.g. 5570562) as a modal interface that should keep Escape's normal behavior. Its "
			+ "visibility is logged at debug level every time Escape is pressed, so you can confirm a candidate "
			+ "widget actually flips hidden/visible correctly before adding it to the plugin's source. Leave 0 "
			+ "to disable."
	)
	default int debugWidgetId()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "printOpenInterfaces",
		name = "Print open interfaces",
		description = "Check this to print the widget IDs of every currently-open interface (from the plugin's "
			+ "modal widget list, plus the debug widget ID above if set) to the game chat. It's a one-shot "
			+ "action, not a persistent setting - it unchecks itself right after printing."
	)
	default boolean printOpenInterfaces()
	{
		return false;
	}
}
