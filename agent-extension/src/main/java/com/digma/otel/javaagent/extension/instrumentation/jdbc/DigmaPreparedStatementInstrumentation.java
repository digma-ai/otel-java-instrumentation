package com.digma.otel.javaagent.extension.instrumentation.jdbc;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.jdbc.internal.JdbcData;
import io.opentelemetry.javaagent.bootstrap.CallDepth;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.semconv.SemanticAttributes;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.sql.PreparedStatement;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class DigmaPreparedStatementInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return implementsInterface(named("java.sql.PreparedStatement"));
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
                @Advice.Local("otelCallDepth") CallDepth callDepth
        ) {
            System.out.println("Digma DigmaPreparedStatementAdvice enter");

            String stmt = JdbcData.preparedStatement.get(statement);
            System.out.println("Digma DigmaPreparedStatementAdvice stmt from JdbcData is " + stmt);
            // skip prepared statements without attached sql, probably a wrapper around the actual
            // prepared statement
            if (stmt == null) {
                return;
            }
            Span span = Java8BytecodeBridge.currentSpan();
            if (span != null) {
                System.out.println("Digma DigmaPreparedStatementAdvice adding stmt to  " + span);
                span.setAttribute("db.statement.bound", stmt);
                span.setAttribute(SemanticAttributes.DB_STATEMENT, "MyBind-" + stmt);
            } else {
                System.out.println("Digma DigmaPreparedStatementAdvice span is null");
            }
        }
    }
}
