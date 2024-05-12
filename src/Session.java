public class Session {
    private String book;
    private String author;
    private String date;
    private String time;
    private User user;

    public Session(String book, String author, String date, String time, User user) {
        this.book = book;
        this.author = author;
        this.date = date;
        this.time = time;
        this.user = user;
    }

    public String getBook() {
        return book;
    }

    public User getUser(){
        return user;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public void setUser(User user){
        this.user = user;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
