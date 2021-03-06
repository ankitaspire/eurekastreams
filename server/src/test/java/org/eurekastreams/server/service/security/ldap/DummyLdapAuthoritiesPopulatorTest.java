/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.service.security.ldap;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.security.GrantedAuthority;

/**
 * Tests for DummyLdapAuthoritiesPopulator class.
 */
public class DummyLdapAuthoritiesPopulatorTest
{    
    /**
     * Verify getGrantedAuthorities() returns non-null array.
     */
    @Test
    public void testGetGrantedAuthorities()
    {
        DummyLdapAuthoritiesPopulator sut = new DummyLdapAuthoritiesPopulator();
        GrantedAuthority[] results = sut.getGrantedAuthorities(null, null);
        assertNotNull(results);
    }
}
