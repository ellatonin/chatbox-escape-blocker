package com.ella.chatboxescapeblocker;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.input.KeyListener;

/**
 * Mirrors the remap technique the core Key Remapping plugin uses (mutating the
 * AWT KeyEvent's
 * keyCode before it reaches the client's own listener) rather than trying to
 * reverse-engineer
 * and click the target widget ourselves.
 */
class ChatboxEscapeBlockerInputListener implements KeyListener {
	@Inject
	private ChatboxEscapeBlockerPlugin plugin;

	@Inject
	private ChatboxEscapeBlockerConfig config;

	private final Map<Integer, Integer> modified = new HashMap<>();

	/**
	 * Whether the Escape press currently being handled got a synthetic native
	 * Escape fired
	 * alongside its remap (see {@link #postSyntheticEscape(KeyEvent, int)}), so the
	 * matching
	 * release knows to fire the paired synthetic release too.
	 */
	private boolean syntheticEscapePending = false;

	/**
	 * Identities of the synthetic Escape {@link KeyEvent}s this listener posts back
	 * onto the AWT
	 * event queue itself, so they're recognised and passed straight through here
	 * instead of being
	 * treated as a second, genuine Escape press/release and remapped all over
	 * again.
	 */
	private final Set<KeyEvent> syntheticEscapeEvents = Collections.newSetFromMap(new HashMap<>());

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (syntheticEscapeEvents.remove(e)) {
			return;
		}

		if (e.getKeyCode() != KeyEvent.VK_ESCAPE || !plugin.shouldRemapEscape()) {
			return;
		}

		ModifierlessKeybind remapTo = config.remapTo();
		int mappedKeyCode = remapTo.getKeyCode();

		if (mappedKeyCode == KeyEvent.VK_UNDEFINED) {
			e.consume();
			return;
		}

		modified.put(e.getKeyCode(), mappedKeyCode);
		e.setKeyCode(mappedKeyCode);
		e.setKeyChar(KeyEvent.CHAR_UNDEFINED);

		if (config.experimentalDialogOnlyMode() && !plugin.isDialogOpen()) {
			postSyntheticEscape(e, KeyEvent.KEY_PRESSED);
			syntheticEscapePending = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (syntheticEscapeEvents.remove(e)) {
			return;
		}

		Integer mappedKeyCode = modified.remove(e.getKeyCode());
		if (mappedKeyCode != null) {
			e.setKeyCode(mappedKeyCode);
			e.setKeyChar(KeyEvent.CHAR_UNDEFINED);
		}

		if (syntheticEscapePending) {
			syntheticEscapePending = false;
			postSyntheticEscape(e, KeyEvent.KEY_RELEASED);
		}
	}

	/**
	 * Posts a brand-new synthetic Escape {@link KeyEvent} to the same component the
	 * real one came
	 * from. The real Escape press/release gets remapped in place above (as usual,
	 * so chat/dialogue
	 * never see it), but outside of a dialogue it should also keep acting like a
	 * real Escape press
	 * (e.g. closing the bank) - which the real event can no longer do once its key
	 * code has already
	 * been overwritten. This fires that native behaviour as an extra keystroke
	 * instead.
	 */
	private void postSyntheticEscape(KeyEvent source, int id) {
		if (!(source.getSource() instanceof Component)) {
			return;
		}

		KeyEvent synthetic = new KeyEvent((Component) source.getSource(), id, System.currentTimeMillis(), 0,
				KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
		syntheticEscapeEvents.add(synthetic);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(synthetic);
	}
}
