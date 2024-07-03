/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.db.dao.db.SearchInfo;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.security.dao.db.EntityACL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class DBExperimentsSearcherTest {
    
    @Autowired
    DBSystemInfoRep dbSystemInfos;
    
    @Autowired
    Fixtures fixtures;
    
    DBExperimentsSearcher instance;
    
    public DBExperimentsSearcherTest() {
    }
    
    @Before
    public void setUp() {
        instance = new DBExperimentsSearcher(dbSystemInfos);
    }

    @Test
    public void sortCriteriaWork() {
        
        assertNotNull(instance);
        
        dbSystemInfos.save(makeInfo(1, "bbb", "ccc", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(1)));
        dbSystemInfos.save(makeInfo(2, "abb", "dcc", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(2)));
        dbSystemInfos.save(makeInfo(3, "cbb", "acc", LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(3)));
        dbSystemInfos.save(makeInfo(4, "dbb", "ecc", LocalDateTime.now().plusHours(0), LocalDateTime.now().plusHours(4)));
        dbSystemInfos.save(makeInfo(5, "ebb", "fcc", LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(0)));
        
        boolean showPublic = true;
        int pageIndex = 0;
        int pageSize = 10;
        Optional<Long> user = Optional.of(fixtures.user1.getId());
        
        SortOption sorting = SortOption.ID;
        List<Long> ids = instance.findAllVisible(user, showPublic, sorting, showPublic, pageIndex, pageSize).data;        
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), ids);
        
        sorting = SortOption.NAME;
        ids = instance.findAllVisible(user, showPublic, sorting, showPublic, pageIndex, pageSize).data;        
        assertEquals(List.of(2L, 1L, 3L, 4L, 5L), ids);
        
        sorting = SortOption.FIRST_AUTHOR;
        ids = instance.findAllVisible(user, showPublic, sorting, showPublic, pageIndex, pageSize).data;        
        assertEquals(List.of(3L, 1L, 2L, 4L, 5L), ids);    
    
        sorting = SortOption.MODIFICATION_DATE;
        ids = instance.findAllVisible(user, showPublic, sorting, showPublic, pageIndex, pageSize).data;        
        assertEquals(List.of(4L, 1L, 2L, 3L, 5L), ids);    
        
        sorting = SortOption.EXECUTION_DATE;
        ids = instance.findAllVisible(user, showPublic, sorting, showPublic, pageIndex, pageSize).data;        
        assertEquals(List.of(5L, 1L, 2L, 3L, 4L), ids);    
        
    }
    
    @Test
    public void handlesAllSortingOptions() {
        
        for (SortOption option: SortOption.values()) {
            Sort sort = instance.sortCriteria(option, true);
            assertNotNull(sort);
        }
        
    }
    
    protected DBSystemInfo makeInfo(long parentId, String name, String firstAuthor,
            LocalDateTime modified, LocalDateTime executed) {
        
        DBSystemInfo info = new DBSystemInfo();
        info.setParentId(parentId);
        info.setEntityType(EntityType.EXP_ASSAY);
        info.setAcl(new EntityACL());
        
        info.getAcl().setOwner(fixtures.user1);
        info.getAcl().setSuperOwner(fixtures.demoBoss);
        info.getAcl().setPublic(false);
        info.getAcl().addCanWrite(fixtures.otherGroup);
        
        info.setEmbargoDate(LocalDate.now().plusDays(5));
        
        info.setSearchInfo(new SearchInfo());
        info.getSearchInfo().setName(name);
        info.getSearchInfo().setFirstAuthor(firstAuthor);
        info.getSearchInfo().setModificationDate(modified);
        info.getSearchInfo().setExecutionDate(executed);
        
        return info;
    }
    
}
