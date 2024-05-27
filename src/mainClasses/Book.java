package mainClasses;

import java.util.Arrays;
import java.util.Objects;

public class Book {
    private String name;
    private String authorName;
    private String authorSurname;
    private int yearOfPublishing;
    private String description;
    private String imagepath;
    private float rating;
    private String[] genres;

    public Book(){}

    public Book(String name, String authorName, String authorSurname,
                int yearOfPublishing, String description, String imagepath, float rating, String[] genres) {
        this.name = name;
        this.authorName = authorName;
        this.authorSurname = authorSurname;

        this.yearOfPublishing = yearOfPublishing;
        this.description = description;
        this.imagepath = imagepath;
        this.rating = rating;
        this.genres = genres;
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

    public String[] getGenres() {
        return genres;
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

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return yearOfPublishing == book.yearOfPublishing && Float.compare(rating, book.rating) == 0 && Objects.equals(name, book.name) && Objects.equals(authorName, book.authorName) && Objects.equals(authorSurname, book.authorSurname) && Objects.equals(description, book.description) && Objects.deepEquals(genres, book.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, authorName, authorSurname, yearOfPublishing, description, rating, Arrays.hashCode(genres));
    }
}
