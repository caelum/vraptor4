/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor4.core;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.vraptor4.Controller;
import br.com.caelum.vraptor4.Convert;
import br.com.caelum.vraptor4.Converter;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.Validator;
import br.com.caelum.vraptor4.config.ApplicationConfiguration;
import br.com.caelum.vraptor4.config.Configuration;
import br.com.caelum.vraptor4.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor4.controller.DefaultControllerNotFoundHandler;
import br.com.caelum.vraptor4.controller.DefaultMethodNotAllowedHandler;
import br.com.caelum.vraptor4.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor4.converter.BigDecimalConverter;
import br.com.caelum.vraptor4.converter.BigIntegerConverter;
import br.com.caelum.vraptor4.converter.BooleanConverter;
import br.com.caelum.vraptor4.converter.ByteConverter;
import br.com.caelum.vraptor4.converter.CharacterConverter;
import br.com.caelum.vraptor4.converter.DoubleConverter;
import br.com.caelum.vraptor4.converter.EnumConverter;
import br.com.caelum.vraptor4.converter.FloatConverter;
import br.com.caelum.vraptor4.converter.IntegerConverter;
import br.com.caelum.vraptor4.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor4.converter.LocaleBasedDateConverter;
import br.com.caelum.vraptor4.converter.LongConverter;
import br.com.caelum.vraptor4.converter.PrimitiveBooleanConverter;
import br.com.caelum.vraptor4.converter.PrimitiveByteConverter;
import br.com.caelum.vraptor4.converter.PrimitiveCharConverter;
import br.com.caelum.vraptor4.converter.PrimitiveDoubleConverter;
import br.com.caelum.vraptor4.converter.PrimitiveFloatConverter;
import br.com.caelum.vraptor4.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor4.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor4.converter.PrimitiveShortConverter;
import br.com.caelum.vraptor4.converter.ShortConverter;
import br.com.caelum.vraptor4.converter.StringConverter;
import br.com.caelum.vraptor4.converter.jodatime.DateMidnightConverter;
import br.com.caelum.vraptor4.converter.jodatime.DateTimeConverter;
import br.com.caelum.vraptor4.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor4.converter.jodatime.LocalDateTimeConverter;
import br.com.caelum.vraptor4.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor4.deserialization.DefaultDeserializers;
import br.com.caelum.vraptor4.deserialization.Deserializer;
import br.com.caelum.vraptor4.deserialization.Deserializers;
import br.com.caelum.vraptor4.deserialization.Deserializes;
import br.com.caelum.vraptor4.deserialization.DeserializesHandler;
import br.com.caelum.vraptor4.deserialization.FormDeserializer;
import br.com.caelum.vraptor4.deserialization.JsonDeserializer;
import br.com.caelum.vraptor4.deserialization.XMLDeserializer;
import br.com.caelum.vraptor4.deserialization.XStreamXMLDeserializer;
import br.com.caelum.vraptor4.http.DefaultControllerTranslator;
import br.com.caelum.vraptor4.http.DefaultFormatResolver;
import br.com.caelum.vraptor4.http.EncodingHandlerFactory;
import br.com.caelum.vraptor4.http.FormatResolver;
import br.com.caelum.vraptor4.http.ParameterNameProvider;
import br.com.caelum.vraptor4.http.ParametersProvider;
import br.com.caelum.vraptor4.http.ParanamerNameProvider;
import br.com.caelum.vraptor4.http.UrlToControllerTranslator;
import br.com.caelum.vraptor4.http.iogi.InstantiatorWithErrors;
import br.com.caelum.vraptor4.http.iogi.IogiParametersProvider;
import br.com.caelum.vraptor4.http.iogi.VRaptorDependencyProvider;
import br.com.caelum.vraptor4.http.iogi.VRaptorInstantiator;
import br.com.caelum.vraptor4.http.iogi.VRaptorParameterNamesProvider;
import br.com.caelum.vraptor4.http.ognl.EmptyElementsRemoval;
import br.com.caelum.vraptor4.http.ognl.OgnlFacade;
import br.com.caelum.vraptor4.http.ognl.OgnlParametersProvider;
import br.com.caelum.vraptor4.http.route.DefaultRouter;
import br.com.caelum.vraptor4.http.route.DefaultTypeFinder;
import br.com.caelum.vraptor4.http.route.Evaluator;
import br.com.caelum.vraptor4.http.route.JavaEvaluator;
import br.com.caelum.vraptor4.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor4.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor4.http.route.Router;
import br.com.caelum.vraptor4.http.route.RoutesConfiguration;
import br.com.caelum.vraptor4.http.route.RoutesParser;
import br.com.caelum.vraptor4.http.route.TypeFinder;
import br.com.caelum.vraptor4.interceptor.ControllerLookupInterceptor;
import br.com.caelum.vraptor4.interceptor.DefaultSimpleInterceptorStack;
import br.com.caelum.vraptor4.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor4.interceptor.DeserializingInterceptor;
import br.com.caelum.vraptor4.interceptor.ExceptionHandlerInterceptor;
import br.com.caelum.vraptor4.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor4.interceptor.FlashInterceptor;
import br.com.caelum.vraptor4.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor4.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor4.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor4.interceptor.OutjectResult;
import br.com.caelum.vraptor4.interceptor.ParameterIncluderInterceptor;
import br.com.caelum.vraptor4.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor4.interceptor.TopologicalSortedInterceptorRegistry;
import br.com.caelum.vraptor4.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor4.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor4.interceptor.multipart.CommonsUploadMultipartInterceptor;
import br.com.caelum.vraptor4.interceptor.multipart.DefaultMultipartConfig;
import br.com.caelum.vraptor4.interceptor.multipart.DefaultServletFileUploadCreator;
import br.com.caelum.vraptor4.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor4.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor4.interceptor.multipart.NullMultipartInterceptor;
import br.com.caelum.vraptor4.interceptor.multipart.Servlet3MultipartInterceptor;
import br.com.caelum.vraptor4.interceptor.multipart.ServletFileUploadCreator;
import br.com.caelum.vraptor4.interceptor.multipart.UploadedFileConverter;
import br.com.caelum.vraptor4.ioc.ControllerHandler;
import br.com.caelum.vraptor4.ioc.ConverterHandler;
import br.com.caelum.vraptor4.ioc.InterceptorStereotypeHandler;
import br.com.caelum.vraptor4.proxy.InstanceCreator;
import br.com.caelum.vraptor4.proxy.JavassistProxifier;
import br.com.caelum.vraptor4.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor4.proxy.Proxifier;
import br.com.caelum.vraptor4.proxy.ReflectionInstanceCreator;
import br.com.caelum.vraptor4.restfulie.RestHeadersHandler;
import br.com.caelum.vraptor4.restfulie.headers.DefaultRestDefaults;
import br.com.caelum.vraptor4.restfulie.headers.DefaultRestHeadersHandler;
import br.com.caelum.vraptor4.restfulie.headers.RestDefaults;
import br.com.caelum.vraptor4.serialization.DefaultRepresentationResult;
import br.com.caelum.vraptor4.serialization.HTMLSerialization;
import br.com.caelum.vraptor4.serialization.I18nMessageSerialization;
import br.com.caelum.vraptor4.serialization.JSONPSerialization;
import br.com.caelum.vraptor4.serialization.JSONSerialization;
import br.com.caelum.vraptor4.serialization.NullProxyInitializer;
import br.com.caelum.vraptor4.serialization.ProxyInitializer;
import br.com.caelum.vraptor4.serialization.RepresentationResult;
import br.com.caelum.vraptor4.serialization.XMLSerialization;
import br.com.caelum.vraptor4.serialization.xstream.NullConverter;
import br.com.caelum.vraptor4.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor4.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor4.serialization.xstream.XStreamConverters;
import br.com.caelum.vraptor4.serialization.xstream.XStreamJSONPSerialization;
import br.com.caelum.vraptor4.serialization.xstream.XStreamJSONSerialization;
import br.com.caelum.vraptor4.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor4.validator.BeanValidator;
import br.com.caelum.vraptor4.validator.DefaultBeanValidator;
import br.com.caelum.vraptor4.validator.DefaultValidator;
import br.com.caelum.vraptor4.validator.MessageConverter;
import br.com.caelum.vraptor4.validator.MessageInterpolatorFactory;
import br.com.caelum.vraptor4.validator.MethodValidatorFactoryCreator;
import br.com.caelum.vraptor4.validator.MethodValidatorInterceptor;
import br.com.caelum.vraptor4.validator.NullBeanValidator;
import br.com.caelum.vraptor4.validator.Outjector;
import br.com.caelum.vraptor4.validator.ReplicatorOutjector;
import br.com.caelum.vraptor4.validator.ValidatorCreator;
import br.com.caelum.vraptor4.validator.ValidatorFactoryCreator;
import br.com.caelum.vraptor4.view.AcceptHeaderToFormat;
import br.com.caelum.vraptor4.view.DefaultAcceptHeaderToFormat;
import br.com.caelum.vraptor4.view.DefaultHttpResult;
import br.com.caelum.vraptor4.view.DefaultLogicResult;
import br.com.caelum.vraptor4.view.DefaultPageResult;
import br.com.caelum.vraptor4.view.DefaultPathResolver;
import br.com.caelum.vraptor4.view.DefaultRefererResult;
import br.com.caelum.vraptor4.view.DefaultStatus;
import br.com.caelum.vraptor4.view.DefaultValidationViewsFactory;
import br.com.caelum.vraptor4.view.EmptyResult;
import br.com.caelum.vraptor4.view.FlashScope;
import br.com.caelum.vraptor4.view.HttpResult;
import br.com.caelum.vraptor4.view.LogicResult;
import br.com.caelum.vraptor4.view.PageResult;
import br.com.caelum.vraptor4.view.PathResolver;
import br.com.caelum.vraptor4.view.RefererResult;
import br.com.caelum.vraptor4.view.SessionFlashScope;
import br.com.caelum.vraptor4.view.Status;
import br.com.caelum.vraptor4.view.ValidationViewsFactory;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * List of base components to vraptor.<br/>
 * Those components should be available with any chosen ioc implementation.
 *
 * @author guilherme silveira
 */
public class BaseComponents {

    static final Logger logger = LoggerFactory.getLogger(BaseComponents.class);

    private final static Map<Class<?>, Class<?>> APPLICATION_COMPONENTS = classMap(
    		EncodingHandlerFactory.class, 	EncodingHandlerFactory.class,
    		AcceptHeaderToFormat.class, 	DefaultAcceptHeaderToFormat.class,
    		Converters.class, 				DefaultConverters.class,
            InterceptorRegistry.class, 		TopologicalSortedInterceptorRegistry.class,
            InterceptorHandlerFactory.class,DefaultInterceptorHandlerFactory.class,
            MultipartConfig.class, 			DefaultMultipartConfig.class,
            UrlToControllerTranslator.class, 	DefaultControllerTranslator.class,
            Router.class, 					DefaultRouter.class,
            TypeNameExtractor.class, 		DefaultTypeNameExtractor.class,
            ControllerNotFoundHandler.class, 	DefaultControllerNotFoundHandler.class,
            MethodNotAllowedHandler.class,	DefaultMethodNotAllowedHandler.class,
            RoutesConfiguration.class, 		NoRoutesConfiguration.class,
            Deserializers.class,			DefaultDeserializers.class,
            Proxifier.class, 				JavassistProxifier.class,
            InstanceCreator.class,          getInstanceCreator(),
            ParameterNameProvider.class, 	ParanamerNameProvider.class,
            TypeFinder.class, 				DefaultTypeFinder.class,
            RoutesParser.class, 			PathAnnotationRoutesParser.class,
            Routes.class,					DefaultRoutes.class,
            RestDefaults.class,				DefaultRestDefaults.class,
            Evaluator.class,				JavaEvaluator.class,
            StaticContentHandler.class,		DefaultStaticContentHandler.class,
            SingleValueConverter.class,     NullConverter.class,
            ProxyInitializer.class,			NullProxyInitializer.class
    );

    private final static Map<Class<?>, Class<?>> CACHED_COMPONENTS = classMap(
    );

    private static final Map<Class<?>, Class<?>> PROTOTYPE_COMPONENTS = classMap(
    		RequestExecution.class, 						EnhancedRequestExecution.class,
    		XStreamBuilder.class, 							XStreamBuilderImpl.class
    );

    private static final Map<Class<?>, Class<?>> REQUEST_COMPONENTS = classMap(
    			InterceptorStack.class, 						DefaultInterceptorStack.class,
    			SimpleInterceptorStack.class,                DefaultSimpleInterceptorStack.class,
            MethodInfo.class, 						MethodInfo.class,
            LogicResult.class, 								DefaultLogicResult.class,
            PageResult.class, 								DefaultPageResult.class,
            HttpResult.class, 								DefaultHttpResult.class,
            RefererResult.class, 							DefaultRefererResult.class,
            PathResolver.class, 							DefaultPathResolver.class,
            ValidationViewsFactory.class,					DefaultValidationViewsFactory.class,
            Result.class, 									DefaultResult.class,
            Validator.class, 								DefaultValidator.class,
            Outjector.class, 								ReplicatorOutjector.class,
            DownloadInterceptor.class, 						DownloadInterceptor.class,
            EmptyResult.class, 								EmptyResult.class,
            ExecuteMethodInterceptor.class, 				ExecuteMethodInterceptor.class,
            ExceptionHandlerInterceptor.class,              ExceptionHandlerInterceptor.class,
            ExceptionMapper.class,                          DefaultExceptionMapper.class,
            FlashInterceptor.class, 						FlashInterceptor.class,
            ForwardToDefaultViewInterceptor.class, 			ForwardToDefaultViewInterceptor.class,
            InstantiateInterceptor.class, 					InstantiateInterceptor.class,
            DeserializingInterceptor.class, 				DeserializingInterceptor.class,
            JsonDeserializer.class,							JsonDeserializer.class,
            FormDeserializer.class,							FormDeserializer.class,
            Localization.class, 							JstlLocalization.class,
            OutjectResult.class, 							OutjectResult.class,
            ParametersInstantiatorInterceptor.class, 		ParametersInstantiatorInterceptor.class,
            ControllerLookupInterceptor.class, 				ControllerLookupInterceptor.class,
            Status.class,									DefaultStatus.class,
            XMLDeserializer.class,			                XStreamXMLDeserializer.class,
            XMLSerialization.class,							XStreamXMLSerialization.class,
            JSONSerialization.class,						XStreamJSONSerialization.class,
            JSONPSerialization.class,						XStreamJSONPSerialization.class,
            HTMLSerialization.class,						HTMLSerialization.class,
            I18nMessageSerialization.class,					I18nMessageSerialization.class,
            RepresentationResult.class,						DefaultRepresentationResult.class,
            FormatResolver.class,							DefaultFormatResolver.class,
            Configuration.class,							ApplicationConfiguration.class,
            RestHeadersHandler.class,						DefaultRestHeadersHandler.class,
            FlashScope.class,								SessionFlashScope.class,
            XStreamConverters.class,                        XStreamConverters.class,
            MessageConverter.class,							MessageConverter.class,
            ParameterIncluderInterceptor.class,					ParameterIncluderInterceptor.class
    );

    @SuppressWarnings({"unchecked", "rawtypes"})
	private static final Set<Class<? extends Converter<?>>> BUNDLED_CONVERTERS = new HashSet(Arrays.asList(
    		BigDecimalConverter.class,
    		BigIntegerConverter.class,
    		BooleanConverter.class,
    		ByteConverter.class,
    		CharacterConverter.class,
    		DoubleConverter.class,
    		EnumConverter.class,
    		FloatConverter.class,
    		IntegerConverter.class,
    		LocaleBasedCalendarConverter.class,
    		LocaleBasedDateConverter.class,
    		LongConverter.class,
    		PrimitiveBooleanConverter.class,
    		PrimitiveByteConverter.class,
    		PrimitiveCharConverter.class,
    		PrimitiveDoubleConverter.class,
    		PrimitiveFloatConverter.class,
			PrimitiveIntConverter.class,
			PrimitiveLongConverter.class,
			PrimitiveShortConverter.class,
			ShortConverter.class,
			StringConverter.class,
			UploadedFileConverter.class));


	private static final HashMap<Class<? extends Annotation>, StereotypeInfo> STEREOTYPES_INFO = new HashMap<Class<? extends Annotation>,StereotypeInfo>();
    static {
    		STEREOTYPES_INFO.put(Controller.class,new StereotypeInfo(Controller.class,ControllerHandler.class,new AnnotationLiteral<ControllerQualifier>() {}));
    		STEREOTYPES_INFO.put(Convert.class,new StereotypeInfo(Convert.class,ConverterHandler.class,new AnnotationLiteral<ConvertQualifier>() {}));
    		STEREOTYPES_INFO.put(Deserializes.class,new StereotypeInfo(Deserializes.class,DeserializesHandler.class,new AnnotationLiteral<DeserializesQualifier>() {}));
    		STEREOTYPES_INFO.put(Intercepts.class,new StereotypeInfo(Intercepts.class,InterceptorStereotypeHandler.class,new AnnotationLiteral<InterceptsQualifier>() {}));

    }

    private static final Set<Class<? extends Deserializer>> DESERIALIZERS = Collections.<Class<? extends Deserializer>>singleton(XMLDeserializer.class);


    public static Set<Class<? extends Deserializer>> getDeserializers() {
		return DESERIALIZERS;
	}

    private static Class<? extends InstanceCreator> getInstanceCreator() {
        if (isClassPresent("org.objenesis.ObjenesisStd")) {
            return ObjenesisInstanceCreator.class;
        }

        return ReflectionInstanceCreator.class;
    }

	public static Map<Class<?>, Class<?>> getCachedComponents() {
		return Collections.unmodifiableMap(CACHED_COMPONENTS);
	}

    public static Map<Class<?>, Class<?>> getApplicationScoped() {
        if (!isClassPresent("ognl.OgnlRuntime")) {
            APPLICATION_COMPONENTS.put(DependencyProvider.class, VRaptorDependencyProvider.class);
        }

        // try put beanval 1.1 or beanval 1.0 if available
        if (isClassPresent("javax.validation.executable.ExecutableValidator")) {
            APPLICATION_COMPONENTS.put(ValidatorCreator.class, ValidatorCreator.class);
            APPLICATION_COMPONENTS.put(ValidatorFactoryCreator.class, ValidatorFactoryCreator.class);
            APPLICATION_COMPONENTS.put(MethodValidatorFactoryCreator.class, MethodValidatorFactoryCreator.class);
            APPLICATION_COMPONENTS.put(MessageInterpolatorFactory.class, MessageInterpolatorFactory.class);
        } else if (isClassPresent("javax.validation.Validation")) {
            APPLICATION_COMPONENTS.put(ValidatorCreator.class, ValidatorCreator.class);
            APPLICATION_COMPONENTS.put(ValidatorFactoryCreator.class, ValidatorFactoryCreator.class);
            APPLICATION_COMPONENTS.put(MessageInterpolatorFactory.class, MessageInterpolatorFactory.class);
        }

    	return Collections.unmodifiableMap(APPLICATION_COMPONENTS);
    }

    public static Map<Class<?>, Class<?>> getRequestScoped() {
        // try put beanval 1.1 or beanval 1.0 if available
        if (isClassPresent("javax.validation.executable.ExecutableValidator")) {
            REQUEST_COMPONENTS.put(BeanValidator.class, DefaultBeanValidator.class);
            REQUEST_COMPONENTS.put(MethodValidatorInterceptor.class, MethodValidatorInterceptor.class);
        } else if (isClassPresent("javax.validation.Validation")) {
            REQUEST_COMPONENTS.put(BeanValidator.class, DefaultBeanValidator.class);
        } else {
            REQUEST_COMPONENTS.put(BeanValidator.class, NullBeanValidator.class);
        }

        if (isClassPresent("org.apache.commons.fileupload.FileItem")) {
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, CommonsUploadMultipartInterceptor.class);
            REQUEST_COMPONENTS.put(ServletFileUploadCreator.class, DefaultServletFileUploadCreator.class);
        } else if (isClassPresent("javax.servlet.http.Part")) {
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, Servlet3MultipartInterceptor.class);
        } else {
    	    logger.warn("There is neither commons-fileupload nor servlet3 handlers registered. " +
    	    		"If you are willing to upload a file, please add the commons-fileupload in " +
    	    		"your classpath or use a Servlet 3 Container");
            REQUEST_COMPONENTS.put(MultipartInterceptor.class, NullMultipartInterceptor.class);
    	}

        if (isClassPresent("ognl.OgnlRuntime")) {
            REQUEST_COMPONENTS.put(ParametersProvider.class, OgnlParametersProvider.class);
            REQUEST_COMPONENTS.put(EmptyElementsRemoval.class, EmptyElementsRemoval.class);
            REQUEST_COMPONENTS.put(OgnlFacade.class, OgnlFacade.class);
        } else {
            REQUEST_COMPONENTS.put(ParametersProvider.class, IogiParametersProvider.class);
            REQUEST_COMPONENTS.put(ParameterNamesProvider.class, VRaptorParameterNamesProvider.class);
            REQUEST_COMPONENTS.put(InstantiatorWithErrors.class, VRaptorInstantiator.class);
            REQUEST_COMPONENTS.put(Instantiator.class, VRaptorInstantiator.class);
        }

        return Collections.unmodifiableMap(REQUEST_COMPONENTS);
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

	private static boolean registerIfClassPresent(Map<Class<?>, Class<?>> components, String className, Class<?>... types) {
		try {
			Class.forName(className);
			for (Class<?> type : types) {
				components.put(type, type);
			}
			return true;
		} catch (ClassNotFoundException e) {
			/* ok, don't register */
			return false;
		}
	}

	private static void registerIfClassPresent(Set<Class<? extends Converter<?>>> components, String className, Class<? extends Converter<?>>... types) {
		if (components.contains(types[0])) {
			return;
		}
		try {
    		Class.forName(className);
    		for (Class<? extends Converter<?>> type : types) {
    			components.add(type);
			}
    	} catch (ClassNotFoundException e) { /*ok, don't register*/ }
	}

    public static Map<Class<?>, Class<?>> getPrototypeScoped() {
		return Collections.unmodifiableMap(PROTOTYPE_COMPONENTS);
	}

    @SuppressWarnings("unchecked")
	public static Set<Class<? extends Converter<?>>> getBundledConverters() {
    	registerIfClassPresent(BUNDLED_CONVERTERS, "org.joda.time.LocalDate",
    			LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class,
    			DateTimeConverter.class, DateMidnightConverter.class);
        return BUNDLED_CONVERTERS;
    }

    public static Set<StereotypeInfo> getStereotypesInfo() {
    		return new HashSet<StereotypeInfo>(STEREOTYPES_INFO.values());
    }

    public static Set<Class<? extends Annotation>> getStereotypes() {
    		Set<StereotypeInfo> stereotypesInfo = getStereotypesInfo();
    		HashSet<Class<? extends Annotation>> stereotypes = new HashSet<Class<? extends Annotation>>();
    		for (StereotypeInfo stereotypeInfo : stereotypesInfo) {
    			stereotypes.add(stereotypeInfo.getStereotype());
		}
    		return stereotypes;
    }
    public static Map<Class<? extends Annotation>,StereotypeInfo> getStereotypesInfoMap() {
    		return STEREOTYPES_INFO;
    }

    private static Map<Class<?>, Class<?>> classMap(Class<?>... items) {
        HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        Iterator<Class<?>> it = Arrays.asList(items).iterator();
        while (it.hasNext()) {
            Class<?> key = it.next();
            Class<?> value = it.next();
            if (value == null) {
                throw new IllegalArgumentException("The number of items should be even.");
            }
            map.put(key, value);
        }
        return map;
    }


}