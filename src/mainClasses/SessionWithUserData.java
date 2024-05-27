package mainClasses;

public class SessionWithUserData {
    private String bookName;
    private String authorName;
    private String authorSurname;
    private String year;
    private String month;
    private String day;
    private String startHour;
    private String endHour;
    private String userName;
    private String userPhone;
    private String userSurname;
    private String userPatronymic;
    private String status;


    public SessionWithUserData(){}

    public SessionWithUserData(String bookName, String authorName, String authorSurname, String year, String month, String day,
                               String startHour, String endHour, String userName, String userPhone, String userSurname,
                               String userPatronymic, String status) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.year = year;
        this.month = month;
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userSurname = userSurname;
        this.userPatronymic = userPatronymic;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserPatronymic() {
        return userPatronymic;
    }

    public void setUserPatronymic(String userPatronymic) {
        this.userPatronymic = userPatronymic;
    }
}
