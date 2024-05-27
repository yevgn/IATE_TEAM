package UserApp;

import mainClasses.Book;
import mainClasses.Phone;
import mainClasses.Session;
import mainClasses.User;
import  mainClasses.BookInfoWithoutGenres;

import database.Database;

import searchSortAlgorithms.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JDialog{
    private JPanel panel1;
    private JButton btnProfile;
    private JButton btnHelp;
    private JButton btnPlannedSessions;
    private JButton btnLibrary;
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
    private JLabel labelLeftBookGenre;
    private JLabel labelUsersForMonth;
    private JLabel labelBooksAvailable;
    private JLabel labelLeftImage;
    private JScrollPane spLeft;
    private JTextField tfID;
    private JButton btnLogOut;
    private JButton btnDeleteAccount;
    private JButton btnLeftBook;
    private JButton btnRightBook;
    private JLabel tfGreeting;
    private JLabel tfText;
    private JPanel MainCardCenterPanel;
    private JPanel pnlEditData;
    private JPanel pnlChangePassw;
    private JPanel pnlRecommendedBooks;
    //private JPanel pnlLibrary;
    private CardLayout cardLayout;
    private User user;
    private JButton btnDeleteSessions;
    private String[] favGenres = new String[2];
    private JButton[] bookButtons;
    private Book[] allBooksFromDB;
    private final int RECOMMENDED_BOOKS_LIMIT = 8;
    private final String helpText = "Если у вас возникли проблемы или вы хотите получить дополнительную информацию, " +
            "свяжитесь\nс командой разработчкиков.";
    private final String WELCOME = "Добро пожаловать";
    private final String LABEL_BOOK_TEXT = "Доступно книг: ";
    private ArrayList<Book> recommendedBooks = new ArrayList<>();
    String QUERY_GET_ALL_BOOKS_FROM_DB  = "select b.name AS book, b.year_publish AS year, b.rating AS b_rating," +
            " b.description AS descr, b.imagepath AS b_imagepath, " +
            "a.name AS author_name, a.surname AS author_surname, g.name AS genre " +
            "from book_genre bg join books b on bg.book_id = b.id join " +
            "genres g on g.id = bg.genre_id join authors a on a.id = b.author_id order by b.name;";
   private ArrayList<Session> ss;
   private final String NO_GENRE_MATTER = "Все жанры";

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

        fillCbGenre();

        JButton[] buttons = {btnMain, btnLibrary, btnPlannedSessions, btnProfile, btnHelp};
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
                setFavGenres();
                showRecommendedBooks();
                cardLayout.show(pnlCards, "pnlCardMain");
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

                pnlCardPlannedSessions.removeAll();
                ss = getFollowingSessionsFromDatabase();
                createSessionsTable();
                btnDeleteSessions.setEnabled(false);
                pnlCardPlannedSessions.updateUI();

                cardLayout.show(pnlCards, "pnlCardPlannedSessions");
            }
        });

        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfileCard();
                cardLayout.show(pnlCards, "pnlCardProfile");
            }
        });

        btnHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "pnlCardHelp");
            }
        });

        allBooksFromDB = getBookListFromDB(QUERY_GET_ALL_BOOKS_FROM_DB).toArray(new Book[0]); // книги с БД
        QuickSort.quickSort(allBooksFromDB, 0, allBooksFromDB.length - 1);
        labelBooksAvailable.setText(LABEL_BOOK_TEXT + allBooksFromDB.length);
        displayButtonsOnPanel(allBooksFromDB);

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // поиск по названию книги
                    String text = tfSearch.getText();
                    String genre = (String) cbGenres.getSelectedItem();
                    boolean genreMatters = !genre.equals(NO_GENRE_MATTER);
                    boolean textMatters = !text.isEmpty();

                    if (!genreMatters && !textMatters)
                        restoreButtons();
                    else {
                        restoreButtons();
                        ArrayList<Book> booksFound = new ArrayList<>();
                        for (Book book : allBooksFromDB) {
                            if (textMatters) {
                                if (book.getName().contains(text) || book.getAuthorName().contains(text)
                                    || book.getAuthorSurname().contains(text) ) { // добавляем ее на окошко
                                    if (!genreMatters)
                                        booksFound.add(book);
                                    else {
                                        String[] genres = book.getGenres();
                                        for (String str : genres) {
                                            if (str.equals(genre)) {
                                                booksFound.add(book);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                String[] genres = book.getGenres();
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

        tfFullName.setText(user.getSurname() + " " + user.getName() + " " +
                (user.getPatronymic() == null ? "" : user.getPatronymic()));
        tfPhone.setText(user.getPhone());
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
                        boolean samePhone = updPhone.equals(oldPhone);
                        boolean sameEmail = updEmail.equals(oldEmail);
                        boolean isUserExists = false;

                        if (sameEmail && samePhone) {

                        } else if (samePhone) { //в базе на существование проверяем только почту
                            if (dataExistsInDatabase("", updEmail)) {
                                showErrorMessage("Пользователь с таким адресом\n" +
                                        "электронной почты уже существует!", "Ошибка!");
                                isUserExists = true;
                                restoreOldValues();
                            }
                        } else if (sameEmail) {// только номер
                            if (dataExistsInDatabase(updPhone, "")) {
                                showErrorMessage("Пользователь с таким номером телефона\n" +
                                        "уже существует!", "Ошибка!");
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

        pnlEditData.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pnlChangePassw.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

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

        tfGreeting.setText(WELCOME + ", " + user.getName() + "!");
        MainCardCenterPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

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

            String query = "delete from sessions where user_id = '" + userId + "' and start_time > current_timestamp";
            statement.executeUpdate(query);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void deleteUserFromDatabase(int userId){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()) {

            String query = "delete from users where id = '" + userId + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private int showConfirmDialog(String message, String title){
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
    }

    private ArrayList<Book> getBookListFromDB(final String QUERY){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){
            HashMap<BookInfoWithoutGenres, ArrayList<String>> books = new HashMap<>();
            BookInfoWithoutGenres bookWithoutGenres;

            ResultSet res = statement.executeQuery(QUERY);

            while(res.next()){
                bookWithoutGenres = new BookInfoWithoutGenres();
                bookWithoutGenres.setName(res.getString("book"));
                bookWithoutGenres.setAuthorName(res.getString("author_name"));
                bookWithoutGenres.setAuthorSurname(res.getString("author_surname"));
                bookWithoutGenres.setYearOfPublishing(res.getInt("year"));
                bookWithoutGenres.setDescription(res.getString("descr"));
                bookWithoutGenres.setRating(res.getFloat("b_rating"));
                bookWithoutGenres.setImagepath(res.getString("b_imagepath"));

                ArrayList<String> val = books.get(bookWithoutGenres);
                if(val == null){
                    val = new ArrayList<>();
                    val.add(res.getString("genre"));
                    books.put(bookWithoutGenres, val);
                } else val.add(res.getString("genre"));

            }

         return convertIntoBookArray(books);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return null;
    }

    private void fillCbGenre(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            final String query = "select name from genres";
            ResultSet res = statement.executeQuery(query);

            cbGenres.addItem(NO_GENRE_MATTER);
            while(res.next())
                cbGenres.addItem(res.getString("name"));

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private ArrayList<Book> convertIntoBookArray(HashMap<BookInfoWithoutGenres, ArrayList<String>> books){
        ArrayList<Book> res = new ArrayList<>(books.size());
        Book book;
        BookInfoWithoutGenres key;
        ArrayList<String> val;

        for(Map.Entry<BookInfoWithoutGenres, ArrayList<String>> entry : books.entrySet()){
            book = new Book();
            key = entry.getKey();
            val = entry.getValue();

            book.setName(key.getName());
            book.setAuthorName(key.getAuthorName());
            book.setAuthorSurname(key.getAuthorSurname());
            book.setDescription(key.getDescription());
            book.setImagepath(key.getImagepath());
            book.setRating(key.getRating());
            book.setYearOfPublishing(key.getYearOfPublishing());
           book.setGenres(val.toArray( new String[0]));

            res.add(book);
        }

        return res;
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

            String query = "update users set password = '" + password + "' where id = '" + getUserId() + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private LoginForm createLoginForm(){
        return new LoginForm(null);
    }


    private ArrayList<Session> getFollowingSessionsFromDatabase(){
        ArrayList<Session> ss = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            Session s;
            ResultSet res;

            final String query = "select b.name AS book, a.name AS author_name, a.surname AS author_surname," +
                    " (select extract(year from start_time)) AS year, (select extract(day from start_time)) AS day," +
                    " (select extract(month from start_time)) AS month," +
                    " (select extract(hour from start_time)) AS start_hour," +
                    " (select extract(hour from end_time)) AS end_hour " +
                    "from sessions s join books b on b.id = s.book_id join authors a on a.id = b.author_id where s.user_id ='" +
                    user.getId() + "' and ( (select extract(day from start_time)) = (select extract(day from current_timestamp)) or " +
                    "start_time > current_timestamp)" +
                    "order by start_time;";

            res = statement.executeQuery(query);
            while(res.next()) {
                s = new Session();
                s.setBookName(res.getString("book"));
                s.setAuthorName(res.getString("author_name"));
                s.setAuthorSurname(res.getString("author_surname"));
                s.setYear(res.getString("year"));
                s.setMonth(res.getString("month"));
                s.setDay(res.getString("day"));
                s.setStartHour(res.getString("start_hour"));
                s.setEndHour(res.getString("end_hour"));
                s.setUserId(user.getId());

                ss.add(s);
            }
            return ss;
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return null;
    }

    private void createSessionsTable(){

        DefaultTableModel model = new DefaultTableModel();
        JTable sessionsTable = new JTable(model){
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        model.addColumn("Книга");
        model.addColumn("Автор");
        model.addColumn("Дата сеанса");
        model.addColumn("Время сеанса");

        for(int i = 0 ; i < ss.size(); i++){
            Session session = ss.get(i);
            String day = session.getDay().length() == 1 ? "0".concat(session.getDay()) : session.getDay();
            String month = session.getMonth().length() == 1 ? "0".concat(session.getMonth()) : session.getMonth();
            String startHour = session.getStartHour().length() == 1 ? "0".concat(session.getStartHour()) : session.getStartHour();
            String endHour = session.getEndHour().length() == 1 ? "0".concat(session.getEndHour()) : session.getEndHour();

            Object[] row = {session.getBookName(), session.getAuthorName() + " " + session.getAuthorSurname(),
                   day + "." + month + "." + session.getYear(),
                           startHour +  ":" + "00" + " - " + endHour + ":" + "00" };
            model.addRow(row);
        }

        sessionsTable.setRowHeight(50);
        JScrollPane sp = new JScrollPane(sessionsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.setBackground(new Color(234, 222, 189));
        southPanel.setPreferredSize(new Dimension(800, 40));
        btnDeleteSessions = new JButton("Отменить сеанс");

        ListSelectionModel listSelectionModel = sessionsTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnDeleteSessions.setEnabled(false);
                int[] selectedSessions = sessionsTable.getSelectedRows();
                if(selectedSessions.length == 1)
                    btnDeleteSessions.setEnabled(true);
            }
        });

        btnDeleteSessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedSessions = sessionsTable.getSelectedRows();
                for(int i =0; i < selectedSessions.length; i++){
                    int selectedIndex = selectedSessions[i];
                    DefaultTableModel model = (DefaultTableModel) sessionsTable.getModel();
                    Session session = ss.get(selectedIndex);
                    String bookName = session.getBookName();
                    String startTime = session.getYear() + "-" + session.getMonth() + "-" + session.getDay() + " " +
                            session.getStartHour() + ":" + "00" + ":" + "00";
                    deleteSessionFromDatabase(bookName, startTime);
                    model.removeRow(selectedIndex);
                    ss.remove(selectedIndex);
                }
            }
        });

        btnDeleteSessions.setPreferredSize(new Dimension(250, 40));
        btnDeleteSessions.setContentAreaFilled(false);
        btnDeleteSessions.setEnabled(false);
        southPanel.add(btnDeleteSessions, BorderLayout.WEST);

        pnlCardPlannedSessions.add(sp, BorderLayout.CENTER);
        pnlCardPlannedSessions.add(southPanel, BorderLayout.SOUTH);
    }


    private void updateProfileCard(){
        tfEmail.setEditable(false);
        tfPhone.setEditable(false);
        btnChangeInfo.setText("Редактировать данные");
        tfEmail.setText(user.getEmail());
        tfPhone.setText(user.getPhone());

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

    private void setFavGenres(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            ResultSet res = statement.executeQuery("select g.name AS genre, count(g.name) from sessions s join book_genre bg " +
                    "on s.book_id = bg.book_id join genres g on bg.genre_id = g.id where s.user_id = '" + user.getId() + "'" +
                    " group by(g.name) " +
                    "order by count(g.name) desc LIMIT 2;");

            int i = 0;
            while(res.next())
                favGenres[i++] = res.getString("genre");

            final String QUERY = "select b.name AS book, b.year_publish AS year, b.rating AS b_rating," +
                    " b.description AS descr, b.imagepath AS b_imagepath, " +
                    "a.name AS author_name, a.surname AS author_surname, g.name AS genre " +
                    "from book_genre bg join books b on bg.book_id = b.id join " +
                    "genres g on g.id = bg.genre_id join authors a on a.id = b.author_id where g.name = '" + favGenres[0] +
                    "' or g.name = '" + favGenres[1] + "' order by b.name";


        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void showRecommendedBooks(){
       MainCardCenterPanel.removeAll();

       pnlRecommendedBooks = new JPanel(new GridLayout(calculateRowsNum(recommendedBooks.size()), 2));
       int count = 0;
        for (Book value : allBooksFromDB) {
            if (count == RECOMMENDED_BOOKS_LIMIT)
                break;

            String[] genres = value.getGenres();
            for (String genre : genres) {
                if (genre.equals(favGenres[0]) || genre.equals(favGenres[1])) {
                    final Book book = value;
                    JButton btn = new JButton(new ImageIcon(book.getImagepath()));
                    btn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            createBookInfoForm(book);
                        }
                    });
                    pnlRecommendedBooks.add(btn);
                    count++;
                    break;
                }
            }
        }

       JScrollPane sp = new JScrollPane(pnlRecommendedBooks, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       sp.setWheelScrollingEnabled(false);
       MainCardCenterPanel.add(sp, BorderLayout.CENTER);
       MainCardCenterPanel.updateUI();
    }

    private boolean dataExistsInDatabase(String phone, String email){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){
            String query = "";
            if(phone.isEmpty()){ // проверяем почту
                query = "select id from users where email = '" + email + "';";
            } else if(email.isEmpty()){//проверяем телефон
                    query = "select id from users where phone = '" + phone + "';";
            } else{
                query = "select id from users where phone = '" + phone + "' or email = '" + email + "';";
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
        tfPhone.setText(user.getPhone());
        tfEmail.setText(user.getEmail());
    }

    private void deleteSessionFromDatabase(String bookName, String startTime){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            String query = "delete from sessions where book_id = (select id from books where name = '" + bookName + "') and" +
                    " start_time = '" + startTime + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void updateDatabase(String phone, String email, int userId){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){
            String query = "update users set phone = '" + phone + "', email = '" + email + "' where id = '" + userId + "';";
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


    private void displayButtonsOnPanel(Book[] books){
        int num = books.length;

        JPanel panel = new JPanel();
        // panel.setBorder(BorderFactory.createLineBorder(Color.red));
        int numOfRows = calculateRowsNum(num);

        // 7 рядов - высота - 2000
       // int panelHeight = calculateCataloguePanelHeight(numOfRows);

      //  panel.setPreferredSize(new Dimension(800, panelHeight));
        panel.setLayout(new GridLayout(numOfRows, 2));


        bookButtons = new JButton[num];
        for(int i = 0; i < num; i++){
            bookButtons[i] = new JButton();
            final JButton btn = bookButtons[i];
            final Book book = books[i];

            btn.setMinimumSize(new Dimension(200, 200));
            btn.setMaximumSize(new Dimension(300, 300));
            btn.setPreferredSize(new Dimension(250, 250));
            //btn.setText(book.getName() + ". " + book.getAuthor());
          //  btn.setIcon(new ImageIcon(book.getImagepath(), book.getImagepath()));
            btn.setIcon(new ImageIcon(book.getImagepath()));
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
