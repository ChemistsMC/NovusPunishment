package me.ebonjaeger.novuspunishment.command;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static me.ebonjaeger.novuspunishment.TestUtils.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link UnbanCommand} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnbanCommandTest {

	@InjectMocks
	private UnbanCommand unbanCommand;

	@Mock
	private Messenger messenger;

	private ServerMock server;

	@Before
	public void setup() {
		server = MockBukkit.mock();
		NovusPunishment plugin = mock(NovusPunishment.class);
		setField(Bukkit.class, "server", null, server);
	}

	@After
	public void destroy() {
		MockBukkit.unload();
	}

	@Test
	public void unbanPlayer() {
		// given
		CommandSender sender = mock(Player.class);
		String target = "Allen";
		server.getBanList(BanList.Type.NAME).addBan(target, "test", null, "Tester");

		// when
		unbanCommand.onCommand(sender, target);

		// then
		assertThat(server.getBanList(BanList.Type.NAME).isBanned(target), equalTo(false));
		verify(messenger).broadcastMessage(Message.PLAYER_UNBANNED, "newpunish.notify.unban", "Allen");
	}

	@Test
	public void playerNotBanned() {
		// given
		CommandSender sender = mock(Player.class);
		String target = "Allen";

		// when
		unbanCommand.onCommand(sender, target);

		// then
		verify(messenger).sendMessage(sender, Message.PLAYER_NOT_BANNED, "Allen");
	}
}
