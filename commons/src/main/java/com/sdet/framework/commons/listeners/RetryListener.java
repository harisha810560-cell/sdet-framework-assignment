package com.sdet.framework.commons.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/** Applies {@link RetryAnalyzer} to every test, so no suite has to opt in by hand. */
public class RetryListener implements IAnnotationTransformer {

  // Raw parameter types are required to override TestNG's own raw interface method.
  @Override
  @SuppressWarnings("rawtypes")
  public void transform(
      ITestAnnotation annotation, Class testClass, Constructor constructor, Method method) {
    Class<?> current = annotation.getRetryAnalyzerClass();
    // TestNG reports its own DisabledRetryAnalyzer when a test declares none.
    if (current == null || current.getSimpleName().equals("DisabledRetryAnalyzer")) {
      annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
  }
}
