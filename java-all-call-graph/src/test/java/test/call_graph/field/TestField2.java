package test.call_graph.field;

import com.adrninistrator.jacg.common.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.extensions.enums.DbStatementEnum;

/**
 * @author adrninistrator
 * @date 2022/9/17
 * @description:
 */
public class TestField2 {
    public String data;

    public static String STRING1 = "aaa";

    public static TestField2 INSTANCE = new TestField2();

    public void test1() {
        ConfigKeyEnum configKeyEnum;
        int i = (int) System.currentTimeMillis() % 10;
        if (i == 1) {
            configKeyEnum = ConfigKeyEnum.CKE_THREAD_NUM;
            System.out.println(configKeyEnum);
        } else if (i == 2) {
            configKeyEnum = ConfigKeyEnum.CKE_DB_H2_FILE_PATH;
        } else {
            configKeyEnum = ConfigKeyEnum.CKE_DB_USERNAME;
        }

        System.out.println(configKeyEnum);
        System.out.println(configKeyEnum.getKey());
    }

    public void test2() {
        DbStatementEnum dbStatementEnum = DbStatementEnum.DSE_DELETE;
        System.out.println(dbStatementEnum.getStatement());
        System.out.println(dbStatementEnum);

        System.out.println(DbStatementEnum.DSE_INSERT.getStatement());
        System.out.println(DbStatementEnum.DSE_INSERT);
    }
}
