package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import com.digma.otel.javaagent.extension.instrumentation.matchers.ClassLoaderHasPackagesNamedMatcher;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.annotation.support.async.AsyncOperationEndSupport;
import io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Collections;

import static com.digma.otel.javaagent.extension.instrumentation.methods.MethodSingletons.instrumenter;
import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperMethod;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class MethodsInPackageTypeInstrumentation extends DigmaTypeInstrumentation {

    private final String packageName;

    public MethodsInPackageTypeInstrumentation(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return new ClassLoaderHasPackagesNamedMatcher(Collections.singletonList(packageName));
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return ElementMatchers.nameStartsWith(packageName + ".");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod().and(not(
                                isAnnotatedWith(namedOneOf(
                                        "org.springframework.web.bind.annotation.RequestMapping",
                                        "org.springframework.web.bind.annotation.GetMapping",
                                        "org.springframework.web.bind.annotation.PostMapping",
                                        "org.springframework.web.bind.annotation.DeleteMapping",
                                        "org.springframework.web.bind.annotation.PutMapping",
                                        "org.springframework.web.bind.annotation.PatchMapping"
                                ))).and(not(
                                        hasSuperMethod(
                                                isAnnotatedWith(namedOneOf(
                                                        "javax.ws.rs.Path",
                                                        "javax.ws.rs.DELETE",
                                                        "javax.ws.rs.GET",
                                                        "javax.ws.rs.HEAD",
                                                        "javax.ws.rs.OPTIONS",
                                                        "javax.ws.rs.PATCH",
                                                        "javax.ws.rs.POST",
                                                        "javax.ws.rs.PUT"
                                                )))
                                )).and(
                                        not(isAnnotatedWith(namedOneOf("io.opentelemetry.instrumentation.annotations.WithSpan"))))
                                .and(not(isGetter())).and(not(isSetter()))
                ),
                MethodsInPackageTypeInstrumentation.class.getName() + "$MethodAdvice");
    }


    public static class MethodAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.Origin("#t") Class<?> declaringClass,
                @Advice.Origin("#m") String methodName,
                @Advice.Local("otelMethod") ClassAndMethod classAndMethod,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {

            Context parentContext = currentContext();
            classAndMethod = ClassAndMethod.create(declaringClass, methodName);
            if (!instrumenter().shouldStart(parentContext, classAndMethod)) {
                return;
            }

            context = instrumenter().start(parentContext, classAndMethod);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void stopSpan(
                @Advice.Origin Method method,
                @Advice.Local("otelMethod") ClassAndMethod classAndMethod,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC, readOnly = false) Object returnValue,
                @Advice.Thrown Throwable throwable) {
            scope.close();

            returnValue =
                    AsyncOperationEndSupport.create(instrumenter(), Void.class, method.getReturnType())
                            .asyncEnd(context, classAndMethod, returnValue, throwable);
        }
    }


}
