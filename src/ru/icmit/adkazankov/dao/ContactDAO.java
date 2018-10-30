package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDAO extends GenericDAOImpl<Contact> {

    private static ContactDAO instance = new ContactDAO();

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
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }

        return o;
    }

    @Override
    public void update(Contact o) {
        String sql = "update "+getTableName()+" set fullname = ?, lastname = ?, firstname = ?, inblacklist = ? where id = "+o.getId();
        if(o.getId()==null || getByKey(o.getId())==null){
            create(o);
            return;
        }

        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){


            st.setString(1, o.getFullName());
            st.setString( 2, o.getLastName());
            st.setString( 3, o.getFirstName());
            st.setBoolean(4, o.isInBlackList());
            System.out.println(st.toString());

            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }
    }

    @Override
    protected int getColumnCount() {
        return 5;
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
            result.setFullName(rs.getString("fullname"));
            result.setLastName(rs.getString("lastname"));
            result.setFirstName(rs.getString("firstname"));
            result.setInBlackList(rs.getBoolean("inblacklist"));
            return result;
        }catch (SQLException e){
            System.err.println("can't parse Contact from ResultSet");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Contact getObjectFromStringArray(String[] split) {
        Contact contact = null;
        if(split.length==getColumnCount()) {
            contact = new Contact();
            contact.setId(Long.parseLong(split[0]));
            contact.setFullName(split[1]);
            contact.setLastName(split[2]);
            contact.setFirstName(split[3]);
            contact.setInBlackList(Boolean.parseBoolean(split[4]));
        }
        else {
            contact = new Contact();
            contact.setId(null);
            contact.setFullName(split[0]);
            contact.setLastName(split[1]);
            contact.setFirstName(split[2]);
            contact.setInBlackList(Boolean.parseBoolean(split[3]));
        }
        System.out.println(contact);
        return contact;
    }
}
