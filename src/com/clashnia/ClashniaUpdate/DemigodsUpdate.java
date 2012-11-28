package com.clashnia.ClashniaUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

import com.WildAmazing.marinating.Demigods.DUtil;

public class DemigodsUpdate {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static boolean shouldUpdate() {
		PluginDescriptionFile pdf = DUtil.getPlugin().getDescription();
		String latestVersion = pdf.getVersion();
		String onlineVersion;
		
		if (latestVersion.startsWith("d")) return false; // development versions shouldn't downgrade

		try {
			URL version = new URL("http://www.clashnia.com/plugins/demigods/version.txt");
			URLConnection versionCon = version.openConnection();
			versionCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); //FIXES 403 ERROR
			BufferedReader in = new BufferedReader(new InputStreamReader(versionCon.getInputStream()));
			onlineVersion = in.readLine();
			if (latestVersion.equals(onlineVersion) || onlineVersion.startsWith("d")) {
				log.info("[Demigods] Demigods is up to date. Version "
						+ latestVersion);
				in.close();
				return false;
			} else {
				log.info("[Demigods] Demigods is not up to date...");
				log.info("[Demigods] Current version: " + latestVersion);
				log.info("[Demigods] New version: " + onlineVersion);
				in.close();
				return true;
			}
		} catch (MalformedURLException ex) {
			log.warning("[Demigods] Error accessing version URL.");
		} catch (IOException ex) {
			log.warning("[Demigods] Error checking for update.");
		}
		return false;
	}

	public static void demigodsUpdate() {
		if (shouldUpdate()) {
			try {
				log.info("[Demigods] Attempting to update to latest version...");
				URL plugin = new URL(
						"http://www.clashnia.com/plugins/demigods/Demigods.jar");
				URLConnection pluginCon = plugin.openConnection();
				pluginCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); //FIXES 403 ERROR
				ReadableByteChannel rbc = Channels.newChannel(pluginCon.getInputStream());
				FileOutputStream fos = new FileOutputStream("plugins"
						+ File.separator + "Demigods.jar");
				fos.getChannel().transferFrom(rbc, 0L, 16777216L);
				
				while (((SeekableByteChannel) fos).size() == ((SeekableByteChannel) fos).position()) {
					log.info("[Demigods] Download complete!");
					fos.close();
					Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Demigods"));
					Bukkit.getServer().getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("Demigods"));
				}
				log.info("[Demigods] Please reload the server!");
			} catch (MalformedURLException ex) {
				log.warning("[Demigods] Error accessing URL: " + ex);
			} catch (FileNotFoundException ex) {
				log.warning("[Demigods] Error accessing URL: " + ex);
			} catch (IOException ex) {
				log.warning("[Demigods] Error downloading file: " + ex);
			}
		} else {
			// do nothing
		}
	}
}