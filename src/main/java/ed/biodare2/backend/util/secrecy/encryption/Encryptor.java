/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.secrecy.encryption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.crypto.codec.Base64;

/**
 *
 * @author tzielins
 */
public class Encryptor {
    
    //for input strings
    static final String UTF = "UTF-8";
    //for output byte[] representation as it is "byte" loss codign
    static final String ISO = "ISO-8859-1";
    
    static final String SALT = "!@uou$(8654";
    
    protected final Key key;
    
    public Encryptor(String password) throws GeneralSecurityException {
        this(makeKey(password));
    }
    
    protected Encryptor(Key key) {
        if (!key.getAlgorithm().equals("AES")) throw new IllegalArgumentException("Current implementation uses only AES keys, got: "+key.getAlgorithm());
        this.key = key;
    }
    
    public String encodeMsg(String msg) throws GeneralSecurityException {
        
        try {            
            Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE);

            byte[] code = cipher.doFinal(msg.getBytes(UTF));
            code = Base64.encode(code);
            return new String(code,UTF);        
        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException("Could not encode text (Unsupported encoding): "+e.getMessage());
        }
    }
    
    
    
    public String decodeMsg(String code) throws GeneralSecurityException {
        try {
            Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE);
            
            byte[] coded = Base64.decode(code.getBytes(UTF));
            byte[] decoded = cipher.doFinal(coded);
            return new String(decoded,UTF);
        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException("Could not encode text (Unsupported encoding): "+e.getMessage());
        }
    }
    
    public void encodeFile(File in,File coded) throws GeneralSecurityException, IOException {
        encodeFile(in.toPath(),coded.toPath());
    }
    
    public void encodeFile(Path in, Path coded) throws GeneralSecurityException, IOException {
        
        Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE);
        try (CipherOutputStream outS = new CipherOutputStream(Files.newOutputStream(coded), cipher)) {
           Files.copy(in, outS);
        }        
    }
    
    public void decodeFile(File in,File decoded) throws GeneralSecurityException, IOException {
        decodeFile(in.toPath(),decoded.toPath());
    }
    
    public void decodeFile(Path in,Path decoded) throws GeneralSecurityException, IOException {
        Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE);
        try (CipherInputStream inS = new CipherInputStream(Files.newInputStream(in), cipher)) {
            Files.copy(inS, decoded);
        }
    }

    /**
     * Encodes content of the input stream into output stream. The output stream is closed afterwards to assure flushing
     * @param in
     * @param coded
     * @throws IOException
     * @throws GeneralSecurityException 
     */
    public void encodeStream(InputStream in, OutputStream coded)
        throws IOException, GeneralSecurityException
    {
        
        Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE);
        try (CipherOutputStream out = new CipherOutputStream(coded, cipher)) {;
            streamCpy(in,out);
        }
    }
    
    public void decodeStream(InputStream in,OutputStream decoded) throws GeneralSecurityException, IOException {
        Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE);
        try (CipherInputStream inC = new CipherInputStream(in, cipher)) {
            streamCpy(inC,decoded);
            decoded.close();
        }
        
    }

    public void encodeObject(Serializable obj,Path out) throws GeneralSecurityException, IOException {
        encodeObject(obj, Files.newOutputStream(out));
    }
    
    public void encodeObject(Serializable obj,File out) throws GeneralSecurityException, IOException {
        encodeObject(obj, Files.newOutputStream(out.toPath()));
    }
    
    public void encodeObject(Serializable obj,OutputStream out) throws GeneralSecurityException, IOException {
        Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE);
        try (ObjectOutputStream str = new ObjectOutputStream(new CipherOutputStream(out, cipher))) {
            str.writeObject(obj);
        }
    }
    
    public <T> T decodeObject(File in) throws GeneralSecurityException, IOException {
        return decodeObject(in.toPath());
    }
    
    public <T> T decodeObject(Path in) throws GeneralSecurityException, IOException {
        return decodeObject(Files.newInputStream(in));
    }    
    
    public <T extends Serializable> T decodeObject(InputStream in) throws GeneralSecurityException, IOException {
        Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE);
        try (ObjectInputStream str = new ObjectInputStream(new CipherInputStream(in, cipher))) {
            return (T) str.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Cannot deserialize: "+e.getMessage());
        }
    }
    
    
    protected void streamCpy(InputStream in,OutputStream out) throws IOException {
        final int BUFFER_SIZE = 8192;        
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);            
        }
        
    }

    protected Cipher prepareCipher(int CIPHER_MODE) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(CIPHER_MODE,key);
        return cipher;
    }
    
    protected static Key makeKey(String password) throws GeneralSecurityException  {
        try {
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be empty");
        byte[] msg = (password+SALT).getBytes(UTF);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = sha.digest(msg);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, "AES");
        }
        
        catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException("Could not create key (Unsupported encoding): "+e.getMessage());
        }        
    }

}
