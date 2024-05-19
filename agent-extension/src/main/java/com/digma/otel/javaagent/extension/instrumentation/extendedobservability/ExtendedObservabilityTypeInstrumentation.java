package com.digma.otel.javaagent.extension.instrumentation.extendedobservability;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static com.digma.otel.javaagent.extension.instrumentation.extendedobservability.DigmaExtendedObservabilityInstrumentationModule.DIGMA_MARKER_ANNOTATION_NAME;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ExtendedObservabilityTypeInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed(DIGMA_MARKER_ANNOTATION_NAME);
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return ElementMatchers.declaresMethod(isAnnotatedWith(named(DIGMA_MARKER_ANNOTATION_NAME)));
    }


    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(isAnnotatedWith(named(DIGMA_MARKER_ANNOTATION_NAME)),
                ExtendedObservabilityTypeInstrumentation.class.getName() + "$MethodAdvice");
    }



    public static class MethodAdvice {

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void stopSpan(
                @Advice.Origin("#t") Class<?> declaringClass) {

            Span currentSpan = Java8BytecodeBridge.currentSpan();
            if (currentSpan != null) {
                String packageName = "";
                if (declaringClass != null){
                    packageName = declaringClass.getPackage().getName();
                }
                currentSpan.setAttribute("digma.instrumentation.extended.package",packageName);
                currentSpan.setAttribute("digma.instrumentation.extended.enabled","true");
            }
        }



    }


}
