/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao;

import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Zielu
 */
public interface UserAccountRep extends JpaRepository<UserAccount, Long> {
    
    Optional<UserAccount> findByLogin(String login);
    
    List<UserAccount> findByEmail(String email);
    
    List<UserAccount> findByInitialEmail(String email);
    
    List<UserAccount> findByActivationDateIsNull();

    List<UserAccount> findByLoginOrEmailOrInitialEmail(String identifier, String identifier0, String identifier1);
    
    List<UserAccount> findBySubscriptionKind(SubscriptionType subscription);
    
    @Modifying
    @Query("UPDATE UserAccount u SET u.locked = false, u.failedAttempts = 0 WHERE u.locked = true")
    void unlockExpiredAccounts();    
}
