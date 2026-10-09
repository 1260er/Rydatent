package de.pritwerk.rydatent;
import org.junit.Test;
import java.util.Set;
import static org.junit.Assert.*;
public class LogicTest {
 @Test public void multipleDevices(){assertTrue(Logic.shouldRun(Set.of("A","B"),Set.of("B","C")));assertFalse(Logic.shouldRun(Set.of("A"),Set.of("B")));}
 @Test public void noSmsOutsideExplicitConditions(){assertFalse(Logic.shouldReply("123","123",false,true));assertFalse(Logic.shouldReply("123","123",true,false));assertFalse(Logic.shouldReply("999","123",true,true));assertTrue(Logic.shouldReply("123","123",true,true));}
}
