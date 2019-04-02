/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.db.UserToken;
import ed.biodare2.backend.security.dao.db.UserTokenKind;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class UserTokenRepTest {
    
    @Autowired
    UserTokenRep tokens;
    
    @Autowired
    Fixtures fixtures;
    
    //@MockBean // neede cause main apps needs it and JPA profile does not set it
    //Jackson2ObjectMapperBuilder builder;
    
    public UserTokenRepTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    protected UserToken makeToken() {
        UserToken token = new UserToken(UUID.randomUUID().toString());
        token.setKind(UserTokenKind.ACTIVATION);
        token.setUser(fixtures.user1);
        token.setExpiring(LocalDateTime.now().plusDays(1));
        return token;
    }

    @Test
    public void findByTokenFinds() {
        
        UserToken token = makeToken();
        
        Optional<UserToken> res = tokens.findByToken(token.getToken());
        assertFalse(res.isPresent());
        
        tokens.saveAndFlush(token);
        
        res = tokens.findByToken(token.getToken());
        assertTrue(res.isPresent());        
    }
    
    @Test
    public void savingSetsCreationDate() {
        UserToken token = makeToken();
        assertNull(token.getCreated());
        
        tokens.saveAndFlush(token);
        token = tokens.findById(token.getToken()).get();

        assertNotNull(token.getCreated());
        assertEquals(LocalDate.now(),token.getCreated().toLocalDate());
        
    }
    
    @Test
    public void findByExperiationDateWorks() {
        UserToken token1 = makeToken();
        tokens.saveAndFlush(token1);
       
        UserToken token2 = makeToken();
        token2.setExpiring(LocalDateTime.now().minusDays(1));
        tokens.saveAndFlush(token2);
        
        UserToken token3 = makeToken();
        token3.setExpiring(LocalDateTime.now().minusDays(2));
        tokens.saveAndFlush(token3);
        
        
        List<UserToken> expired = tokens.findByExpiringBefore(LocalDateTime.now());
        assertEquals(2,expired.size());
        
        assertTrue(expired.contains(token2));
        assertTrue(expired.contains(token3));
        
        expired = tokens.findByExpiringBefore(LocalDateTime.now().plusDays(10));
        assertEquals(3,expired.size());
    }
    
    @Test
    public void findByUserWorks() {
        
        List<UserToken> userTokens = tokens.findByUser(fixtures.user1);
        assertTrue(userTokens.isEmpty());
        
        UserToken token1 = makeToken();
        tokens.saveAndFlush(token1);
       
        UserToken token2 = makeToken();
        tokens.saveAndFlush(token2);
        
        userTokens = tokens.findByUser(fixtures.user1);
        assertEquals(2,userTokens.size());
        
        assertTrue(userTokens.contains(token1));
        assertTrue(userTokens.contains(token2));
        
    }
    
}
