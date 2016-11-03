package strange;

public class StrangeClass {

    public String toPrint() {
        return "someString";
    }

    public boolean tf(int i) {
        return i == 1;
    }

    public void throwStrange() throws StrangeException {
        throw new StrangeException();
    }

    public static void main(String[] args) {
        String[] methods = {"aaaa", "bbb", "c", "dffffffffffffs"};
        for (String s : methods) {
            System.out.printf("%15s\n", s);
        }
    }
}
