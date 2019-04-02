/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
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
@RequestMapping("api/scrab")
public class ScrabController {
   
    private static final String template = "Hello, %s called by %s!";
    private final AtomicLong counter = new AtomicLong();
    
    @RequestMapping(method = RequestMethod.GET)
    public Map<String,String> greeting(Principal principal,@RequestParam(required=false, defaultValue="Tomek") String name) {
        Map<String,String> data = new HashMap<>();
        data.put("greeting", String.format(template, name,principal != null ? principal.getName(): "null"));
        return data;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public Map<String,String> post(@RequestBody Map<String,String> request,Principal principal) {
        String content = request.getOrDefault("content", "empty");
        Map<String,String> data = new HashMap<>();
        data.put("greeting", content +" by: "+(principal != null ? principal.getName(): "null"));
        return data;
    }    
    
}
