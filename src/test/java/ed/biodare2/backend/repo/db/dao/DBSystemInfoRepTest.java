/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.db.dao;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
//@SpringBootTest()
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class DBSystemInfoRepTest {


    
    @Autowired
    DBSystemInfoRep repository;
    
    @Autowired
    Fixtures fixtures;

    DBSystemInfo info;
        
    //@MockBean
    //Jackson2ObjectMapperBuilder jacksonB;
    
    
    public DBSystemInfoRepTest() {
    }
    
    @Before
    public void init() {
        info = new DBSystemInfo();
        info.setParentId(12);
        info.setEntityType(EntityType.EXP_ASSAY);
        info.setAcl(new EntityACL());
        
        info.getAcl().setOwner(fixtures.user1);
        info.getAcl().setSuperOwner(fixtures.demoBoss);
        info.getAcl().setPublic(false);
        info.getAcl().addCanWrite(fixtures.otherGroup);
    }

    @Test
    //@Transactional
    public void canFindByParent() {
        
        Optional<DBSystemInfo> op = repository.findByParentIdAndEntityType(info.getParentId(), info.getEntityType());
        assertFalse(op.isPresent());
        
        info = repository.save(info);
        assertNotNull(info);
        
        op = repository.findByParentIdAndEntityType(info.getParentId(), info.getEntityType());        
        assertTrue(op.isPresent());
        
    }
    
    @Ignore
    @Test
    //@Transactional
    public void canFindByOwner() {
        
        fail("Uncoment below to test for the owner");
        /*
        List<DBSystemInfo> infos = repository.findByOwnerAndEntityType((UserAccount)info.acl.getOwner(), info.entityType).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        infos = repository.findByOwnerAndEntityType((UserAccount)info.acl.getOwner(), info.entityType).collect(Collectors.toList());        
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.parentId = 13;
        info2.entityType = EntityType.EXP_ASSAY;
        info2.acl = new EntityACL();
        
        info2.acl.setOwner(fixtures.user1);
        info2.acl.setSuperOwner(fixtures.demoBoss);
        info2.acl.addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.parentId = 13;
        info3.entityType = EntityType.INVESTIGATION;
        info3.acl = new EntityACL();
        
        info3.acl.setOwner(fixtures.user1);
        info3.acl.setSuperOwner(fixtures.demoBoss);
        info3.acl.addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        
        infos = repository.findByOwnerAndEntityType((UserAccount)info.acl.getOwner(), info.entityType).collect(Collectors.toList());        
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
        */
    }    
    
    @Ignore
    @Test
    //@Transactional
    public void canFindByOwnerLogin() {
        
        fail("Uncoment below to test for the owner");
        /*
        List<DBSystemInfo> infos = repository.findByEntityTypeAndOwnerLogin(info.entityType,info.acl.getOwner().getLogin()).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        infos = repository.findByEntityTypeAndOwnerLogin(info.entityType,info.acl.getOwner().getLogin()).collect(Collectors.toList());
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.parentId = 13;
        info2.entityType = EntityType.EXP_ASSAY;
        info2.acl = new EntityACL();
        
        info2.acl.setOwner(fixtures.user1);
        info2.acl.setSuperOwner(fixtures.demoBoss);
        info2.acl.addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.parentId = 13;
        info3.entityType = EntityType.INVESTIGATION;
        info3.acl = new EntityACL();
        
        info3.acl.setOwner(fixtures.user1);
        info3.acl.setSuperOwner(fixtures.demoBoss);
        info3.acl.addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        
        infos = repository.findByEntityTypeAndOwnerLogin(info.entityType,info.acl.getOwner().getLogin()).collect(Collectors.toList());
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
        */
    }    
    
    @Test
    @Transactional
    public void canFindByOwnerId() {
        
        List<DBSystemInfo> infos = repository.findByOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        infos = repository.findByOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(13);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(14);
        info3.setEntityType(EntityType.INVESTIGATION);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        // differnt entity
        info3 = repository.save(info3);   

        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(15);
        info4.setEntityType(EntityType.EXP_ASSAY);
        info4.setAcl(new EntityACL());
        
        info4.getAcl().setOwner(fixtures.user2);
        info4.getAcl().setSuperOwner(fixtures.demoBoss);
        info4.getAcl().addCanWrite(fixtures.otherGroup);
        
        // different user
        info4 = repository.save(info4);         
        
        infos = repository.findByOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
        
        infos = repository.findByOwnerIdAndEntityType(fixtures.user2.getId(),EntityType.EXP_ASSAY).collect(Collectors.toList());
        assertEquals(1,infos.size());
        assertTrue(infos.contains(info4));
    } 
    
    @Test
    //@Transactional
    public void canFindByOpenOrOwnerId() {
        
        List<DBSystemInfo> infos = repository.findByOpenOrOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        infos = repository.findByOpenOrOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(13);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setPublic(true);
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(14);
        info3.setEntityType(EntityType.INVESTIGATION);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        // differnt entity
        info3 = repository.save(info3);   

        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(15);
        info4.setEntityType(EntityType.EXP_ASSAY);
        info4.setAcl(new EntityACL());
        
        info4.getAcl().setOwner(fixtures.user2);
        info4.getAcl().setSuperOwner(fixtures.demoBoss);
        info4.getAcl().addCanWrite(fixtures.otherGroup);
        
        // different user
        info4 = repository.save(info4);         
        
        infos = repository.findByOpenOrOwnerIdAndEntityType(info.getAcl().getOwner().getId(),info.getEntityType()).collect(Collectors.toList());
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
        
        infos = repository.findByOpenOrOwnerIdAndEntityType(fixtures.user2.getId(),EntityType.EXP_ASSAY).collect(Collectors.toList());
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info2));
        assertTrue(infos.contains(info4));
    }     

    @Test
    //@Transactional
    public void canFindByOpenOrOwnerIdWithPagination() {
        
        PageRequest page = PageRequest.of(0, 10);
        boolean showPublic = true;
        List<DBSystemInfo> infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(info.getAcl().getOwner().getId(),info.getEntityType(), showPublic, page).getContent();
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(info.getAcl().getOwner().getId(),info.getEntityType(), showPublic, page).getContent();
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(13);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setPublic(true);
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(1);
        info3.setEntityType(EntityType.EXP_ASSAY);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);   

        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(2);
        info4.setEntityType(EntityType.EXP_ASSAY);
        info4.setAcl(new EntityACL());
        
        info4.getAcl().setOwner(fixtures.user2);
        info4.getAcl().setSuperOwner(fixtures.demoBoss);
        info4.getAcl().addCanWrite(fixtures.otherGroup);
        
        // different user
        info4 = repository.save(info4);         
        
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user1.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(3,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
        
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user2.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info2));
        assertTrue(infos.contains(info4));
        
        page = PageRequest.of(0, 2);
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user1.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(2,infos.size());

        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user2.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(2,infos.size());
        
        page = PageRequest.of(1, 2);
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user2.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(0,infos.size());
        
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user1.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(1,infos.size());
        
        page = PageRequest.of(0, 2, Sort.by("parentId"));
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user1.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(2, infos.size());
        assertEquals(1, infos.get(0).getParentId());
        assertEquals(12, infos.get(1).getParentId());
        
        page = PageRequest.of(0, 2, Sort.by("parentId").descending());
        Page<DBSystemInfo> paged = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user1.getId(),EntityType.EXP_ASSAY, showPublic, page);
        infos = paged.getContent();
        assertEquals(2, infos.size());
        assertEquals(13, infos.get(0).getParentId());
        assertEquals(12, infos.get(1).getParentId());
        assertEquals(3, paged.getTotalElements());
        
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user2.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(2,infos.size());
        showPublic = false;
        infos = repository.findByOpenOrOwnerIdAndEntityTypeWithPagination(fixtures.user2.getId(),EntityType.EXP_ASSAY, showPublic, page).getContent();
        assertEquals(1,infos.size());
    }     
    
    @Test
    //@Transactional
    public void canFindPublicByEntityType() {
        
        List<DBSystemInfo> infos = repository.findByOpenAndEntityType(info.getEntityType()).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info = repository.save(info);
        assertNotNull(info);
        
        infos = repository.findByOpenAndEntityType(info.getEntityType()).collect(Collectors.toList());
        assertTrue(infos.isEmpty());
        
        info.getAcl().setPublic(true);
        info = repository.save(info);
        
        
        infos = repository.findByOpenAndEntityType(info.getEntityType()).collect(Collectors.toList());
        assertEquals(1,infos.size());
        assertEquals(info,infos.get(0));
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(13);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(13);
        info3.setEntityType(EntityType.INVESTIGATION);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setPublic(true);
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        
        infos = repository.findByOpenAndEntityType(info.getEntityType()).collect(Collectors.toList());
        assertEquals(1,infos.size());
        
        info2.getAcl().setPublic(true);
        info2 = repository.save(info2);    
        
        infos = repository.findByOpenAndEntityType(info.getEntityType()).collect(Collectors.toList());
        assertEquals(2,infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));
    } 
    
    
    @Test
    public void canFindByOwnerInAccl() {
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(100);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.demoUser);
        info2 = repository.saveAndFlush(info2);
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(101);
        info3.setEntityType(EntityType.INVESTIGATION);
        info3.setAcl(new EntityACL());        
        info3.getAcl().setOwner(fixtures.demoUser);        
        info3 = repository.saveAndFlush(info3);
        
        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(102);
        info4.setEntityType(EntityType.INVESTIGATION);
        info4.setAcl(new EntityACL());        
        info4.getAcl().setOwner(fixtures.demoUser1);        
        info4 = repository.saveAndFlush(info4);
        
        List<DBSystemInfo> res = repository.findByAclOwner(fixtures.demoUser)
                .collect(Collectors.toList());
        
        assertTrue(res.contains(info2));
        assertTrue(res.contains(info3));
        assertFalse(res.contains(info4));
        
        res = repository.findByAclOwner(fixtures.demoUser1)
                .collect(Collectors.toList());        
        assertFalse(res.contains(info2));
        assertFalse(res.contains(info3));
        assertTrue(res.contains(info4));
    }
    
    
    
    //@Transactional
    @Test
    public void duplicateParentIdCauseException() {
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(13);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(13);
        info3.setEntityType(EntityType.STUDY);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        repository.flush();
        
        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(13);
        info4.setEntityType(EntityType.EXP_ASSAY);
        info4.setAcl(new EntityACL());
        
        info4.getAcl().setOwner(fixtures.user1);
        info4.getAcl().setSuperOwner(fixtures.demoBoss);
        info4.getAcl().addCanWrite(fixtures.otherGroup);
        
        
        try {
            info4 = repository.save(info4);          
            repository.flush();
            fail("Exception expected");
        } catch (javax.persistence.PersistenceException|DataIntegrityViolationException e) {};
        
    }     
    
    
    @Test
    //@Transactional
    public void maxParentGivesParrent() {

        info.setParentId(100);
        info = repository.save(info);
        assertNotNull(info);
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(130);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);    
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(300);
        info3.setEntityType(EntityType.INVESTIGATION);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        
        long last = repository.getMaxParentId(EntityType.EXP_ASSAY);
        assertEquals(130,last);
    }    
    
    @Test
    //@Transactional
    public void getLastParentIdBeforeBoundFindsWithinBound() {

        info.setParentId(100);
        info = repository.save(info);
        assertNotNull(info);
        
        DBSystemInfo info2 = new DBSystemInfo();
        info2.setParentId(130);
        info2.setEntityType(EntityType.EXP_ASSAY);
        info2.setAcl(new EntityACL());
        
        info2.getAcl().setOwner(fixtures.user1);
        info2.getAcl().setSuperOwner(fixtures.demoBoss);
        info2.getAcl().addCanWrite(fixtures.otherGroup);
        
        info2 = repository.save(info2);   
        
        DBSystemInfo info4 = new DBSystemInfo();
        info4.setParentId(135);
        info4.setEntityType(EntityType.INVESTIGATION);
        info4.setAcl(new EntityACL());
        
        info4.getAcl().setOwner(fixtures.user1);
        info4.getAcl().setSuperOwner(fixtures.demoBoss);
        info4.getAcl().addCanWrite(fixtures.otherGroup);
        
        info4 = repository.save(info4);         
        
        DBSystemInfo info3 = new DBSystemInfo();
        info3.setParentId(300);
        info3.setEntityType(EntityType.EXP_ASSAY);
        info3.setAcl(new EntityACL());
        
        info3.getAcl().setOwner(fixtures.user1);
        info3.getAcl().setSuperOwner(fixtures.demoBoss);
        info3.getAcl().addCanWrite(fixtures.otherGroup);
        
        info3 = repository.save(info3);           
        
        long last = repository.getLastParentIdBeforeBound(EntityType.EXP_ASSAY,300);
        assertEquals(130,last);
        
        last = repository.getLastParentIdBeforeBound(EntityType.EXP_ASSAY,130);
        assertEquals(100,last);
        
        last = repository.getLastParentIdBeforeBound(EntityType.EXP_ASSAY,400);
        assertEquals(300,last);
        
        last = repository.getLastParentIdBeforeBound(EntityType.INVESTIGATION,400);
        assertEquals(135,last);
    }    
    
}
