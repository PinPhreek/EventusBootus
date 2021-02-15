package de.pinphreek.Listeners;

import de.pinphreek.config.Config;
import de.pinphreek.events.Event;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		// Auf die eigenen reactions wird nicht reagiert, wir sind hier nicht auf
		// youtube, bitches!
		if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
			return;
		}
		try {
			if (event.getReactionEmote().getEmoji().equals("➕")) {
				for (Event e : Config.events) {
					if (e.messageId == event.getMessageIdLong() && !isAlreadyRegistered(event.getUserIdLong(), e)) {
						e.participants.add(event.getUserIdLong());
						// event.getReaction().removeReaction().queue();
						return;
					}
				}

			} else if (event.getReactionEmote().getEmoji().equals("➖")) {
				for (Event e : Config.events) {
					if (e.messageId == event.getMessageIdLong()) {
						e.participants.remove(event.getUserIdLong());
						// event.getReaction().removeReaction().queue();
						return;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(event.getChannel().getHistoryAfter(event.getMessageId(), 1).toString());
		}
	}

	private boolean isAlreadyRegistered(long userId, Event e) {
		for (Long l : e.participants) {
			if (l == userId) {
				return true;
			}
		}
		return false;
	}

}
