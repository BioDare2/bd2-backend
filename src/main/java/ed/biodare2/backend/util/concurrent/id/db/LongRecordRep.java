/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Zielu
 */
public interface LongRecordRep extends JpaRepository<LongRecord,String> {
    
}
