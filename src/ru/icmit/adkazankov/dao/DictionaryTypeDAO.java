package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.DictionaryType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public abstract class DictionaryTypeDAO<T extends DictionaryType> extends GenericDAOImpl<T> {


    @Override
    public T create(T o) {
        String sql =
                "insert into dict_phonetype (id, name, fullname, code) values ( ? , ? , ? , ? ) ";

        if(o.getId()==null){
            o.setId(db.generateId(getIdSeqName()));
        }
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){

            st.setLong(1, o.getId());
            st.setString(2, o.getName());
            st.setString( 3, o.getFullName());
            st.setString( 4, o.getCode());

            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }

        return (T)o;
    }

    public abstract String getIdSeqName();

    public T getByName(String name){
        String sql = "SELECT * FROM dict_phonetype WHERE name = "+name;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public T getByCode(String code){
        String sql = "SELECT * FROM dict_phonetype WHERE code = "+code;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(T o) {
        if(o.getId()==null){
            create(o);
            return;
        }
        String sql =
                "update dict_phonetype set name = ? , fullname = ? , code = ? where id = ? ";
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){

            st.setString(1, o.getName());
            st.setString( 2, o.getFullName());
            st.setString( 3, o.getCode());
            st.setLong(4, o.getId());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public T getObjectFromResultSet(ResultSet rs) {
        try {
            
            T result = createNewDTEntity();
            result.setId(rs.getLong("id"));
            result.setFullName(rs.getString("fullname"));
            result.setName(rs.getString("name"));
            result.setCode(rs.getString("code"));
            return result;
        }catch (SQLException e){
            System.err.println("can't parse DictionaryType from ResultSet");
            e.printStackTrace();
        }
        return null;
    }

    public abstract T createNewDTEntity();

}
