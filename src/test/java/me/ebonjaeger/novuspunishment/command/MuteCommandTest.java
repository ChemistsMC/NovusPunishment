package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.*;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MuteCommand} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class MuteCommandTest {

	@InjectMocks
	private MuteCommand muteCommand;

	@Mock
	private BukkitService bukkitService;

	@Mock
	private Messenger messenger;

	@Mock
	private MySQL dataSource;

	@Mock
	private StateManager stateManager;

	@Test
	public void senderSameAsTarget() {
		// given
		Player sender = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		OnlinePlayer player = new OnlinePlayer(sender);

		// when
		muteCommand.onCommand(sender, player, null, "test");

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
		given(target.hasPermission("newpunish.bypass.mute")).willReturn(true);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		muteCommand.onCommand(sender, player, null, "test");

		// then
		verify(messenger).sendMessage(sender, Message.MUTE_EXEMPT, target.getName());
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
		verifyZeroInteractions(stateManager);
	}

	@Test
	public void invalidDuration() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(sender.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.mute")).willReturn(false);
		given(stateManager.getPlayerState(target.getUniqueId())).willReturn(null);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		muteCommand.onCommand(sender, player, "bogus", "test");

		// then
		verify(messenger).sendMessage(sender, Message.INVALID_DURATION, "bogus");
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
		verify(stateManager).getPlayerState(target.getUniqueId());
		verifyNoMoreInteractions(stateManager);
	}

	@Test
	public void SuccessfulUnmute() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.mute")).willReturn(false);
		PlayerState state = new PlayerState(target.getUniqueId(), "Allen", true);
		given(stateManager.getPlayerState(target.getUniqueId())).willReturn(state);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		muteCommand.onCommand(sender, player, null);

		// then
		verify(messenger).sendMessage(sender, Message.UNMUTE_SUCCESS, "Allen");
		verify(messenger).sendMessage(target, Message.UNMUTE_PLAYER);
		verifyZeroInteractions(bukkitService);
		verifyZeroInteractions(dataSource);
		verify(stateManager, times(2)).getPlayerState(target.getUniqueId());
		verifyNoMoreInteractions(stateManager);
		assertThat(state.isMuted(), equalTo(false));
	}

	@Test
	public void successfulPermanentMuteFromPlayer() {
		// given
		Player sender = mock(Player.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("Bob");
		given(sender.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.mute")).willReturn(false);
		PlayerState state = new PlayerState(target.getUniqueId(), "Allen", false);
		given(stateManager.getOrCreateState(target)).willReturn(state);
		given(stateManager.getPlayerState(target.getUniqueId())).willReturn(null);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		muteCommand.onCommand(sender, player, null, "test");

		// then
		verify(bukkitService).runTaskAsync(any());
		verify(stateManager).addPlayerState(target.getUniqueId(), state);
		assertThat(state.isMuted(), equalTo(true));
		verify(messenger).sendMessage(target, Message.MUTE_PLAYER, "forever", "test");
		verify(messenger).broadcastMessageExcept(Message.MUTE_NOTIFICATION, target, "newpunish.notify.mute", "Allen", "forever", "test");
	}

	@Test
	public void successfulPermanentMuteFromConsole() {
		// given
		CommandSender sender = mock(ConsoleCommandSender.class);
		Player target = mock(Player.class);
		given(sender.getName()).willReturn("CONSOLE");
		given(target.getName()).willReturn("Allen");
		given(target.getUniqueId()).willReturn(UUID.randomUUID());
		given(target.hasPermission("newpunish.bypass.mute")).willReturn(false);
		PlayerState state = new PlayerState(target.getUniqueId(), "Allen", false);
		given(stateManager.getOrCreateState(target)).willReturn(state);
		given(stateManager.getPlayerState(target.getUniqueId())).willReturn(null);
		OnlinePlayer player = new OnlinePlayer(target);

		// when
		muteCommand.onCommand(sender, player, null, "test");

		// then
		verify(bukkitService).runTaskAsync(any());
		verify(stateManager).addPlayerState(target.getUniqueId(), state);
		assertThat(state.isMuted(), equalTo(true));
		verify(messenger).sendMessage(target, Message.MUTE_PLAYER, "forever", "test");
		verify(messenger).broadcastMessageExcept(Message.MUTE_NOTIFICATION, target, "newpunish.notify.mute", "Allen", "forever", "test");
	}
}
