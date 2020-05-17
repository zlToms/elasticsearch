package elasticsearch.test;

import elasticsearch.entity.TUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtils {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "123456";

    public int getRows(String sql){
        int row = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // STEP 2: 注册mysql的驱动
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: 获得一个连接
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4: 创建一个查询
            System.out.println("Creating statement...");

           // sql = "SELECT count(*) FROM t_user ";
            stmt = conn.prepareStatement(sql);

            System.out.println(stmt.toString());//打印sql
            ResultSet rs = stmt.executeQuery();
            rs.next();
            row = rs.getInt(1);

            // STEP 6: 关闭连接
            rs.close();
            stmt.close();
            conn.close();
           
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return row;
    }

    public List<Map<String,String>>  queryData(String sql,List<String> keyList) {
        Connection conn = null;
        PreparedStatement stmt = null;
       List<Map<String,String>> list= new ArrayList();
        try {
            // STEP 2: 注册mysql的驱动
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: 获得一个连接
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4: 创建一个查询
            System.out.println("Creating statement...");
            String table = "t_user";

            stmt = conn.prepareStatement(sql);

            System.out.println(stmt.toString());//打印sql
            ResultSet rs = stmt.executeQuery();



            // STEP 5: 从resultSet中获取数据并转化成bean
            while (rs.next()) {
                Map<String,String> map = new HashMap();
                for(String key:keyList){
                    String[] split = key.split(":");
                    if(split[1].endsWith("1")){
                        map.put(split[0],""+rs.getInt(split[0]));
                    }else if(split[1].endsWith("2")){
                        map.put(split[0],rs.getString(split[0]));
                    }

                }
                list.add(map);
            }
            // STEP 6: 关闭连接
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
            return list;
    }

    //批量插入数据
    public void bathInsert(){
        long start = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "insert  into t_user (id,userName,realName,sex,mobile,email,note) " +
                                    " values (?,?,?,?,?,?,?) ";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql);

            for (int i = 0; i < 1000000; i++) {//100万条数据
                stmt.setInt(1, i+10);
                stmt.setString(2, "li"+i);
                stmt.setString(3, "老王"+i);
                stmt.setString(4, ""+1);
                stmt.setString(5, "1325314212");
                stmt.setString(6, "lision@qq");
                stmt.setString(7, "note"+i);
                //stmt.setInt(8, i);
                stmt.addBatch();
                if(i%1000==0){
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            conn.commit();

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("批量插入需要时间:"+(end - start));
    }
}
