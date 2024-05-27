package searchSortAlgorithms;

import mainClasses.Book;

public class BinarySearch {

    public static int binarySearch(Book[] sortedArray, String valueToFind, int low, int high) {
        int index = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if ( sortedArray[mid].getImagepath().compareTo(valueToFind) < 0) {
                low = mid + 1;
            } else if (sortedArray[mid].getImagepath().compareTo(valueToFind) > 0) {
                high = mid - 1;
            } else if (sortedArray[mid].getImagepath().equals(valueToFind)) {
                index = mid;
                break;
            }
        }
        return index;
    }
}
