/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.db.dao;

import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Zielu
 */
public interface DBSystemInfoRep extends JpaRepository<DBSystemInfo, Long> {
    
    Optional<DBSystemInfo> findByParentIdAndEntityType(long parentId,EntityType entityType);
    
    //@Query("SELECT dbinfo FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND dbinfo.acl.owner = :owner")  
    //Stream<DBSystemInfo> findByOwnerAndEntityType(@Param("owner") BioDare2User owner,@Param("entityType") EntityType entityType);
    
    //@Query("SELECT dbinfo FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND dbinfo.acl.owner.login = :login")  
    //Stream<DBSystemInfo> findByEntityTypeAndOwnerLogin(@Param("entityType") EntityType entityType,@Param("login") String login);
    
    @Query("SELECT dbinfo FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND dbinfo.acl.owner.id = :id")  
    Stream<DBSystemInfo> findByOwnerIdAndEntityType(@Param("id") long id,@Param("entityType") EntityType entityType);

    @Query("SELECT dbinfo FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND (dbinfo.acl.owner.id = :id OR dbinfo.acl.isOpen = TRUE)")  
    Stream<DBSystemInfo> findByOpenOrOwnerIdAndEntityType(@Param("id") long id,@Param("entityType") EntityType entityType);

    @Query("SELECT dbinfo.parentId FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND (dbinfo.acl.owner.id = :id OR ((TRUE = :showPublic) AND (dbinfo.acl.isOpen = TRUE) ))")  
    Page<Long> findParentIdsByOpenOrOwnerIdAndEntityTypeWithPagination(@Param("id") long id,@Param("entityType") EntityType entityType, 
                                                                      @Param("showPublic") boolean showPublic, Pageable pageable);
    
    @Query("SELECT dbinfo FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND dbinfo.acl.isOpen = TRUE")  
    Stream<DBSystemInfo> findByOpenAndEntityType(@Param("entityType") EntityType entityType);
    
    @Query("SELECT max(dbinfo.parentId) FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType")  
    Long getMaxParentId(@Param("entityType") EntityType entityType);
    
    @Query("SELECT max(dbinfo.parentId) FROM DBSystemInfo dbinfo WHERE dbinfo.entityType = :entityType AND dbinfo.parentId < :upbound")  
    Long getLastParentIdBeforeBound(@Param("entityType") EntityType entityType, @Param("upbound") long upbound);
    
    Stream<DBSystemInfo> findByAclOwner(UserAccount owner);

    
}
