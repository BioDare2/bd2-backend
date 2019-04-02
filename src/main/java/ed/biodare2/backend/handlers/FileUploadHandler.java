/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import ed.biodare2.EnvironmentVariables;

import ed.biodare2.backend.web.rest.DeniedAccessException;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.util.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tzielins
 */
@Service
public class FileUploadHandler {
    
    final static String UPLOADS_STORAGE_DIR = "tmp_uploads";
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final static String T_PREFIX = "_upload";
    final Path uploadsDir;
    final ObjectReader infoReader;
    final ObjectWriter infoWriter;
    final FileUtil fileUtil = new FileUtil();
    long timeToLive = 1;
    
    @Autowired
    public FileUploadHandler(EnvironmentVariables environment,@Qualifier("DomMapper") ObjectMapper mapper) {
        
        Path storageDir = environment.storageDir;//Paths.get(storageDirPath);
        this.uploadsDir = storageDir.resolve(UPLOADS_STORAGE_DIR);
        
        if (!Files.exists(uploadsDir)) {
            try {
                Files.createDirectories(uploadsDir);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot create uploads storage dir: "+ex.getMessage(),ex);
            }
        }
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        
        this.infoReader = mapper.readerFor(UploadFileInfo.class);
        this.infoWriter = mapper.writerFor(UploadFileInfo.class);
        
        log.info("FileUploadHandler is using storage: "+this.uploadsDir);
        
    }
    
    
    public UploadFileInfo save(Path uploadedFile,BioDare2User user) {
        
        try {
            Path tmpFile = getTmpFile();
            String id = getIdFromTmp(tmpFile.getFileName().toString());

            Files.copy(uploadedFile, tmpFile,StandardCopyOption.REPLACE_EXISTING);
            //uploadedFile.transferTo(tmpFile.toFile());

            UploadFileInfo fileInfo = makeUploadInfo(uploadedFile, tmpFile, user);
            fileInfo.id = id;
            
            saveFileInfo(fileInfo);

            return fileInfo;
        } catch (IOException e) {
            log.error("Cannot save file {}: {}",uploadedFile.getFileName().toString(),e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot save uploaded file: "+e.getMessage(),e) {
            };
        }
        
    }
    
    public UploadFileInfo save(MultipartFile uploadedFile,BioDare2User user) {
        
        try {
            Path tmpFile = getTmpFile();
            String id = getIdFromTmp(tmpFile.getFileName().toString());

            uploadedFile.transferTo(tmpFile.toFile());

            UploadFileInfo fileInfo = makeUploadInfo(uploadedFile, tmpFile, user);
            fileInfo.id = id;
            
            saveFileInfo(fileInfo);

            return fileInfo;
        } catch (IOException e) {
            log.error("Cannot save uploaded file {}: {}",uploadedFile.getOriginalFilename(),e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot save uploaded file: "+e.getMessage(),e) {
            };
        }
        
    }
    
    public UploadFileInfo getInfo(String uploadId) {
        
        try {
            Path infoFile = getInfoFile(uploadId);
            if (!Files.isRegularFile(infoFile))
                throw new NotFoundException("Upload: "+uploadId);

            return infoReader.readValue(infoFile.toFile());
        } catch (IOException e) {
            log.error("Cannot get uploaded file {}: {}",uploadId,e.getMessage(),e);
            throw new NonTransientDataAccessException("Cannot get uploaded file: "+e.getMessage(),e) {
            };
        }
    }
    
    
    public Path get(String uploadId,BioDare2User user) {
        
        UploadFileInfo info = getInfo(uploadId);
        if (!info.uploadedBy.equals(user.getLogin()))
            throw new DeniedAccessException("Uploads can be accesses only by uploader");
        
        Path file = uploadsDir.resolve(info.tmpFileName);
        if (!Files.isRegularFile(file))
            throw new NotFoundException("Missing upload content: "+uploadId);
        
        return file;
    }
    
    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60)
    //@Scheduled(fixedRate = 1000*60*2, initialDelay = 1000*10)
    public void cleanUP() {
        log.warn("Cleaning tmp upload files with time to live: {} hours",timeToLive);
        
        
        Instant deadline = Instant.now().minus(timeToLive, ChronoUnit.HOURS);
        //Instant deadline = Instant.now().minus(2, ChronoUnit.MINUTES);
        try (Stream<Path> paths = Files.list(uploadsDir)){
            List<Path> toClean = paths
                .filter( path -> path.getFileName().toString().startsWith(T_PREFIX))
                .filter( path -> {
                        try {
                        return Files.getLastModifiedTime(path).toInstant().isBefore(deadline);
                        } catch (IOException e) {
                        throw new RuntimeException(e);
                        }
                        })
                .collect(Collectors.toList());
            
            for (Path path : toClean) {
                log.info("Cleaning temp upload file: "+path);
                Files.delete(path);
            }
            
        } catch(IOException|RuntimeException e) {
            log.error("Could not clean files: {}",e.getMessage(),e);
        }
    }
    
    protected UploadFileInfo makeUploadInfo(MultipartFile uploaded,Path tmpFile,BioDare2User user) {
            UploadFileInfo fileInfo = new UploadFileInfo();
            fileInfo.tmpFileName = tmpFile.getFileName().toString();
            fileInfo.originalFileName = fileUtil.sanitizeFileName(uploaded.getOriginalFilename());
            fileInfo.contentType = uploaded.getContentType();
            fileInfo.uploadedBy = user.getLogin();
            fileInfo.uploadedOn = LocalDateTime.now();
            return fileInfo;
    }
    
    protected UploadFileInfo makeUploadInfo(Path uploaded, Path tmpFile, BioDare2User user) {
            UploadFileInfo fileInfo = new UploadFileInfo();
            fileInfo.tmpFileName = tmpFile.getFileName().toString();
            fileInfo.originalFileName = uploaded.getFileName().toString();
            fileInfo.contentType = "";
            fileInfo.uploadedBy = user.getLogin();
            fileInfo.uploadedOn = LocalDateTime.now();
            return fileInfo;
    }    

    protected String getIdFromTmp(String tmpName) {
        if (!tmpName.endsWith(".tmp"))
            throw new IllegalArgumentException("Tmp file name should end with .tmp to gets its id");
        
        return tmpName.substring(0,tmpName.lastIndexOf(".tmp"));
            
    }
    
    protected Path getTmpFile() throws IOException {
        
        return Files.createTempFile(uploadsDir, T_PREFIX,null);
    }

    protected Path getInfoFile(String id) {
        
        return uploadsDir.resolve(id+".json");
    }
    
    protected void saveFileInfo(UploadFileInfo fileInfo) throws IOException {
        
        Path file = getInfoFile(fileInfo.id);
        
        infoWriter.writeValue(file.toFile(), fileInfo);
    }






    
}
