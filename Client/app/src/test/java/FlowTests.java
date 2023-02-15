import front.navigation.Flow;
import front.scenes.Scenes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arnaud MOREAU
 */
public class FlowTests {
    @Test
    @DisplayName("Flow add & pop Test")
    public void flowAddNPopTest() {
        Flow.clear();
        assertEquals(0, Flow.size());
        Flow.add(Scenes.AuthScene);
        Flow.add(Scenes.SignInScene);
        assertEquals(2, Flow.size());
        assertEquals(Scenes.SignInScene, Flow.tail());
        Flow.pop();
        assertEquals(1, Flow.size());
        assertEquals(Scenes.AuthScene, Flow.tail());
        assertEquals(Scenes.AuthScene, Flow.pop());
        assertEquals(0, Flow.size());
        Flow.clear();
    }

    @Test
    @DisplayName("Flow back & forward Test")
    public void flowBackNForwardTest() {
        Flow.clear();
        Flow.add(Scenes.AuthScene);
        Flow.add(Scenes.SignInScene);
        assertEquals(Flow.back(), Scenes.AuthScene);
        assertEquals(Scenes.SignInScene, Flow.forward(Scenes.SignInScene));
        assertEquals(Scenes.SignInScene, Flow.tail());
        Flow.clear();
    }

    @Test
    @DisplayName("Flow clear Test")
    public void flowClearTest() {
        Flow.clear();
        Flow.add(Scenes.AuthScene);
        Flow.add(Scenes.SignInScene);
        assertEquals(2, Flow.size());
        Flow.clear();
        assertEquals(0, Flow.size());
        Flow.add(Scenes.SignInScene);
        Flow.add(Scenes.SignInScene);
        Flow.add(Scenes.SignInScene);
        assertEquals(3, Flow.size());
        Flow.clear();
    }
}
