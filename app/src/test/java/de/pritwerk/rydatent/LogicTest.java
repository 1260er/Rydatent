package de.pritwerk.rydatent;

import org.junit.Test;
import java.util.Set;
import static org.junit.Assert.*;

public class LogicTest {
    @Test public void multipleDevices() {
        assertTrue(Logic.shouldRun(Set.of("A", "B"), Set.of("B", "C")));
        assertFalse(Logic.shouldRun(Set.of("A"), Set.of("B")));
    }

    @Test public void noSmsOutsideExplicitConditions() {
        assertFalse(Logic.shouldReply("123", "123", false, true));
        assertFalse(Logic.shouldReply("123", "123", true, false));
        assertFalse(Logic.shouldReply("999", "123", true, true));
        assertFalse(Logic.shouldReply(null, "123", true, true));
        assertTrue(Logic.shouldReply("123", "123", true, true));
    }

    @Test public void cooldownDoesNotSuppressRejection() {
        assertTrue(Logic.shouldReply("123", "123", true, true));
        assertFalse(Logic.smsCooldownElapsed(1_000_000L, 1_000_001L));
        assertTrue(Logic.shouldReply("123", "123", true, true));
        assertTrue(Logic.smsCooldownElapsed(1_000_000L, 1_600_000L));
        assertTrue(Logic.smsCooldownElapsed(0L, 1_000_001L));
    }

    @Test public void blitzerWatchRequiresEvidenceAndConnection() {
        assertTrue(Logic.mayRestartBlitzer(true, true, true, false, 60_000L));
        assertFalse(Logic.mayRestartBlitzer(false, true, true, false, 60_000L));
        assertFalse(Logic.mayRestartBlitzer(true, false, true, false, 60_000L));
        assertFalse(Logic.mayRestartBlitzer(true, true, false, false, 60_000L));
        assertFalse(Logic.mayRestartBlitzer(true, true, true, true, 60_000L));
        assertFalse(Logic.mayRestartBlitzer(true, true, true, false, 59_999L));
    }
}
