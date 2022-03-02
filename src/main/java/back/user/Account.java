package back.user;

public class Account {
    public String name;
    private boolean status = true;  // Represents whether the account is toggled on or off

    public Account(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name : " + name + "           Status : " + (status ? "activated" : "deactivated");
    }

    /**
     * Return whether the account is toggled on or not.
     * @return Whether the account is toggled on or not
     */
    public boolean isActive() {
        // TODO : back-end : implement method to check if an account is toggled on or not
        return status;
    }

    /**
     * Toggles the account on.
     */
    public void toggleOn() {
        // TODO : back-end : toggle product on in database
        status = true;
    }

    /**
     * Toggles the account off.
     */
    public void toggleOff() {
        // TODO : back-end : toggle product off in database
        status = false;
    }

    /**
     * Returns the status of the account, that is whether it's toggled on (true) or off (false).
     * @return The status of the account
     */
    public boolean getStatus() {
        return status;
    }
}
