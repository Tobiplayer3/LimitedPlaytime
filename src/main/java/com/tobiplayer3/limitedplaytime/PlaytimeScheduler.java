package com.tobiplayer3.limitedplaytime;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeScheduler extends BukkitRunnable {

    private final LimitedPlaytime limitedPlaytime;
    private final PlaytimeManager playtimeManager;
    private final MessageManager messageManager;
    private final Utils utils;

    public PlaytimeScheduler(LimitedPlaytime limitedPlaytime) {
        this.limitedPlaytime = limitedPlaytime;
        playtimeManager = limitedPlaytime.getPlaytimeManager();
        messageManager = limitedPlaytime.getMessageManager();
        utils = limitedPlaytime.getUtils();
    }

    public void run() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Playtime playtime = playtimeManager.getCachedPlaytime(player.getUniqueId());
                    if (playtime == null) {
                        continue;
                    }

                    int newPlaytime = playtime.getTimeRemaining() - 1;
                    playtime.setTimeRemaining(newPlaytime);

                    if(!StringUtils.containsIgnoreCase(Bukkit.getVersion(), "1.8") && !StringUtils.containsIgnoreCase(Bukkit.getVersion(), "1.9")) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(messageManager.getMessage(Message.TIME_SHORT, playtime)));
                    }else{
                        if (Bukkit.getPluginManager().isPluginEnabled("ActionBarAPI")) {
                            //ActionBarAPI.sendActionBar(player, messageManager.getMessage(Message.TIME_SHORT, playtime));
                        }
                    }

                    if (playtimeManager.isNotifyStep(newPlaytime)) {
                        player.sendMessage(Integer.toString(playtime.getTimeRemaining() / 20));
                    }

                    if (newPlaytime <= 0) {
                        utils.runSync(() -> player.kickPlayer("test!"));
                    }
                }

            }
        }.runTask(limitedPlaytime);

    }

}
