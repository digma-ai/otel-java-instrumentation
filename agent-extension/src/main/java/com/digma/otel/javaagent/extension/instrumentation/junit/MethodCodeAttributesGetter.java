package com.digma.otel.javaagent.extension.instrumentation.junit;

import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter;

import java.lang.reflect.Method;

enum MethodCodeAttributesGetter implements CodeAttributesGetter<Method> {
  INSTANCE;

  @Override
  public Class<?> getCodeClass(Method method) {
    return method.getDeclaringClass();
  }

  @Override
  public String getMethodName(Method method) {
    return method.getName();
  }
}
