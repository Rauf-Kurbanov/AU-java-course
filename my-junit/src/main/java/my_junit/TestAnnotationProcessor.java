package my_junit;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class TestAnnotationProcessor {

    @Data
    private static class TestsSummary {
        private final int passedCount;
        private final int failedCount;
        private final int ignoredCount;
    }

    public TestsSummary parseAnnotation(Object testObject) throws  IllegalAccessException {
        int passedCount = 0;
        int ignoredCount = 0;

        Class<?> testClass = testObject.getClass();
        log.debug("tests class is {}", testClass);

        Method[] methods = testClass.getDeclaredMethods();
        for (Method method : methods) {
            log.debug("testing {}", method);
            if (method.isAnnotationPresent(Test.class)) {
                Test test = method.getAnnotation(Test.class);
                log.debug("current test : {}", test);

                if (!test.ignore().equals("[unspecified]")) {
                    System.out.printf("Test ignored because %s\n", test.ignore());
                    ignoredCount++;
                    continue;
                }

                Class<? extends Throwable> expectedThrowable = test.expected();
                if (test.expected() == Test.None.class) {
                    try {
                        method.invoke(testObject);
                        passedCount++;
                        log.debug("test of {} PASSED", method);
                    } catch (InvocationTargetException assertionFailed) {
                    }
                    continue;
                }
                try {
                    method.invoke(testObject);
                } catch (InvocationTargetException e) {
                    log.debug("catched class : {}", e.getClass());
                    log.debug("cause class : {}", e.getCause().getClass());
                    log.debug("expected class : {}", expectedThrowable);

                    if (expectedThrowable.isInstance(e.getCause())) {
                        passedCount++;
                        log.debug("test of {} PASSED", method);
                        continue;
                    }
                }
                log.debug("test of {} FAILED", method);
            }
        }
        System.out.printf("passed: %d\n", passedCount);
        System.out.printf("failed: %d\n", methods.length - passedCount - ignoredCount);
        System.out.printf("ignored: %d\n", ignoredCount);

        return new TestsSummary(passedCount,
                methods.length - passedCount - ignoredCount,
                ignoredCount);
    }

}