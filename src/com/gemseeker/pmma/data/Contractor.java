package com.gemseeker.pmma.data;

/**
 *
 * @author Gem Seeker
 */
public class Contractor {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String contactPerson;
    
    public Contractor(){}

    public Contractor(int id, String name, String address, String phone,
            String mobile, String email, String contactPerson){
        setId(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
        setMobile(mobile);
        setEmail(email);
        setContactPerson(contactPerson);
    }
    
    public int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public final void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public final void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public final void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
}
