/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.talend.ws.mapper;

import java.util.Map;
import javax.wsdl.Message;
import org.talend.ws.exception.LocalizedException;

/**
 *
 * @author rlamarche
 */
public class EmptyMessageMapper implements MessageMapper {

    public Message getMessage() {
        return null;
    }

    public Object[] convertToParams(Object value) throws LocalizedException {
        return null;
    }

    public Map<String, Object> convertToValue(Object[] params) throws LocalizedException {
        return null;
    }

    public boolean isUnwrapped() {
        return false;
    }

    public void setUnwrapped(boolean unwrapped) {
    }

}
