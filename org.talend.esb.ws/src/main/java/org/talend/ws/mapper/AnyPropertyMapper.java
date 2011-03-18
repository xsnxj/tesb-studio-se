package org.talend.ws.mapper;

import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.beanutils.PropertyUtils;
import org.talend.ws.exception.IllegalPropertyAccessException;
import org.talend.ws.exception.InvocationTargetPropertyAccessor;
import org.talend.ws.exception.LocalizedException;
import org.w3c.dom.Document;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class AnyPropertyMapper implements PropertyMapper {

    private MapperFactory mapperFactory;

    private TypeMapper xmlBeanMapper;

    private String propertyName;

    private final String AnyPropertyName = "any";

    private final String AnyContentPropertyName = "content";

    private PropertyDescriptor propertyDescriptor;

    private QName schemaTypeQName;

    public AnyPropertyMapper(Class<?> clazz, MapperFactory mapperFactory) throws LocalizedException {
        this.mapperFactory = mapperFactory;

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            if (AnyPropertyName.equalsIgnoreCase(descriptor.getName())
                    || AnyContentPropertyName.equalsIgnoreCase(descriptor.getName())) {
                this.propertyName = descriptor.getName();
                propertyDescriptor = descriptor;
                break;
            }
        }
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Unable to get propertyDescriptor for bean " + clazz.getName() + " and property "
                    + propertyName);
        }
    }

    public String getMappedPropertyName() {
        return propertyName;
    }

    public void setValueTo(Object destination, Object value) throws LocalizedException {
        try {
            Method method = propertyDescriptor.getWriteMethod();
            if (method.getParameterTypes()[0].equals(JAXBElement.class)) {
                value = new JAXBElement(new QName(getMappedPropertyName()), getMappedClass(), value);
                propertyDescriptor.getWriteMethod().invoke(destination, value);
            } else {
                propertyDescriptor.getWriteMethod().invoke(destination, value);
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalPropertyAccessException(propertyDescriptor.getName(), destination.getClass().getName(), ex);
        } catch (InvocationTargetException ex) {
            throw new InvocationTargetPropertyAccessor(propertyDescriptor.getName(), destination.getClass().getName(), ex
                    .getTargetException());
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    public Object getValueFrom(Object source) throws LocalizedException {
        try {
            Method method = propertyDescriptor.getReadMethod();
            String value = "";
            if (method.getReturnType().equals(java.util.List.class)) {
                List<ElementNSImpl> anyList = (List<ElementNSImpl>) propertyDescriptor.getReadMethod().invoke(source);
                for (ElementNSImpl child : anyList) {
                    Document doc = child.getOwnerDocument();
                    DOMSource domSource = new DOMSource(doc);
                    StringWriter writer = new StringWriter();
                    Result result = new StreamResult(writer);
                    Transformer transformer;
                    try {
                        transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.transform(domSource, result);
                    } catch (TransformerConfigurationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (TransformerFactoryConfigurationError e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    value += writer.getBuffer().toString();
                }
                return value;
            } else {
                // shouldn't be there.
                return null;
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalPropertyAccessException(propertyDescriptor.getName(), source.getClass().getName(), ex);
        } catch (InvocationTargetException ex) {
            throw new InvocationTargetPropertyAccessor(propertyDescriptor.getName(), source.getClass().getName(), ex
                    .getTargetException());
        }
    }

    public Class<?> getMappedClass() {
        return xmlBeanMapper.getClazz();
    }

    public Object createProperty(Object value) throws LocalizedException {
        xmlBeanMapper = mapperFactory.schemaTypeMap.get(schemaTypeQName);
        return xmlBeanMapper.convertToType(value);
    }

    public Object createValue(Object property) throws LocalizedException {
        xmlBeanMapper = mapperFactory.schemaTypeMap.get(schemaTypeQName);
        return xmlBeanMapper.typeToValue(property);
    }
}
