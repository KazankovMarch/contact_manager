package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.domain.Contact;
import ru.icmit.adkazankov.domain.Phone;
import ru.icmit.adkazankov.domain.PhoneType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class PhoneDAO extends GenericDAOImpl<Phone> {

    private static PhoneDAO instance = new PhoneDAO();

    private PhoneDAO(){}

    public static PhoneDAO getInstance(){
        if(instance == null){
            instance = new PhoneDAO();
        }
        return instance;
    }

    public  ArrayList<Phone> getByContact(Contact contact){
        String sql = "SELECT * FROM "+getTableName()+" WHERE contact_id = "+contact.getId();

        return executeGetMany(sql);
    }

    public  ArrayList<Phone> getByPhoneType(PhoneType type){
        String sql = "SELECT * FROM "+getTableName()+" WHERE phonetype = "+type.getId();
        return executeGetMany(sql);
    }




    @Override
    public Phone create(Phone o) {
        String sql = "insert into "+getTableName()+" (contact_id, id, phonetype_id, phonenumber) values ( ? , ? , ? , ? )";

        if(o.getId()==null){
            o.setId(db.generateId("phone_seq"));
        }

        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){
            st.setLong(1, o.getContact().getId());
            st.setLong(2, o.getId());
            st.setLong( 3, o.getPhoneType().getId());
            st.setString( 4, o.getPhoneNumber());
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            o = null;
        }
        return o;
    }

    @Override
    public void update(Phone o) {
        if(o.getId()==null || getByKey(o.getId())==null){
            create(o);
            return;
        }

        String sql =
                "update "+getTableName()+" set contact_id = ? , phonetype_id = ? , phonenumber = ? where id = ? ";

        PhoneTypeDAO phoneTypeDAO = PhoneTypeDAO.getInstance();
        if(phoneTypeDAO.getByKey(o.getPhoneType().getId())==null){
            phoneTypeDAO.create(o.getPhoneType());
        }
        ContactDAO contactDAO = ContactDAO.getInstance();
        if(contactDAO.getByKey(o.getContact().getId())==null){
            contactDAO.create(o.getContact());
        }
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){
            st.setLong(1, o.getContact().getId());
            st.setLong( 2, o.getPhoneType().getId());
            st.setString(3, o.getPhoneNumber());
            st.setLong(4, o.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Phone getObjectFromResultSet(ResultSet rs) {
        try {
            Phone result = new Phone();
            result.setId(rs.getLong("id"));
            PhoneType type = PhoneTypeDAO.getInstance().getByKey(rs.getLong("phonetype_id"));
            result.setPhoneType(type);
            result.setPhoneNumber(rs.getString("phonenumber"));
            Contact contact = ContactDAO.getInstance().getByKey(rs.getLong("contact_id"));
            result.setContact(contact);
            return result;
        }catch (SQLException e){
            System.err.println("can't parse Phone from ResultSet");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Phone getObjectFromStringArray(String[] split) {
        Phone phone = null;
        if(split.length==getColumnCount()) {
            phone = new Phone();
            phone.setContact(ContactDAO.getInstance().getByKey(Long.parseLong(split[0])));
            phone.setId(Long.parseLong(split[1]));
            phone.setPhoneType(PhoneTypeDAO.getInstance().getByKey(Long.parseLong(split[2])));
            phone.setPhoneNumber(split[3]);
        }
        else if(split.length==getColumnCount()-1){
            phone = new Phone();
            phone.setContact(ContactDAO.getInstance().getByKey(Long.parseLong(split[0])));
            phone.setId(null);
            phone.setPhoneType(PhoneTypeDAO.getInstance().getByKey(Long.parseLong(split[1])));
            phone.setPhoneNumber(split[2]);
        }
        return phone;
    }

    @Override
    protected int getColumnCount() {
        return 4;
    }

    @Override
    public String getTableName() {
        return "phone";
    }


}
