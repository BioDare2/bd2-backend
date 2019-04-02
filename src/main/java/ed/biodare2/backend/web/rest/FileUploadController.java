/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.web.tracking.FileTracker;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/upload")
public class FileUploadController extends BioDare2Rest {
   
    final FileUploadHandler handler;
    final FileTracker tracker;
    
    @Autowired
    public FileUploadController(FileUploadHandler handler,FileTracker tracker) {
        this.handler = handler;
        this.tracker = tracker;
    }

    @RequestMapping(path = "one",method = RequestMethod.POST)
    public UploadFileInfo uploadFile(@RequestParam("file") MultipartFile fileInfo,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("Upload file: {} size: {}; {}",fileInfo.getOriginalFilename(),fileInfo.getSize(),user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to upload file");
        
        try {
        UploadFileInfo info = handler.save(fileInfo, user);
        tracker.fileUpload(info,user);
        
        return info;
        } catch(WebMappedException e) {
            log.error("Cannot upload file {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot upload file {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }
    
    
    @RequestMapping(value = "{uploadId}",method = RequestMethod.GET)
    public UploadFileInfo getFileInfo(@PathVariable String uploadId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get UploadInfo: {}; {}",uploadId,user);
        
        try {
            UploadFileInfo resp = handler.getInfo(uploadId);
            tracker.fileView(resp,user);
            return resp;
        } catch(WebMappedException e) {
            log.error("Cannot get uploaded file {} {}",uploadId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get uploaded file {} {}",uploadId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
             
        
    }
}
