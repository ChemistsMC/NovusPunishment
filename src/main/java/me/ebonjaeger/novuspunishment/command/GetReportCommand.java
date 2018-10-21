package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.StateManager;
import me.ebonjaeger.novuspunishment.Utils;
import me.ebonjaeger.novuspunishment.action.Action;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class GetReportCommand extends BaseCommand {

    private final int PAGE_SIZE = 10;

    private BukkitService bukkitService;
    private MySQL dataSource;
    private Messenger messenger;
    private StateManager stateManager;

    @Inject GetReportCommand(BukkitService bukkitService, MySQL dataSource, Messenger messenger, StateManager stateManager) {
        this.bukkitService = bukkitService;
        this.dataSource = dataSource;
        this.messenger = messenger;
        this.stateManager = stateManager;
    }

    @CommandAlias("getreport|gr")
    @CommandPermission("newpunish.command.getreport")
    @CommandCompletion("@players")
    public void onCommand(CommandSender sender, OfflinePlayer target, @Optional Integer page) {
        if (page == null) {
            page = 1;
        }

        int finalPage = page;
        bukkitService.runTaskAsync(() -> {
            int totalIncidents = dataSource.getTotalIncidents(target.getUniqueId());

            // Make sure there was no error getting the count
            if (totalIncidents == -1) {
                bukkitService.runTask(() -> {
                    messenger.sendMessage(sender, Message.ERROR_GETTING_COUNT);
                });

                return;
            }

            int totalPages = (totalIncidents + PAGE_SIZE - 1) * PAGE_SIZE;

            // Check page bounds
            if (finalPage < 1 || finalPage > totalPages) {
                bukkitService.runTask(() -> {
                    messenger.sendMessage(sender, Message.INVALID_PAGE);
                });

                return;
            }

            List<Action> incidents = dataSource.getActionsAgainstUser(target.getUniqueId(), finalPage, PAGE_SIZE);

            // Get player state for muted status
            PlayerState state = stateManager.getPlayerState(target.getUniqueId());
            if (state == null) {
                state = dataSource.getPlayerState(target.getUniqueId());
            }

            boolean muted;
            if (state != null) {
                muted = state.isMuted();
            } else {
                muted = false;
            }

            bukkitService.runTask(() -> {
                Report report = new Report(target.getName(), finalPage, totalIncidents, incidents, muted);

                sendReport(sender, report);
            });
        });
    }

    private void sendReport(CommandSender sender, Report report) {
        int totalPages = (report.getTotalIncidents() + PAGE_SIZE - 1) * PAGE_SIZE;

        // Send header and counts
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + " ---------------------------------------------------- ");
        sender.sendMessage(ChatColor.BLUE + "Player: " + ChatColor.WHITE + report.getName());
        sender.sendMessage(ChatColor.BLUE + "Total incidents: " + ChatColor.WHITE + report.getTotalIncidents());
        sender.sendMessage(ChatColor.BLUE + "Currently muted: " + ChatColor.WHITE + report.isMuted());
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + " ---------------" + ChatColor.BLUE + " Page " + report.getPage() + "/" + totalPages + ChatColor.GRAY + ChatColor.STRIKETHROUGH + " --------------- ");

        // Send incident information
        for (Action incident : report.getIncidents()) {
            String timeStamp = Utils.formatTime(incident.getTimestamp());

            String staffName;
            if (incident.getStaff().equals(Bukkit.getConsoleSender().getName())) {
                staffName = "console";
            } else {
                staffName = Bukkit.getOfflinePlayer(UUID.fromString(incident.getStaff())).getName();
            }

            String reason = "";
            if (incident.getReason() != null) {
                reason = incident.getReason();
            }

            sender.sendMessage(String.format(ChatColor.GRAY + " - " + ChatColor.WHITE + "%s " + ChatColor.GRAY + "on " + ChatColor.WHITE + "%s " +
                    ChatColor.GRAY + "by " + ChatColor.WHITE + "%s " + ChatColor.GRAY + "for: " + ChatColor.WHITE + "%s",
                incident.getType().getName(), timeStamp, staffName, reason
            ));
        }

        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + " ---------------------------------------------------- ");
    }

    /**
     * Class to wrap report data in
     */
    private class Report {

        private String name;
        private int page;
        private int totalIncidents;
        private List<Action> incidents;
        private boolean muted;

        Report(String name, int page, int totalIncidents, List<Action> incidents, boolean muted) {
            this.name = name;
            this.page = page;
            this.totalIncidents = totalIncidents;
            this.incidents = incidents;
            this.muted = muted;
        }

        public String getName() {
            return name;
        }

        public int getPage() {
            return page;
        }

        public int getTotalIncidents() {
            return totalIncidents;
        }

        public List<Action> getIncidents() {
            return incidents;
        }

        public boolean isMuted() {
            return muted;
        }
    }
}
