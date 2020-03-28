/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.io;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author tzielins
 */
public class FileUtil {
    
    final Pattern nonFileCharacter = Pattern.compile("[^a-zA-Z_0-9.]");
    final Pattern accentCharacters = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");    
    
    public Path backup(Path orgFile,Path dstDir) throws IOException {
        return backup(orgFile, dstDir,".bck");
    }
    
    public Path backup(Path orgFile,Path dstDir,String suffix) throws IOException {
        
        String stamp = currentStamp();
        
        Path uniqueFile = uniqueFile(orgFile.getFileName().toString()+"."+stamp,suffix,dstDir);
        
        Files.copy(orgFile, uniqueFile, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFile;
    }

    protected String currentStamp() {
        LocalDate date = LocalDate.now();
        
        return date.getYear()+"_"+date.getMonthValue()+"_"+date.getDayOfMonth();
    }

    protected Path uniqueFile(String prefix, String suffix, Path dstDir) throws IOException {
        
        for (int i = 1;i<=100;i++) {
            String name = prefix+"."+i+suffix;
            Path file = dstDir.resolve(name);
            try {
                return Files.createFile(file);                
            } catch (FileAlreadyExistsException e){};
        }
        throw new IOException("Iteration limit meet when trying to produce unique name");
    }

    public Path zip(Map<String, Path> files, Path destinationFile) throws IOException {
        
        ZipMaker zipMaker = new ZipMaker();
        zipMaker.packFiles(files,destinationFile);
        
        return destinationFile;
    }
    
    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) fileName = "blank_name";
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFKD);
        fileName = accentCharacters.matcher(fileName).replaceAll("");
        fileName = nonFileCharacter.matcher(fileName).replaceAll("_");
        return fileName;
    }    

    public void removeRecursively(Path path) throws IOException {
        if (!Files.isDirectory(path)) return;
        

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
        }

        });
    }
    
    
}
