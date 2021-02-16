package de.pinphreek.Listeners;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import de.pinphreek.config.Config;
import de.pinphreek.events.Event;
import de.pinphreek.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScheduleListener extends ListenerAdapter {

	public static long msgId = 0;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message;
		String content, lowContent;

		// We don't want to respond to other bot accounts, including ourself
		if (event.getAuthor().isBot()) {
			try {
				if (event.getMember().getUser().equals(event.getJDA().getSelfUser()) && Config.events.size() > 0) {
					for (int i = Config.events.size() - 1; i > -1; i++) {
						if (event.getChannel().getIdLong() == Config.events.get(i).channelId) {
							if (event.getMessage().getEmbeds().get(event.getMessage().getEmbeds().size() - 1).getTitle()
									.startsWith("Scheduled") || event.getMessage().getContentRaw().startsWith("Scheduled")) {
								Config.events.get(i).messageId = event.getMessageIdLong();

								// Add reactions
								event.getMessage().addReaction("➕").queue();
								event.getMessage().addReaction("➖").queue();
								return;
							}
						}
					}
					return;
				}
			} catch (Exception e) {
				return;
			}
			return;
		}

		// command?
		if (!event.getMessage().getContentRaw().startsWith("!"))
			return;

		// getContentDisplay() is a lazy getter which modifies the content for e.g.
		// console view (strip discord formatting)
		message = event.getMessage();
		lowContent = message.getContentRaw().toLowerCase();
		content = message.getContentRaw();
		// new time
		if (lowContent.startsWith("!schedule")) {
			EmbedBuilder error = new EmbedBuilder();
			error.setColor(0xC43C1A);
			error.setTitle("Wrong Syntax");
			error.setDescription("Syntax: !schedule <topic> <time> [<date>]");

			if (lowContent.contains("https://") || lowContent.contains("http://") || lowContent.contains("www.")) {// Danke Robin
				error.setTitle("Links are not allowed!");
				error.setDescription("Please use a different title.");
				event.getChannel().sendMessage(error.build()).queue();
				return;
			}
			ZonedDateTime dt = null;
			int date = -1, time = -1;
			// !shedule <was> <time> [<date>]
			String[] split = content.split("\\s+");
			switch (split.length) {
			case 1:
			case 2:
				event.getChannel().sendMessage(error.build()).queue();
				return;
			case 3:
				if (split[2].contains(":") && split[2].length() < 6 && split[2].length() > 2) {
					time = 2;
				}
				break;
			default:
				if (split[split.length - 1].contains(".") && split[split.length - 1].length() > 2
						&& split[split.length - 1].length() < 10) {
					date = split.length - 1;
				} else if (split[split.length - 1].contains(":") && split[split.length - 1].length() < 6
						&& split[split.length - 1].length() > 2) {
					time = split.length - 1;
				} else {
					event.getChannel().sendMessage(error.build()).queue();
					return;
				}
				if (time == -1) {
					if (split[split.length - 2].contains(":") && split[split.length - 2].length() < 6
							&& split[split.length - 2].length() > 2) {
						time = split.length - 2;
					} else {
						event.getChannel().sendMessage(error.build()).queue();
						return;
					}
				}
			}
			
			// date not given
			if (date < 0 && time >= 0) {
				try {
					/* DANKE FÜR NICHTS, JAVA 8 */
					String sdate[] = split[time].split(":");
					int idate[] = new int[sdate.length];
					idate[0] = Integer.valueOf(sdate[0]);
					idate[1] = Integer.valueOf(sdate[1]);
					dt = ZonedDateTime.of(LocalDate.now(), LocalTime.of(idate[0], idate[1]),
							ZoneId.of("Europe/Berlin"));
				} catch (Exception e) {
					event.getChannel().sendMessage(error.build()).queue();
					return;
				}
			}
			
			// everything given
			else if (date >= 0 && time >= 0) {
				try {
					String sdate[] = split[time].split(":");
					int idate[] = new int[sdate.length];
					idate[0] = Integer.valueOf(sdate[0]);
					idate[1] = Integer.valueOf(sdate[1]);
					LocalTime lt = LocalTime.of(idate[0], idate[1]);

					// lil reset
					sdate = null;
					idate = null;

					sdate = split[date].split("[.]");
					idate = new int[sdate.length];
					
					//user comfort
					if(sdate[0].length() == 2 && sdate[0].charAt(0) == '0') {
						idate[0] = Integer.valueOf(sdate[0].replace("0", ""));
					}
					else {
						idate[0] = Integer.valueOf(sdate[0]);
					}
					
					if(sdate[1].length() == 2 && sdate[1].charAt(0) == '0') {
						idate[1] = Integer.valueOf(sdate[1].replace("0", ""));
					}
					else {
						idate[1] = Integer.valueOf(sdate[1]);
					}
					idate[2] = Integer.valueOf(sdate[2]);
					LocalDate ld = LocalDate.of(idate[2], idate[1], idate[0]);// #Amerika

					dt = ZonedDateTime.of(LocalDateTime.of(ld, lt), ZoneId.of("Europe/Berlin"));
				} catch (Exception e) {
					event.getChannel().sendMessage(error.build()).queue();
					return;
				}
			}
			// error
			else {
				event.getChannel().sendMessage(error.build()).queue();
				return;
			}
			
			if (dt != null) {
				if (ZonedDateTime.now().isAfter(dt)) {
					EmbedBuilder timov = new EmbedBuilder();
					timov.setColor(0xC43C1A);

					if (date > -1)
						timov.setTitle("The date " + split[date] + " " + split[time] + " is already over!");
					else
						timov.setTitle("The time " + split[time] + " is already over!");

					event.getChannel().sendMessage(timov.build()).queue();
					return;
				} else if (ZonedDateTime.now().isEqual(dt)) {
					EmbedBuilder timov = new EmbedBuilder();
					timov.setColor(0xC43C1A);
					timov.setTitle("Take a later time! It's already " + split[time] + ".");

					event.getChannel().sendMessage(timov.build()).queue();
					return;
				}

				String back = "Scheduled ";
				for (int i = 1; i < time; i++) {
					back += split[i] + " ";
				}
				back = date < 0 ? back + " at " + split[time] : back + " at " + split[time] + " " + split[date];
				if (back.length() < 151) {//Grüße gehen raus an Robin und Chris
					EmbedBuilder builder = new EmbedBuilder();
					builder.setTitle(back);
					builder.setDescription("The meeting is in aprox. "
							+ ((dt.toInstant().toEpochMilli() - System.currentTimeMillis()) / 60000) + " Minutes");
					builder.setColor(0xFFFF00);

					event.getChannel().sendMessage(builder.build()).queue();
				}
				else {
					event.getChannel().sendMessage(back).queue();
				}
				back = "";
				for (int i = 1; i < time; i++) {
					back = back + split[i] + " ";
				}

				/**********************
				 * add event to watch *
				 **********************/
				Event e = new Event(dt.toInstant().toEpochMilli(), back, event.getChannel().getIdLong());
				e.participants.add(event.getAuthor().getIdLong());

				// adding
				Config.events.add(e);
				Main.timer.schedule(e, e.calculateRemainingMillis());
				Config.getInstance().saveEvents();
				return;
			}
			event.getChannel().sendMessage(error.build()).queue();
		}

		else if (content.equalsIgnoreCase("!python-vs-java")) {
			event.getChannel().sendMessage("Java " + event.getAuthor().getAsMention()).queue();
		}

		// display help and filter other commands in different files
		else if (!content.equalsIgnoreCase("!events")) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Help");
			builder.setDescription("Schedule a meeting: !schedule <topic> <time> [<date>]\nList all events: !events\n");
			event.getChannel().sendMessage(builder.build()).queue();
		}
	}

}