package org.talend.designer.camel.spring.core.intl;

import org.apache.camel.spring.CamelContextFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.convert.ConversionService;

public class XmlFileApplicationContext extends
		org.springframework.context.support.FileSystemXmlApplicationContext {

	public XmlFileApplicationContext(String configLocation)
			throws BeansException {
		super(configLocation);
	}

	protected void finishRefresh() {
		// Initialize lifecycle processor for this context.
		initLifecycleProcessor();
	}

	@Override
	protected void finishBeanFactoryInitialization(
			ConfigurableListableBeanFactory beanFactory) {
		// create a dummy bean
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME)
				&& beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME,
						ConversionService.class)) {
			beanFactory.setConversionService(beanFactory.getBean(
					CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		// Stop using the temporary ClassLoader for type matching.
		beanFactory.setTempClassLoader(null);

		// Allow for caching all bean definition metadata, not expecting
		// further changes.
		beanFactory.freezeConfiguration();
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new DefaultListableBeanFactory(getInternalParentBeanFactory()) {
			@Override
			protected Class resolveBeanClass(RootBeanDefinition mbd,
					String beanName, Class... typesToMatch)
					throws CannotLoadBeanClassException {
				try {
					return super.resolveBeanClass(mbd, beanName, typesToMatch);
				} catch (CannotLoadBeanClassException e) {
					/*
					 * if the bean can't be resolved or not exist
					 * replace it by using dummy bean
					 */
					mbd.setBeanClass(DummyBean.class);
					return super.resolveBeanClass(mbd, beanName, typesToMatch);
				}
			}

			@Override
			protected Object initializeBean(String beanName, Object bean,
					RootBeanDefinition mbd) {
				if(bean instanceof CamelContextFactoryBean){
					//clear to avoid load typeConverters, else there's problem when load typeConverter
					((CamelContextFactoryBean)bean).setTrace(null);
					((CamelContextFactoryBean)bean).setThreadPoolProfiles(null);
				}
				return super.initializeBean(beanName, bean, mbd);
			}
		};
	}

	/**
	 * get registered bean class name
	 * according to the id
	 * @param schema
	 * @return
	 */
	public String getRegisterBeanClassName(String schema) {
		try {
			BeanDefinition beanDefinition = getBeanFactory().getBeanDefinition(
					schema);
			if (beanDefinition == null) {
				return null;
			} else {
				return beanDefinition.getBeanClassName();
			}
		} catch (Exception e) {
			return null;
		}
	}
}
