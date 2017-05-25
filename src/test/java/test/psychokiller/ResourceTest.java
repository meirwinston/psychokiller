package test.psychokiller;

import com.psychokiller.wire.messages.User;
import com.psychokiller.ws.resources.AuthResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Ignore
public class ResourceTest {
    private static final String API_URL = "/v1/auth";
    private Client client;

    @ClassRule
    public static DaoRule daoRule = new DaoRule();

    @ClassRule
    public static ResourceTestRule resourceTestRule =
            ResourceTestRule
                    .builder()
                    .addProvider(new AuthResource(daoRule.getDBI()))
                    .build();

    @Before
    public void setUp() {
        this.client = resourceTestRule.client();
    }

    @After
    public void tearDown() {
        try(Handle handle = daoRule.getDBI().open()){
            Query<Map<String,Object>> result = handle.createQuery("show tables");
            System.out.println(result.list());

        }
        daoRule.truncateTable("account");
    }

    @Test
    public void testPath() {
        Path p = AuthResource.class.getAnnotation(Path.class);
        assertEquals(API_URL, p.value());
    }

    @Test
    public void testLogin() {
        String phoneNumber = "7329999999";
        String password = "";
        User account = new User("555");


        client.target(String.format("/v1/session",phoneNumber))
              .request(MediaType.APPLICATION_JSON_TYPE)
              .post(Entity.json(account), User.class);
        System.out.println("DONE");
    }

}
