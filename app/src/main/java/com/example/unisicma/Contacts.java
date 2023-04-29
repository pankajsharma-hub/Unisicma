package com.example.unisicma;

public class Contacts {

    private String id;

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    private String last_date;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }

    private String child_name;

    public String getMother_Id() {
        return mother_Id;
    }

    public void setMother_Id(String mother_Id) {
        this.mother_Id = mother_Id;
    }

    private String mother_Id;

    public Contacts(String name, String mother_Id, String id, String dob, String Last_Date)
    {
       this.setId(id);
       this.setChild_name(name);
       this.setFacility(dob);
       this.setMother_Id(mother_Id);
       this.setLast_date(Last_Date);

    }




    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    private String facility;


}
