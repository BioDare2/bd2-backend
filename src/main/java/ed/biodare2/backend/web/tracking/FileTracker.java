/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.tracking;

import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import org.springframework.stereotype.Service;
import static ed.biodare2.backend.web.tracking.TargetType.*;
import static ed.biodare2.backend.web.tracking.ActionType.*;
/**
 *
 * @author tzielins
 */
@Service
public class FileTracker extends AbstractTracker {

    public void fileUpload(UploadFileInfo info, BioDare2User user) {
        track(FILE,NEW,info.id,user);
    }

    public void fileView(UploadFileInfo info, BioDare2User user) {
        track(FILE,VIEW,info.id,user);
    }

    public void fileFormatedView(String fileId, String format, BioDare2User user) {
        track(FILE,VIEW,fileId,user,format);
    }

    public void fileFormatCheck(String fileId, ImportFormat format, BioDare2User user) {
        track(FILE,CHECK,fileId,user,format.name());
    }
    
}
