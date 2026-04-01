/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.IOException;
import java.util.Map;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
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
    EntityManagerFactory emf;
    
    @Autowired
    Fixtures fixtures;

    @Autowired
    UserAccountRep accounts;

    String baseURL() {
        return "http://localhost:" + port + "/api";
    }
    
    String logoutURL() {
        //without api at the end
        return baseURL().substring(0, baseURL().indexOf("/api")) + "/logout";
    }

    @Test
    public void CORSWorks() throws IOException, InterruptedException {
	java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .method("OPTIONS", java.net.http.HttpRequest.BodyPublishers.noBody())
            .header("Origin", "http://localhost:3000")
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

	assertEquals(200, response.statusCode());
	assertEquals("http://localhost:3000",
		     response.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
	assertEquals("true",
		     response.headers().firstValue("Access-Control-Allow-Credentials").orElse(null));
	assertTrue(response.headers().allValues("Access-Control-Expose-Headers").stream()
		   .anyMatch(v -> v.contains("x-auth-token")));
    }

    @Test
    public void userGivesAnonymousUserWithoutAuth() throws IOException, InterruptedException {
	java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertTrue(obj.get("login").contains("ANONY"));
    }
    
    @Test
    public void userGivesRightUserAfterAuth() throws IOException, InterruptedException {
	String basicAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));
	java.net.CookieManager cookieManager = new java.net.CookieManager();
	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));
    }

    @Test
    public void userThrowsAnouthorizedForBadCredentials() throws IOException, InterruptedException {
	java.net.CookieManager cookieManager = new java.net.CookieManager();

	String goodAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));
	java.net.http.HttpClient goodClient = java.net.http.HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

	java.net.http.HttpRequest goodRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + goodAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            goodClient.send(goodRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	String badAuth = java.util.Base64.getEncoder().encodeToString("demo:wrong".getBytes(java.nio.charset.StandardCharsets.UTF_8));
	java.net.http.HttpClient badClient = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest badRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + badAuth)
            .GET()
            .build();

	response = badClient.send(badRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(401, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("Unauthorized", obj.get("error"));
    }
    
    @Test
    public void userLocksAccountAfterMultipleBadCredentials() throws IOException, InterruptedException {
	EntityManager em = emf.createEntityManager();
	em.getTransaction().begin();
	UserAccount user = em.find(UserAccount.class, fixtures.demoUser1.getId());
	user.setLocked(false);
	user.setFailedAttempts(0);
	em.getTransaction().commit();

	String login = user.getLogin();
	String good = "demo";
	String bad = "wrong";

	String goodAuth = java.util.Base64.getEncoder().encodeToString((login + ":" + good).getBytes(java.nio.charset.StandardCharsets.UTF_8));
	java.net.http.HttpClient goodClient = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest goodRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + goodAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            goodClient.send(goodRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals(login, obj.get("login"));

	String badAuth = java.util.Base64.getEncoder().encodeToString((login + ":" + bad).getBytes(java.nio.charset.StandardCharsets.UTF_8));
	java.net.http.HttpClient badClient = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest badRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + badAuth)
            .GET()
            .build();

	for (int i = 0; i < 5; i++) {
	    response = badClient.send(badRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	    assertEquals(401, response.statusCode());
	    obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	    assertEquals("Unauthorized", obj.get("error"));
	}

	java.net.http.HttpClient lockedClient = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest lockedRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + goodAuth)
            .GET()
            .build();

	response = lockedClient.send(lockedRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(401, response.statusCode());
	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("Unauthorized", obj.get("error"));
    }
    
    @Test
    public void userDoesNotGiveWWAuthenticateWhenXRequestedWithSet() throws IOException, InterruptedException {
	String badAuth = java.util.Base64.getEncoder().encodeToString("demo:wrong".getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + badAuth)
            .header("X-Requested-With", "XMLHttpRequest")
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

	assertEquals(401, response.statusCode());
	assertTrue(response.headers().firstValue("WWW-Authenticate").isEmpty());
    }
 
    @Test
    public void userGivesSameUserFromSession() throws IOException, InterruptedException {
	java.net.CookieManager cookieManager = new java.net.CookieManager();
	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

	String basicAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpRequest firstRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(firstRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));

	java.net.http.HttpRequest secondRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .GET()
            .build();

	response = client.send(secondRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));
    }
    
    @Test
    public void logoutsLogoutsFromRoot() throws IOException, InterruptedException {
	java.net.CookieManager cookieManager = new java.net.CookieManager();
	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

	String basicAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpRequest loginRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(loginRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));

	java.net.http.HttpRequest userRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .GET()
            .build();

	response = client.send(userRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());
	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));

	java.net.http.HttpRequest logoutRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(logoutURL()))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(""))
            .header("Content-Type", "text/plain")
            .build();

	response = client.send(logoutRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	response = client.send(userRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());
	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertTrue(obj.get("login").contains("ANONY"));
    }
    
    @Test
    public void logoutsLogoutsFromBasePath() throws IOException, InterruptedException {
	java.net.CookieManager cookieManager = new java.net.CookieManager();
	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

	String basicAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpRequest loginRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(loginRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));

	java.net.http.HttpRequest userRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .GET()
            .build();

	response = client.send(userRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());
	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals("demo", obj.get("login"));

	java.net.http.HttpRequest logoutRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/logout"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(""))
            .header("Content-Type", "text/plain")
            .build();

	response = client.send(logoutRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	response = client.send(userRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());
	obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertTrue(obj.get("login").contains("ANONY"));
    }
    
    @Test
    public void serviceUserIsUnauthorizedForNormalPoints() throws IOException, InterruptedException {
	String basicAuth = java.util.Base64.getEncoder().encodeToString((ppaUsername + ":" + ppaPassword)
									.getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest serviceRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/services/status"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(serviceRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	java.net.http.HttpRequest userRequest = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/user"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	response = client.send(userRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(403, response.statusCode());
    }
    
    @Test
    public void serviceUserIsAuthorizedForServices() throws IOException, InterruptedException {
	String basicAuth = java.util.Base64.getEncoder().encodeToString((ppaUsername + ":" + ppaPassword)
									.getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/services/status"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(200, response.statusCode());

	Map<String, String> obj = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() { });
	assertEquals(ppaUsername, obj.get("user"));
    }
    
    @Test
    public void frontEndUserIsUnauthorizedForServices() throws IOException, InterruptedException {
	String basicAuth = java.util.Base64.getEncoder().encodeToString("demo:demo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

	java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .cookieHandler(new java.net.CookieManager())
            .build();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/services/status"))
            .header("Authorization", "Basic " + basicAuth)
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(403, response.statusCode());
    }
    
    @Test
    public void authorizationIsRequiredForServices() throws IOException, InterruptedException {
	java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

	java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(baseURL() + "/services/status"))
            .GET()
            .build();

	java.net.http.HttpResponse<String> response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
	assertEquals(403, response.statusCode());
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
