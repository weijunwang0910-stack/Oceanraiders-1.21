package me.cryptidyy.oceanraiders.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLHelper {

    private MySQL sql;
    public SQLHelper(MySQL sql)
    {
        this.sql = sql;
    }

    public void createTable(String tableName, String primaryKey, String[]... parameters)
    {
        PreparedStatement ps;

        try
        {
            ps = sql.getConnection().prepareStatement
                ("CREATE TABLE IF NOT EXISTS " + tableName + " (" + parametersToStatement(parameters) + "PRIMARY KEY(" + primaryKey + "))");
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteTable(String tableName)
    {
        PreparedStatement ps;

        try
        {
            ps = sql.getConnection().prepareStatement
                    ("DROP TABLE IF EXISTS " + tableName);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void createEntry(String tableName, String column, String data)
    {
        try
        {
            PreparedStatement ps = sql.getConnection().prepareStatement("INSERT IGNORE INTO " + tableName + " (" + column + ") " + "VALUES (?)");
            ps.setString(1, data);
            ps.executeUpdate();

            return;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateTable(String tableName, String keyColumn, String keyName, String column, String data)
    {
        try
        {
            PreparedStatement ps = sql.getConnection().prepareStatement
                    ("UPDATE " + tableName + " SET " + column + "='" + data + "' WHERE " + keyColumn + "='" + keyName + "'");
            ps.executeUpdate();

            return;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateTable(String tableName, String keyColumn, String keyName, String column, int data)
    {
        try
        {
            PreparedStatement ps = sql.getConnection().prepareStatement
                    ("UPDATE " + tableName + " SET " + column + "=" + data + " WHERE " + keyColumn + "='" + keyName + "'");
            ps.executeUpdate();

            return;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateTable(String tableName, String keyColumn, String keyName, String column, boolean data)
    {
        try
        {
            PreparedStatement ps = sql.getConnection().prepareStatement
                    ("UPDATE " + tableName + " SET " + column + "=" + data + " WHERE " + keyColumn + "='" + keyName + "'");
            ps.executeUpdate();

            return;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private String parametersToStatement(String[]... parameters)
    {
        //{parameterName, type}
        //{UUID, VARCHAR(100)}

        StringBuilder result = new StringBuilder();
        for(String[] parameter : parameters)
        {
            result.append(parameter[0] + " ");
            result.append(parameter[1] +", ");
        }

        return result.toString();
    }
}
