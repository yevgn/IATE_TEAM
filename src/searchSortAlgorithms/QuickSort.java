package searchSortAlgorithms;

import mainClasses.Book;

public class QuickSort {

    public static void quickSort(Book[] books, int low, int high) {
        if (books.length == 0)
            return;//завершить выполнение, если длина массива равна 0

        if (low >= high)
            return;//завершить выполнение если уже нечего делить

        // выбрать опорный элемент
        int middle = low + (high - low) / 2;
        Book opora = books[middle];

        // разделить на подмассивы, который больше и меньше опорного элемента
        int i = low, j = high;
        while (i <= j) {
            while (books[i].getImagepath().compareTo(opora.getImagepath()) < 0) {
                i++;
            }

            while (books[j].getImagepath().compareTo( opora.getImagepath() )> 0) {
                j--;
            }

            if (i <= j) {//меняем местами
                Book temp = books[i];
                books[i] = books[j];
                books[j] = temp;
                i++;
                j--;
            }
        }

        // вызов рекурсии для сортировки левой и правой части
        if (low < j)
            quickSort(books, low, j);

        if (high > i)
            quickSort(books, i, high);
    }

}