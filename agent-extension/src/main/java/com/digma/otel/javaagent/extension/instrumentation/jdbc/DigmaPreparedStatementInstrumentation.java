package com.digma.otel.javaagent.extension.instrumentation.jdbc;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.jdbc.internal.JdbcData;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.semconv.SemanticAttributes;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.ttddyy.dsproxy.listener.MethodExecutionListener;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import java.sql.PreparedStatement;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class DigmaPreparedStatementInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return implementsInterface(named("java.sql.PreparedStatement").
                and(not(implementsInterface(named("java.sql.CallableStatement")))));
    }

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("java.sql.PreparedStatement");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                nameStartsWith("execute").and(takesArguments(0)).and(isPublic()),
                DigmaPreparedStatementInstrumentation.class.getName() + "$DigmaPreparedStatementAdvice");
    }


    public static class DigmaPreparedStatementAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.This PreparedStatement statement,
                @Advice.Origin("#m") String methodName,
                @Advice.AllArguments() Object[] args
        ) {
            //keep the System.out for debugging during development
//            System.out.println("*******************************************");
//            System.out.println("DigmaPreparedStatementAdvice enter method " + methodName);

            //we support only the no args executeXXX methods of PreparedStatement
            if (args != null && args.length > 0) {
//                System.out.println("DigmaPreparedStatementAdvice more then 0 args, not our method ");
                return;
            }


            String stmt = JdbcData.preparedStatement.get(statement);
//            System.out.println("DigmaPreparedStatementAdvice stmt from JdbcData is " + stmt);
            // skip prepared statements without attached sql, probably a wrapper around the actual
            // prepared statement
            if (stmt == null) {
                return;
            }

            try {
                Span span = Java8BytecodeBridge.currentSpan();
                if (span != null) {
//                    System.out.println("span is not null " + span);
//                    System.out.println("DigmaPreparedStatementAdvice statement is  " + statement.getClass());

                    //try to unwrap without checking isWrapperFor because some implementations will not implement isWrapperFor correctly
                    // but may implement unwrap correctly.
                    //worst case we get an exception
                    ProxyJdbcObject proxyJdbcObject = null;
                    try {
                        proxyJdbcObject = statement.unwrap(ProxyJdbcObject.class);
                    } catch (Throwable e) {
//                        System.out.println("DigmaPreparedStatementAdvice unwrap failed " + e.getMessage());
                        //ignore
                    }

                    if (proxyJdbcObject != null) {

//                        System.out.println("DigmaPreparedStatementAdvice got " + ProxyJdbcObject.class.getName());
                        MethodExecutionListener digmaMethodListener = proxyJdbcObject.getProxyConfig().getMethodListener().getListeners().get(0);
//                        System.out.println("DigmaPreparedStatementAdvice digma method listener is " + digmaMethodListener.getClass().getName());
                        String queryWithParamsFromMethodListener = (String) digmaMethodListener.getClass().getDeclaredMethod("buildQueryWithParameters", String.class).invoke(digmaMethodListener, methodName);
//                        System.out.println("queryWithParamsFromMethodListener is " + queryWithParamsFromMethodListener);

                        String parameters = (String) digmaMethodListener.getClass().getDeclaredMethod("buildQueryParameters", String.class).invoke(digmaMethodListener, methodName);
//                        System.out.println("parameters from digmaMethodListener is " + parameters);

                        if (queryWithParamsFromMethodListener != null) {
                            span.setAttribute(SemanticAttributes.DB_STATEMENT, queryWithParamsFromMethodListener);
                        }

                        if (parameters != null) {
                            span.setAttribute("db.statement.parameters", parameters);
                        }

                        span.setAttribute("db.statement.method", methodName);

                    } else {
//                        System.out.println("DigmaPreparedStatementAdvice statement is not wrapper for " + ProxyJdbcObject.class);
                    }
                }
            } catch (Throwable e) {
//                e.printStackTrace();
            }


//            System.out.println("*******************************************");
        }

    }
}
