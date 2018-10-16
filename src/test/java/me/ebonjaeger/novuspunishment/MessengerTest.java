package me.ebonjaeger.novuspunishment;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static me.ebonjaeger.novuspunishment.TestUtils.setField;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link Messenger}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MessengerTest {

	@InjectMocks
	private Messenger messenger;

	@Mock
	private NovusPunishment plugin;

	@Before
	public void setup() {
		Server server = mock(Server.class);
		setField(JavaPlugin.class, "server", plugin, server);
		given(server.getConsoleSender()).willReturn(mock(ConsoleCommandSender.class));
	}

	@Test
	public void verifyBroadcastPermissions() {
		// given
		Player player1 = mock(Player.class);
		Player player2 = mock(Player.class);
		given(player1.hasPermission("newpunish.notify.test")).willReturn(false);
		given(player2.hasPermission("newpunish.notify.test")).willReturn(true);
		Collection<Player> onlinePlayers = new ArrayList<>();
		Collections.addAll(onlinePlayers, player1, player2);
		when(plugin.getServer().getOnlinePlayers()).thenAnswer(invocation -> onlinePlayers);

		// when
		messenger.broadcastMessage(Message.PLAYER_UNBANNED, "newpunish.notify.test");

		// then
		verify(player1, never()).sendMessage(anyString());
		verify(player2).sendMessage(anyString());
		verify(plugin.getServer().getConsoleSender()).sendMessage(anyString());
	}

	@Test
	public void verifyBroadcastException() {
		// given
		Player player1 = mock(Player.class);
		Player player2 = mock(Player.class);
		given(player1.hasPermission("newpunish.notify.test")).willReturn(true);
		Collection<Player> onlinePlayers = new ArrayList<>();
		Collections.addAll(onlinePlayers, player1, player2);
		when(plugin.getServer().getOnlinePlayers()).thenAnswer(invocation -> onlinePlayers);

		// when
		messenger.broadcastMessageExcept(Message.PLAYER_UNBANNED, player2, "newpunish.notify.test");

		// then
		verify(player1).sendMessage(anyString());
		verify(player2, never()).sendMessage(anyString());
		verify(plugin.getServer().getConsoleSender()).sendMessage(anyString());
	}

	@Test
	public void verifyCorrectReplacement() {
		// given
		Player player = mock(Player.class);

		// when
		messenger.sendMessage(player, Message.MUTE_PLAYER, "forever", "testing");

		// then
		String expected = ChatColor.RED + "You have been muted until " + ChatColor.WHITE + "forever " + ChatColor.GRAY + "for: " + ChatColor.WHITE + "testing";
		verify(player).sendMessage(expected);
	}
}
