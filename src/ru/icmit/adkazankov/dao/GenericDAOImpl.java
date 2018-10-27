package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.domain.Entity;
import ru.icmit.adkazankov.util.DbWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class GenericDAOImpl<T extends Entity> implements GenericDAO<T> {

    protected DbWork db = DbWork.getInstance();
    protected GenericDAOImpl(){}
    protected LinkedList<T> buffer;

    public abstract T getObjectFromResultSet(ResultSet rs);
    void updateOrAddToBuffer(T o) {
        if(buffer.contains(o)) {
            buffer.remove(buffer.indexOf(o));
            buffer.add(o);
        }
        else {
            buffer.add(o);
        }
    }
    protected T getFromBuffer(T o){
        if(buffer.contains(o)) {
            return buffer.get(buffer.indexOf(o));
        }
        else{
            return null;
        }
    }


    @Override
    public abstract T create(T o);

    @Override
    public abstract void update(T o);

    @Override
    public void delete(T o){
        buffer.remove(o);
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
        for(T o: result){
            updateOrAddToBuffer(o);
        }
        return result;
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

    public abstract String getTableName();

}
