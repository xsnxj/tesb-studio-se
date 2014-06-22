package org.talend.ws.mapper;

import org.apache.ws.commons.schema.XmlSchemaType;

/**
 *
 * @author rlamarche
 */
public interface ClassMapper {

    public Class<?> getClassForType(XmlSchemaType xmlSchemaType);
    public XmlSchemaType getTypeForClass(Class<?> clazz);
}
