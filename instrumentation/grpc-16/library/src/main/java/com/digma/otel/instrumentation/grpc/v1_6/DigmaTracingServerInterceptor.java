package com.digma.otel.instrumentation.grpc.v1_6;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public final class DigmaTracingServerInterceptor implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(DigmaTracingServerInterceptor.class.getName());

    private static final AttributeKey<String> CODE_FULL_METHOD_NAME = stringKey("code.full.method.name");
    private static final Class<UnparseableClass> UNKNOWN_CLASS = UnparseableClass.class;
    private static final String UNKNOWN_METHOD_NAME = "<UnparseableMethodName>";

    /**
     * ClassesWhichHoldTheActualImpl.
     * there are 2 private classes (named UnaryServerCallHandler and UnaryServerCallHandler)
     * under {@link io.grpc.stub.ServerCalls} which are expected to hold the GRPC MethodHandlers.
     */
    private static final List<String> ClassesWhichHoldTheActualImpl = List.of(
            "io.grpc.stub.ServerCalls$UnaryServerCallHandler",
            "io.grpc.stub.ServerCalls$StreamingServerCallHandler"
    );

    private final ConcurrentMap<String, ClassAndMethod> mapMethodFullName2ServiceImpl =
            new ConcurrentHashMap<>(32);

    @Override
    public <REQUEST, RESPONSE> ServerCall.Listener<REQUEST> interceptCall(
            ServerCall<REQUEST, RESPONSE> call,
            Metadata headers,
            ServerCallHandler<REQUEST, RESPONSE> next) {

        MethodDescriptor<REQUEST, RESPONSE> methodDescriptor = call.getMethodDescriptor();
        String fullMethodName = methodDescriptor.getFullMethodName();
        ClassAndMethod classAndMethod = mapMethodFullName2ServiceImpl.get(fullMethodName);
        if (classAndMethod == null) {
            Class<?> classOfServiceImpl = extractClassOfServiceImpl(next);
            String methodName = extractJavaMethodName(fullMethodName);
            classAndMethod = ClassAndMethod.create(classOfServiceImpl, methodName);
            mapMethodFullName2ServiceImpl.putIfAbsent(fullMethodName, classAndMethod);
        }

        Span currentSpan = Span.current();
        currentSpan.setAttribute(CODE_FULL_METHOD_NAME, fullMethodName);
        currentSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, classAndMethod.declaringClass().getName());
        currentSpan.setAttribute(SemanticAttributes.CODE_FUNCTION, classAndMethod.methodName());

        return next.startCall(call, headers);
    }

    /**
     * extractJavaMethodName. works similar to {@link MethodDescriptor#extractFullServiceName(String)}
     *
     * @param fullMethodName , value for example: helloworld.Greeter/SayHello
     * @return java method name (first letter is lower case). value for example: sayHello
     */
    @VisibleForTesting
    public static String extractJavaMethodName(String fullMethodName) {
        int indexOfSlash = fullMethodName.lastIndexOf('/');
        if (indexOfSlash < 0) {
            return UNKNOWN_METHOD_NAME; // think of throwing some exception
        }
        String methodPart = fullMethodName.substring(indexOfSlash + 1);
        char firstCharAsLower = Character.toLowerCase(methodPart.charAt(0));
        String javaMethodName;
        if (methodPart.length() > 1) {
            javaMethodName = firstCharAsLower + methodPart.substring(1);
        } else {
            javaMethodName = String.valueOf(firstCharAsLower);
        }
        return javaMethodName;
    }

    private static <REQUEST, RESPONSE> Class<?> extractClassOfServiceImpl(ServerCallHandler<REQUEST, RESPONSE> next) {
        Class<? extends ServerCallHandler> classOfNext = next.getClass();
        String classNameOfNext = classOfNext.getName();
        if (!ClassesWhichHoldTheActualImpl.contains(classNameOfNext)) {
            return UNKNOWN_CLASS;
        }

        Field fieldOfMethod = declaredField(classOfNext, "method", true);
        // get reference (usually MethodHandlers)
        Object potentialMethodHandlers = valueOfField(next, fieldOfMethod);
        Class<?> classOfMh = potentialMethodHandlers.getClass();
        String classNameOfMh = classOfMh.getName();

        if (!classNameOfMh.endsWith("MethodHandlers")) {
            return UNKNOWN_CLASS;
        }

        // taking the serviceImpl
        Field fieldOfServiceImpl = declaredField(classOfMh, "serviceImpl", true);
        Object serviceImplObject = valueOfField(potentialMethodHandlers, fieldOfServiceImpl);
        Class<?> classOfServiceImpl = serviceImplObject.getClass();
        return classOfServiceImpl;
    }

    private static Field declaredField(Class<?> clazz, String fieldName, boolean toSetAccessible) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            // declared exceptions
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
            // undeclared exceptions
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (toSetAccessible) {
            field.setAccessible(true);
        }
        return field;
    }

    private static Object valueOfField(Object objectContainingField, Field field) {
        Object theValue = null;
        try {
            theValue = field.get(objectContainingField);
            // declared exceptions
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
            // undeclared exceptions
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return theValue;
    }

    // used when could not parse the class
    static class UnparseableClass {
    }

}
