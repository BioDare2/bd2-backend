/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security;

import java.util.List;

/**
 *
 * @author tzielins
 */
public interface BioDare2Group {
    
    public Long getId();

    public String getName();
    public void setName(String name);
    
    public String getLongName();
    public void setLongName(String name);    

    //public List<BioDare2User> getMembers();
    //public void addMember(BioDare2User memeber);

    public boolean isSystem();
    //public boolean isSpecial();    
}
