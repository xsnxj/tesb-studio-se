// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.core;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import test.Tester;


/**
 * DOC LiXP  class global comment. Detailled comment
 */
public class CFileParserTest {

    private static final String FILE_PATH = "tests/file.xml";

    private CamelSpringParser camelSpringParser;

    /**
     * DOC LiXP Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        camelSpringParser = new CamelSpringParser();
       
    }

    /**
     * Test method for {@link org.talend.designer.camel.spring.core.CamelSpringParser#startParse(java.lang.String)}.
     */
    @Test
    public void testStartParse() {
        camelSpringParser.addListener(new ISpringParserListener() {
            
            public void process(int componentType, Map<String, String> parameters, int connectionType, String sourceId,
                    Map<String, String> connectionParameters) {
               if(componentType == ICamelSpringConstants.FILE){
                   assertEquals(ICamelSpringConstants.ROUTE, connectionType);
                   assertEquals(3, parameters.size());
                   assertNotNull(parameters.get(ICamelSpringConstants.UNIQUE_NAME_ID));
               }else{
//                   assertTrue(false);
               }
                
            }
            
            public void preProcess() {
               
                
            }
            
            public void postProcess() {
                
                
            }
        });
        try {
            camelSpringParser.startParse(FILE_PATH);
        } catch (Exception e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

}
