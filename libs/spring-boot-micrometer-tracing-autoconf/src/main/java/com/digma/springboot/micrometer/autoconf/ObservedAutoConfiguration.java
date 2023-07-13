package com.digma.springboot.micrometer.autoconf;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * ObservedAutoConfiguration.
 * provides the aspect for annotation io.micrometer.observation.annotation.Observed
 */
@AutoConfiguration(after = {ObservationAutoConfiguration.class})
@ConditionalOnClass(ObservedAspect.class)
public class ObservedAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

}
