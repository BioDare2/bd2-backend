/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.secrecy.encryption;

import ed.biodare2.backend.util.secrecy.encryption.Encryptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class EncryptorTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public EncryptorTest() {
    }

    public Encryptor makeInstance(String pass) throws GeneralSecurityException {
        return new Encryptor(pass);
    }
    public Encryptor makeInstance() throws GeneralSecurityException {
        return new Encryptor("some password");
    }
    
    @Test
    public void testEncodeMsg() throws GeneralSecurityException {
        System.out.println("encodeMsg");
        String msg = "My simple message";
        Encryptor instance = makeInstance();
        String code = instance.encodeMsg(msg);
        assertFalse(msg.equals(code));
        
        String back = instance.decodeMsg(code);
        System.out.println(code);
        assertEquals(msg, back);
    }

    @Test
    public void testIdentity() throws GeneralSecurityException {
        System.out.println("testIdentity");
        String pass = "ala";
        String msg = "My simple message";
        Encryptor instance1 = makeInstance(pass);
        String code1 = instance1.encodeMsg(msg);
        
        Encryptor instance2 = makeInstance(pass);
        String code2 = instance2.encodeMsg(msg);
        
        assertEquals(code1,code2);
        
    }
    
    @Test
    public void testPassword() throws GeneralSecurityException {
        System.out.println("testPasswords");
        String pass1 = "ala";
        String msg = "My simple message";
        Encryptor instance1 = makeInstance(pass1);
        String code1 = instance1.encodeMsg(msg);
        
        String pass2 = "aba";
        Encryptor instance2 = makeInstance(pass2);
        String code2 = instance2.encodeMsg(msg);
        
        assertFalse(code1.equals(code2));
        
    }
    
    @Test 
    public void testEncodeFile() throws GeneralSecurityException, IOException {
    
        System.out.println("encodeFile");
        String msg = "My simple message to be saved in file\n and its rest";
        Path file = testFolder.newFile().toPath(); //new File("D:/Temp/sec_test_org.txt");
        Files.write(file, msg.getBytes("UTF-8"));
        
        Encryptor instance = makeInstance();
        Path coded = testFolder.newFile().toPath();
        
        instance.encodeFile(file, coded);
        
        Path decoded = testFolder.newFolder().toPath().resolve("decoded");
        instance.decodeFile(coded, decoded);
        
        List<String> org = Files.readAllLines(file, Charset.forName("UTF-8"));
        List<String> read = Files.readAllLines(decoded, Charset.forName("UTF-8"));
        
        assertEquals(org,read);

    }
    
    @Test
    public void testEncodeStream() throws GeneralSecurityException, IOException {
        System.out.println("encodeStream");
        String msg = "My simple message to passed in stream\n and its rest";
        
        Encryptor instance = makeInstance();
        
        TestInStream in = new TestInStream(msg);
        TestOutStream coded = new TestOutStream();
        instance.encodeStream(in, coded);
        assertTrue(coded.closed);
        
        assertFalse(Arrays.equals(msg.getBytes("UTF-8"), coded.toByteArray()));
        
        TestInStream codedIn = new TestInStream(coded.toByteArray());
        TestOutStream decoded = new TestOutStream();
        
        instance.decodeStream(codedIn, decoded);
        assertTrue(decoded.closed);
        
        String res = new String(decoded.toByteArray(),"UTF-8");
        assertEquals(msg,res);
        
    }
    
    @Test
    public void testTestStreams() throws GeneralSecurityException, IOException {
        
        String msg = "My simple message to passed in stream\n and its rest";
        TestInStream in = new TestInStream(msg);
        TestOutStream out = new TestOutStream();
        
        Encryptor instance = makeInstance();
        instance.streamCpy(in, out);
        
        String res = new String(out.toByteArray(),"UTF-8");
        assertEquals(msg,res);
    }    
    
    @Test
    public void testEncodeObj() throws GeneralSecurityException, IOException, ClassNotFoundException {
        
        System.out.println("encodeObj");
        
        TestObj org = new TestObj();
        org.msg ="A message";
        org.nr = -32322;
        
        Encryptor instance = makeInstance();
        TestOutStream coded = new TestOutStream();
        
        instance.encodeObject(org, coded);
        
        

        try {
            TestInStream in = new TestInStream(coded.toByteArray());
            ObjectInputStream str = new ObjectInputStream(in);
            TestObj sec = (TestObj) str.readObject();
            fail("Should not work");
        } catch (IOException e) {            
        }
        
        TestInStream in = new TestInStream(coded.toByteArray());
        TestObj sec = instance.decodeObject(in);
        assertNotNull(sec);
        assertEquals(org, sec);
    }
    
    static class TestInStream extends ByteArrayInputStream {
        
        boolean closed = false;
        
        TestInStream(byte[] buf) {
            super(buf);
        }
        
        TestInStream(String content) throws UnsupportedEncodingException {
            super(content.getBytes("UTF-8"));
        }

        @Override
        public void close() throws IOException {
            super.close(); //To change body of generated methods, choose Tools | Templates.
            closed = true;
        }
    }
    
    static class TestOutStream extends ByteArrayOutputStream {
        boolean closed = false;
        @Override
        public void close() throws IOException {
            super.close(); //To change body of generated methods, choose Tools | Templates.
            closed = true;
        }
        
    }
    
    
    static class TestObj implements Serializable {
        String msg;
        int nr;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + Objects.hashCode(this.msg);
            hash = 47 * hash + this.nr;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestObj other = (TestObj) obj;
            if (!Objects.equals(this.msg, other.msg)) {
                return false;
            }
            if (this.nr != other.nr) {
                return false;
            }
            return true;
        }
        
        
    }
    
}
