/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.accounts;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class AccountsCleanerTest {
    
    
    AccountsCleaner cleaner;
    
    UserAccountRep users;
    
    DBSystemInfoRep systemInfos;    
    
    public AccountsCleanerTest() {
    }
    
    @Before
    public void setUp() {
        
        users = mock(UserAccountRep.class);
        systemInfos = mock(DBSystemInfoRep.class);
        cleaner = new AccountsCleaner(users, systemInfos);
    }

    
    @Test
    public void isNonInnerFiltersSystemUsers() {
        
        UserAccount acc = UserAccount.testInstance(1);
        acc.setSystem(true);
        
        assertFalse(cleaner.isNonInner(acc));
        
        acc = UserAccount.testInstance(2);
        acc.setBackendOnly(true);
        assertFalse(cleaner.isNonInner(acc));
        
        acc = UserAccount.testInstance(3);
        assertTrue(cleaner.isNonInner(acc));        
    }
    
    @Test
    public void hasNoEntriesChecksForDBSystemInfo() {
        UserAccount acc1 = UserAccount.testInstance(1);
        when(systemInfos.findByAclOwner(eq(acc1))).thenReturn(Stream.of(new DBSystemInfo()));
        
        assertFalse(cleaner.hasNoEntries(acc1));
        
        when(systemInfos.findByAclOwner(eq(acc1))).thenReturn(Stream.empty());
        assertTrue(cleaner.hasNoEntries(acc1));
        
    }

    
    @Test
    public void filterFreshAndUsedRemovesNewAccounts() {
        UserAccount acc1 = UserAccount.testInstance(1);
        acc1.setRegistrationDate(LocalDate.now());
        
        UserAccount acc2 = UserAccount.testInstance(2);
        acc2.setRegistrationDate(LocalDate.now().minusDays(10));
        
        when(systemInfos.findByAclOwner(any())).thenReturn(Stream.empty());
        
        List<UserAccount> res = cleaner.filterFreshAndUsed(Arrays.asList(acc1,acc2));
        
        assertTrue(res.contains(acc2));
        assertFalse(res.contains(acc1));
    }
    
    @Test
    public void filterFreshAndUsedIngoresNullDates() {
        UserAccount acc1 = UserAccount.testInstance(1);
        acc1.setRegistrationDate(null);
        
        UserAccount acc2 = UserAccount.testInstance(2);
        acc2.setRegistrationDate(LocalDate.now().minusDays(1));
        
        when(systemInfos.findByAclOwner(any())).thenReturn(Stream.empty());
        
        List<UserAccount> res = cleaner.filterFreshAndUsed(Arrays.asList(acc1,acc2));
        
        assertFalse(res.contains(acc2));
        assertFalse(res.contains(acc1));
        assertTrue(res.isEmpty());
    }    
    
    @Test
    public void filterFreshAndUsedRemovesAccountsWithEntries() {
        UserAccount acc1 = UserAccount.testInstance(1);
        acc1.setRegistrationDate(LocalDate.now().minusDays(10));
        when(systemInfos.findByAclOwner(eq(acc1))).thenReturn(Stream.empty());
        
        UserAccount acc2 = UserAccount.testInstance(2);
        acc2.setRegistrationDate(LocalDate.now().minusDays(10));
        when(systemInfos.findByAclOwner(eq(acc2))).thenReturn(Stream.of(new DBSystemInfo()));
        
        
        List<UserAccount> res = cleaner.filterFreshAndUsed(Arrays.asList(acc1,acc2));
        
        assertFalse(res.contains(acc2));
        assertTrue(res.contains(acc1));
    }
    
    @Test
    public void removeNonActivatedDeletesThem() {
        UserAccount acc1 = UserAccount.testInstance(1);
        acc1.setRegistrationDate(LocalDate.now().minusDays(10));
        
        UserAccount acc2 = UserAccount.testInstance(2);
        acc2.setRegistrationDate(LocalDate.now().minusDays(10));

        when(users.findByActivationDateIsNull()).thenReturn(Arrays.asList(acc1,acc2));
        when(systemInfos.findByAclOwner(eq(acc1))).thenReturn(Stream.empty());
        when(systemInfos.findByAclOwner(eq(acc2))).thenReturn(Stream.empty());
        
        cleaner.removeNonActivated();
        verify(users).deleteAll(anyList());
    }
    
    
    
}
