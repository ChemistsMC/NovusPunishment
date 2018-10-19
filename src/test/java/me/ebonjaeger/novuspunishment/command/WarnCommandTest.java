package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import me.ebonjaeger.novuspunishment.StateManager;
import me.ebonjaeger.novuspunishment.configuration.ActionSettings;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link WarnCommand} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class WarnCommandTest {

    @InjectMocks
    private WarnCommand warnCommand;

    @Mock
    private BukkitService bukkitService;

    @Mock
    private Messenger messenger;

    @Mock
    private MySQL dataSource;

    @Mock
    private SettingsManager settings;

    @Mock
    private StateManager stateManager;

    @Test
    public void senderSameAsTarget() {
        // given
        Player sender = mock(Player.class);
        given(sender.getName()).willReturn("Bob");
        OnlinePlayer player = new OnlinePlayer(sender);

        // when
        warnCommand.onCommand(sender, player, "test");

        // then
        verify(messenger).sendMessage(sender, Message.ACTION_AGAINST_SELF);
        verifyZeroInteractions(bukkitService);
        verifyZeroInteractions(dataSource);
        verifyZeroInteractions(stateManager);
    }

    @Test
    public void targetIsExempt() {
        // given
        Player sender = mock(Player.class);
        Player target = mock(Player.class);
        given(sender.getName()).willReturn("Bob");
        given(target.getName()).willReturn("Allen");
        given(target.hasPermission("newpunish.bypass.warn")).willReturn(true);
        OnlinePlayer player = new OnlinePlayer(target);

        // when
        warnCommand.onCommand(sender, player, "test");

        // then
        verify(messenger).sendMessage(sender, Message.WARN_EXEMPT, target.getName());
        verifyZeroInteractions(bukkitService);
        verifyZeroInteractions(dataSource);
        verifyZeroInteractions(stateManager);
    }

    @Test
    public void successfulWarnFromPlayer() {
        // given
        Player sender = mock(Player.class);
        Player target = mock(Player.class);
        given(sender.getName()).willReturn("Bob");
        given(sender.getUniqueId()).willReturn(UUID.randomUUID());
        given(target.getName()).willReturn("Allen");
        given(target.getUniqueId()).willReturn(UUID.randomUUID());
        given(target.hasPermission("newpunish.bypass.warn")).willReturn(false);
        given(settings.getProperty(ActionSettings.WARNS_UNTIL_KICK)).willReturn(3);
        given(stateManager.getWarnings(target.getUniqueId())).willReturn(1);
        OnlinePlayer player = new OnlinePlayer(target);

        // when
        warnCommand.onCommand(sender, player, "test");

        // then
        verify(bukkitService).runTaskAsync(any());
        verify(stateManager).incrementWarnings(target.getUniqueId());
        verify(messenger).sendMessage(target, Message.WARN_PLAYER, "test");
        verify(messenger).broadcastMessageExcept(Message.WARN_NOTIFICATION, target, "newpunish.notify.warn", "Allen", "test");
        verify(target, never()).kickPlayer(any());
    }

    @Test
    public void successfulWarnFromConsole() {
        // given
        CommandSender sender = mock(ConsoleCommandSender.class);
        Player target = mock(Player.class);
        given(sender.getName()).willReturn("CONSOLE");
        given(target.getName()).willReturn("Allen");
        given(target.getUniqueId()).willReturn(UUID.randomUUID());
        given(target.hasPermission("newpunish.bypass.warn")).willReturn(false);
        given(settings.getProperty(ActionSettings.WARNS_UNTIL_KICK)).willReturn(3);
        given(stateManager.getWarnings(target.getUniqueId())).willReturn(1);
        OnlinePlayer player = new OnlinePlayer(target);

        // when
        warnCommand.onCommand(sender, player, "test");

        // then
        verify(bukkitService).runTaskAsync(any());
        verify(stateManager).incrementWarnings(target.getUniqueId());
        verify(messenger).sendMessage(target, Message.WARN_PLAYER, "test");
        verify(messenger).broadcastMessageExcept(Message.WARN_NOTIFICATION, target, "newpunish.notify.warn", "Allen", "test");
        verify(target, never()).kickPlayer(any());
    }

    @Test
    public void successfulWarnFromResultsInKick() {
        // given
        Player sender = mock(Player.class);
        Player target = mock(Player.class);
        given(sender.getName()).willReturn("Bob");
        given(sender.getUniqueId()).willReturn(UUID.randomUUID());
        given(target.getName()).willReturn("Allen");
        given(target.getUniqueId()).willReturn(UUID.randomUUID());
        given(target.hasPermission("newpunish.bypass.warn")).willReturn(false);
        given(settings.getProperty(ActionSettings.WARNS_UNTIL_KICK)).willReturn(3);
        given(stateManager.getWarnings(target.getUniqueId())).willReturn(3);
        OnlinePlayer player = new OnlinePlayer(target);

        // when
        warnCommand.onCommand(sender, player, "test");

        // then
        verify(bukkitService).runTaskAsync(any());
        verify(stateManager).incrementWarnings(target.getUniqueId());
        verify(messenger).broadcastMessageExcept(Message.WARN_NOTIFICATION, target, "newpunish.notify.warn", "Allen", "test");
        verify(target).kickPlayer(any());
    }
}
