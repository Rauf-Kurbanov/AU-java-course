package protocol;

public enum Command {
    DISCONNECT(-1),
    UNKNOWN(0),
    LIST(1),
    GET(2);

    public final int id;

    Command(int id) {
        this.id = id;
    }

    public static Command fromInt(int id) {
        for (Command i : Command.values()) {
            if (i.id == id) {
                return i;
            }
        }

        return UNKNOWN;
    }
}
