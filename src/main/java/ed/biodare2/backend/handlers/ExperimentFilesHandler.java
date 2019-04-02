/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.FileAssetRep;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentFilesHandler extends BaseExperimentHandler {
    
    final ExperimentPackHub experiments;    
    final FileAssetRep fileAssets;
    final FileUploadHandler uploadHandler;

    public ExperimentFilesHandler(ExperimentPackHub experiments, 
            FileAssetRep fileAssets, 
            FileUploadHandler uploadHandler
            ) {
        this.experiments = experiments;
        this.fileAssets = fileAssets;
        this.uploadHandler = uploadHandler;
    }
    
    @Transactional
    public List<FileAsset> uploadFiles(AssayPack exp,List<MultipartFile> filesUpload, BioDare2User user) {
        
        List<UploadFileInfo> uploads = filesUpload.stream()
                            .map( file -> uploadHandler.save(file, user))
                            .collect(Collectors.toList());
        
        List<FileAsset> assets = fileAssets.storeFileUploads(uploads, AssetType.FILE, exp, user);
        
        exp = experiments.enableWriting(exp);
        registerFilesUpload(exp,assets,user);
        copySystemFeatures(exp.getSystemInfo(),exp.getAssay());        
        exp = experiments.save(exp);        
        
        return assets;
    }
    
    public List<FileAsset> getFiles(AssayPack exp) {
        
        return fileAssets.getAssets(exp)
                .collect(Collectors.toList());
    }
    
    
    public Optional<FileAsset> getFile(AssayPack exp,long fileId) {
    
        return fileAssets.getAsset(exp,fileId);
    }
    
    public Optional<AssetVersion> getFileVersion(AssayPack exp,long fileId, long versionId) {
        
        return fileAssets.getAsset(exp,fileId)
                .flatMap( f -> f.versions.stream()
                                    .filter( v -> v.versionId == versionId)
                                    .findFirst()                
                );
    }

    public Path getFilePath(AssayPack exp,AssetVersion asset) {
        
        return fileAssets.getFile(asset,exp);
    }    
    
    protected AssayPack registerFilesUpload(AssayPack exp, List<FileAsset> assets, BioDare2User user) {
        
        SystemInfo systemInfo = exp.getSystemInfo();
        
        systemInfo.currentDataVersion++;
        systemInfo.experimentCharacteristic.hasAttachments = true;
        systemInfo.experimentCharacteristic.attachmentsSize = fileAssets.attachmentsSize(exp);
        
        updateProvenance(systemInfo.provenance, user, OperationType.FILE_UPLOAD, systemInfo.getVersionId());
        
        return exp;
    }



    
    
}
