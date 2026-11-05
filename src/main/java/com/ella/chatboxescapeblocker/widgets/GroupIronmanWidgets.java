package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/** Group Ironman interfaces. Probably more of these, can't verify right now. */
public final class GroupIronmanWidgets {
	private GroupIronmanWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {
			ModalWidget.of(InterfaceID.GimSettings.INFINITE, "Group Ironman settings"),
			ModalWidget.of(InterfaceID.GimOptions.INFINITE, "Group Ironman options"),
			ModalWidget.of(InterfaceID.SharedBank.INFINITE, "Group Ironman shared bank"),
	};
}
