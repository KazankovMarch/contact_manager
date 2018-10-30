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
                "insert into "+getTableName()+" (id, name, fullname, code) values ( ? , ? , ? , ? ) ";

        if(o.getId()==null){
            o.setId(db.generateId(getIdSeqName()));
            System.out.println("dictDAO create o.id==null, created id = "+o.getId());
        }
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){

            st.setLong(1, o.getId());
            st.setString(2, o.getName());
            st.setString( 3, o.getFullName());
            st.setString( 4, o.getCode());
            System.out.println("dictDAO create st = "+st);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }

        return (T)o;
    }

    public abstract String getIdSeqName();

    public T getByName(String name){
        String sql = "SELECT * FROM "+getTableName()+" WHERE name = "+name;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public T getByCode(String code){
        String sql = "SELECT * FROM "+getTableName()+" WHERE code = "+code;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(T o) {
        if(o.getId()==null || getByKey(o.getId())==null){
            if(o.getId()==null){
                System.out.println("dictDAO update o.id==null");
            }
            if(getByKey(o.getId())==null){
                System.out.println("dictDAO update getByKey(o.id)==null");
            }
            create(o);
            return;
        }
        String sql =
                "update "+getTableName()+" set name = ? , fullname = ? , code = ? where id = ? ";
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
    @Override
    public T getObjectFromStringArray(String[] split) {
        T type = null;
        if(split.length==getColumnCount()) {
            type = createNewDTEntity();
            type.setId(Long.parseLong(split[0]));
            type.setCode(split[1]);
            type.setName(split[2]);
            type.setFullName(split[3]);
        }
        else if(split.length==getColumnCount()-1){
            type = createNewDTEntity();
            type.setId(null);
            type.setCode(split[0]);
            type.setName(split[1]);
            type.setFullName(split[2]);
        }
        return type;
    }

    @Override
    protected int getColumnCount() {
        return 4;
    }

    public abstract T createNewDTEntity();

}
