package ed.biodare2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SimpleRepoTestConfig.class, TestConfig.class})
public class BioDare2WSApplicationTest {

    @Autowired
    Environment env;

    @LocalServerPort
    int port;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testConfigurationFileShouldBeLoaded() {
        assertNotNull(env);
        assertTrue(
                env.getProperty("bd2.storage.dir", "MISSING").endsWith("test"),
                "Should end with test, got: " + env.getProperty("bd2.storage.dir", "MISSING")
        );
    }

    @Test
    public void CORSWorks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/user"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://localhost:3000")
                .build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals(200, response.statusCode());
        assertEquals(
                "http://localhost:3000",
                response.headers().firstValue("Access-Control-Allow-Origin").orElse(null)
        );
        assertEquals(
                "true",
                response.headers().firstValue("Access-Control-Allow-Credentials").orElse(null)
        );
        assertTrue(
                response.headers().allValues("Access-Control-Expose-Headers").stream()
                        .anyMatch(v -> v.contains("x-auth-token"))
        );
    }
}
