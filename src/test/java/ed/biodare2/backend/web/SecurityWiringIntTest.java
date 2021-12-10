/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(SimpleRepoTestConfig.class)
public class SecurityWiringIntTest {
    
    @Autowired
    String ppaPassword;
    
    @Autowired
    String ppaUsername;

    @TestConfiguration
    public static class Configuration {

            @Bean
            @Transactional        
            public Fixtures fixtures(UserAccountRep accountsR,UserGroupRep groups,PasswordEncoder passwordEncoder) {
                return Fixtures.build(accountsR,groups,passwordEncoder);
            }
    

            /*
            @Transactional
            @Bean
            @Order(1)
            public CommandLineRunner init(Environment env,DBFixer fixer) {
            
                // System.out.println("--- TEST INIT ---");
                return (evt) -> {};
            }  */  
    }
        

    //@Autowired
    @Resource(name = "DomMapper" ) 
    ObjectMapper mapper;
    
    //@Value("${local.server.port}")
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;
    
    @Autowired
    EntityManagerFactory emf;
    
    @Autowired
    Fixtures fixtures;

    @Autowired
    UserAccountRep accounts;
    
    @Before
    public void init() {
        //template = new TestRestTemplate();
        
        /*System.out.println("Current user:\n");
        accounts.findAll().forEach( user -> {
            System.out.println(user.getLogin()+""+user.isSystem());
        });*/
    }

    String baseURL() {
        return "http://localhost:"+port+"/api";
    }
    
    String logoutURL() {
        //without api at the end
        return baseURL().substring(0,baseURL().indexOf("/api"))+"/logout";
    }



    @Test
    public void CORSWorks() throws IOException {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:3000");
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(baseURL() + "/user", HttpMethod.OPTIONS, request, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HttpHeaders rHeaders = response.getHeaders();
        assertEquals("http://localhost:3000",rHeaders.getAccessControlAllowOrigin());
        assertTrue("true",rHeaders.getAccessControlAllowCredentials());
        assertTrue(rHeaders.getAccessControlExposeHeaders().contains("x-auth-token"));
    }
    
    @Test
    public void userGivesAnonymousUserWithoutAuth() throws IOException {
            ResponseEntity<String> response = template.getForEntity(baseURL() + "/user", String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
            assertTrue(obj.get("login").contains("ANONY"));
    }
    
    @Test
    public void userGivesRightUserAfterAuth() throws IOException {
        
        template = new TestRestTemplate("demo","demo",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        System.out.println("\n\nRESP: "+response.getBody());
        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login"));
    }    
        
    
    @Test
    public void userThrowsAnouthorizedForBadCredentials() throws IOException {
        
        template = new TestRestTemplate("demo","demo",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        template = new TestRestTemplate("demo","wrong",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        //System.out.println(obj.toString());

        //assertEquals("Bad credentials",obj.get("message"));
        assertEquals("Unauthorized",obj.get("error"));
    }   
    
    @Test
    public void userLocksAccountAfterMultipleBadCredentials() throws IOException {
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        UserAccount user = em.find(UserAccount.class, fixtures.demoUser1.getId());
        user.setLocked(false);
        user.setFailedAttempts(0);
        em.getTransaction().commit();
        
        String login = user.getLogin();
        String good = "demo";
        String bad = "wrong";
        template = new TestRestTemplate(login,good,TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals(login,obj.get("login"));
        
        template = new TestRestTemplate(login,bad,TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        for (int i = 0;i<5;i++) {
            response = template.getForEntity(baseURL() + "/user", String.class);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
            //System.out.println(obj.toString());

            //assertEquals("Bad credentials",obj.get("message"));
            assertEquals("Unauthorized",obj.get("error"));
        }
        
        template = new TestRestTemplate(login,good,TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        //assertEquals("User account is locked",obj.get("message"));
        assertEquals("Unauthorized",obj.get("error"));
        
    }     
    
    @Test
    public void userDoesNotGiveWWAuthenticateWhenXRequestedWithSet() throws IOException {
        
        //that prevents popup windown in the browser
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Requested-With", "XMLHttpRequest");
        
        template = new TestRestTemplate("demo","wrong",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(baseURL() + "/user", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getHeaders().containsKey("WWW-Authenticate"));

        //System.out.println("B: "+response.getBody());
        //Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        //assertEquals("Bad credentials",obj.get("message"));
    }    
    
    
    @Test
    public void userGivesSameUserFromSession() throws IOException {
        
        HttpHeaders headers = new HttpHeaders();
        //demo:demo, base64 encoded.
        headers.add("Authorization", "Basic ZGVtbzpkZW1v");
        
        template = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(baseURL() + "/user", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login"));
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login"));
        
    }  
    
    
    @Test
    public void logoutsLogoutsFromRoot() throws IOException {
        
        HttpHeaders headers = new HttpHeaders();
        //demo:demo, base64 encoded.
        headers.add("Authorization", "Basic ZGVtbzpkZW1v");
        
        template = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(baseURL() + "/user", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login"));
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login")); 
        
        //cause logout is without api
        response = template.postForEntity(logoutURL(), "", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertTrue(obj.get("login").contains("ANONY"));        
    }    
    
    @Test
    public void logoutsLogoutsFromBasePath() throws IOException {
        
        HttpHeaders headers = new HttpHeaders();
        //demo:demo, base64 encoded.
        headers.add("Authorization", "Basic ZGVtbzpkZW1v");
        
        template = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(baseURL() + "/user", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login"));
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals("demo",obj.get("login")); 
        
        //cause logout is without api
        response = template.postForEntity(baseURL()+"/logout", "", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertTrue(obj.get("login").contains("ANONY"));        
    }    
    
    
    
    @Test
    public void serviceUserIsUnauthorizedForNormalPoints() throws IOException {
        
        //System.out.println("PPA:"+ppaPassword);
        template = new TestRestTemplate(ppaUsername,ppaPassword,TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/services/status", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        response = template.getForEntity(baseURL() + "/user", String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        //System.out.println(response.toString());
        
        // this testing stopped working with SB.2.6 as there is no body just forbidegen
        //Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        //priort to 2.6
        //assertEquals("Forbidden",obj.get("error"));
        //assertEquals("Bad credentials",obj.get("message"));
        //System.out.println(obj.toString());
        //assertEquals("Unauthorized",obj.get("message"));
    }      
    
    @Test
    public void serviceUserIsAuthorizedForServices() throws IOException {
        
        //System.out.println("PPA:"+ppaPassword);
        template = new TestRestTemplate(ppaUsername,ppaPassword,TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
        //template = new TestRestTemplate("demo","demo",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/services/status", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String,String> obj = mapper.readValue(response.getBody(), new TypeReference<Map<String,String>>() { });
        assertEquals(ppaUsername,obj.get("user"));
    }      
    
    @Test
    public void frontEndUserIsUnauthorizedForServices() throws IOException {
        
        template = new TestRestTemplate("demo","demo",TestRestTemplate.HttpClientOption.ENABLE_COOKIES);

        ResponseEntity<String> response = template.getForEntity(baseURL() + "/services/status", String.class);
        //assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }      
    
    @Test
    public void authorizationIsRequiredForServices() throws IOException {
        
        ResponseEntity<String> response = template.getForEntity(baseURL() + "/services/status", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }      
    
        /*
	@Test
	public void userEndpointProtected() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:"
				+ port + "/user", String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		String auth = response.getHeaders().getFirst("WWW-Authenticate");
		assertTrue("Wrong header: " + auth, auth.startsWith("Bearer realm=\""));
	}*/

	/*@Test
	public void authorizationRedirects() {
		ResponseEntity<String> response = template.getForEntity("http://localhost:"
				+ port + "/uaa/oauth/authorize", String.class);
		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		String location = response.getHeaders().getFirst("Location");
		assertTrue("Wrong header: " + location,
				location.startsWith("http://localhost:" + port + "/uaa/login"));
	}*/

        /*
	@Test
	public void oathTokenSucceeds() {
            //{"access_token":"8a233474-3b5c-4320-a05c-0a954009f9df","token_type":"bearer","refresh_token":"356a1845-dedc-4e86-beea-7fb5d9380d6f","expires_in":43199,"scope":"write"}

            
            ResponseEntity<String> response = callToken("demo","demo");
            assertEquals(OK,response.getStatusCode());
            String token = response.getBody();
            assertTrue(token.contains("\"access_token\":\""));
            assertTrue(token.contains("\"token_type\":\"bearer\""));

        }
        
        @Test
        public void userEndpointGivesUserMap() {

            ResponseEntity<String> response = callToken("demo","demo");
            assertEquals(OK,response.getStatusCode());
            
            String access_token = extractToken(response.getBody());
            
            String url = "http://localhost:"+ port + "/user";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Bearer " + access_token);

            HttpEntity<String> request = new HttpEntity<>(headers);                

            response = template.exchange(url, HttpMethod.GET, request, String.class);
            assertEquals(OK,response.getStatusCode());
            
            String userMap = response.getBody();
            assertTrue(userMap.contains("\"login\":\"demo\""));
            
        }
        
        protected ResponseEntity<String> callToken(String user,String password) {
            String url = "http://localhost:"+ port + "/oauth/token";
            String body = "password="+password+"&username="+user+"&grant_type=password&scope=write&client_secret=123456&client_id=biodare2-ui";
            String base64Creds = "YmlvZGFyZTItdWk6MTIzNDU2";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Basic " + base64Creds);

            HttpEntity<String> request = new HttpEntity<>(body, headers);                

            ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, request, String.class);
            return response;
        }
        
        
        @Test
        public void extractTokenWorks() {
            String resp = "{\"access_token\":\"8a233474-3b5c-4320-a05c-0a954009f9df\",\"token_type\":\"bearer\",\"refresh_token\":\"356a1845-dedc-4e86-beea-7fb5d9380d6f\",\"expires_in\":43199,\"scope\":\"write\"}";
            String exp = "8a233474-3b5c-4320-a05c-0a954009f9df";
            String token = extractToken(resp);
            assertEquals(exp,token);                    
        }
        
        protected String extractToken(String response) {
            return Arrays.stream(response.split(","))
                    .filter(e -> e.contains("access_token"))
                    .map( e-> e.replace("\"", ""))
                    .map( e-> e.substring(e.indexOf(":")+1))
                    .findFirst().get();
        }
        



	private String getCsrf(String soup) {
		Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*")
				.matcher(soup);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}*/

}