package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.PhoneType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class PhoneTypeDAO extends GenericDAOImpl<PhoneType> {

    private static PhoneTypeDAO instance = new PhoneTypeDAO();
    static {
        instance.buffer = new LinkedList<PhoneType>();
    }

    private PhoneTypeDAO(){}

    public static PhoneTypeDAO getInstance(){
        if(instance == null){
            instance = new PhoneTypeDAO();
        }
        return instance;
    }

    @Override
    public PhoneType create(PhoneType o) {
        String sql =
                "insert into dict_phonetype (id, name, fullname, code) values ( ? , ? , ? , ? ) ";

        if(o.getId()==null){
            o.setId(db.generateId("phonetype_id"));
        }
        updateOrAddToBuffer(o);
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

        return o;
    }
    public PhoneType getByName(String name){
        for(PhoneType o : buffer){
            if(o.getName().equals(name)){
                return o;
            }
        }
        String sql = "SELECT * FROM dict_phonetype WHERE name = "+name;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public PhoneType getByCode(String code){
        for(PhoneType o : buffer){
            if(o.getCode().equals(code)){
                return o;
            }
        }
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
    public void update(PhoneType o) {
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
        updateOrAddToBuffer(o);
    }

    @Override
    public String getTableName() {
        return "dict_phonetype";
    }


    @Override
    public PhoneType getObjectFromResultSet(ResultSet rs) {
        try {
            PhoneType result = new PhoneType();
            result.setId(rs.getLong("id"));
            if(buffer.contains(result)){
                return getFromBuffer(result);
            }
            else {
                result.setFullName(rs.getString("fullname"));
                result.setName(rs.getString("name"));
                result.setCode(rs.getString("code"));
                updateOrAddToBuffer(result);
                return result;
            }
        }catch (SQLException e){
            System.err.println("can't parse PhoneType from ResultSet");
            e.printStackTrace();
        }
        return null;
    }

}
