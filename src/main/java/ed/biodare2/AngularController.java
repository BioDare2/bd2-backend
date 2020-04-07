/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller needed to redirect to index.html for direct paths to angular app.
 * @author tzielins
 */
@Controller
public class AngularController {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    //matches any routes that do not ends with a file (dont contain dot in the name)
    @RequestMapping(value = "**/{path:[^\\.]*}")
    public String notStaticFiles(@PathVariable String path) {

        log.debug("AngularControler, path: {}", path);
        // Forward to home page so that route is preserved.
        return "forward:/";
    }    
}
