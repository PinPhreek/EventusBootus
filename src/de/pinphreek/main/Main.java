package de.pinphreek.main;

import java.util.Timer;

import javax.security.auth.login.LoginException;

import de.pinphreek.Listeners.*;
import de.pinphreek.config.Config;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

	public static JDA jda;
	public static Timer timer = new Timer();
	
	public static void main(String[] args) {
		try {
			//loading config
			Config.getInstance().loadConfig();
			
			//starting bot
			jda = JDABuilder.createDefault(Config.TOKEN).build();
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
			jda.getPresence().setActivity(Activity.playing("with dead souls"));
			jda.addEventListener(new ScheduleListener());
			jda.addEventListener(new EventsListener());
			jda.addEventListener(new ReactionListener());
			
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

}