package powercraft_new.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author James
 * I genuinely have no idea how to make annotations
 * so, this is here for personal example
 */
@Documented // Means stuff needs javadoc
@Target(ElementType.METHOD) // What do you want it to be put over
@Inherited // Includes classes that the class parsed inherits
@Retention(value = RetentionPolicy.RUNTIME) // Leave as is
public @interface Test {
	@SuppressWarnings("javadoc")
	String valueOfAnote() default "There can be default values!";
	@SuppressWarnings("javadoc")
	int valueOfAnoteInt(); // NOT having a default makes it required
}
