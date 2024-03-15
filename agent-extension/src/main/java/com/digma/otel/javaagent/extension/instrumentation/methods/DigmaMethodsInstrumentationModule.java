package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.digma.otel.instrumentation.common.CommonUtils.getEnvOrSystemProperty;
import static net.bytebuddy.matcher.ElementMatchers.isBootstrapClassLoader;
import static net.bytebuddy.matcher.ElementMatchers.not;


@AutoService(InstrumentationModule.class)
public class DigmaMethodsInstrumentationModule extends InstrumentationModule {

    private static final String TRACE_PACKAGES_CONFIG = "digma.otel.instrumentation.packages.include";

    private final List<String> packageNames;

    public DigmaMethodsInstrumentationModule() {
        super("digma-methods");
        String pNames = getEnvOrSystemProperty(TRACE_PACKAGES_CONFIG);
        if (pNames != null){
            packageNames = Arrays.asList(pNames.split(";"));
        }else{
            packageNames = Collections.emptyList();
        }
    }

    @Override
    public int order() {
        return 121;
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        //todo: match match class loaders that contains the packages
        return not(isBootstrapClassLoader());
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return packageNames.stream().map(MethodsInPackageTypeInstrumentation::new).collect(Collectors.toList());
    }

    @Override
    public boolean isHelperClass(String className) {
        return className.startsWith("com.digma.otel.instrumentation.common") ;
    }


    @Override
    public List<String> getAdditionalHelperClassNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("com.digma.otel.javaagent.extension.instrumentation.methods.MethodSingletons");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesExtractor");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.util.AutoValue_ClassAndMethod");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethodAttributesGetter");
        list.add("io.opentelemetry.instrumentation.api.instrumenter.code.CodeSpanNameExtractor");
        list.add("io.opentelemetry.semconv.trace.attributes.SemanticAttributes");
        return list;
    }

}
