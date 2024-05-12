public class Book {
    private String name;
    private String author;
    private int yearOfPublishing;
    private String description;
    private String imagepath;
    private float rating;
    private String genre;

    public Book(){}

    public Book(String name, String author, int yearOfPublishing, String description, String imagepath, float rating, String genre) {
        this.name = name;
        this.author = author;
        this.yearOfPublishing = yearOfPublishing;
        this.description = description;
        this.imagepath = imagepath;
        this.rating = rating;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getYearOfPublishing() {
        return yearOfPublishing;
    }

    public String getDescription() {
        return description;
    }

    public String getImagepath() {
        return imagepath;
    }

    public float getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYearOfPublishing(int yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}
