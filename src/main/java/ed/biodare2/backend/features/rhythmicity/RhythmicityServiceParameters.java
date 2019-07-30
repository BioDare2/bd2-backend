/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;
import java.net.URL;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityServiceParameters {
 
    JobCentreEndpointDirections directions;
    
    public URL backendURL;
    public String ppaUsername;
    public String ppaPassword; 
    public boolean testClient = true;
    
}
