import my_junit.TestAnnotationProcessor;
import tests.StrangeClassTest;

public class Runner {

    public static void main(String[] args) {
        TestAnnotationProcessor testAnotAnalyz = new TestAnnotationProcessor();
        try {
            testAnotAnalyz.parseAnnotation(new StrangeClassTest());
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }
}