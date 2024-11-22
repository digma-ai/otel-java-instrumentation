package com.digma.otel.instrumentation.grpc.v1_6;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.api.incubator.semconv.util.ClassAndMethod;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

public final class DigmaTracingServerInterceptor implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(DigmaTracingServerInterceptor.class.getName());

    private static final String UNKNOWN_METHOD_NAME = "<UnparseableMethodName>";

    /**
     * grpc call handlers that host the actual MethodHandlers.
     * there are 2 classes (named UnaryServerCallHandler and UnaryServerCallHandler)
     * under {@code io.grpc.stub.ServerCalls} which are expected to hold the GRPC MethodHandlers.
     */
    private static final List<String> actualCallHandlersNames = asList(
            "io.grpc.stub.ServerCalls$UnaryServerCallHandler",
            "io.grpc.stub.ServerCalls$StreamingServerCallHandler"
    );


    private final Map<String, ClassAndMethod> classAndMethodCache =
            Collections.synchronizedMap(new LinkedHashMap<String, ClassAndMethod>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ClassAndMethod> eldest) {
                    return size() > 1000;
                }
            });


    private DigmaTracingServerInterceptor() {
        super();
    }

    public static DigmaTracingServerInterceptor create() {
        return new DigmaTracingServerInterceptor();
    }

    @Override
    public <REQUEST, RESPONSE> ServerCall.Listener<REQUEST> interceptCall(
            ServerCall<REQUEST, RESPONSE> call,
            Metadata headers,
            ServerCallHandler<REQUEST, RESPONSE> next
    ) {

        //todo: the interceptor is added twice in the advice and will be invoked twice for each call,
        // otel have a solution for that with CallDepth but we can't use the same CallDepth because we will
        // interfere with otel advice.
        // its solvable with ThreadLocal here to only execute the code once.

        MethodDescriptor<REQUEST, RESPONSE> methodDescriptor = call.getMethodDescriptor();
        String fullMethodName = methodDescriptor.getFullMethodName();
        ClassAndMethod classAndMethod = classAndMethodCache.get(fullMethodName);
        if (classAndMethod == null) {
            Class<?> classOfServiceImpl = extractServiceClassName(next);
            String methodName = extractJavaMethodName(fullMethodName);
            classAndMethod = ClassAndMethod.create(classOfServiceImpl, methodName);
            classAndMethodCache.putIfAbsent(fullMethodName, classAndMethod);
        }

        Span currentSpan = Java8BytecodeBridge.currentSpan();

        currentSpan.setAttribute("code.namespace", classAndMethod.declaringClass().getName());
        currentSpan.setAttribute("code.function", classAndMethod.methodName());

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



    private <REQUEST, RESPONSE> Class<?> extractServiceClassName(ServerCallHandler<REQUEST, RESPONSE> next) {

        logger.fine("extracting service class name, call handler is " + next.getClass().getName());
        ServerCallHandler<REQUEST, RESPONSE> serverCallHandler = tryFindActualCallHandler(next);
        String callHandlerClassName = serverCallHandler.getClass().getName();
        if (!actualCallHandlersNames.contains(callHandlerClassName)) {
            logger.warning(String.format("Cannot parse service class, call handler class '%s' is not supported.", callHandlerClassName));
            return DigmaUnparseableClassSinceCallHandlerNotSupported.class;
        }

        logger.fine("found actual call handler " + serverCallHandler.getClass().getName());

        Object methodHandler = getFieldValue(serverCallHandler,"method");
        if (methodHandler == null){
            logger.warning(String.format("Cannot parse service class, can not find method handler in '%s'", serverCallHandler.getClass()));
            return DigmaUnparseableClassSinceNoMethodHandler.class;
        }

        Object serviceObject = getFieldValue(methodHandler,"serviceImpl");
        if (serviceObject == null){
            logger.warning(String.format("Cannot parse service object from '%s'", methodHandler.getClass()));
            return DigmaUnparseableClassSinceNoneStandardMethodHandlers.class;
        }

        return serviceObject.getClass();
    }



    @Nonnull
    private <REQUEST, RESPONSE> ServerCallHandler<REQUEST, RESPONSE> tryFindActualCallHandler(ServerCallHandler<REQUEST, RESPONSE> next) {

        //InterceptCallHandler is a handler that calls an interceptor.
        //try to skip all InterceptCallHandlers. InterceptCallHandler has a field named 'callHandler' that points to the
        // next callHandler which may be another InterceptCallHandler or an actual call handler

        try {
            String className = next.getClass().getName();
            while (className.equals("io.grpc.ServerInterceptors$InterceptCallHandler")) {

                @SuppressWarnings("unchecked")
                ServerCallHandler<REQUEST, RESPONSE> serverCallHandler = (ServerCallHandler<REQUEST, RESPONSE>) getFieldValue(next,"callHandler");
                if (serverCallHandler == null){
                    return next;
                }

                next = serverCallHandler;
                className = next.getClass().getName();
            }
        } catch (Throwable e) {
            logger.warning("Failed to find actual call handler for class: " + next.getClass().getName() + "," + e.getMessage());
        }

        return next;
    }





    @Nullable
    private Object getFieldValue(Object object, String fieldName) {
        try {

            Field field = getDeclaredField(object.getClass(),fieldName);
            if (field == null){
                return null;
            }

            return field.get(object);

        }catch (Throwable e) {
            logger.warning("Cannot find field value" + fieldName + " in class " + object.getClass().getName() + "," + e.getMessage());
            return null;
        }
    }




    @Nullable
    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Throwable e) {
            logger.warning("Cannot find field " + fieldName + " in class " + clazz.getName());
            return null;
        }
    }




    static class DigmaUnparseableClassSinceNoneStandardMethodHandlers {
    }

    static class DigmaUnparseableClassSinceCallHandlerNotSupported {
    }

    static class DigmaUnparseableClassSinceNoMethodHandler {
    }

}
