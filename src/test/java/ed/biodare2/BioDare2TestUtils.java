/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class BioDare2TestUtils {
    
    public static long count(Class c,EntityManager em) {
        TypedQuery<Long> q = em.createQuery("select count(*) from "+c.getSimpleName(), Long.class);
        return q.getSingleResult();
    }
    
    public static void assertFieldsEquals(Object org,Object cpy) {
        assertReflectionEquals(org,cpy);
    }
    
     
}
