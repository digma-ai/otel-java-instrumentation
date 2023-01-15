package com.digma.otel.instrumentation.spring.autoconfigure.webmvc;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * CodeAttributesToRequestMappingAspect.
 * Adds attributes of code.namespace and code.function to current span, while trapping the XxxMapping annotation.
 *
 * @see RequestMapping - trapped annotation
 * @see GetMapping     - trapped annotation
 * @see PostMapping    - trapped annotation
 * @see DeleteMapping  - trapped annotation
 * @see PutMapping     - trapped annotation
 * @see PatchMapping   - trapped annotation
 * @see SemanticAttributes#CODE_NAMESPACE
 * @see SemanticAttributes#CODE_FUNCTION
 */
@Aspect
class CodeAttributesToRequestMappingAspect {

    protected Object processCommonMapping(ProceedingJoinPoint pjp) throws Throwable {
        Span currentSpan = Span.current();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();

        Class<?> classOfMethod = method.getDeclaringClass();

        currentSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, classOfMethod.getName());
        currentSpan.setAttribute(SemanticAttributes.CODE_FUNCTION, method.getName());

        return pjp.proceed();
    }

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object processGetMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object processPostMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

    @Around("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object processDeleteMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object processPutMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object processPatchMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object processRequestMapping(ProceedingJoinPoint pjp) throws Throwable {
        return processCommonMapping(pjp);
    }

}
