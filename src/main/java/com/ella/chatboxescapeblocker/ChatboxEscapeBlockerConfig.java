package com.ella.chatboxescapeblocker;

import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;

@ConfigGroup("chatboxescapeblocker")
public interface ChatboxEscapeBlockerConfig extends Config
{
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
}
