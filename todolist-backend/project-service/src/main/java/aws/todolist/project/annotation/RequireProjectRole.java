package aws.todolist.project.annotation;

import aws.todolist.project.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)   // Áp dụng cho method
@Retention(RetentionPolicy.RUNTIME)  // Có thể đọc runtime
public @interface RequireProjectRole {
    Role[] value() default {};
}
