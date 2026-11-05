package com.ella.chatboxescapeblocker.widgets;

/**
 * Pairs an InterfaceID widget with a human-readable description of what it
 * is, so the plugin's printOpenInterfaces() can report something more
 * useful than a bare widget id.
 */
public final class ModalWidget {
	public final int id;
	public final String description;

	private ModalWidget(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public static ModalWidget of(int id, String description) {
		return new ModalWidget(id, description);
	}
}
