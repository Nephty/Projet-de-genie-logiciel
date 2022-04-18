package front.navigation;

import javafx.scene.Scene;

import java.util.LinkedList;

/**
 * Describes the scene flow at runtime. Uses a <code>LinkedList</code> that adds a new <code>Scene</code> object
 * to describe the navigation between one scene to another further one. Removes the last element to describe
 * a back button clicked. The objects in the <code>LinkedList</code> show the scene switching flow.
 * How to use :
 * - To go forward to another scene : yourScene.setScene(Flow.forward(yourNewScene));
 * - To go back to the previous scene : yourScene.setScene(Flow.back());
 * @author Arnaud MOREAU
 */
public class Flow {
    private static LinkedList<Scene> FLOW = new LinkedList<>();

    /**
     * Add a new element to the Flow.
     *
     * @param scene The next Scene the user navigates to
     */
    public static void add(Scene scene) {
        FLOW.add(scene);
    }

    /**
     * Removes the last element of the Flow and returns it.
     *
     * @return <code>Scene</code> - The last element that was removed from the Flow
     */
    public static Scene pop() {
        Scene last = FLOW.getLast();
        FLOW.remove(FLOW.size() - 1);
        return last;
    }

    /**
     * Returns the last element of the Flow.
     *
     * @return <code>Scene</code> - The last element of the Flow.
     */
    public static Scene tail() {
        return FLOW.getLast();
    }

    /**
     * Returns the size of the Flow (how many Scenes there are in the current execution flow).
     *
     * @return <code>int</code> - The size of the Flow
     */
    public static int size() {
        return FLOW.size();
    }

    /**
     * Removes the last element of the Flow and returns the new last element.
     *
     * @return <code>Scene</code> - The new last element
     */
    public static Scene back() {
        FLOW.remove(Flow.size() - 1);
        return Flow.tail();
    }

    /**
     * Adds a new element to the Flow and returns it. Used when the user goes forward in the execution flow (that is,
     * when he clicks on a button that brings him "forward" - on a further scene).
     *
     * @param scene The next <code>Scene</code> the user switches to
     * @return The new last element (the one that was added)
     */
    public static Scene forward(Scene scene) {
        Flow.add(scene);
        return Flow.tail();
    }

    /**
     * Clears the Flow by removing every element there is.
     */
    public static void clear() {
        Flow.FLOW = new LinkedList<>();
    }

    /**
     * Returns the content of the flow as a string for display purposes.
     *
     * @return The content of the flow as a string
     */
    public static String getContentAsString() {
        StringBuilder res = new StringBuilder();
        for (Scene s : FLOW) {
            if (FLOW.indexOf(s) != FLOW.size() - 1) res.append(s).append(" -> ");
            else res.append(s);
        }
        return res.toString();
    }

    /**
     * Replaces the before last element of the flow and ensures the integrity of the last element.
     *
     * @param replaceWith The scene that will replace the before last scene in the flow
     * @return The replaced scene
     */
    public static Scene replaceBeforeLastElement(Scene replaceWith) {
        // Save last scene
        Scene lastScene = Flow.tail();
        // Removed the last element
        Flow.pop();
        // Get the replaced scene (returned value) : the new last element after the pop (which is the before last if we
        // consider the original linked list)
        Scene replacedScene = Flow.pop();
        // Add the replacement scene
        FLOW.add(replaceWith);
        // Restore the last element
        FLOW.add(lastScene);
        return replacedScene;
    }

    /**
     * Replaces the last element of the flow.
     *
     * @param replaceWith The scene that will replace the last scene in the flow
     * @return The replaced scene
     */
    public static Scene replaceLastElement(Scene replaceWith) {
        Scene replacedScene = pop();
        Flow.forward(replaceWith);
        return replacedScene;
    }
}
