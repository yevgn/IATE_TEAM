import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainFrame extends JDialog{
    private JPanel panel1;
    private JButton btnPlannedSessions;
    private JButton btnHelp;
    private JButton btnLibrary;
    private JButton btnProfile;
    private JPanel pnlCards;
    private JPanel pnlCardProfile;
    private JPanel pnlCardLibrary;
    private JPanel pnlCardPlannedSessions;
    private JPanel pnlCardHelp;
    private JComboBox cbGenres;
    private JTextField tfSearch;
    private JButton btnChangeInfo;
    private JButton btnChangePassword;
    private JLabel labelPopUpHint;
    private JTextField tfFullName;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JPasswordField pfPassword1;
    private JLabel labelEnterPassword;
    private JLabel labelRepeatPassword;
    private JTextArea taHelp;
    private JButton btnMain;
    private JPanel pnlCardMain;
    private JLabel labelLeftBookName;
    private JLabel labelLeftBookAuthor;
    private JLabel labelLeftBookGenre;
    private JLabel labelRightBookName;
    private JLabel labelRightBookAuthor;
    private JLabel labelRightBookGenre;
    private JLabel labelUsersForMonth;
    private JLabel labelBooksAvailable;
    private JLabel labelLeftImage;
    private JLabel labelRightImage;
    private JScrollPane spLeft;
    private JScrollPane spRight;
    private JTextField tfID;
    private JButton btnLogOut;
    private JButton btnDeleteAccount;
    //private JPanel pnlLibrary;
    private CardLayout cardLayout;
    private User user;
    private JButton btnDeleteSessions;
    private JButton[] bookButtons;
    private Book[] allBooksFromDB;
    private final String helpText = "Если у вас возникли проблемы или вы хотите получить дополнительную информацию, свяжитесь с нашим специалистом.";
    private int numOfUsersForMonth;
    private Book firstPopularBook;
    private Book secondPopularBook;
    HashMap<String, Integer> uniqueBooks = new HashMap<>();

    public MainFrame(JFrame parent, User user){
        super(parent);
        setContentPane(panel1);
        setLocation(400, 100);
        setModal(true);
        setSize(new Dimension(1200, 900));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cardLayout = (CardLayout) pnlCards.getLayout();
        this.user = user;
        cbGenres.setVisible(false);
        tfSearch.setVisible(false);

        numOfUsersForMonth = getNumOfUsersForMonth();
        labelUsersForMonth.setText(labelUsersForMonth.getText() + numOfUsersForMonth);

        JButton[] buttons = {btnMain, btnProfile, btnLibrary, btnPlannedSessions, btnHelp};
        for(JButton btn : buttons){
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setOpaque(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setOpaque(false);
                }
            });
        }

        btnMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "pnlCardMain");
            }
        });

        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbGenres.setVisible(false);
                tfSearch.setVisible(false);
                updateProfileCard();
                cardLayout.show(pnlCards, "pnlCardProfile");
            }
        });

        btnLibrary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbGenres.setVisible(true);
                tfSearch.setVisible(true);
                tfSearch.setText("Поиск");
                restoreButtons();
                cardLayout.show(pnlCards, "pnlCardLibrary");
            }
        });

        btnPlannedSessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbGenres.setVisible(false);
                tfSearch.setVisible(false);
                pnlCardPlannedSessions.removeAll();
                createSessionsTable();
                btnDeleteSessions.setEnabled(false);
                cardLayout.show(pnlCards, "pnlCardPlannedSessions");
            }
        });

        btnHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "pnlCardHelp");
            }
        });



        allBooksFromDB = getBookListFromDB().toArray(new Book[0]); // ВСЕГДА ОДНИ И ТЕ ЖЕ
        QuickSort.quickSort(allBooksFromDB, 0, allBooksFromDB.length - 1);
        //ArrayList<Book> books = getBookListFromDB();
        labelBooksAvailable.setText(labelBooksAvailable.getText() + allBooksFromDB.length);
        displayButtonsOnPanel(allBooksFromDB);

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // поиск по названию книги
                    String text = tfSearch.getText();
                    String genre = (String) cbGenres.getSelectedItem();
                    boolean genreMatters = !genre.equals("Любой жанр");
                    boolean textMatters = !text.isEmpty();

                    if (!genreMatters && !textMatters)
                        restoreButtons();
                    else {
                        restoreButtons();
                        ArrayList<Book> booksFound = new ArrayList<>();
                        for (Book book : allBooksFromDB) {
                            if (textMatters) {
                                if (book.getName().contains(text) || book.getAuthor().contains(text)) { // добавляем ее на окошко
                                    if (!genreMatters)
                                        booksFound.add(book);
                                    else {
                                        String[] genres = book.getGenre().split("\n");
                                        for (String str : genres) {
                                            if (str.equals(genre)) {
                                                booksFound.add(book);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                String[] genres = book.getGenre().split("\n");
                                for (String str : genres) {
                                    if (str.equals(genre)) {
                                        booksFound.add(book);
                                        break;
                                    }
                                }
                            }
                        }
                        alterButtons(booksFound);
                    }
                }
            }
        });


        tfFullName.setText(user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
        tfPhone.setText(user.getPhone().substring(2));
        tfEmail.setText(user.getEmail());
        tfFullName.setBorder(null);
        tfPhone.setBorder(null);
        tfEmail.setBorder(null);
        tfID.setBorder(null);
        tfID.setText(String.valueOf(user.getId()));

        labelEnterPassword.setVisible(false);
        pfPassword.setVisible(false);
        labelRepeatPassword.setVisible(false);
        pfPassword1.setVisible(false);

        btnChangePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = btnChangePassword.getText();
                if(text.equals("Изменить пароль")) {
                    btnChangeInfo.setEnabled(false);
                    labelEnterPassword.setVisible(true);
                    pfPassword.setVisible(true);
                    labelRepeatPassword.setVisible(true);
                    pfPassword1.setVisible(true);
                    pfPassword.setText("");
                    pfPassword1.setText("");
                    btnChangePassword.setText("Сохранить новый пароль");
                }
                else if(text.equals("Сохранить новый пароль")) {
                    String password = String.valueOf(pfPassword.getPassword());
                    String password1 = String.valueOf(pfPassword1.getPassword());

                    if (password.isEmpty() || password1.isEmpty())
                        showErrorMessage("Некоторые поля остались незаполненными", "Ошибка!");
                    else {
                        if(!password.equals(password1)){
                            showErrorMessage("Пароли не совпадают!", "Ошибка!");
                        }
                        else if (password.equals(user.getPassword()))
                            showErrorMessage("Новый пароль совпадает со старым!", "Ошибка!");
                        else {
                            updatePasswordInDatabase(password);
                            user.setPassword(password);
                            showInformationMessage("Пароль был успешно изменен!", "");
                            labelEnterPassword.setVisible(false);
                            pfPassword.setVisible(false);
                            labelRepeatPassword.setVisible(false);
                            pfPassword1.setVisible(false);
                            btnChangePassword.setText("Изменить пароль");
                            btnChangeInfo.setEnabled(true);
                        }
                    }
                }
            }
        });

        tfSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tfSearch.setText("");
            }
        } );


        btnChangeInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = btnChangeInfo.getText();
                String oldPhone = user.getPhone();
                String oldEmail = user.getEmail();

                if (text.equals("Редактировать данные")) {
                    //labelPopUpHint.setVisible(true);
                    btnChangePassword.setEnabled(false);
                    tfPhone.setEditable(true);
                    tfEmail.setEditable(true);
                    tfPhone.setOpaque(true);
                    tfEmail.setOpaque(true);
                    btnChangeInfo.setText("Сохранить");
                } else if (text.equals("Сохранить")) {
                    String updPhone = tfPhone.getText();
                    String updEmail = tfEmail.getText();

                   if(!Phone.isCorrectNumber(updPhone) || updEmail.isEmpty()) {
                        showErrorMessage("Неправильно указаны данные", "Ошибка!");
                        restoreOldValues();
                    }
                    else {
                        updPhone = "+7" + updPhone;
                        boolean samePhone = updPhone.equals(oldPhone);
                        boolean sameEmail = updEmail.equals(oldEmail);
                        boolean isUserExists = false;

                        if (sameEmail && samePhone) {

                        } else if (samePhone) { //в базе на существование проверяем только почту
                            if (dataExistsInDatabase("", updEmail)) {
                                showErrorMessage("Пользователь с таким номером телефона или адресом\n" +
                                        "электронной почты уже существует!", "Ошибка!");
                                isUserExists = true;
                                restoreOldValues();
                            }
                        } else if (sameEmail) {// только номер
                            if (dataExistsInDatabase(updPhone, "")) {
                                showErrorMessage("Пользователь с таким номером телефона или адресом\n" +
                                        "электронной почты уже существует!", "Ошибка!");
                                isUserExists = true;
                                restoreOldValues();
                            }
                        } else {// и почту и номер
                            if (dataExistsInDatabase(updPhone, updEmail)) {
                                showErrorMessage("Пользователь с таким номером телефона или адресом\n" +
                                        "электронной почты уже существует!", "Ошибка!");
                                isUserExists = true;
                                restoreOldValues();
                            }
                        }

                        if (!isUserExists) {
                            if (!(sameEmail && samePhone)) {
                                    updateDatabase(updPhone, updEmail, user.getId());
                                user.setPhone(updPhone);
                                user.setEmail(updEmail);
                            }
                        }
                        tfEmail.setEditable(false);
                        tfPhone.setEditable(false);
                        tfEmail.setOpaque(false);
                        tfPhone.setOpaque(false);
                        btnChangeInfo.setText("Редактировать данные");
                        btnChangePassword.setEnabled(true);
                    }
                }
            }
        });


        JButton[] changeButtons = {btnChangePassword, btnChangeInfo};
        for(JButton btn : changeButtons){
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(189, 155, 242));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(242, 242, 242));
                }
            });
        }


        taHelp.setText(helpText);
        spLeft.setBorder(null);
        spRight.setBorder(null);


//        btnMoveToCatalogue.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                cbGenres.setVisible(true);
//                tfSearch.setVisible(true);
//                tfSearch.setText("Поиск");
//                restoreButtons();
//                cardLayout.show(pnlCards, "pnlCardLibrary");
//            }
//        });

//        btnMoveToCatalogue.addMouseListener(new MouseAdapter() {
//            Font font = btnMoveToCatalogue.getFont();
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                Font newFont = new Font(font.getFontName(), Font.ITALIC, 16);
//                btnMoveToCatalogue.setFont(newFont);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                btnMoveToCatalogue.setFont(font);
//            }
//        });

        setTwoMostPopularBooks();

        if(firstPopularBook == null){ // НЕТ ПОПУЛЯРНЫХ КНИГ

        } else{
            if(secondPopularBook == null) { // ТОЛЬКО ОДНА КНИГА
                labelLeftBookAuthor.setText("   " + firstPopularBook.getAuthor());
                labelLeftBookName.setText("   " +firstPopularBook.getName());
                String[] leftGenres = firstPopularBook.getGenre().split("\n");
                String leftText = "";
                for(int i = 0; i < leftGenres.length; i++){
                    if(i == leftGenres.length - 1)
                        leftText += leftGenres[i];
                    else
                        leftText += leftGenres[i] + ", ";
                }

                labelLeftBookGenre.setText("   " +leftText);
                labelLeftImage.setIcon(new ImageIcon(firstPopularBook.getImagepath()));

            } else{

                labelLeftBookAuthor.setText("   " + firstPopularBook.getAuthor());
                labelLeftBookName.setText("   " +firstPopularBook.getName());
                String[] leftGenres = firstPopularBook.getGenre().split("\n");
                String leftText = "";
                for(int i = 0; i < leftGenres.length; i++){
                    if(i == leftGenres.length - 1)
                        leftText += leftGenres[i];
                    else
                        leftText += leftGenres[i] + ", ";
                }

                labelLeftBookGenre.setText("   " +leftText);
                labelLeftImage.setIcon(new ImageIcon(firstPopularBook.getImagepath()));
                labelRightBookAuthor.setText("   " +secondPopularBook.getAuthor());
                labelRightBookName.setText("   " +secondPopularBook.getName());

                String[] rightGenres = secondPopularBook.getGenre().split("\n");
                String rightText = "";
                for(int i = 0; i < rightGenres.length; i++){
                    if(i == rightGenres.length - 1)
                        rightText += rightGenres[i];
                    else
                        rightText += rightGenres[i] + ", ";
                }


                labelRightBookGenre.setText("   " +rightText);

                labelRightImage.setIcon(new ImageIcon(secondPopularBook.getImagepath()));
            }
        }

       btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                createLoginForm();
            }
        });

        btnDeleteAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = showConfirmDialog("Все запланированные сеансы будут автоматически отменены.\n\n" +
                        "Вы уверены, что хотите удалить аккаунт?", "Удалить аккаунт");
                if(answer == 0){ // удаляем аккаунт
                    deleteFollowingSessionsFromDatabase(getUserId());
                    deleteUserFromDatabase(getUserId());
                    dispose();
                    createLoginForm();
                }
            }
        });

        setVisible(true);

    }

    private void deleteFollowingSessionsFromDatabase(int userId){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                    Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){

            String query = "select id, userid, date, starttime from sessions where userid = '" + userId + "';";
            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                if(isFollowingMoment(res.getString("date"),
                        Integer.parseInt(res.getString("starttime").substring(0, 2 ))) == 1)
                    res.deleteRow();
            }

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void deleteUserFromDatabase(int userId){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()) {

            String query = "delete from users where userid = '" + userId + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private int showConfirmDialog(String message, String title){
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
    }

    private ArrayList<Book> getBookListFromDB(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            ArrayList<Book> books = new ArrayList<>();
            String query  = "select * from books where imagepath != '';";
            ResultSet res = statement.executeQuery(query);
            while(res.next()){
                Book book = new Book();
                book.setName(res.getString("name"));
                book.setAuthor(res.getString("author"));
                book.setYearOfPublishing(res.getInt("yearofpublishing"));
                book.setDescription(res.getString("description"));
                book.setRating(res.getFloat("rating"));
                book.setImagepath(res.getString("imagepath"));
                book.setGenre(res.getString("genre"));
                books.add(book);
            }
         return books;

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return null;
    }

    private int calculateRowsNum(int numOfBooks){
        if(numOfBooks % 2 == 0)
            return numOfBooks/2;
        else
            return numOfBooks/2 + 1;
    }

    private void updatePasswordInDatabase(String password){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "update users set password = '" + password + "' where userid = '" + getUserId() + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private LoginForm createLoginForm(){
        return new LoginForm(null);
    }

    private void createSessionsTable(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            LocalDate currentDate = LocalDate.now();
            int currentDay = currentDate.getDayOfMonth();
            int currentMonth = currentDate.getMonthValue();
            ArrayList<Session> sessions = new ArrayList<>();

            final int numOfColumns = 5;
            String query = "";
            ResultSet res;


            query = "select book, author, date, starttime, endtime from sessions where userid = '" + getUserId() + "';";
            res = statement.executeQuery(query);
            while(res.next()) {
                if(isFollowingMoment(res.getString("date"),
                        Integer.parseInt(res.getString("starttime").substring(0, 2))) == 1)
                    sessions.add(new Session(res.getString("book"), res.getString("author"), res.getString("date"),
                            res.getString("starttime") + " - " + res.getString("endtime"), null));

//                String date = res.getString("date");
//                int day = Integer.parseInt(date.substring(0, 2));
//                int month = Integer.parseInt(date.substring(3));
//                if(currentMonth == month)
//                    if(currentDay <= day)
//                        sessions.add(new Session(res.getString("book"), res.getString("author"), date,
//                                    res.getString("starttime") + " - " + res.getString("endtime"), null));
//                if(currentMonth < month)
//                    sessions.add(new Session(res.getString("book"), res.getString("author"), date,
//                            res.getString("starttime") + " - " + res.getString("endtime"), null));
            }

            sortArrayList(sessions);
            //надо отсортировать массив

            DefaultTableModel model = new DefaultTableModel();
            JTable sessionTable = new JTable(model){
              //  private static final long serialVersionUID = 1L;

                public boolean isCellEditable(int row, int col)
                {
                    return false;
                }
            };

            model.addColumn("Книга");
            model.addColumn("Автор");
            model.addColumn("Дата сеанса");
            model.addColumn("Время сеанса");

            for(int i = 0 ; i < sessions.size(); i++){
                Session session = sessions.get(i);
                Object[] row = {session.getBook(), session.getAuthor(), session.getDate(), session.getTime() };
                model.addRow(row);
            }

            sessionTable.setRowHeight(50);
            JScrollPane sp = new JScrollPane(sessionTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setPreferredSize(new Dimension(800, 40));
            btnDeleteSessions = new JButton("Отменить сеанс");

            ListSelectionModel listSelectionModel = sessionTable.getSelectionModel();
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {
               @Override
                public void valueChanged(ListSelectionEvent e) {
                   btnDeleteSessions.setEnabled(false);
                   int[] selectedSessions = sessionTable.getSelectedRows();
                   if(selectedSessions.length == 1)
                    btnDeleteSessions.setEnabled(true);
                }
            });


            btnDeleteSessions.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedSessions = sessionTable.getSelectedRows();
                    for(int i =0; i < selectedSessions.length; i++){
                        int selectedIndex = selectedSessions[i];
                        DefaultTableModel model = (DefaultTableModel) sessionTable.getModel();
                        String bookName = (String) model.getValueAt(selectedIndex, 0);
                        String date = (String) model.getValueAt(selectedIndex, 2);
                        String startTime = ((String) model.getValueAt(selectedIndex, 3)).substring(0, 5);
                        deleteSessionFromDatabase(bookName, date, startTime);
                        //((DefaultTableModel)sessionTable.getModel()).removeRow(selectedIndex);
                        model.removeRow(selectedIndex);
                    }
                }
            });

            btnDeleteSessions.setPreferredSize(new Dimension(250, 40));
            btnDeleteSessions.setContentAreaFilled(false);
            btnDeleteSessions.setEnabled(false);
            southPanel.add(btnDeleteSessions, BorderLayout.WEST);


            pnlCardPlannedSessions.add(sp, BorderLayout.CENTER);
            pnlCardPlannedSessions.add(southPanel, BorderLayout.SOUTH);

            //sessionTable

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private int isFollowingMoment( String date, int startHour){
        LocalDateTime currentMoment = LocalDateTime.now();
        int currentDay = currentMoment.getDayOfMonth();
        int currentMonth = currentMoment.getMonthValue();
        int currentHour = currentMoment.getHour();
        int month = Integer.parseInt( date.substring(3));
        int day =  Integer.parseInt(date.substring(0, 2));

        if(month > currentMonth)
            return 1;
        else if(month == currentMonth){
            if(day > currentDay)
                return 1;
            else if(day == currentDay){
                if(startHour == currentHour)
                    return 2;
                else if(startHour > currentHour)
                    return 1;
            }
        }
        return 0;
    }

    private void updateProfileCard(){
        tfEmail.setEditable(false);
        tfPhone.setEditable(false);
        btnChangeInfo.setText("Редактировать данные");
        tfEmail.setText(user.getEmail());
        tfPhone.setText(user.getPhone().substring(2));

        labelEnterPassword.setVisible(false);
        pfPassword.setText("");
        pfPassword.setVisible(false);
        labelRepeatPassword.setVisible(false);
        pfPassword1.setText("");
        pfPassword1.setVisible(false);
        btnChangePassword.setText("Изменить пароль");

        btnChangePassword.setEnabled(true);
        btnChangeInfo.setEnabled(true);
    }

    private void setTwoMostPopularBooks(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            ArrayList<String> bookNamesForLastMonth = new ArrayList<>();
            int currentMonth = LocalDate.now().getMonthValue();

            ResultSet res = statement.executeQuery("select date, book from sessions where userarrived = 1;");
            while(res.next()){
                int month = Integer.parseInt(res.getString("date").substring(3));
                if(currentMonth == month || currentMonth == month + 1)
                   bookNamesForLastMonth.add(res.getString("book"));
            }

            firstPopularBook = getPopularBook(getMostPopularBookName(bookNamesForLastMonth));
            if(firstPopularBook == null)
                secondPopularBook = null;
            else {
                //secondPopularBook = getPopularBook(getMostPopularBookName(bookNamesForLastMonth));
                uniqueBooks.remove(firstPopularBook.getName());

                int maxValue = 0;
                String book  = "";
                for(Map.Entry<String, Integer> set : uniqueBooks.entrySet()){
                    int value = set.getValue();
                    if(value > maxValue){
                        maxValue = value;
                        book = set.getKey();
                    }
                }

                secondPopularBook = getPopularBook(book);
            }

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private String getMostPopularBookName(ArrayList<String> books){
       uniqueBooks = new HashMap<>();
        for (String book : books) {
            if (uniqueBooks.containsKey(book)) {
                int oldValue = uniqueBooks.get(book);
                uniqueBooks.replace(book, oldValue + 1);
            } else {
                uniqueBooks.put(book, 1);
            }
        }

        int maxValue = 0;
        String book  = "";
        for(Map.Entry<String, Integer> set : uniqueBooks.entrySet()){
            int value = set.getValue();
            if(value > maxValue){
                maxValue = value;
                book = set.getKey();
            }
        }
        return book;
    }

    private Book getPopularBook(String name){
        try(Connection connection =DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            ResultSet res = statement.executeQuery("select author, genre, imagepath from books where name = '" + name + "';");
            if(res.next())
                return new Book(name, res.getString("author"),-1, "", res.getString("imagepath"), 0,
                        res.getString("genre"));
            else
                return null;

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return null;
    }

    private boolean dataExistsInDatabase(String phone, String email){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            String query = "";
            if(phone.isEmpty()){ // проверяем почту
                query = "select userid from users where email = '" + email + "';";
            } else if(email.isEmpty()){//проверяем телефон
                    query = "select userid from users where phone = '" + phone + "';";
            } else{
                query = "select userid from users where phone = '" + phone + "' or email = '" + email + "';";
            }

            ResultSet res = statement.executeQuery(query);
            return res.next();
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return false;
    }

    private void restoreOldValues(){
        tfPhone.setText(user.getPhone().substring(2));
        tfEmail.setText(user.getEmail());
    }

    private void deleteSessionFromDatabase(String bookName, String date, String startTime){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            String query = "delete from sessions where book = '" + bookName + "' and date = '" + date + "' and starttime = '" + startTime + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void updateDatabase(String phone, String email, int userId){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){
            String query = "update users set phone = '" + phone + "', email = '" + email + "' where userid = '" + userId + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void restoreButtons(){
        String imagepath;
        for(int i = 0; i < allBooksFromDB.length; i++){
            imagepath = allBooksFromDB[i].getImagepath();
            bookButtons[i].setIcon(new ImageIcon(imagepath, imagepath));
            bookButtons[i].setVisible(true);

            ActionListener[] actionListeners = bookButtons[i].getActionListeners();
            for(ActionListener actionListener : actionListeners) {
                bookButtons[i].removeActionListener(actionListener);
            }

            final Book book = allBooksFromDB[i];
            bookButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createBookInfoForm(book);
                }
            });
        }
    }

    private int getNumOfUsersForMonth(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement= connection.createStatement()){

            int currentMonth = LocalDate.now().getMonthValue();
            String query = "select date, userid from sessions where userarrived = 1;";
            ResultSet res = statement.executeQuery(query);
            HashSet<Integer> usersForMonth = new HashSet<>();

            while(res.next()){
                int month = Integer.parseInt(res.getString("date").substring(3));
                if(currentMonth == month || currentMonth == month + 1)
                    usersForMonth.add(res.getInt("userid"));
            }

            return usersForMonth.size();

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return -1;
    }

    private void sortArrayList(ArrayList<Session> sessions){
            //МЕТОД ПУЗЫРЬКОВОЙ СОРТИРОВКИ
            for (int out = sessions.size() - 1; out >= 1; out--){
                for (int in = 0; in < out; in++){
//                    if(a[in] > a[in + 1])
//                        toSwap(in, in + 1);
                int inDay = Integer.parseInt(sessions.get(in).getDate().substring(0, 2));
                int inMonth = Integer.parseInt(sessions.get(in).getDate().substring(3));
                int inStartTime = Integer.parseInt(sessions.get(in).getTime().substring(0, 2));
                int outDay = Integer.parseInt(sessions.get(out).getDate().substring(0, 2));
                int outMonth = Integer.parseInt(sessions.get(out).getDate().substring(3));
                int outStartTime = Integer.parseInt(sessions.get(out).getTime().substring(0, 2));

                if(inMonth > outMonth){
                    //меняем местами
                    Session temp = sessions.get(out);
                    sessions.set(out, sessions.get(in));
                    sessions.set(in, temp);
                }
                if(inMonth == outMonth){
                    if(inDay > outDay){
                        Session temp = sessions.get(out);
                        sessions.set(out, sessions.get(in));
                        sessions.set(in, temp);
                    }
                    if(inDay == outDay){
                        if(inStartTime > outStartTime) {
                            Session temp = sessions.get(out);
                            sessions.set(out, sessions.get(in));
                            sessions.set(in, temp);
                        }
                    }
                }

                }
            }
    }

    private void displayButtonsOnPanel(Book[] books){
        int num = books.length;

        JPanel panel = new JPanel();
        // panel.setBorder(BorderFactory.createLineBorder(Color.red));
        int numOfRows = calculateRowsNum(num);

        // 7 рядов - высота - 2000
        int panelHeight = calculateCataloguePanelHeight(numOfRows);

        panel.setPreferredSize(new Dimension(800, panelHeight));
        panel.setLayout(new GridLayout( calculateRowsNum(num), 2));


        bookButtons = new JButton[num];
        for(int i = 0; i < num; i++){
            bookButtons[i] = new JButton();
            final JButton btn = bookButtons[i];

            final Book book = books[i];
            btn.setMinimumSize(new Dimension(200, 200));
            btn.setMaximumSize(new Dimension(300, 300));
            btn.setPreferredSize(new Dimension(250, 250));
            //btn.setText(book.getName() + ". " + book.getAuthor());
            btn.setIcon(new ImageIcon(book.getImagepath(), book.getImagepath()));
            btn.setContentAreaFilled(false);
            //btn.setBorderPainted(false);
            btn.setOpaque(true);
            //btn.setBackground(new Color(55, 78, 242));

            btn.addMouseListener(new MouseAdapter() {
                Color oldColor = btn.getBackground();

                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground( new Color(156, 121, 242));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(oldColor);
                }
            });

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createBookInfoForm(book);
                }
            });


            panel.add(bookButtons[i]);

        }




        JScrollPane catalogue = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        catalogue.setWheelScrollingEnabled(false);

        pnlCardLibrary.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(150, 75, 213));
        topPanel.setPreferredSize(new Dimension(800, 40));
        tfSearch.setPreferredSize(new Dimension(200, 25));
        cbGenres.setPreferredSize(new Dimension(200, 25));
        topPanel.add(tfSearch);
        topPanel.add(cbGenres);


        pnlCardLibrary.add(catalogue, BorderLayout.CENTER);
        pnlCardLibrary.add(topPanel, BorderLayout.NORTH);
    }

    private void alterButtons(ArrayList<Book> booksFound){
        int pos;
        for(pos = 0; pos < booksFound.size(); pos++){
            String imagepath = booksFound.get(pos).getImagepath();
            bookButtons[pos].setIcon(new ImageIcon(imagepath, imagepath));

           ActionListener[] actionListeners = bookButtons[pos].getActionListeners();
           for(ActionListener listener : actionListeners)
               bookButtons[pos].removeActionListener(listener);

           bookButtons[pos].addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) { // должны найти книгу в allBooksFromDb с таким же imagepath
                    int index = BinarySearch.binarySearch(allBooksFromDB, imagepath, 0, allBooksFromDB.length - 1);
                    Book book = allBooksFromDB[index];
                    createBookInfoForm(book);
               }
           });
        }

        for(int i = pos; i < allBooksFromDB.length; i++) {
            bookButtons[i].setVisible(false);
        }
    }

    private BookInfo createBookInfoForm(Book book){
        return new BookInfo(null,  book, getUserId());
    }

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInformationMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public int getUserId(){
        return user.getId();
    }

    private int calculateCataloguePanelHeight(final int numOfRows){
        // 7 рядов - 2000
        // numOfRows рядов - x
        return (int) 2000*numOfRows/7;
    }


}
