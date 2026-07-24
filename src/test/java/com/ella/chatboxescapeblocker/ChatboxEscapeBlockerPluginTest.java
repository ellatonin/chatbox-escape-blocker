package com.ella.chatboxescapeblocker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ChatboxEscapeBlockerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ChatboxEscapeBlockerPlugin.class);
		RuneLite.main(args);
	}
}
