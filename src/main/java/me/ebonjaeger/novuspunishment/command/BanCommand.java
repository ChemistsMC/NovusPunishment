package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import me.ebonjaeger.novuspunishment.*;
import me.ebonjaeger.novuspunishment.action.PermanentBan;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class BanCommand extends BaseCommand {

    private BukkitService bukkitService;
    private Messenger messenger;
    private MySQL dataSource;

    @Inject BanCommand(BukkitService bukkitService, Messenger messenger, MySQL dataSource) {
        this.bukkitService = bukkitService;
        this.messenger = messenger;
        this.dataSource = dataSource;
    }

    @CommandAlias("ban")
    @CommandPermission("newpunish.command.ban")
    @CommandCompletion("@players")
    public void onCommand(CommandSender sender, OfflinePlayer player, String... reason) {
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(player.getName())) {
            messenger.sendMessage(sender, Message.ALREADY_BANNED, player.getName());
            return;
        }

        if (sender.getName().equals(player.getName())) {
            messenger.sendMessage(sender, Message.ACTION_AGAINST_SELF);
            return;
        }

        // Because looking up an offline player may result in a blocking request,
        // we have to somehow check if they have the bypass permission set in an
        // asynchronous manner
        bukkitService.runTaskAsync(() -> {
            boolean exempt = bukkitService.hasPermission(player, "newpunish.bypass.ban");

            bukkitService.runTask(() -> {
                if (exempt) {
                    messenger.sendMessage(sender, Message.BAN_EXEMPT, player.getName());
                    return;
                }

                String staff = "console";
                if (sender instanceof Player) {
                    staff = ((Player) sender).getUniqueId().toString();
                }

                String _reason = String.join(", ", reason);
                Instant timestamp = Instant.now();
                PermanentBan ban = new PermanentBan(player.getUniqueId(), staff, timestamp, _reason);

                bukkitService.runTaskAsync(() -> dataSource.saveBan(ban));

                // Add ban entry and kick from the server if online
                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), _reason, null, sender.getName());
                if (player.isOnline()) {
                    player.getPlayer().kickPlayer(Utils.formatBanMessage(ban.getReason()));
                }

                // Notify players
                messenger.broadcastMessage(Message.BAN_NOTIFICATION, "newpunish.notify.ban", player.getName(), ban.getReason());
            });
        });
    }
}
