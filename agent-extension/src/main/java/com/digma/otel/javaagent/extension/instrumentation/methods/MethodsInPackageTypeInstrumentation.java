package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
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

import static com.digma.otel.javaagent.extension.instrumentation.methods.MethodSingletons.instrumenter;
import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;

public class MethodsInPackageTypeInstrumentation extends DigmaTypeInstrumentation {

    private final String packageName;

    public MethodsInPackageTypeInstrumentation(String packageName) {

        this.packageName = packageName;
    }

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        //todo: match match class loaders that contains the package
        // can copy ClassLoaderHasClassesNamedMatcher and change to check packages
        return super.classLoaderOptimization();
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return ElementMatchers.nameStartsWith(packageName);
    }

    //todo: don't instrument methods that are already instrumented by otel
    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
//                isMethod().and(not(isConstructor())),
                isMethod(),
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
