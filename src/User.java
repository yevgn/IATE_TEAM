public class User {
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private String email;
    private String password;
    private int age;
    private String passportId;
    private String issuedBy;
    private String departmentNum;
    private String dateOfIssue;

    public User() {}

    public User(int id, String name, String surname, String patronymic, String phone, String email, String password, int age,
                String passportId, String issuedBy, String departmentNum, String dateOfIssue) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.age = age;
        this.passportId = passportId;
        this.issuedBy = issuedBy;
        this.departmentNum =departmentNum;
        this.dateOfIssue = dateOfIssue;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public void setDepartmentNum(String departmentNum) {
        this.departmentNum = departmentNum;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getPassword(){
        return password;
    }

    public String getPassportId() {
        return passportId;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public String getDepartmentNum() {
        return departmentNum;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }
}
