package com.digma.otel.javaagent.extension.instrumentation.junit;

import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesGetter;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

enum MethodCodeAttributesGetter implements CodeAttributesGetter<Method> {
  INSTANCE;

//  @Override
//  public Class<?> getCodeClass(Method method) {
//    return method.getDeclaringClass();
//  }
//
//  @Override
//  public String getMethodName(Method method) {
//    return method.getName();
//  }

  @Nullable
  @Override
  public Class<?> getCodeClass(Method method) {
    return method.getDeclaringClass();
  }

  @Nullable
  @Override
  public String getMethodName(Method method) {
    return method.getName();
  }
}
