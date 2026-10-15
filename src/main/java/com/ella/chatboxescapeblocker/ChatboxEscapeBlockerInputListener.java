package com.ella.chatboxescapeblocker;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.input.KeyListener;

/**
 * Mirrors the remap technique the core Key Remapping plugin uses (mutating the AWT KeyEvent's
 * keyCode before it reaches the client's own listener) rather than trying to reverse-engineer
 * and click the target widget ourselves.
 */
class ChatboxEscapeBlockerInputListener implements KeyListener
{
	@Inject
	private ChatboxEscapeBlockerPlugin plugin;

	@Inject
	private ChatboxEscapeBlockerConfig config;

	private final Map<Integer, Integer> modified = new HashMap<>();

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() != KeyEvent.VK_ESCAPE || !plugin.shouldRemapEscape())
		{
			return;
		}

		ModifierlessKeybind remapTo = config.remapTo();
		int mappedKeyCode = remapTo.getKeyCode();

		if (mappedKeyCode == KeyEvent.VK_UNDEFINED)
		{
			e.consume();
			return;
		}

		modified.put(e.getKeyCode(), mappedKeyCode);
		e.setKeyCode(mappedKeyCode);
		e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		Integer mappedKeyCode = modified.remove(e.getKeyCode());
		if (mappedKeyCode != null)
		{
			e.setKeyCode(mappedKeyCode);
			e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
		}
	}
}
