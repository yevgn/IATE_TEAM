public class Phone {

    public static boolean isCorrectNumber(String phone) {
        int length = phone.length();
        if (length != 10)
            return false;

        char ch;
        for (int i = 0; i < length; i++) {
            ch = phone.charAt(i);
            if (ch < 48 || ch > 57)
                return false;
        }
        return true;
    }
}