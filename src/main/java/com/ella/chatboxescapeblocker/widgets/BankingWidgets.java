package com.ella.chatboxescapeblocker.widgets;

import net.runelite.api.gameval.InterfaceID;

/** Bank, Grand Exchange, trade, Death's Office, and shop interfaces. */
public final class BankingWidgets {
	private BankingWidgets() {
	}

	public static final ModalWidget[] WIDGETS = {

			ModalWidget.of(InterfaceID.Bankmain.UNIVERSE, "Bank"),
			ModalWidget.of(InterfaceID.BankDepositbox.UNIVERSE, "Bank deposit box"),
			ModalWidget.of(InterfaceID.BankDepositImp.INVENTORY, "Bank deposit imp"),
			ModalWidget.of(InterfaceID.BankpinKeypad.UNIVERSE, "Bank PIN entry keypad"),
			ModalWidget.of(InterfaceID.BankpinSettings.SETTINGS, "Bank PIN settings"),
			ModalWidget.of(InterfaceID.GeCollect.UNIVERSE, "Grand Exchange collection box"),
			ModalWidget.of(InterfaceID.Menu.LJ_LAYER2, "Purchase additional bank slots"),

			// Grand Exchange
			ModalWidget.of(InterfaceID.GeOffers.CONTENTS, "Grand Exchange offer screen"),
			ModalWidget.of(InterfaceID.GePricelist.LIST, "Grand Exchange price list / search"),
			ModalWidget.of(InterfaceID.GeHistory.LIST, "Grand Exchange offer history"),
			ModalWidget.of(InterfaceID.GeCollect.UNIVERSE, "Grand Exchange collection box"),
			ModalWidget.of(InterfaceID.Itemsets.ITEMLIST, "Item sets (bundle buy/sell) list"),

			// Trade
			ModalWidget.of(InterfaceID.Trademain.YOUR_OFFER, "Trade screen"),
			ModalWidget.of(InterfaceID.Tradeconfirm.YOUR_OFFER, "Trade confirmation screen"),

			// Death's Office
			ModalWidget.of(InterfaceID.DeathOffice.ITEMS, "Death's Office item recovery screen"),
			ModalWidget.of(InterfaceID.DeathCoffer.CONTENTS, "Death's Office coffer (death fee payment)"),

			// Shop
			ModalWidget.of(InterfaceID.Shopmain.ITEMS, "Generic shop interface"),
	};
}
