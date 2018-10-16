package me.ebonjaeger.novuspunishment.command;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static me.ebonjaeger.novuspunishment.TestUtils.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link TempbanCommand} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class TempbanCommandTest {

	@InjectMocks
	private TempbanCommand tempbanCommand;

	@Mock
	private BukkitService bukkitService;

	@Mock
	private Messenger messenger;

	@Mock
	private MySQL dataSource;

	private ServerMock server;

	@Before
	public void setup() {
		server = MockBukkit.mock();
		setField(Bukkit.class, "server", null, server);
	}

	@After
	public void destroy() {
		MockBukkit.unload();
	}

	@Test
	public void senderSameAsTarget() {
		// given
		Player sender = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		OnlinePlayer player = new OnlinePlayer(sender);

		// when
		tempbanCommand.onCommand(sender, player, "20m", "test");

		// then
		verify(messenger).sendMessage(sender, Message.ACTION_AGAINST_SELF);
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
	}

	@Test
	public void targetIsAlreadyBanned() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(target.getName()).willReturn("Allen");
		OnlinePlayer player = new OnlinePlayer(target);
		server.getBanList(BanList.Type.NAME).addBan("Allen", "test", null, "Tester");

		// when
		tempbanCommand.onCommand(sender, player, "20m", "test");

		// then
		verify(messenger).sendMessage(sender, Message.ALREADY_BANNED, target.getName());
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
		given(target.hasPermission("newpunish.bypass.tempban")).willReturn(true);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		tempbanCommand.onCommand(sender, player, "20m", "test");

		// then
		verify(messenger).sendMessage(sender, Message.BAN_EXEMPT, target.getName());
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
	}

	@Test
	public void invalidDuration() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(target.getName()).willReturn("Allen");
		given(target.hasPermission("newpunish.bypass.tempban")).willReturn(false);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		tempbanCommand.onCommand(sender, player, "bogus", "test");

		// then
		verify(messenger).sendMessage(sender, Message.INVALID_DURATION, "bogus");
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
	}

	@Test
	public void successfulTempbanFromPlayer() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(sender.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.tempban")).willReturn(false);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		tempbanCommand.onCommand(sender, player, "20m", "test");

		// then
		verify(bukkitService).runTaskAsync(any());
		assertThat(server.getBanList(BanList.Type.NAME).isBanned("Allen"), equalTo(true));
		String duration = "0 Days, 0 Hours, 20 Minutes and 00 Seconds";
		verify(messenger).broadcastMessage(Message.TEMPBAN_NOTIFICATION, "newpunish.notify.tempban", "Allen", duration, "test");
	}

	@Test
	public void successfulTempbanFromConsole() {
		// given
		CommandSender sender = mock(ConsoleCommandSender.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("CONSOLE");
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.tempban")).willReturn(false);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		tempbanCommand.onCommand(sender, player, "20m", "test");

		// then
		verify(bukkitService).runTaskAsync(any());
		assertThat(server.getBanList(BanList.Type.NAME).isBanned("Allen"), equalTo(true));
		String duration = "0 Days, 0 Hours, 20 Minutes and 00 Seconds";
		verify(messenger).broadcastMessage(Message.TEMPBAN_NOTIFICATION, "newpunish.notify.tempban", "Allen", duration, "test");
	}
}
