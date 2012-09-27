/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wms.featureinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport2;
import org.geoserver.wms.featureinfo.dummy.Dummy;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureTemplateTest extends WMSTestSupport2 {
    
  
    @Test
    public void testWithDateAndBoolean() throws Exception {

        SimpleFeatureSource source = getFeatureSource( MockData.PRIMITIVEGEOFEATURE );
        SimpleFeatureCollection fc = source.getFeatures();
        Iterator i = fc.iterator();
        try {
            SimpleFeature f = (SimpleFeature) i.next();
            
            FeatureTemplate template = new FeatureTemplate();
            try {
                template.description( f );    
            }
            catch ( Exception e ) {
                e.printStackTrace();
                fail("template threw exception on null value");
            }
        }
        finally {
            fc.close( i );
        }
    }
     
    @Test
    public void testRawValue() throws Exception {
        SimpleFeatureSource source = getFeatureSource(MockData.PRIMITIVEGEOFEATURE);
        SimpleFeatureCollection fc = source.getFeatures();
        Iterator i = fc.iterator();
        try {
            SimpleFeature f = (SimpleFeature) i.next();

            FeatureTemplate template = new FeatureTemplate();
            try {
                template.template(f, "rawValues.ftl", FeatureTemplateTest.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw(e);
            }
        } finally {
            fc.close(i);
        }
    }

    @Test
    public void testWithNull() throws Exception {
        
        SimpleFeatureSource source = getFeatureSource( MockData.BASIC_POLYGONS );
        SimpleFeatureCollection fc = source.getFeatures();
        Iterator i = fc.iterator();
        try {
            SimpleFeature f = (SimpleFeature) i.next();
            
            FeatureTemplate template = new FeatureTemplate();
            template.description( f );
            
            //set a value to null
            f.setAttribute(1,null);
            try {
                template.description( f );    
            }
            catch ( Exception e ) {
                e.printStackTrace();
                fail("template threw exception on null value");
            }
            
        }
        finally {
            fc.close( i );
        }
      
    }
    
    @Test
    public void testAlternateLookup() throws Exception {
        
        SimpleFeatureSource source = getFeatureSource( MockData.PRIMITIVEGEOFEATURE );
        SimpleFeatureCollection fc = source.getFeatures();
        SimpleFeature f = fc.features().next();
        
        FeatureTemplate template = new FeatureTemplate();
        String result = template.template(f, "dummy.ftl", Dummy.class );
        
        assertEquals( "dummy", result );
    }
}
