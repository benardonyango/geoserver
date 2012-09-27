/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */

package org.geoserver.security.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.geoserver.data.test.SystemTestData;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.GeoServerUserGroupStore;
import org.geoserver.security.config.SecurityUserGroupServiceConfig;
import org.geoserver.security.impl.AbstractUserGroupServiceTest;
import org.junit.After;
import org.junit.Test;


public abstract class JDBCUserGroupServiceTest extends AbstractUserGroupServiceTest {

    static Logger LOGGER = org.geotools.util.logging.Logging.getLogger("org.geoserver.security.jdbc");
    
    protected abstract String getFixtureId();

    
    @After
    public void dropExistingTables() throws Exception {
        if (store!=null) {
            JDBCUserGroupStore jdbcStore =(JDBCUserGroupStore)store;
            JDBCTestSupport.dropExistingTables(jdbcStore,jdbcStore.getConnection());
            store.store();
        }
    }

   
    
    @Override
    public void setServiceAndStore() throws Exception {
        if (getTestData().isTestDataAvailable()) {
            service = getSecurityManager().loadUserGroupService(getFixtureId());
            store = createStore(service);
        }
    }

 
    @Override
    protected SecurityUserGroupServiceConfig createConfigObject(String name) {

        try {
            return JDBCTestSupport.createConfigObject(getFixtureId(), 
                (LiveDbmsDataSecurity)getTestData(), getSecurityManager());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
        
    public GeoServerUserGroupService createUserGroupService(String serviceName) throws Exception {
        
        return JDBCTestSupport.createUserGroupService(getFixtureId(), 
            (LiveDbmsDataSecurity)getTestData(), getSecurityManager());
    }
        
    @Override
    public GeoServerUserGroupStore createStore(GeoServerUserGroupService service) throws IOException {
        JDBCUserGroupStore store = 
            (JDBCUserGroupStore) super.createStore(service);
        try {
            JDBCTestSupport.dropExistingTables(store,store.getConnection());
        } catch (SQLException e) {
            throw new IOException(e);
        }
        store.createTables();
        store.store();
        return store;        
    }
    @Test
    public void testUserGroupDatabaseSetup() {
        try {                        
            JDBCUserGroupStore jdbcStore = 
                (JDBCUserGroupStore) store;            
            assert(jdbcStore.tablesAlreadyCreated());
            jdbcStore.checkDDLStatements();
            jdbcStore.checkDMLStatements();
            jdbcStore.clear();
            jdbcStore.dropTables();
            jdbcStore.store();
            assert(!jdbcStore.tablesAlreadyCreated());
            jdbcStore.load();

        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }
        

    @Override
    protected SystemTestData createTestData() throws Exception {
        if ("h2".equalsIgnoreCase(getFixtureId()))
            return super.createTestData();
        return new LiveDbmsDataSecurity(getFixtureId());
    }
    
    @Override
    protected boolean isJDBCTest() {
        return true;
    }


}
