/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.assets.FileAssets;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class FileAssetRep {
 
    final static String ASSETS_DIR = "ASSETS";
    final static String FILES_DIR = "FILES";
    final static String ASSETS_INFO_FILE = "FILES_INFO.json";
    
    final IdGenerator assetsIdGenerator;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    final ObjectReader assetsReader;
    final ObjectWriter assetsWriter;    
    final ExperimentsStorage expStorage;
    final FileUploadHandler uploads;
    
    final ResourceGuard<Long> guard = new ResourceGuard<>(100);
    final FileUtil fileUtil = new FileUtil();
    

    
    @Autowired
    public FileAssetRep(
            @Qualifier("AssetsIdProvider") IdGenerator assetsIdGenerator,
            ExperimentsStorage expStorage,
            FileUploadHandler uploads,
            @Qualifier("DomMapper") ObjectMapper mapper) {
        this.expStorage = expStorage;
        this.uploads = uploads;
        

        this.assetsReader = mapper.readerFor(FileAssets.class);
        this.assetsWriter = mapper.writerFor(FileAssets.class); 
        this.assetsIdGenerator = assetsIdGenerator;
    }
    
    public long lastId() {
        
        try(Stream<Path> files =  Files.list(expStorage.getExperimentsDir())) {
            return files
             .map( p -> p.resolve(ASSETS_DIR))
             .filter(p -> Files.isDirectory(p))
             .map( p -> getAssetsInfo(p))
             .flatMap(assets -> assets.stream())
             .mapToLong(asset -> asset.id)
             .max().orElse(0);
        
        } catch (IOException e) {
            throw new ServerSideException(e.getMessage(),e);
        }
         
    }    
    
    public FileAsset storeFileUpload(String uploadId, String assetName,AssetType type, AssayPack exp, BioDare2User user) {
        
        UploadFileInfo info = uploads.getInfo(uploadId);
        Map<String,UploadFileInfo> map = new HashMap<>();
        map.put(assetName, info);
        return storeFileUploads(map,type,exp,user).get(0);

    }
    
    public List<FileAsset> storeFileUploads(List<UploadFileInfo> files, AssetType type, AssayPack exp, BioDare2User user) {
        
        return storeFileUploads(files.stream().collect(Collectors.toMap( f->f.originalFileName,f->f)),
                type,
                exp,
                user);
                
    }    
    
    
    public List<FileAsset> storeFileUploads(Map<String,UploadFileInfo> files, AssetType type, AssayPack exp, BioDare2User user) {
        
        Path assetsDir = getAssetsDir(exp.getId());        
        Path storageDir = assetsDir.resolve(FILES_DIR);
        
        return guard.guard(exp.getId(), (id)-> {

            FileAssets assets = getAssetsInfo(assetsDir);
            List<FileAsset> records = new ArrayList<>();

            for (Map.Entry<String,UploadFileInfo> entry: files.entrySet()) {
                
                UploadFileInfo fileInfo = entry.getValue();
                String assetName = entry.getKey();
                String originalName = fileInfo.originalFileName;

                Path storageFile = makeUniqueName(storageDir,assetName);    
                try {
                    Files.copy(uploads.get(fileInfo.id, user), storageFile);
                } catch (IOException e) {
                    throw new ServerSideException("Cannot save upload: "+e.getMessage(),e);
                }

                FileAsset assetDsc = assets.findByNameAndType(assetName,type)
                    .orElseGet(()-> {
                        //it has to be orElseGet so generator is not called otherwise
                        return new FileAsset(assetsIdGenerator.next(),assetName,originalName,type);
                    });

                //assetDsc.assetType = type;
                assetDsc.add(storageFile.getFileName().toString(),originalName, fileInfo.contentType);

                assets.set(assetDsc);

                records.add(assetDsc);
            };
            saveAssetsInfo(assets,assetsDir);
            return records;
        });
    }    
    
    public Path getFile(AssetVersion asset,AssayPack exp) {

        Path assetsDir = getAssetsDir(exp.getId());        
        Path storageDir = assetsDir.resolve(FILES_DIR);
        
        Path file = storageDir.resolve(asset.localFile);
        return file;
    }
    
    
    public Optional<FileAsset> getAsset(AssayPack exp, long fileId) {
        
        return getAssets(exp)
                .filter( f -> f.id == fileId)
                .findFirst();                
    }
    
    //one of usage
    @Deprecated
    private FileAssets fixAssetsInfo(long expId,String tsDataName) {
         Path assetsDir = getAssetsDir(expId);
         
         return guard.guard(expId,(id)-> {
            FileAssets assets = getAssetsInfo(assetsDir);
         
            FileAssets fixed = fixAssets(assets,tsDataName);
            saveAssetsInfo(fixed, assetsDir);
            return fixed;
         });        
    }
    
    //one of usage
    @Deprecated
    protected FileAssets fixAssets(FileAssets old,String tsDataName) {
        

        FileAssets fixed = new FileAssets();
        
        List<FileAsset> tsFiles = old.stream().filter( fa -> fa.assetType.equals(AssetType.TS_DATA))
                .sorted(Comparator.comparing(fa -> fa.last().created))
                .collect(Collectors.toList());
        
        List<AssetVersion> tsVersions = tsFiles.stream()
                .flatMap( fa -> fa.versions.stream())
                .sorted(Comparator.comparing(va -> va.created))
                .collect(Collectors.toList());

        if (!tsFiles.isEmpty()) {
            FileAsset last = tsFiles.get(tsFiles.size()-1);
            FileAsset tsData = new FileAsset(last.id, tsDataName, last.originalName, AssetType.TS_DATA);
            for (AssetVersion ver: tsVersions) {
                ver.versionId = 0;
                tsData.add(ver);
            }        
            
            fixed.set(tsData);
            
        }
        
        old.stream().filter( fa -> !fa.assetType.equals(AssetType.TS_DATA))
                .forEach( fa -> fixed.set(fa));
        
        fixed.stream().filter( fa -> fa.assetName == null)
                .forEach( fa -> fa.assetName = fa.originalName);
        
        return fixed;
    }
    
    public Stream<FileAsset> getAssets(AssayPack exp) {
         Path assetsDir = getAssetsDir(exp.getId());
         
         return guard.guard(exp.getId(),(id)-> {
            FileAssets assets = getAssetsInfo(assetsDir);
         
            return assets.stream();
         });
    }
    
    public Stream<FileAsset> getAssets(AssayPack exp,AssetType type) {
         return getAssets(exp).filter( as -> as.assetType.equals(type));
    }    
    
    public long attachmentsSize(AssayPack exp) {
        
        return getAssets(exp)
                .filter( asset -> !AssetType.TS_DATA.equals(asset.assetType))
                .count();
    }    
    
    /*
    public void storeTSUpload(String fileId, TSImportParameters importParameters, AssayPack exp, BioDare2User user,boolean cleanExisting) {
        
        Path assetsDir = getAssetsDir(exp.getId());        
        Path storageDir = assetsDir.resolve(FILES_DIR);

        UploadFileInfo info = uploads.getInfo(fileId);
        final String originalName = info.originalFileName;
        
        FileAssets assets = getAssetsInfo(assetsDir);
        
        Path storageFile = makeUniqueName(storageDir,originalName);    
        try {
            Files.copy(uploads.get(fileId, user), storageFile);
        } catch (IOException e) {
            throw new ServerSideException("Cannot save upload: "+e.getMessage(),e);
        }
            
                
        FileAsset assetDsc = assets.tsFiles.getOrDefault(originalName,new FileAsset(originalName));
        assetDsc.add(storageFile.getFileName().toString(), info.contentType, importParameters);
        
        if (cleanExisting)
            assets.tsFiles.clear();

        assets.tsFiles.put(originalName, assetDsc);

        saveAssetsInfo(assets,assetsDir);
    }*/
    
    protected Path getAssetsDir(long expId)  {
        try {
        Path p = expStorage.getExperimentDir(expId).resolve(ASSETS_DIR);
        //if (!Files.isDirectory(p))
        //    Files.createDirectory(p);
        if (!Files.isDirectory(p.resolve(FILES_DIR)))
            Files.createDirectories(p.resolve(FILES_DIR));
        return p;
        } catch (IOException e) {
            throw new ServerSideException("Cannot access assets dir: "+e.getMessage(),e);
        }
    }

    protected Path getAssetsInfoFile(Path assetsDir) {
        return assetsDir.resolve(ASSETS_INFO_FILE);
    }
    
    protected FileAssets getAssetsInfo(Path assetsDir) {
        try {
            Path file = getAssetsInfoFile(assetsDir);
            if (!Files.exists(file)) return new FileAssets();

            return assetsReader.readValue(file.toFile());
        } catch (IOException e) {
            throw new ServerSideException("Cannot get assets info: "+e.getMessage(),e);
        }
            
    }


    protected void saveAssetsInfo(FileAssets assets,Path assetsDir) {
        try {
            Path file = getAssetsInfoFile(assetsDir);
            if (Files.exists(file))
                fileUtil.backup(file,assetsDir);
            
            assetsWriter.writeValue(file.toFile(), assets);
        } catch (IOException e) {
            throw new ServerSideException("Cannot save assets: "+e.getMessage(),e);
        }
    }

    protected Path makeUniqueName(Path storageDir, String originalName) {
        
        for (int i = 1;i<1000;i++) {
            Path candidate = storageDir.resolve(i+"_"+originalName);
            if (!Files.exists(candidate)) return candidate;
        }
        throw new IllegalStateException("Iteration limit reached when looking for unique name for file: "+originalName+" in: "+storageDir);
    }








    
}
