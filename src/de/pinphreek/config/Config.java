package de.pinphreek.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import de.pinphreek.events.Event;
import de.pinphreek.main.Main;

public class Config implements Serializable{

	private static final long serialVersionUID = 4480373974209462255L;
	public static String TOKEN = "";
	public static ArrayList<Event> events = new ArrayList<>();
	
	private static Config instance;
	
	public synchronized static Config getInstance() {
        if (Config.instance == null) {
            Config.instance = new Config();
        }
        return Config.instance;
    }
	
	public void saveEvents() {
		try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Config"));
            oos.writeObject(events);
            oos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	public void loadEvents() {
		try {
            FileInputStream fis = new FileInputStream("Config");
            ObjectInputStream ois = new ObjectInputStream(fis);
            events = (ArrayList<Event>) ois.readObject();
            
            for(Event e : events) {	
            	if(e.scheduledAt < System.currentTimeMillis()) {
            		events.remove(e);
            		continue;
            	}
            	Main.timer.schedule(e, e.calculateRemainingMillis());
            }            
            
            ois.close();
            fis.close();
            saveEvents();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();

        }
	}
	public void loadConfig() {
		File f = new File("token.txt");
		if(!f.exists()) {
			try {
				f.createNewFile();
				System.err.println("Please paste the token for the bot in the file " + f.getAbsolutePath() + "!\nAborting.");
				System.exit(0);
			} catch (IOException e) {
				System.err.println("Can't create token-file! Read-Only-Filesystem/No Permissions?");
				System.err.println("Here is the stacktrace:");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		try {
			Scanner sc = new Scanner(f);
			TOKEN = sc.nextLine();
			sc.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can't read " + f.getAbsolutePath() + "!");
			System.exit(-1);
		}
		
		loadEvents();
		System.out.println("Loaded " + events.size() + " Events.\nLoading complete, connecting to discord...");
	}
}
