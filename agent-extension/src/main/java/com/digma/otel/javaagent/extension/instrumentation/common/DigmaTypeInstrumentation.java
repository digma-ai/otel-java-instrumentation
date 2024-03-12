package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.hasClassFileVersionAtLeast;

public abstract class DigmaTypeInstrumentation implements TypeInstrumentation {

    @Override
    public final ElementMatcher<TypeDescription> typeMatcher() {
        return hasClassFileVersionAtLeast(ClassFileVersion.JAVA_V8).and(digmaTypeMatcher());
    }

    public abstract ElementMatcher<TypeDescription> digmaTypeMatcher();

}
