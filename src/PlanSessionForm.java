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
import java.util.*;

public class PlanSessionForm extends JDialog {
    private JPanel panel1;
    private JComboBox cbDay;
    private JComboBox cbTime;
    private JButton btnOK;
    final int daysToPlanSessionAhead = 30;
    private final String bookName;
    private boolean dayIsChosen = false;
    private final String SESSION_NOT_COMPLETED = "не завершена";

    public PlanSessionForm(JFrame parent, BookInfo bookInfo, int userId){
        super(parent);
       setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(330, 170));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                bookInfo.setVisible(true);
            }
        });

        bookName = bookInfo.getBook().getName();

        fillCbDay();


        cbDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dayIsChosen = true;
                cbTime.removeAllItems();
                String fullDate = (String)cbDay.getSelectedItem();
                String date = fullDate.substring(fullDate.length() - 5); // УБРАЛ ДЕНЬ НЕДЕЛИ ИЗ ДАТЫ : ПРИМЕР - 25.02
                ArrayList<String> availableHours = getAvailableHours(date);
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
                    String startTime = getCurrentYear() + "-" + date.substring(3) + "-" + date.substring(0, 2)
                            + " " +  time.substring(0, 5);
                    String endTime = getCurrentYear() + "-" + date.substring(3) + "-" + date.substring(0, 2)
                    + " " + time.substring(8, 13);

                    try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                        Statement statement = connection.createStatement()){

                       final String query = "insert into sessions (user_id, book_id, start_time, end_time, status) " +
                               "values('" + userId + "', (select id from books where name = '" + bookInfo.getBook().getName() +
                               "'), '" + startTime + "', '" + endTime + "', '" + SESSION_NOT_COMPLETED + "');";
                       statement.executeUpdate(query);
                       showInformationMessage("Сеанс назначен", "Сообщение");
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

    private void fillCbDay(){

        LocalDate currentDate = LocalDate.now();
        LocalDate bufDate = currentDate.plusDays(1);

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
    }

    private ArrayList<String> getAvailableHours(String date){
        ArrayList<String> availableHours = new ArrayList<>(Arrays.asList("08:00 - 09:00", "09:00 - 10:00",
                "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00",
                "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00",
                "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00"));

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()) {

            String query = "select (select extract(hour from start_time)) AS start_hour," +
                    " (select extract(hour from end_time)) AS end_hour from sessions where (select extract(month from start_time)) = '" +
                    date.substring(3) + "' and (select extract(day from start_time)) = '" +
                    date.substring(0, 2)  + "' and book_id = (select id from books where name = '" + bookName + "');";

            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                String startTime = res.getString("start_hour");
                startTime = startTime.length() == 1 ? "0".concat(startTime) : startTime;
                String endTime = res.getString("end_hour");
                endTime = endTime.length() == 1 ? "0".concat(endTime) : endTime;
                alterAvailableHours(availableHours, startTime, endTime);
            }

            return availableHours;

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }

        return null;
    }

    private void alterAvailableHours(ArrayList<String> availableHours, String startTime, String endTime){
        Iterator<String> iter = availableHours.iterator();
        while(iter.hasNext()){
            String elem = iter.next();
            String firstPart = elem.substring(0, 2);
            String secondPart = elem.substring(8, 10);
            if(firstPart.equals(startTime) && secondPart.equals(endTime))
                iter.remove();
        }

    }

    private int getCurrentYear(){
        return LocalDate.now().getYear();
    }

    private void showInformationMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
