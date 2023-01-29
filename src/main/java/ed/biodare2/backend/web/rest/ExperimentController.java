/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.features.search.SortOption;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;

import ed.biodare2.backend.repo.ui_dom.exp.ExperimentSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api")
public class ExperimentController extends BioDare2Rest {

    final ExperimentHandler handler;
    final PermissionsResolver permissionsResolver;
    final ExperimentTracker tracker;
            
    @Autowired
    public ExperimentController(ExperimentHandler handler,PermissionsResolver permissionsResolver,ExperimentTracker tracker) {        
        this.handler = handler;
        this.permissionsResolver = permissionsResolver;
        this.tracker = tracker;
    }
    
    @RequestMapping(path="experiments",method = RequestMethod.GET)
    @Transactional    
    public ListWrapper<ExperimentSummary> getExperiments(
            @RequestParam(name = "showPublic",defaultValue = "false") boolean showPublic, 
            @RequestParam(name="pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name="pageSize", defaultValue = "25") int pageSize,            
            @RequestParam(name="sorting", defaultValue = "modified") String sorting,
            @RequestParam(name="direction", defaultValue = "") String direction,            
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        
        log.debug("get experiments, public:{}; {} dir:{} sort:{}",showPublic,user, direction, sorting);

        try {
          
            Page page = paramsToPage(pageIndex, pageSize);

            SortOption sort = paramsToSort(sorting, direction);
            boolean ascending = "asc".equals(direction);
            
            ListWrapper<ExperimentalAssay> exps = handler.listExperiments(user, showPublic, sort, ascending, page);
            page = exps.currentPage;
            
            List<ExperimentSummary> sums = exps.data.stream()                            
                            .map( exp -> new ExperimentSummary(exp))
                            .collect(Collectors.toList());

            tracker.experimentList(user);
        
            return new ListWrapper(sums, page);
                
            
        } catch(WebMappedException e) {
            log.error("Cannot retrieve experiments {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve experiments {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }    
    
    @RequestMapping(path="experiments/search",method = RequestMethod.GET)
    @Transactional    
    public ListWrapper<ExperimentSummary> searchExperiments(
            @RequestParam(name = "query",defaultValue = "") String query, 
            @RequestParam(name = "showPublic",defaultValue = "false") boolean showPublic, 
            @RequestParam(name="pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name="pageSize", defaultValue = "25") int pageSize,            
            @RequestParam(name="sorting", defaultValue = "modified") String sorting,
            @RequestParam(name="direction", defaultValue = "") String direction,            
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        
        log.debug("search experiments, query:{}; {}",query,user);

        try {
          
            Page page = paramsToPage(pageIndex, pageSize);

            SortOption sort = paramsToSort(sorting, direction);
            boolean ascending = "asc".equals(direction);
            
            ListWrapper<ExperimentalAssay> exps = handler.searchExperiments(query, user, showPublic, sort, ascending, page);
            page = exps.currentPage;
            
            List<ExperimentSummary> sums = exps.data.stream()
                            .map( exp -> new ExperimentSummary(exp))
                            .collect(Collectors.toList());

            tracker.experimentSearch(query, user);
        
            return new ListWrapper(sums, page);
                
            
        } catch(WebMappedException e) {
            log.error("Cannot retrieve experiments {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve experiments {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }    
    
    
    @RequestMapping(path="experiment/draft",method = RequestMethod.GET)
    public ExperimentalAssayView newDraft(@NotNull @AuthenticationPrincipal BioDare2User user) {
        
        log.debug("new exp draft; {}",user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to create new experiment");
        
        try {
        ExperimentalAssayView exp = handler.newDraft(user);
        
        tracker.experimentDraft(user);
        
        return exp;
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot make draft {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot make draft {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    }
    
    private AssayPack getExperimentPack(long expId) {
        return handler.getExperiment(expId)
                .orElseThrow(()-> new NotFoundException("Experiment "+expId+" not found"));
    }
    
    protected AssayPack getExperimentForRead(long expId,BioDare2User user) throws NotFoundException, InsufficientRightsException {
        AssayPack exp = getExperimentPack(expId);        
        verifyCanRead(user,exp);
        return exp;
    }

    protected AssayPack getExperimentForWrite(long expId,BioDare2User user) throws NotFoundException, InsufficientRightsException {
        AssayPack exp = getExperimentPack(expId);        
        verifyCanWrite(user,exp);
        return exp;
    }    
    
    
    @RequestMapping(value = "experiment/{expId}",method = RequestMethod.GET)
    public ExperimentalAssayView getExperiment(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get experiment: {}; {}",expId,user);
        
        try {
        AssayPack exp = getExperimentForRead(expId,user);        
        
        tracker.experimentView(exp,user);
        
        return handler.assayToView(exp, user);
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot retrieve experiment {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve experiment {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    } 

    @RequestMapping(value = "experiment",method = RequestMethod.PUT)
    public ExperimentalAssayView insertExperiment(@RequestBody ExperimentalAssayView expDesc,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("new experiment: {}; {}",expDesc.generalDesc.name,user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to create new experiment");
        
        try {
        ExperimentalAssayView view = handler.insert(expDesc, user);
        tracker.experimentNew(view.id,user);
        return view;
        
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot insert experiment {} {}",expDesc.id,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot insert experiment {} {}",expDesc.id,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    }     
    
    @RequestMapping(value = "experiment/bd1-import",method = RequestMethod.PUT)
    public ExperimentalAssayView importBD1Experiment(@RequestBody @NotNull ExperimentalAssay expDesc,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("import BD1 experiment: {}; {}",expDesc.generalDesc.name,user);
        
        if (true)
            throw new UnsupportedOperationException("Import not supported at the moment");
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to import experiment experiment");
        
        if (false) {
            return new ExperimentalAssayView(expDesc);
        }
        
        try {
        ExperimentalAssayView view = handler.importBD1(expDesc, user);
        tracker.experimentImport(view.id,user);
        return view;
        
        } catch(WebMappedException e) {
            log.error("Cannot import BD1 experiment {} {}",expDesc.getId(),e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot import BD1 experiment {} {}",expDesc.getId(),e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    }     
    
    @RequestMapping(value = "experiment/{expId}",method = RequestMethod.POST)
    //@Transactional
    public ExperimentalAssayView updateExperiment(@PathVariable long expId,@RequestBody ExperimentalAssayView expDesc,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("update experiment: {}; {}",expId,user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to create new experiment");
        
        AssayPack exp = getExperimentForWrite(expId,user);        
        
        try {
        ExperimentalAssayView view = handler.update(exp,expDesc,user);
        tracker.experimentUpdate(exp,user);
        return view;
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot update experiment {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot update experiment {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    }     
    
    @RequestMapping(value = "experiment/{expId}/publish",method = RequestMethod.PUT)
    public ExperimentalAssayView publishExperiment(@PathVariable long expId,@RequestBody @NotNull OpenAccessLicence licence, @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("publish experiment: {}; {}",expId,user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Login to publish experiment");
        
        AssayPack exp = getExperimentForWrite(expId,user); 
        
        verifyIsOwner(user, exp);
        
        try {
        ExperimentalAssayView view = handler.publish(exp,licence,user);
        tracker.experimentPublish(exp,user);
        return view;
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot publish experiment {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot publish experiment {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
    }    
    
    
    protected void verifyCanRead(BioDare2User user,AssayPack exp) {
        if (! permissionsResolver.canRead(exp.getACL(), user))
            throw new InsufficientRightsException("Cannot read experiment: "+exp.getId());
    }
    
    protected void verifyCanWrite(BioDare2User user,AssayPack exp) {
        if (! permissionsResolver.canWrite(exp.getACL(), user))
            throw new InsufficientRightsException("Cannot write experiment: "+exp.getId());
    }

    protected void verifyIsOwner(BioDare2User user,AssayPack exp) {
        if (! permissionsResolver.isOwner(exp.getACL(), user))
            throw new InsufficientRightsException("Only allowed for owner of: "+exp.getId());
    }

    static Page paramsToPage(int pageIndex, int pageSize) {
        
        pageSize = Math.min(pageSize, 1000);
        pageSize = Math.max(pageSize, 1);
        
        pageIndex = pageIndex < 0 ? 0 : pageIndex;
        
        return new Page(pageIndex, pageSize);         
    }

    static SortOption paramsToSort(String sorting, String direction) {
        
        if (direction == null || direction.isBlank()) return SortOption.RANK;
        
        switch(sorting) {
            case "author": return SortOption.FIRST_AUTHOR;
            case "modified": return SortOption.MODIFICATION_DATE;
            case "executed": return SortOption.EXECUTION_DATE;
            case "uploaded": return SortOption.UPLOAD_DATE;
            default: return SortOption.valueOf(sorting.toUpperCase());
        }
        
    }
    
}
