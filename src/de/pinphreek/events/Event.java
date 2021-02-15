package de.pinphreek.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimerTask;

import de.pinphreek.config.Config;
import de.pinphreek.main.Main;

public class Event extends TimerTask implements Serializable{

	private static final long serialVersionUID = -7306799209962197500L;
	public long scheduledAt = 0;
	public long messageId = 0;
	public long channelId = 0;
	public ArrayList<Long> participants = new ArrayList<>();
	public String title = null;
	public boolean pinged = false;

	public Event(long scheduledTime, String title, long textchannel) {
		this.scheduledAt = scheduledTime;
		this.title = title;
		this.channelId = textchannel;
	}

	public long calculateRemainingMillis() {
		return this.scheduledAt - System.currentTimeMillis();
	}

	@Override
	public void run() {
		
		String s = "";
		try {
			for (long participant : this.participants) {
				s += Main.jda.getUserById(participant).getAsMention() + " ";
			}

			s = "The meeting " + title + " is now " + s;
			Main.jda.getTextChannelById(this.channelId).sendMessage(s).queue();
		}
		catch(Exception e){
			//Main.jda.getTextChannelById(this.channelId).sendMessage("Sentence the developer of this bot to go to hell.").queue();
		}
		
		//aufräumen
		Config.events.remove(this);
		Config.getInstance().saveEvents();
		this.cancel();
		
	}
}
