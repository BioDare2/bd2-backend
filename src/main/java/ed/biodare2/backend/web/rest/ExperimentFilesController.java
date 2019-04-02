/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.handlers.ExperimentFilesHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("api/experiment/{expId}/file")
public class ExperimentFilesController extends ExperimentController {

    final ExperimentFilesHandler filesHandler;    
    
    @Autowired
    public ExperimentFilesController(ExperimentHandler handler,ExperimentFilesHandler filesHandler,
            PermissionsResolver permissionsResolver,ExperimentTracker tracker) {        
        super(handler,permissionsResolver,tracker);
        this.filesHandler = filesHandler;
    }
    
    
    @RequestMapping(method = RequestMethod.POST)    
    public ListWrapper<FileAsset> attachFiles(@PathVariable long expId,@RequestParam("file") List<MultipartFile> fileInfos,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("new files; exp:{} {}; {}",expId,fileInfos.size(),user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to attach files");
        
        AssayPack exp = getExperimentForWrite(expId,user);

        try {
        ListWrapper<FileAsset> resp = new ListWrapper<>(filesHandler.uploadFiles(exp, fileInfos, user));
        resp.data.forEach( file -> {
            tracker.fileNew(exp,file,user);
        });
        return resp;
        } catch(WebMappedException e) {
            log.error("Cannot attach files {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot attach files {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    @RequestMapping(method = RequestMethod.GET)    
    public ListWrapper<FileAsset> getFiles(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get files; exp:{}; {}",expId,user);
        
        AssayPack exp = getExperimentForRead(expId,user);
        

        try {
        ListWrapper<FileAsset> resp = new ListWrapper<>(filesHandler.getFiles(exp));
        tracker.fileList(exp,user);
        return resp;
        } catch(WebMappedException e) {
            log.error("Cannot get files {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get files {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }     
    
    @RequestMapping(value = "{fileId}", method = RequestMethod.GET)
    public void getFile(@PathVariable long expId,@PathVariable long fileId, 
            @NotNull @AuthenticationPrincipal BioDare2User user,HttpServletResponse response) {
        
        log.debug("get file:{} exp:{}; {}",fileId,expId,user);
        
        AssayPack exp = getExperimentForRead(expId,user);

        try {
        AssetVersion asset = filesHandler.getFile(exp, fileId)
                .orElseThrow(() -> new NotFoundException("Files asset: "+fileId+" in exp: "+expId))
                .last();
        
        sendAsset(asset, exp, response);
        tracker.fileDownload(exp,fileId,asset,user);
        } catch(WebMappedException e) {
            log.error("Cannot get file {} {} {}",expId,fileId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get file {} {} {}",expId,fileId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }
    
    @RequestMapping(value = "{fileId}/{versionId}", method = RequestMethod.GET)
    public void getFile(@PathVariable long expId,@PathVariable long fileId,@PathVariable long versionId, 
            @NotNull @AuthenticationPrincipal BioDare2User user,HttpServletResponse response) {
        
        log.debug("get file:{} v:{} exp:{}; {}",fileId,versionId,expId,user);
        
        AssayPack exp = getExperimentForRead(expId,user);

        try {
        AssetVersion asset = filesHandler.getFileVersion(exp,fileId,versionId)
                .orElseThrow(() -> new NotFoundException("Files asset: "+fileId+":"+versionId+" in exp: "+expId));
        
        sendAsset(asset, exp, response);
        tracker.fileDownload(exp,fileId,asset,user);
        } catch(WebMappedException e) {
            log.error("Cannot get file version {} {} {}",expId,fileId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get file version {} {} {}",expId,fileId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }   
    
    
    
    protected void sendAsset(AssetVersion asset, AssayPack exp, HttpServletResponse response) {
     
        Path assetFile = filesHandler.getFilePath(exp,asset);
        if (!Files.isRegularFile(assetFile))
            throw new ServerSideException("Asset file: "+assetFile+" does not exists in exp: "+exp.getId());
        
        sendFile(assetFile, asset.originalName, asset.contentType, false, response);
        
    }
    
}
