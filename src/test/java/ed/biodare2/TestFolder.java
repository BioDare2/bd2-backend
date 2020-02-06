/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
class TestFolder {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    public final Path tmp;
    
    public TestFolder() throws IOException {
        tmp = Files.createTempDirectory("biodare2tmp");
        log.info("TestFolder created at: "+tmp.toAbsolutePath());
    }
    
    public TestFolder(Path parent) throws IOException {
        tmp = Files.createTempDirectory(parent,"biodare2tmp");
        log.info("TestFolder created at: "+tmp.toAbsolutePath());
    }
    
    public void clean() throws IOException {
        
        log.info("TestFolder cleaning");
           Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {
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
        log.info("TestFolder cleanned");
   }
    
}
