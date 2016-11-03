package my_junit;

public class MyAsserts {

    private static boolean equalsRegardingNull(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }
        return expected.equals(actual);
    }

    private static String format(Object expected, Object actual) {
        return String.format("expected: %s\nbut got:  %s", expected, actual);
    }

    private static void failNotEquals(Object expected,
                                      Object actual) {
        System.out.println(format(expected, actual));
        throw new AssertionError();
    }

    public static void assertEquals(Object expected,
                                    Object actual) {
        if (equalsRegardingNull(expected, actual)) {
            return;
        }
        failNotEquals(expected, actual);
    }

//    public static void assertEquals(String str1, String str2) {
//        if (!str1.equals(str2))
//            throw new AssertionError();
//    }
}