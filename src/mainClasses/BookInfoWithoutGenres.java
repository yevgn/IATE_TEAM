package mainClasses;

import java.util.Objects;

public class BookInfoWithoutGenres {
    private String name;
    private String authorName;
    private String authorSurname;
    private int yearOfPublishing;
    private String description;
    private String imagepath;
    private float rating;

    public BookInfoWithoutGenres(){}

    public BookInfoWithoutGenres(String name, String authorName, String authorSurname,
                int yearOfPublishing, String description, String imagepath, float rating) {
        this.name = name;
        this.authorName = authorName;
        this.authorSurname = authorSurname;

        this.yearOfPublishing = yearOfPublishing;
        this.description = description;
        this.imagepath = imagepath;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookInfoWithoutGenres that = (BookInfoWithoutGenres) o;
        return yearOfPublishing == that.yearOfPublishing && Float.compare(rating, that.rating) == 0 && Objects.equals(name, that.name) && Objects.equals(authorName, that.authorName) && Objects.equals(authorSurname, that.authorSurname) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, authorName, authorSurname, yearOfPublishing, description, rating);
    }
}
