package android.example.contactslist.entities;

public class Contact {

    private String nameContact;
    private String phoneContact;
    private Integer idContact;
    private String uriPhotoContact;

    public Contact(String nameContact, String phoneContact, Integer idContact, String uriPhotoContact) {
        this.nameContact = nameContact;
        this.phoneContact = phoneContact;
        this.idContact = idContact;
        this.uriPhotoContact = uriPhotoContact;
    }

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public String getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(String phoneContact) {
        this.phoneContact = phoneContact;
    }

    public Integer getIdContact() {
        return idContact;
    }

    public void setIdContact(Integer idContact) {
        this.idContact = idContact;
    }

    public String getUriPhotoContact() {
        return uriPhotoContact;
    }

    public void setUriPhotoContact(String uriPhotoContact) {
        this.uriPhotoContact = uriPhotoContact;
    }

}
