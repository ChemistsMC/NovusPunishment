package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.*;
import me.ebonjaeger.novuspunishment.action.Kick;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class KickCommand extends BaseCommand {

    private BukkitService bukkitService;
    private Messenger messenger;
    private MySQL dataSource;

    @Inject KickCommand(BukkitService bukkitService, Messenger messenger, MySQL dataSource) {
        this.bukkitService = bukkitService;
        this.messenger = messenger;
        this.dataSource = dataSource;
    }

    @CommandAlias("kick")
    @CommandPermission("newpunish.command.kick")
    @CommandCompletion("@players")
    public void onCommand(CommandSender sender, OnlinePlayer player, String[] reason) {
        Player target = player.getPlayer();
        String _reason = String.join(", ", reason);

        if (sender.getName().equals(target.getName())) {
            messenger.sendMessage(sender, Message.ACTION_AGAINST_SELF);
            return;
        }

        if (target.hasPermission("newpunish.bypass.kick")) {
            messenger.sendMessage(sender, Message.KICK_EXEMPT, target.getName());
            return;
        }

        String staff = "console";
        if (sender instanceof Player) {
            staff = ((Player) sender).getUniqueId().toString();
        }

        Instant timestamp = Instant.now();
        Kick kick = new Kick(target.getUniqueId(), staff, timestamp, _reason);

        bukkitService.runTaskAsync(() -> dataSource.saveKick(kick));

        target.kickPlayer(Utils.formatKickMessage(kick.getReason()));

        messenger.broadcastMessage(Message.KICK_NOTIFICATION, "newpunish.notify.kick", target.getName(), kick.getReason());
    }
}
