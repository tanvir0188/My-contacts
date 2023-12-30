package edu.ewubd.mycontacts;

public class ContactInfos {
    String  name,userEmail, email, homePhone, officePhone, photo;

    public ContactInfos(String name,String userEmail, String email, String homePhone, String officePhone, String photo)
    {
        this.name = name;
        this.email = email;
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.photo = photo;
        this.userEmail = userEmail;
    }

    public String getName() {
        return name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getEmail() {
        return email;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getPhoto() {
        return photo;
    }
}
