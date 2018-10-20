package me.ebonjaeger.novuspunishment.listener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.StateManager;
import me.ebonjaeger.novuspunishment.configuration.ActionSettings;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests for {@link PlayerChatListener} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlayerChatListenerTest {

    private PlayerChatListener listener;

    @Mock
    private Messenger messenger;

    @Mock
    private SettingsManager settings;

    @Mock
    private StateManager stateManager;

    @Before
    public void setup() {
        List<String> disallowedCommands = new ArrayList<>();
        disallowedCommands.add("msg");
        given(settings.getProperty(ActionSettings.DISALLOWED_COMMANDS)).willReturn(disallowedCommands);
        listener = new PlayerChatListener(messenger, settings, stateManager);
    }

    @Test
    public void chatShouldBeDisallowed() {
        // given
        Player player = mock(Player.class);
        String message = "hey, who was that jerk who muted me?";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());
        PlayerState state = new PlayerState(player.getUniqueId(), "Allen", true, Instant.MAX);
        given(stateManager.getPlayerState(player.getUniqueId())).willReturn(state);

        // when
        listener.onPlayerAsyncChat(event);

        // then
        assertThat(event.isCancelled(), equalTo(true));
        verify(messenger).sendMessage(player, Message.CHAT_WHILE_MUTED);
    }

    @Test
    public void chatShouldBeAllowedAndUnmute() {
        // given
        Player player = mock(Player.class);
        String message = "hey, who was that jerk who muted me?";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());
        PlayerState state = new PlayerState(player.getUniqueId(), "Allen", true, Instant.MIN);
        given(stateManager.getPlayerState(player.getUniqueId())).willReturn(state);

        // when
        listener.onPlayerAsyncChat(event);

        // then
        assertThat(event.isCancelled(), equalTo(false));
        assertThat(state.isMuted(), equalTo(false));
        assertThat(state.getUntil(), equalTo(null));
        verifyZeroInteractions(messenger);
    }

    @Test
    public void chatPlayerNotMuted() {
        // given
        Player player = mock(Player.class);
        String message = "hey, that person who unmuted me was really nice!";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());

        // when
        listener.onPlayerAsyncChat(event);

        // then
        assertThat(event.isCancelled(), equalTo(false));
        verifyZeroInteractions(messenger);
    }

    @Test
    public void commandShouldBeDisallowed() {
        // given
        Player player = mock(Player.class);
        String message = "/msg Bob hey, who was that jerk who muted me?";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());
        PlayerState state = new PlayerState(player.getUniqueId(), "Allen", true, Instant.MAX);
        given(stateManager.getPlayerState(player.getUniqueId())).willReturn(state);

        // when
        listener.onPlayerCommand(event);

        // then
        assertThat(event.isCancelled(), equalTo(true));
        verify(messenger).sendMessage(player, Message.CHAT_WHILE_MUTED);
    }

    @Test
    public void commandShouldBeAllowedAndUnmute() {
        // given
        Player player = mock(Player.class);
        String message = "/msg Bob hey, who was that jerk who muted me?";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());
        PlayerState state = new PlayerState(player.getUniqueId(), "Allen", true, Instant.MIN);
        given(stateManager.getPlayerState(player.getUniqueId())).willReturn(state);

        // when
        listener.onPlayerCommand(event);

        // then
        assertThat(event.isCancelled(), equalTo(false));
        assertThat(state.isMuted(), equalTo(false));
        assertThat(state.getUntil(), equalTo(null));
        verifyZeroInteractions(messenger);
    }

    @Test
    public void commandPlayerNotMuted() {
        // given
        Player player = mock(Player.class);
        String message = "/msg Bob hey, that person who unmuted me was really nice!";

        Player extra = mock(Player.class);
        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);
        onlinePlayers.add(extra);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, message, onlinePlayers);

        given(player.getUniqueId()).willReturn(UUID.randomUUID());

        // when
        listener.onPlayerCommand(event);

        // then
        assertThat(event.isCancelled(), equalTo(false));
        verifyZeroInteractions(messenger);
    }

    @Test
    public void commandNotDisallowed() {
        // given
        Player player = mock(Player.class);
        String message = "/ping";

        Set<Player> onlinePlayers = new HashSet<>();
        onlinePlayers.add(player);

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, message, onlinePlayers);

        // when
        listener.onPlayerCommand(event);

        // then
        assertThat(event.isCancelled(), equalTo(false));
        verifyZeroInteractions(messenger);
    }
}
