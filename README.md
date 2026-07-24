# Chatbox Escape Blocker

A [RuneLite](https://runelite.net/) plugin that remaps the Escape key so it no longer closes
chatbox dialogues (NPC/player dialogue, options, level-up, etc.) or accidentally clears a chat
message you're typing.

Escape is left alone (and still works normally) while the bank, bank deposit box, Grand
Exchange, or a shop is open, so it keeps closing those the way it always has.

## Configuration

- **Remap Escape to** - whenever Escape is pressed (outside of the interfaces above), send this
  key to the game instead. Leave it unset to just block Escape outright, or set it to another
  keybind - e.g. your Inventory tab hotkey - so pressing Escape opens your inventory instead of
  doing nothing.
