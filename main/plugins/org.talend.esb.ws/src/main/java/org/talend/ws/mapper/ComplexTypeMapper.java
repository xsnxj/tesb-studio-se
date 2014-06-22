/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.ws.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.ws.exception.LocalizedException;

/**
 * 
 * @author rlamarche
 */
public class ComplexTypeMapper implements TypeMapper {

    private Map<String, PropertyMapper> mappers;

    private Class<?> clazz;

    private List<String> propertiesOrder;

    protected ComplexTypeMapper(Map<String, PropertyMapper> mappers, Class<?> clazz, List<String> propertiesOrder) {
        this.mappers = mappers;
        this.clazz = clazz;
        this.propertiesOrder = propertiesOrder;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object convertToType(Object value) throws LocalizedException {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("You must provide a Map to create a complexType.");
        }

        Map<String, Object> values = (Map<String, Object>) value;

        Object bean = null;
        try {
            bean = clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException("Unable to instantiate bean of type " + clazz.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to instantiate bean of type " + clazz.getName(), ex);
        }

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            PropertyMapper propertyMapper = mappers.get(entry.getKey());

            if (propertyMapper != null) {
                propertyMapper.setValueTo(bean, entry.getValue());
            } else {
                // TODO log a warning ?
            }
        }

        return bean;
    }

    public Object typeToValue(Object bean) throws LocalizedException {
        if (bean == null) {
            return null;
        } else {
            if (!clazz.isInstance(bean)) {
                throw new IllegalArgumentException("You must provide an object of type specified by property clazz.");
            }
            Map<String, Object> values = new HashMap<String, Object>(mappers.size());

            for (Map.Entry<String, PropertyMapper> entry : mappers.entrySet()) {
                Object value = entry.getValue().getValueFrom(bean);
                if (value != null) {
                    values.put(entry.getKey(), value);
                }
            }

            return values;
        }
    }

    public Object[] convertToTypeUnwrapped(Map<String, Object> values) throws LocalizedException {
        Object[] objects = new Object[propertiesOrder.size()];
        int i = 0;
        for (String property : propertiesOrder) {
            Object value = values.get(property);
            if (value != null) {
                PropertyMapper propertyMapper = mappers.get(property);
                objects[i] = propertyMapper.createProperty(value);
            }

            i++;
        }
        return objects;
    }

    public Map<String, Object> typeToValueUnwrapped(Object[] params) throws LocalizedException {
        if (params == null) {
            return null;
        }
        Map<String, Object> values = new HashMap<String, Object>(mappers.size());

        int i = 0;
        for (Object param : params) {
            if (i >= propertiesOrder.size()) {
                throw new IllegalArgumentException("Too much params.");
            }
            String property = propertiesOrder.get(i);
            PropertyMapper propertyMapper = mappers.get(property);
            values.put(property, propertyMapper.createValue(param));
            i++;
        }

        return values;
    }
}
