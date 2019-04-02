/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao;

import ed.biodare2.backend.security.dao.db.UserGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Zielu
 */
public interface UserGroupRep extends JpaRepository<UserGroup, Long> {
    
    Optional<UserGroup> findByName(String name);
    
}
