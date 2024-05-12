public class Passport {

    public static boolean isCorrectPassportId(String id){
        int len = id.length();
        if(len != 10)
            return false;
        char ch;
        for(int i = 0; i < len; i++){
            ch = id.charAt(i);
            if(ch < 48 || ch > 57)
                return false;
        }
        return true;
    }

    public static boolean isCorrectDepartmentNumber(String num){
        int len = num.length();
        if(len != 7)
            return false;
        else if(num.charAt(3) != '-')
            return false;
        else if(!isNumber(num.substring(0, 3)) || !isNumber(num.substring(4, 7)))
            return false;
        else
            return true;
    }

    public static boolean isCorrectDateOfIssue(String date){
        int len = date.length();
        if(len != 10)
            return false;
        else if(date.charAt(2) != '.' && date.charAt(5) != '.')
            return false;

        String strDay = date.substring(0, 2);
        String strMonth = date.substring(3, 5);
        String strYear = date.substring(6, 10);

        if(!isNumber(strDay) || !isNumber(strMonth) || !isNumber(strYear))
            return false;

        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));
        boolean isLeapYear = year % 4 == 0;

        if(month == 0 || month > 12)
            return false;
        else if(day == 0 || day > 31)
            return false;
        else if(year == 0)
            return false;
        else if(month == 4 || month == 6 || month == 9 || month == 11){ // должно быть 30 дней
            if(day > 30)
                return false;
        } else if(month == 2){
            if(isLeapYear){
                if(day > 29)
                    return false;
            } else{
                if(day > 28)
                    return false;
            }
        }
        return true;
    }

    private static boolean isNumber(String num){
        char ch;
        for(int i =0; i < num.length(); i++){
            ch = num.charAt(i);
            if(ch < 48 || ch > 57)
                return false;
        }
        return true;
    }

}
