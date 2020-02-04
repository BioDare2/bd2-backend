/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search.lucene;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class Fields {
    
    public static final String EXP_ID = "ExpId";
    public static final String EXP_ID_S = "ExpIdS";
    
    public static final String NAME = "Name";
    public static final String NAME_S = "NameS";
    public static final String PURPOSE = "Purpose";
    public static final String DESCRIPTION = "Description";

    public static final String DATA_CATEGORY = "DataCategory";
    public static final String SPECIES = "Species";
    
    public static final String WHOLE_CONTENT = "WholeContent";

    public static final String FIRST_AUTHOR = "FirstAuthor";
    public static final String FIRST_AUTHOR_S = "FirstAuthorS";
    
    public static final String AUTHORS = "Authors";
    
    public  static final String OWNER = "Owner";
    public  static final String IS_PUBLIC = "IsPublic";

    public  static final String MODIFIED = "Modified";
    public  static final String MODIFIED_S = "ModifiedS";
    public  static final String UPLOADED = "Uploaded";
    public  static final String UPLOADED_S = "UploadedS";
    public  static final String EXECUTED = "Executed";
    public  static final String EXECUTED_S = "ExecutedS";


    static List<String> allFields() throws Exception {
       
        List<String> values = new ArrayList<>();
        for (Field f : Fields.class.getDeclaredFields()) {
            
           values.add(f.get(null).toString());
       }
        
        return values;
    }

}