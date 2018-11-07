package obi.mapfind.details;



public class UserInformation  {

    private String name;
    private String email;
    private String url;
    private String location;
    private String phone_number;
    public UserInformation(){

    }

    public UserInformation(String email, String name, String url, String phone_number, String location) {
        this.email = email;
        this.name = name;
        this.url = url;
        this.phone_number = phone_number;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url= url;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}