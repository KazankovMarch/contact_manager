package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.domain.DictionaryType;
import ru.icmit.adkazankov.domain.PhoneType;

import java.util.LinkedList;

public class PhoneTypeDAO extends DictionaryTypeDAO<PhoneType> {

    private static PhoneTypeDAO instance = new PhoneTypeDAO();

    private PhoneTypeDAO(){}

    public static PhoneTypeDAO getInstance(){
        if(instance == null){
            instance = new PhoneTypeDAO();
        }
        return instance;
    }

    @Override
    public String getIdSeqName() {
        return "phonetype_seq";
    }

    @Override
    public PhoneType createNewDTEntity() {
        return new PhoneType();
    }

    @Override
    public String getTableName() {
        return "dict_phonetype";
    }
}
