package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.domain.Entity;
import ru.icmit.adkazankov.util.DbWork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public abstract class GenericDAOImpl<T extends Entity> implements GenericDAO<T> {

    protected DbWork db = DbWork.getInstance();
    protected GenericDAOImpl(){}

    @Override
    public abstract T create(T o);

    @Override
    public abstract void update(T o);

    @Override
    public void delete(T o){
        String sql = "DELETE FROM "+getTableName()+" WHERE ID = "+o.getId();
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<T> getAll() {
        String sql = "SELECT * FROM "+getTableName();
        return executeGetMany(sql);
    }

    @Override
    public T getByKey(Long id) {
        String sql = "SELECT * FROM "+getTableName()+" WHERE ID = "+id;
        ResultSet resultSet = null;
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            resultSet = statement.executeQuery();
            resultSet.next();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int updateFromFile(File file, String separator) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String s;
        //reader.read();
        int count = 0;
        while((s = reader.readLine()) != null){
            String[] split = s.split(separator);
            update(getObjectFromStringArray(split));
            count++;
        }
        return count;
    }

    protected abstract int getColumnCount();

    public int executeUpdate(String sql){
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public ArrayList<T> executeGetMany(String sql){
        ResultSet resultSet = null;
        ArrayList<T> result = new ArrayList<>();
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(getObjectFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public abstract T getObjectFromStringArray(String[] split);

    public abstract T getObjectFromResultSet(ResultSet rs);

    public abstract String getTableName();

}
