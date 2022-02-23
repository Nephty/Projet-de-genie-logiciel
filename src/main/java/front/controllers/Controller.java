package front.controllers;

import javafx.scene.Node;

/**
 * This mother Controller class gives access to a few useful methods to all children classes.
 * Having access to these methods provide shortcuts. For example :
 * The method isVisibleUsingOpacity returns whether the node's opacity is not 0, instead of using isVisible() (which
 * is totally different) and spares time, since you don't have to type "node.getOpacity() != 0". It also makes
 * the code more readable.
 */
public class Controller {
    /**
     * Returns the visibility of a <code>Node</code> using its opacity. If the node's opacity is 0, then it is
     * considered invisible. If the node's opacity is not 0, it is considered visible. This method provides an
     * alternative to the isVisible() method which returns whether the attribute "visible" is true or false.
     * This is mainly used with transitions, since these use the opacity of the node, and not it's core visibility.
     * It is not recommended using this method if you don't alter the node's opacity is such a way that it
     * purposely reaches 0 or 1 (for example, using fade in/fade out transitions). If you alter the node's visibility
     * to make it, let's say, 0.4, using the <code>visible</code> attribute of the node is probably a better way to go,
     * because you'd probably want to know if it is visible at its core, not if the opacity is other than 0.
     * @param node - <code>Node</code> - The node to check
     * @return Whether the node's opacity is not 0 or not : that is, true if the node's opacity is not 0,
     * false otherwise.
     */
    public static boolean isVisibleUsingOpacity(Node node) {
        return node.getOpacity() != 0;
    }
}
