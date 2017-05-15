package test.psychokiller;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.rules.ExternalResource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimeZone;

public final class DaoRule extends ExternalResource {

    private volatile DataSource dataSource;
    private DBI dbi;
    private MysqldConfig mysqldConfig;

    public DaoRule() {
    }

    @Override
    protected void before() throws Throwable {
        mysqldConfig = MysqldConfig
                .aMysqldConfig(Version.v5_7_17)
                .withPort(3308)
                .withUser("test", "test")
                .withTimeZone(TimeZone.getTimeZone("UTC"))
                .build();
        EmbeddedMysql mysqld = EmbeddedMysql
                .anEmbeddedMysql(mysqldConfig)
                .addSchema("psychokiller", ScriptResolver.classPathScript("db/migration/V1__create_account.sql"))
                .start();


        HikariConfig cfg = new HikariConfig();
        cfg.setPassword("test");
        cfg.setUsername("test");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3308/psychokiller?serverTimezone=UTC");
        HikariDataSource dataSource = new HikariDataSource(cfg);
        dbi = new DBI(dataSource);
    }

    @Override
    protected void after() {
        dataSource = null;
    }

    public void truncateTable(String tableName) {
        try (Handle h = dbi.open()) {
            h.execute("TRUNCATE TABLE " + tableName);
        }
    }

    public <T> T getDao(Class<T> daoClass) {
        return dbi.onDemand(daoClass);
    }

    public DBI getDBI(){
        return dbi;
    }
}
