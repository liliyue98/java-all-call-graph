package com.adrninistrator.jacg.extensions.code_parser;

import com.adrninistrator.jacg.extensions.dto.db_operate.DbOperateData;
import com.adrninistrator.jacg.extensions.enums.DbStatementEnum;
import com.adrninistrator.jacg.extensions.util.JsonUtil;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.jacg.xml.NoOpEntityResolver;
import com.adrninistrator.javacg.dto.counter.CallIdCounter;
import com.adrninistrator.javacg.dto.method.MethodCallDto;
import com.adrninistrator.javacg.extensions.code_parser.AbstractCustomCodeParser;
import com.adrninistrator.javacg.extensions.dto.ExtendedData;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author adrninistrator
 * @date 2021/8/25
 * @description: 从MyBatis的XML文件获取对应的数据库操作语句及被操作的数据库表名，基类
 */
public abstract class AbstractGetMybatisSqlInfoCodeParser extends AbstractCustomCodeParser {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGetMybatisSqlInfoCodeParser.class);

    public static final String DATA_TYPE = "MB_SQL";

    // 保存简单处理后的SQL语句
    private final Map<String, Map<String, String>> mapperSqlMap = new HashMap<>(200);

    // 保存保存经过解析后的SQL语句对应的数据库操作，及对应的数据库表
    private final Map<String, Map<String, DbOperateData>> mapperDbOperateMap = new HashMap<>(200);

    @Override
    public void init() {
        extendedDataList = new ArrayList<>(1000);
    }

    @Override
    public void handleJarEntryFile(JarFile jarFile, JarEntry jarEntry) {
        if (!StringUtils.endsWithIgnoreCase(jarEntry.getName(), ".xml")) {
            return;
        }

        try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
            // 获取Mybatis的XML中的SQL语句
            getMybatisXmlSql(inputStream, jarEntry.getName());
        } catch (Exception e) {
            logger.error("error ", e);
        }
    }

    /**
     * 判断当前被调用的类名及方法名是否需要处理
     *
     * @param calleeClassName  被调用的类名
     * @param calleeMethodName 被调用的方法名
     * @return true: 需要处理； false: 不需要处理
     */
    protected abstract boolean checkClassNameAndMethod(String calleeClassName, String calleeMethodName);

    @Override
    public void handleMethodCall(CallIdCounter callIdCounter,
                                 String calleeClassName,
                                 String calleeMethodName,
                                 Type[] arguments,
                                 InstructionHandle mcIh,
                                 MethodGen methodGen,
                                 List<MethodCallDto> methodCalls) {
        if (!checkClassNameAndMethod(calleeClassName, calleeMethodName)) {
            return;
        }

        Map<String, DbOperateData> dbOperateDataMap = mapperDbOperateMap.computeIfAbsent(calleeClassName, k -> new HashMap<>());
        DbOperateData dbOperateData = dbOperateDataMap.get(calleeMethodName);
        if (dbOperateData != null) {
            // 当前被调用的Mapper的方法已被记录
            ExtendedData extendedData = ExtendedData.genExtendedData(callIdCounter.getCurrentCallId(), getDataType(), JsonUtil.getJsonStr(dbOperateData));
            extendedDataList.add(extendedData);
            return;
        }

        // 获取当前被调用的Mapper的方法对应的数据库操作
        Map<String, String> sqlMap = mapperSqlMap.get(calleeClassName);
        if (JACGUtil.isMapEmpty(sqlMap)) {
            logger.error("### 未查找到对应的mapper {}", calleeClassName);
            return;
        }

        String sql = sqlMap.get(calleeMethodName);
        if (sql == null) {
            logger.error("### 未查找到对应的sql语句 {} {}", calleeClassName, calleeMethodName);
            return;
        }

        DbOperateData dbOperateDataNew = getDbOperateData(sql);
        dbOperateDataNew.setSimpleClassName(JACGUtil.getSimpleClassNameFromFull(calleeClassName));
        dbOperateDataNew.setMethodName(calleeMethodName);

        // 记录当前被调用的Mapper的方法
        dbOperateDataMap.put(calleeMethodName, dbOperateDataNew);

        ExtendedData extendedData = ExtendedData.genExtendedData(callIdCounter.getCurrentCallId(), getDataType(), JsonUtil.getJsonStr(dbOperateDataNew));
        extendedDataList.add(extendedData);
    }

    @Override
    public String getDataType() {
        return DATA_TYPE;
    }

    // 获取Mybatis的XML中的SQL语句
    private void getMybatisXmlSql(InputStream inputStream, String filePath) throws DocumentException {
        SAXReader reader = new SAXReader();
        // 不读取DTD
        reader.setEntityResolver(new NoOpEntityResolver());

        Document document = reader.read(inputStream);

        Element root = document.getRootElement();
        if (!"mapper".equals(root.getName())) {
            logger.info("跳过非Mybatis XML 1: {}", filePath);
            return;
        }

        String namespace = root.attributeValue("namespace");
        if (StringUtils.isBlank(namespace)) {
            logger.info("跳过非Mybatis XML 2: {}", filePath);
            return;
        }

        // 以上用于跳过非Mybatis mapper的XML文件
        logger.info("开始处理Mybatis XML: {}", filePath);

        Map<String, String> sqlMap = new HashMap<>();
        for (Iterator it = root.elementIterator(); it.hasNext(); ) {
            Element element = (Element) it.next();
            if (StringUtils.equalsAny(element.getName(), "select", "insert", "update", "delete")) {
                // 处理SQL语句
                String sqlId = element.attributeValue("id");
                // 获取一个SQL Element中的SQL语句
                StringBuilder stringBuilder = new StringBuilder();
                getElementSql(element, stringBuilder);

                // 对sql语句进行格式化
                String formattedSql = formatSql(stringBuilder.toString());
                sqlMap.put(sqlId, formattedSql);
            }
        }

        mapperSqlMap.put(namespace, sqlMap);
    }

    // 获取一个SQL Element中的SQL语句
    private void getElementSql(Element element, StringBuilder stringBuilder) {
        List contentList = element.content();
        for (Object content : contentList) {
            if (content instanceof DefaultText) {
                // 处理一个SQL Element中的文本
                DefaultText defaultText = (DefaultText) content;
                String text = defaultText.getText();
                addData(stringBuilder, text);
            } else if (content instanceof DefaultElement) {
                // 处理一个SQL Element中的Element
                DefaultElement defaultElement = (DefaultElement) content;
                String elementName = defaultElement.getName();
                if (StringUtils.equalsAny(elementName, "foreach", "if", "choose", "when", "otherwise")) {
                    getElementSql(defaultElement, stringBuilder);
                } else if (StringUtils.equalsAny(elementName, "where", "set")) {
                    addData(stringBuilder, elementName);
                }

                List attributeList = defaultElement.attributes();
                for (Object attribute : attributeList) {
                    if (attribute instanceof DefaultAttribute) {
                        // 处理一个SQL Element中的Attribute
                        DefaultAttribute defaultAttribute = (DefaultAttribute) attribute;
                        String attributeValue = defaultAttribute.getValue();
                        if (StringUtils.equals(defaultAttribute.getName(), "prefix")) {
                            addData(stringBuilder, attributeValue);
                        }
                    }
                }
            } else if (content instanceof DefaultCDATA) {
                // 处理一个SQL Element中的文本
                DefaultCDATA defaultCDATA = (DefaultCDATA) content;
                String text = defaultCDATA.getText();
                addData(stringBuilder, text);
            }
        }
    }

    private void addData(StringBuilder stringBuilder, String data) {
        stringBuilder.append(data);
    }

    /**
     * 解析SQL语句，获得对应的操作，及相关数据库表
     *
     * @param sql
     * @return
     */
    private DbOperateData getDbOperateData(String sql) {
        DbOperateData dbOperateData = new DbOperateData();

        // 获取数据库操作语句
        DbStatementEnum dbStatementEnum = getSqlStatement(sql);
        if (dbStatementEnum == null) {
            dbOperateData.setStatement("");
            dbOperateData.setTableList(new ArrayList<>(0));

            return dbOperateData;
        }

        Set<String> tableSet = new HashSet<>();

        switch (dbStatementEnum) {
            case DSE_SELECT:
                getTablesFromSql(tableSet, sql, " from ", " from (", new String[]{" where ", " limit ", ";", " in ", ")", " order by ", " group by ", " having ", " join ", " " +
                        "union "});
                getTablesFromSql(tableSet, sql, " join ", " join (", new String[]{" on "});
                break;
            case DSE_INSERT:
            case DSE_REPLACE:
                getTablesFromSql(tableSet, sql, " into ", null, new String[]{"(", " values", ";", "select "});
                break;
            case DSE_UPDATE:
                getTablesFromSql(tableSet, sql, "update ", null, new String[]{" set "});
                break;
            case DSE_DELETE:
                getTablesFromSql(tableSet, sql, " from ", " from (", new String[]{" where ", " limit ", ";"});
                break;
            default:
                logger.error("不支持的类型 {}", dbStatementEnum);
                break;
        }

        dbOperateData.setStatement(dbStatementEnum.getStatement());

        List<String> tableList = new ArrayList<>(tableSet);
        Collections.sort(tableList);
        dbOperateData.setTableList(tableList);
        return dbOperateData;
    }

    /**
     * 获取数据库操作语句
     *
     * @param sql
     * @return
     */
    private DbStatementEnum getSqlStatement(String sql) {
        DbStatementEnum minIndexDbStatementEnum = null;
        // 记录各类SQL语句中，出现下标最小的
        int minIndex = -1;

        for (DbStatementEnum dbStatementEnum : DbStatementEnum.values()) {
            int index = StringUtils.indexOfIgnoreCase(sql, dbStatementEnum.getStatement());
            if (index == 0) {
                return dbStatementEnum;
            }

            if (index == -1) {
                continue;
            }

            if (minIndex == -1 || index < minIndex) {
                minIndex = index;
                minIndexDbStatementEnum = dbStatementEnum;
            }
        }

        return minIndexDbStatementEnum;
    }

    // 对sql语句进行格式化
    private String formatSql(String sql) {
        String newSql = sql.replaceAll("[\r\n\t]", " ")
                .replaceAll("[ ][ ]*", " ").trim();

        if (newSql.endsWith(";")) {
            return newSql;
        }
        return newSql + ";";
    }

    /**
     * 从数据库表名相关的sql语句中获得表名
     *
     * @param tableSet
     * @param sql      示例： "table1"    "table1, table2"    "table1 as t1, table2 as t2"
     * @return
     */
    private void getTablesFromPartSql(Set<String> tableSet, String sql) {
        String[] array1 = sql.split(",");
        for (String str1 : array1) {
            String[] array2 = str1.trim().split(" ");
            tableSet.add(array2[0]);
        }
    }

    /**
     * 从sql语句中获得对应的表名
     *
     * @param tableSet
     * @param sql
     * @param startFlag
     * @param ignoreStartFlag
     * @param endFlagArray
     * @return
     */
    private void getTablesFromSql(Set<String> tableSet, String sql, String startFlag, String ignoreStartFlag, String[] endFlagArray) {
        int skipIndex = 0;
        while (true) {
            // 查找开始标志下标
            int startIndex = StringUtils.indexOfIgnoreCase(sql, startFlag, skipIndex);
            if (startIndex == -1) {
                break;
            }

            // 跳过不处理的开始标志
            if (ignoreStartFlag == null || startIndex != StringUtils.indexOfIgnoreCase(sql, ignoreStartFlag, skipIndex)) {
                doGetTablesFromSql(startIndex, tableSet, sql, startFlag, endFlagArray);
            }

            skipIndex = startIndex + startFlag.length();
        }
    }

    private void doGetTablesFromSql(int startIndex, Set<String> tableSet, String sql, String startFlag, String[] endFlagArray) {
        // 判断是否

        // 查找结束标志下标
        int minEndFlagIndex = -1;
        for (String endFlag : endFlagArray) {
            int endFlagIndex = StringUtils.indexOfIgnoreCase(sql, endFlag, startIndex + startFlag.length());
            if (endFlagIndex == -1) {
                continue;
            }
            if (minEndFlagIndex == -1) {
                minEndFlagIndex = endFlagIndex;
            } else {
                minEndFlagIndex = Math.min(minEndFlagIndex, endFlagIndex);
            }
        }

        String partSql;
        if (minEndFlagIndex == -1) {
            partSql = sql.substring(startIndex + startFlag.length()).trim();
        } else {
            partSql = sql.substring(startIndex + startFlag.length(), minEndFlagIndex).trim();
        }

        // 从数据库表名相关的sql语句中获得表名
        getTablesFromPartSql(tableSet, partSql);
    }
}
