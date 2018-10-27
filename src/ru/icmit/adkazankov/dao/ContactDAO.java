package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class ContactDAO extends GenericDAOImpl<Contact> {

    private static ContactDAO instance = new ContactDAO();
    static {
        instance.buffer = new LinkedList<Contact>();
    }

    private ContactDAO(){}

    public static ContactDAO getInstance() {
        if(instance==null){
            instance = new ContactDAO();
        }
        return instance;
    }

    @Override
    public Contact create(Contact o) {
        String sql = "insert into "+getTableName()+" (id, fullname, lastname, firstname, inblacklist) values (? , ? , ? , ? , ? ) ";

        if(o.getId()==null){
            o.setId(db.generateId("contact_seq"));
        }

        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){

            st.setLong(1, o.getId());
            st.setString(2, o.getFullName());
            st.setString( 3, o.getLastName());
            st.setString( 4, o.getFirstName());
            st.setBoolean(5, o.isInBlackList());

            st.execute();
            updateOrAddToBuffer(o);
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }

        return o;
    }

    @Override
    public void update(Contact o) {
        String sql = "update "+getTableName()+" set fullname = ?, lastname = ?, firstname = ?, inblacklist = ? where id = "+o.getId();

        if(o.getId()==null){
            create(o);
            return;
        }

        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){

            st.setString(1, o.getFullName());
            st.setString( 2, o.getLastName());
            st.setString( 3, o.getFirstName());
            st.setBoolean(4, o.isInBlackList());

            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }
    }

    @Override
    public String getTableName() {
        return "contact";
    }

    @Override
    public Contact getObjectFromResultSet(ResultSet rs) {
        try {
            Contact result = new Contact();
            result.setId(rs.getLong("id"));
            if(buffer.contains(result)){
                return getFromBuffer(result);
            }
            else {
                result.setFullName(rs.getString("fullname"));
                result.setLastName(rs.getString("lastname"));
                result.setFirstName(rs.getString("firstname"));
                result.setInBlackList(rs.getBoolean("inblacklist"));
                updateOrAddToBuffer(result);
                return result;
            }
        }catch (SQLException e){
            System.err.println("can't parse Contact from ResultSet");
            e.printStackTrace();
        }
        return null;
    }
}
