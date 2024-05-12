import javax.swing.*;
import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.AccessDeniedException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class PlanSessionForm extends JDialog {
    private JPanel panel1;
    private JComboBox cbDay;
    private JComboBox cbTime;
    private JButton btnOK;
    final int daysToPlanSessionAhead = 20;
    //final int maximumPeopleInSessionTime = 10;
    private final String bookName;
    private final String bookAuthor;
    private boolean dayIsChosen = false;

    public PlanSessionForm(JFrame parent, BookInfo bookInfo, int userId){
        super(parent);
       setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(330, 300));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                bookInfo.setVisible(true);
            }
        });
        LocalDate currentDate = LocalDate.now().plusDays(1);
        bookName = bookInfo.getBookName();
        bookAuthor = bookInfo.getBookAuthor().substring(8);

        LocalDate bufDate = currentDate;
        String startMonth = bufDate.getMonth().toString();
        int[] daysOfMonth = new int[daysToPlanSessionAhead];
        String[] daysOfWeek = new String[daysToPlanSessionAhead];

        for(int i = 0; i < daysToPlanSessionAhead; i++){ // доступные дни для записи
            daysOfMonth[i] = bufDate.getDayOfMonth();
            daysOfWeek[i] = bufDate.getDayOfWeek().toString();
            bufDate = bufDate.plusDays(1);
        }

        String[] daysAvailable = new String[daysToPlanSessionAhead];
        boolean isNextMonth = false;

        for(int i =0; i < daysToPlanSessionAhead; i++){
            String dayOfWeek = daysOfWeek[i];
            String dayOfMonth = String.valueOf(daysOfMonth[i]);
            String month;
            if(dayOfMonth.equals("1") && i != 0)
                isNextMonth = true;

            if(isNextMonth)
                month = String.valueOf(currentDate.plusMonths(1).getMonthValue());
            else
                month = String.valueOf(currentDate.getMonthValue());

            if(month.length() == 1)
                month = "0" + month;
            if(dayOfMonth.length() == 1)
                dayOfMonth = "0" + dayOfMonth;
            String date = dayOfMonth + "." + month;
            daysAvailable[i] = dayOfWeek + " " + date;
            cbDay.addItem(daysAvailable[i]); // ДОБАВИЛИ В ПЕРВЫЙ СОМБОБОКС ВСЕ ДОСТУПНЫЕ ДНИ
        }


       // cbTime.addItem("Выберите день");

        cbDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dayIsChosen = true;
                cbTime.removeAllItems();
                String fullDate = (String)cbDay.getSelectedItem();
                String date = fullDate.substring(fullDate.length() - 5); // УБРАЛ ДЕНЬ НЕДЕЛИ ИЗ ДАТЫ : ПРИМЕР - 25.02
                ArrayList<String> availableHours = getAvailableHours(date, userId);
                if(availableHours.isEmpty())
                    cbTime.addItem("Нет свободных сеансов");
                for(int i = 0; i < availableHours.size(); i++){
                    cbTime.addItem(availableHours.get(i));
                }
            }
        });

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!dayIsChosen )
                    showErrorMessage("Выберите день и время сеанса!", "Ошибка!");
                else{
                    String fullDate = (String) cbDay.getSelectedItem();
                    String date = fullDate.substring(fullDate.length() - 5);
                    String time = (String) cbTime.getSelectedItem();
                    String startTime = time.substring(0, 5);
                    String endTime = time.substring(8, 13);

                    try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                        Statement statement = connection.createStatement()){

                       String query = "insert into sessions (date, starttime, endtime, book, author, userid) values ('" + date + "', '" + startTime + "', '" +
                               endTime + "', '" + bookName + "', '" + bookAuthor + "', '" + userId + "');";
                       statement.executeUpdate(query);
                       showInformationMessage("Успех!", "");
                       dispose();
                       bookInfo.dispose();
                    } catch (SQLException ex){
                        showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
                        ex.printStackTrace();
                    }
                }
            }
        });

        setVisible(true);
    }

    private ArrayList<String> getAvailableHours(String date, int userId){
        ArrayList<String> availableHours = new ArrayList<>(Arrays.asList("08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00"));

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()) {

            String query = "select starttime, endtime from sessions where date = '" + date + "' and book = '" + bookName  + "' and " +
                    "author = '" + bookAuthor + "';";

            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                String startTime = res.getString("starttime");
                String endTime = res.getString("endtime");
                alterAvailableHours(availableHours, date, startTime, endTime);
            }

            query = "select starttime, endtime from sessions where userid = '" + userId + "' and date = '" + date + "';";
            ResultSet res1 = statement.executeQuery(query);
            while(res1.next()){
                String startTime = res1.getString("starttime");
                String endtTime = res1.getString("endtime");
                alterAvailableHours(availableHours, date, startTime, endtTime);
            }

            return availableHours;

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }

        return null;
    }

    private void alterAvailableHours(ArrayList<String> availableHours, String date, String startTime, String endTime){
        for(int i = 0; i < availableHours.size(); i++){
            String firstPart = availableHours.get(i).substring(0,5);
            String secondPart = availableHours.get(i).substring(8, 13);
            if(firstPart.equals(startTime) && secondPart.equals(endTime))
                availableHours.remove(i);
//            else if(isPeopleLimitReachedInOneSession(date, startTime, endTime))
//                availableHours.remove(i);
        }
    }

//    private boolean isPeopleLimitReachedInOneSession(String date, String startTime, String endTime){
//        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
//            Statement statement = connection.createStatement()){
//
//            String query = "select count (*) from sessions where date = '" + date + "' and starttime = '" + startTime + "' and endtime = '" + endTime + "" +
//                    "' limit 1;";
//            ResultSet res = statement.executeQuery(query);
//            if(res.next())
//                return res.getInt("count") >= 10;
//
//        } catch (SQLException ex){
//            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
//            ex.printStackTrace();
//        }
//        return false;
//    }


    private void showInformationMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
