package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import com.digma.otel.javaagent.extension.instrumentation.matchers.ClassLoaderHasPackagesNamedMatcher;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.annotation.support.async.AsyncOperationEndSupport;
import io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;

import static com.digma.otel.javaagent.extension.instrumentation.methods.MethodSingletons.instrumenter;
import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperType;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.*;
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
        return ElementMatchers.nameStartsWith(packageName + ".")
                .and(not(typeFilterByAnnotation()))
                .and(not(isSynthetic()))
                .and(not(isEnum()))
                .and(not(extendsClass(named("io.grpc.ServerBuilder"))))
                .and(not(jaxrsTypes()))
                .and(not(kafkaTypes()))
                .and(not(implementsInterface(named("org.apache.camel.CamelContext"))))
                .and(not(hibernate6Types()))
                .and(not(hibernate4Types()))
                .and(not(nameContains("$")));
    }



    private ElementMatcher<? super TypeDescription> hibernate6Types(){
        return implementsInterface(namedOneOf(
                "org.hibernate.query.CommonQueryContract",
                "org.hibernate.SessionFactory",
                "org.hibernate.SessionBuilder",
                "org.hibernate.SharedSessionContract",
                "org.hibernate.Transaction"));
    }

    private ElementMatcher<? super TypeDescription> hibernate4Types(){
        return implementsInterface(namedOneOf(
                "org.hibernate.Criteria",
                "org.hibernate.Query",
                "org.hibernate.SessionFactory",
                "org.hibernate.SessionBuilder",
                "org.hibernate.SharedSessionContract",
                "org.hibernate.Transaction"));
    }

    private ElementMatcher<? super TypeDescription> jaxrsTypes(){
        return hasSuperType(
                isAnnotatedWith(named("javax.ws.rs.Path"))
                        .or(declaresMethod(isAnnotatedWith(named("javax.ws.rs.Path")))));
    }

    private ElementMatcher<? super TypeDescription> kafkaTypes(){
        return declaresMethod(isAnnotatedWith(named("org.springframework.kafka.annotation.KafkaListener")))
                .or(
                        extendsClass(
                                declaresMethod(isAnnotatedWith(named("org.springframework.kafka.annotation.KafkaListener")))));
    }


    private ElementMatcher<? super TypeDescription> typeFilterByAnnotation() {
        return isAnnotatedWith(namedOneOf(
                "org.junit.jupiter.api.Disabled",
                "org.junit.Ignore")
        );
    }


    @Override
    public void transform(TypeTransformer transformer) {}
    public void transform_(TypeTransformer transformer) {

        System.out.println("MethodsInPackageTypeInstrumentation: in transform ");

        ElementMatcher<? super MethodDescription> methodMatcher = isMethod()

                .and(not(namedIgnoreCase("get")))
                .and(not(methodsFilterByAnnotation()))
                .and(not(isSynthetic()))
                .and(not(isBridge()))
                .and(not(isMain()))
                .and(not(isFinalizer()))
                .and(not(isHashCode()))
                .and(not(isEquals()))
                .and(not(isClone()))
                .and(not(isToString()))
                .and(not(isTypeInitializer()))
                .and(not(isSetter()))
                .and(not(isGetter()))
                .and(not(isNative()))
                .and(not(nameContains("$")));

        transformer.applyTransformer(new AgentBuilder.Transformer() {

            @Nonnull
            @Override
            public DynamicType.Builder<?> transform(@Nonnull DynamicType.Builder<?> builder, @Nonnull TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                return builder.method(methodMatcher);
                try {

                    System.out.println("MethodsInPackageTypeInstrumentation: in bytebuddy transform ,typeDescription:" + typeDescription+",class loader:"+classLoader);

                    String withSpan = "io_opentelemetry_instrumentation_annotations_WithSpan".replaceAll("_",".");
//                    String withSpan = "application_io_opentelemetry_instrumentation_annotations_WithSpan".replaceAll("_",".");
                    AnnotationDescription annotationDescription = AnnotationDescription.Latent.Builder.ofType((Class<? extends Annotation>) Class.forName(withSpan,false,classLoader)).build();

//                    return new AgentBuilder.Default().type(ElementMatchers.ofType(typeDescription.getComponentType()))

                    return new ByteBuddy()
                            .redefine(typeDescription, ClassFileLocator.ForClassLoader.of(classLoader))
//                            .name(typeDescription.getCanonicalName() + "$renamed")
                            .visit(new MemberAttributeExtension.ForMethod().annotateMethod(annotationDescription)
                                    .on(methodMatcher));
//                    new ByteBuddy()
//                            .redefine(typeDescription, ClassFileLocator.ForClassLoader.of(classLoader))
////                            .name(typeDescription.getCanonicalName() + "$renamed")
//                            .visit(new MemberAttributeExtension.ForMethod().annotateMethod(annotationDescription)
//                                    .on(methodMatcher)).make().load(classLoader, ClassLoadingStrategy.Default.CHILD_FIRST).getLoaded();

//                    return builder;
//                    return builder
//                            .method(methodMatcher)
//                            .intercept(SuperMethodCall.INSTANCE)
//                            .annotateMethod(annotationDescription);
//                            .visit(new MemberAttributeExtension.ForMethod()
//                                    .annotateMethod(annotationDescription)
//                                            .on(methodMatcher));
                } catch (Throwable e) {
                    System.out.println("MethodsInPackageTypeInstrumentation: got exception "+e);
                    e.printStackTrace();
                    return builder;
                }
            }
        });

    }


    private ElementMatcher<? super MethodDescription> methodsFilterByAnnotation() {

        return isAnnotatedWith(namedOneOf(
                "org.springframework.web.bind.annotation.RequestMapping",
                "org.springframework.web.bind.annotation.GetMapping",
                "org.springframework.web.bind.annotation.PostMapping",
                "org.springframework.web.bind.annotation.DeleteMapping",
                "org.springframework.web.bind.annotation.PutMapping",
                "org.springframework.web.bind.annotation.PatchMapping"
        )).or(
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
        ).or(
                //for some reason otel in WithSpanInstrumentation checks for application.io.opentelemetry.instrumentation.annotations.WithSpan
                isAnnotatedWith(namedOneOf("io.opentelemetry.instrumentation.annotations.WithSpan",
                        "application.io.opentelemetry.instrumentation.annotations.WithSpan"))
        ).or(
                isAnnotatedWith(namedOneOf(
                        "org.junit.jupiter.api.Test",
                        "org.junit.jupiter.api.Disabled",
                        "org.junit.jupiter.api.BeforeEach",
                        "org.junit.jupiter.api.BeforeEach",
                        "org.junit.jupiter.api.BeforeAll",
                        "org.junit.jupiter.api.AfterAll",
                        "org.junit.jupiter.api.RepeatedTest",
                        "org.junit.jupiter.params.ParameterizedTest",
                        "org.junit.jupiter.api.TestFactory",
                        "org.junit.jupiter.api.TestTemplate",
                        "kotlin.test.Test",
                        "kotlin.test.BeforeEach",
                        "kotlin.test.BeforeEach",
                        "kotlin.test.Ignore",
                        "org.junit.Test",
                        "org.junit.Ignore",
                        "org.junit.Rule",
                        "org.junit.ClassRule",
                        "org.junit.BeforeClass",
                        "org.junit.Before",
                        "org.junit.AfterClass",
                        "org.junit.After",
                        "org.junit.runners.Parameterized.AfterParam",
                        "org.junit.runners.Parameterized.BeforeParam",
                        "org.junit.runners.Parameterized.Parameters"))
        ).or(
                isAnnotatedWith(namedOneOf(
                        "io.micrometer.tracing.annotation.NewSpan",
                        "io.micrometer.tracing.annotation.ContinueSpan",
                        "io.micrometer.observation.annotation.Observed"))
        );

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
