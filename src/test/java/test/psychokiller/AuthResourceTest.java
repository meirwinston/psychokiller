package test.psychokiller;

import com.psychokiller.ws.resources.AuthResource;
import io.dropwizard.testing.junit.*;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import javax.ws.rs.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AuthResourceTest {
    private static final String API_URL = "/v1/auth";

    @ClassRule
    public static DaoRule daoRule = new DaoRule();

    @ClassRule
    public static ResourceTestRule resources =
            ResourceTestRule
                    .builder()
                    .addProvider(new AuthResource(daoRule.getDBI()))
                    .build();
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

}
