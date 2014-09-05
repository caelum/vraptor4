package br.com.caelum.vraptor.observer.upload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadSizeLimit {

	long sizeLimit() default MultipartConfig.DEFAULT_SIZE_LIMIT;

	long fileSizeLimit() default MultipartConfig.DEFAULT_SIZE_LIMIT;
}