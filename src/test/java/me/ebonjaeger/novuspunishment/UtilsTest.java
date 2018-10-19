package me.ebonjaeger.novuspunishment;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UtilsTest {

    @Test
    public void shouldMatchStrings() {
        // given
        String[] strings = {"123s", "567S", "2533m", "98M", "35h", "875H", "1277d", "485D"};

        // when/then
        for (String string : strings) {
            assertThat(String.format("String '%s' wasn't matched, but should have been.", string),
                Utils.matchesDurationPattern(string), equalTo(true)
            );
        }
    }

    @Test
    public void shouldNotMatchStrings() {
        // given
        String[] strings = {"1f23s", "567Sm", "2533k", "9%8M", "3E5h", "b0b"};

        // when/then
        for (String string : strings) {
            assertThat(String.format("String '%s' was matched, but should not have been.", string),
                Utils.matchesDurationPattern(string), equalTo(false)
            );
        }
    }

    @Test
    public void addsCorrectDuration() {
        // given
        Instant from = Instant.now();

        // when
        Instant actual = Utils.addDuration("42S", from);

        // then
        Instant expected = from.plus(42, ChronoUnit.SECONDS);
        assertThat(actual, equalTo(expected));

        // when 2
        actual = Utils.addDuration("42m", actual);

        // then 2
        expected = expected.plus(42, ChronoUnit.MINUTES);
        assertThat(actual, equalTo(expected));

        // when 3
        actual = Utils.addDuration("42h", actual);

        // then 3
        expected = expected.plus(42, ChronoUnit.HOURS);
        assertThat(actual, equalTo(expected));

        // when 4
        actual = Utils.addDuration("42D", actual);

        // then 4
        expected = expected.plus(42, ChronoUnit.DAYS);
        assertThat(actual, equalTo(expected));
    }
}
