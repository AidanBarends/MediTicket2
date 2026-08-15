package za.ac.cput.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * All domain classes (User, Patient, Appointment, Payment, Name, etc.) use
 * the Builder pattern with no public setters and a protected/private
 * constructor. Jackson can't deserialize incoming JSON (@RequestBody) into
 * these classes by default, since it normally relies on setters or a
 * public no-arg constructor + setters.
 *
 * This tells Jackson to read/write private fields directly via reflection
 * instead, so @RequestBody deserialization works app-wide without adding
 * setters to every domain class or breaking the existing Builder pattern
 * (which is still used everywhere for constructing objects in code).
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonFieldVisibilityCustomizer() {
        return builder -> builder.visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
}