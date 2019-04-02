/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.db.UserToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author tzielins
 */
public interface UserTokenRep extends JpaRepository<UserToken, String> {
    
    Optional<UserToken> findByToken(String token);
    List<UserToken> findByExpiringBefore(LocalDateTime date);
    List<UserToken> findByUser(BioDare2User user);
}
