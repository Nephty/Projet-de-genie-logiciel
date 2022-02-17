import front.Main;
import front.navigation.Flow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @AfterAll
    static void autoClear() {
        if (Flow.size() > 0) Flow.clear();
    }

    @Test
    @DisplayName("Flow add & pop Test")
    public void flowAddNPopTest() {
        Flow.add(Main.AuthScene);
        Flow.add(Main.SignInScene);
        assertEquals(2, Flow.size());
        assertEquals(Main.SignInScene, Flow.tail());
        Flow.pop();
        assertEquals(1, Flow.size());
        assertEquals(Main.AuthScene, Flow.tail());
        assertEquals(Main.AuthScene, Flow.pop());
        assertEquals(0, Flow.size());
    }

    @Test
    @DisplayName("Flow back & forward Test")
    public void flowBackNForwardTest() {
        Flow.add(Main.AuthScene);
        Flow.add(Main.SignInScene);
        assertEquals(Flow.back(), Main.AuthScene);
        assertEquals(Main.SignInScene, Flow.forward(Main.SignInScene));
        assertEquals(Main.SignInScene, Flow.tail());
    }

    @Test
    @DisplayName("Flow clear Test")
    public void flowClearTest() {
        Flow.add(Main.AuthScene);
        Flow.add(Main.SignInScene);
        assertEquals(2, Flow.size());
        Flow.clear();
        assertEquals(0, Flow.size());
        Flow.add(Main.SignInScene);
        Flow.add(Main.SignInScene);
        Flow.add(Main.SignInScene);
        assertEquals(3, Flow.size());
    }
}
