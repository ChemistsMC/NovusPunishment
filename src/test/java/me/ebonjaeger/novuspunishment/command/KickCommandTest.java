package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
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
 * Tests for the {@link KickCommand} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class KickCommandTest {

	@InjectMocks
	private KickCommand kickCommand;

	@Mock
	private BukkitService bukkitService;

	@Mock
	private Messenger messenger;

	@Mock
	private MySQL dataSource;

	@Test
	public void senderSameAsTarget() {
		// given
		Player sender = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		OnlinePlayer player = new OnlinePlayer(sender);

		// when
		kickCommand.onCommand(sender, player, new String[]{"test"});

		// then
		verify(messenger).sendMessage(sender, Message.ACTION_AGAINST_SELF);
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
	}

	@Test
	public void targetIsExempt() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(target.getName()).willReturn("Allen");
		given(target.hasPermission("newpunish.bypass.kick")).willReturn(true);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		kickCommand.onCommand(sender, player, new String[]{"test"});

		// then
		verify(messenger).sendMessage(sender, Message.KICK_EXEMPT, target.getName());
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
	}

	@Test
	public void successfulKickFromPlayer() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(sender.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.kick")).willReturn(false);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		kickCommand.onCommand(sender, player, new String[]{"test"});

		// then
		verify(bukkitService).runTaskAsync(any());
		verify(target).kickPlayer(anyString());
		verify(messenger).broadcastMessage(Message.KICK_NOTIFICATION, "newpunish.notify.kick", "Allen", "test");
	}

	@Test
	public void successfulKickFromConsole() {
		// given
		CommandSender sender = mock(ConsoleCommandSender.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("CONSOLE");
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.kick")).willReturn(false);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		kickCommand.onCommand(sender, player, new String[]{"test"});

		// then
		verify(bukkitService).runTaskAsync(any());
		verify(target).kickPlayer(anyString());
		verify(messenger).broadcastMessage(Message.KICK_NOTIFICATION, "newpunish.notify.kick", "Allen", "test");
	}
}
