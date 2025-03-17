abstract class User {
    private String location;
    private String linkedIn;
    private String x;
    private String name ;
    private String email;
    private String password;
    private String bio;
    private String leetcode;
    private String github;


    public User(){

    }

    public String getname(){
        return name;
    }
    public String getemail(){
        return email;
    }
    public String getpassword(){
        return password;
    }
    public String getbio(){
        return bio;
    }
    public String getx(){
        return x;
    }
    public String getlocation(){
        return location;
    }
    public String getlinkedin(){
        return linkedIn;
    }
    public String getLeetcode(){
        return leetcode;
    }
    public String getgithub(){
        return github;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }
    public void setName(String n) {
        this.name = n;
    }
    public void setPassword(String p) {
        this.password = p;
    }
    public void setx(String x) {
        this.x = x;
    }
    public void setlocation(String location) {
        this.location = location;
    }
    public void setgithub(String github) {
        this.github = github;
    }
    public void setleetcode(String leetcode) {
        this.leetcode = leetcode;
    }

}
