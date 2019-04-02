/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import ed.biodare2.backend.security.dao.db.UserAccount;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class PermissionsResolverTest {
    
    public PermissionsResolverTest() {
    }
    
    PermissionsResolver resolver;
    Fixtures fixtures;
    BioDare2User user;
    
    @Before
    public void init() {
        resolver = new PermissionsResolver();
        fixtures = Fixtures.build();
        user = fixtures.demoUser;
        
     }

    @Test
    public void createsACLWIthUserAsOwnerWithSuperviosor() {
        
        EntityACL acl = resolver.createNewACL(user);
        
        assertEquals(user,acl.getCreator());    
        assertFalse(user.equals(user.getSupervisor()));
        assertEquals(user,acl.getOwner());
        assertEquals(user.getSupervisor(),acl.getSuperOwner());
    }
    
    @Test
    public void createsACLWIthWritesGroupFromDefaultWriteGroups() {
        
        user.addGroup(fixtures.demoGroup);
        user.getDefaultToRead().clear();
        user.getDefaultToWrite().clear();
        
        user.getDefaultToRead().add(fixtures.demoGroup);
        user.getDefaultToWrite().add(fixtures.otherGroup);
        
        EntityACL acl = resolver.createNewACL(user);
        
        assertTrue(acl.getAllowedToWrite().contains(fixtures.otherGroup));
        assertFalse(acl.getAllowedToWrite().contains(fixtures.demoGroup));
        
    } 
    
    @Test
    public void createsACLWIthReadGroupFromDefaultWriteAndReadGroups() {
        
        user.addGroup(fixtures.demoGroup);
        user.getDefaultToRead().clear();
        user.getDefaultToWrite().clear();
        
        user.getDefaultToRead().add(fixtures.demoGroup);
        user.getDefaultToWrite().add(fixtures.otherGroup);
        
        EntityACL acl = resolver.createNewACL(user);
        
        assertTrue(acl.getAllowedToRead().contains(fixtures.otherGroup));
        assertTrue(acl.getAllowedToRead().contains(fixtures.demoGroup));
        
    }  
    
    @Test
    public void createsCorrectPermissionsSummary() {
        
        EntityACL acl = new EntityACL();
        acl.setOwner(user);
        acl.setSuperOwner(user.getSupervisor());
        
        SecuritySummary sum = resolver.permissionsSummary(acl, user);
        assertTrue(sum.isOwner);
        assertFalse(sum.isSuperOwner);
        assertTrue(sum.canRead);
        assertTrue(sum.canWrite);
        
        sum = resolver.permissionsSummary(acl, user.getSupervisor());
        assertFalse(sum.isOwner);
        assertTrue(sum.isSuperOwner);
        assertTrue(sum.canRead);
        assertTrue(sum.canWrite);
        
        sum = resolver.permissionsSummary(acl, fixtures.user1);
        assertFalse(sum.isOwner);
        assertFalse(sum.isSuperOwner);
        assertFalse(sum.canRead);
        assertFalse(sum.canWrite);
        
        acl.addCanRead(fixtures.otherGroup);
        sum = resolver.permissionsSummary(acl, fixtures.user1);
        assertFalse(sum.isOwner);
        assertFalse(sum.isSuperOwner);
        assertTrue(sum.canRead);
        assertFalse(sum.canWrite);
        
        acl.addCanWrite(fixtures.otherGroup);
        sum = resolver.permissionsSummary(acl, fixtures.user1);
        assertFalse(sum.isOwner);
        assertFalse(sum.isSuperOwner);
        assertTrue(sum.canRead);
        assertTrue(sum.canWrite);
        
    }
    
    @Test
    public void isOwnerTrueForOwnerAndSuperowner() {
        EntityACL acl = resolver.createNewACL(user);

        assertEquals(user,acl.getOwner());
        assertEquals(user.getSupervisor(),acl.getSuperOwner());
        assertNotEquals(acl.getOwner(),acl.getSuperOwner());
        UserAccount user2 = fixtures.demoUser1;
        assertNotEquals(user2, user);
        
        assertFalse(resolver.isOwner(acl, null));
        assertFalse(resolver.isOwner(acl, user2));
        assertTrue(resolver.isOwner(acl, user));
        assertTrue(resolver.isOwner(acl, user.getSupervisor()));
        
        
    }
    
    
}
