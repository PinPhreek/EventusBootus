package de.pinphreek.Listeners;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

import de.pinphreek.config.Config;
import de.pinphreek.events.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class EventsListener extends ListenerAdapter{
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		if(event.getAuthor().isBot()) return;
		ArrayList<Event> events = getEventForChannel(event.getChannel().getIdLong());
		
		if(event.getMessage().getContentRaw().equalsIgnoreCase("!events")) {
			EmbedBuilder builder = new EmbedBuilder();
			if(events.size() <= 0) {
				builder.setColor(0xC43C1A);
				builder.setTitle("No meetings are planned.");
				builder.setDescription("To add a meeting type !schedule");
				event.getChannel().sendMessage(builder.build()).queue();
			}
			else {
				builder.setColor(0x54F542);
				builder.setTitle("Meetings planned:");
				
				/*Declaring vars for decrypting date and Time*/
				String s = "";
				LocalDate dateNow = LocalDate.now(ZoneId.of("Europe/Berlin"));
				LocalDate dateCompare = null;
				LocalTime timeScheduled = null;
				
				/*decrypting date and time and add to Embed*/
				for(int i = 0; i < events.size(); i++) {
					dateCompare = Instant.ofEpochMilli(events.get(i).scheduledAt).atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
					timeScheduled = Instant.ofEpochMilli(events.get(i).scheduledAt).atZone(ZoneId.of("Europe/Berlin")).toLocalTime();
					if(dateCompare.isAfter(dateNow)){
						s += events.get(i).title + " " + MarkdownUtil.bold(dateCompare.getDayOfMonth() + "." + dateCompare.getMonthValue() + "." + dateCompare.getYear() +
								" " + timeScheduled.toString()) + "\n";
					}
					else {
						s += events.get(i).title + " " + MarkdownUtil.bold(timeScheduled.toString() + "\n");
					}
				}
				
				builder.setDescription(s);
				event.getChannel().sendMessage(builder.build()).queue();
				return;
			}
		}
	}
	private ArrayList<Event> getEventForChannel(long channelId){
		ArrayList<Event> ret = new ArrayList<>();
		for(int i = 0; i < Config.events.size(); i++) {
			if(Config.events.get(i).channelId == channelId) {
				ret.add(Config.events.get(i));
			}
		}
		return ret;
	}

}
