public class Session {
    private String bookName;
    private String authorName;
    private String authorSurname;
    private String year;
    private String month;
    private String day;
    private String start_hour;
    private String end_hour;
    private int user_id;

    public Session(){}

    public Session(String bookName, String authorName, String authorSurname, String year, String month,
                   String day, String start_hour, String end_hour, int user_id) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.year = year;
        this.month = month;
        this.day = day;
        this.start_hour = start_hour;
        this.end_hour = end_hour;
        this.user_id = user_id;
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
        return start_hour;
    }

    public void setStartHour(String start_time) {
        this.start_hour = start_time;
    }

    public String getEndHour() {
        return end_hour;
    }

    public void setEndHour(String end_time) {
        this.end_hour = end_time;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}
