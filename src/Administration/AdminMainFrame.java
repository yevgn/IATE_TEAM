package Administration;

import database.Database;
import mainClasses.Book;
import mainClasses.BookInfoWithoutGenres;
import mainClasses.SessionWithUserData;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminMainFrame extends JDialog {
    private JPanel panel1;
    private JButton btnTodaySessions;
    private JButton btnAllTimeSessions;
    private JButton btnBookList;
    private JButton btnUsers;
    private JButton btnBlackList;
    private JPanel pnlCards;
    private JPanel pnlTodaySessions;
    private JPanel pnlAllTimeSessions;
    private JComboBox cbDay;
    private JPanel pnlBookList;
    private JPanel pnlUsers;
    private JPanel pnlBlackList;
    CardLayout cardLayout;
    String today;
    boolean allTimeSessionsWasClicked = false;

    JTable usersTable;
    JTable tableBlackList;
    private final String PHONE_NUM_BEGIN = "+7";
    private ArrayList<SessionWithUserData> todaySessions = new ArrayList<>();
    private final String SESSION_STATUS_COMPLETED = "завершена";
    private ArrayList<Book> books = new ArrayList<>();

    public AdminMainFrame(JFrame parent){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(800, 510));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cardLayout = (CardLayout) pnlCards.getLayout();

        LocalDate currentDate = LocalDate.now();
        String day = String.valueOf(currentDate.getDayOfMonth());
        String month = String.valueOf(currentDate.getMonthValue());
        if(day.length() == 1)
            day = "0".concat(day);
        if(month.length() == 1)
            month = "0".concat(month);
        today = day + "." + month;

        JButton[] buttons = {btnTodaySessions, btnAllTimeSessions, btnUsers, btnBlackList, btnBookList};
        for(JButton btn : buttons){
            btn.addMouseListener(new MouseAdapter() {
                Color oldColor = btn.getBackground();
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(153, 167, 242));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(oldColor);
                }
            });
        }

        fillCbDay();
        createBookListPanel();

        btnTodaySessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pnlTodaySessions.removeAll();
                showTodaySessions();
                pnlTodaySessions.updateUI();
                cardLayout.show(pnlCards, "CardTodaySessions");
            }
        });

        btnAllTimeSessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardAllTimeSessions");
            }
        });

        btnBookList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardBookList");
            }
        });


        btnUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTableUsers();
                cardLayout.show(pnlCards, "CardUsers");
            }
        });

        btnBlackList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTableBlackList();
                cardLayout.show(pnlCards, "CardBlackList");
            }
        });


        cbDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(allTimeSessionsWasClicked)
                    pnlAllTimeSessions.remove(1);

               String date = (String)cbDay.getSelectedItem();

                String month = date.substring(3);
                 month = month.length() == 1 ? "0".concat(month) : month;
                 String day =date.substring(0, 2) ;
                 day = day.length() == 1 ? "0".concat(day) : day;


                 final String QUERY = "select s.status AS status, (select extract(year from start_time)) AS year, " +
                         "(select extract(month from start_time)) " +
                         "AS month, (select extract(day from start_time)) AS day," +
                         " (select name from books where id = s.book_id) AS book, (select surname from users where id = s.user_id)" +
                         " AS user_surname," +
                         " (select name from users where id = s.user_id) AS user_name," +
                         " (select patronymic from users where id = s.user_id) " +
                         "AS user_patronymic, (select phone from users where id = s.user_id) AS user_phone," +
                         " (select extract(hour from s.start_time)) AS start_hour, (select extract(hour from s.end_time)) " +
                         "AS end_hour from sessions s" +  " where (select extract(month from start_time)) = '"
                         + month + "' and " +
                         "(select extract(day from start_time)) = '" + day + "' order by start_time;";
              ArrayList<SessionWithUserData> sessions = getSessionsFromDatabase(QUERY);
              JTable table = createSessionTable(sessions);
              JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              pnlAllTimeSessions.add(sp, BorderLayout.CENTER);

                pnlAllTimeSessions.updateUI();
                allTimeSessionsWasClicked = true;
            }
        });

        setVisible(true);

    }

    private void showTodaySessions(){
        final String QUERY = "select s.status AS status, (select extract(year from start_time)) AS year, " +
                "(select extract(month from start_time)) " +
                "AS month, (select extract(day from start_time)) AS day," +
                " (select name from books where id = s.book_id) AS book, (select surname from users where id = s.user_id)" +
                " AS user_surname," +
                " (select name from users where id = s.user_id) AS user_name, (select patronymic from users where id = s.user_id) " +
                "AS user_patronymic, (select phone from users where id = s.user_id) AS user_phone," +
                " (select extract(hour from s.start_time)) AS start_hour, (select extract(hour from s.end_time)) " +
                "AS end_hour from sessions s" +
                " where (select extract(day from start_time)) = " +
                " (select extract(day from current_timestamp)) order by start_time;";
        todaySessions = getSessionsFromDatabase(QUERY);
        JTable table = createSessionTable(todaySessions);

        JButton btnMarkedCompleted = new JButton("Отметить завершенным");
        btnMarkedCompleted.setFocusPainted(false);
        btnMarkedCompleted.setEnabled(false);
        pnlTodaySessions.add(btnMarkedCompleted, BorderLayout.SOUTH);


        ListSelectionModel selModel = table.getSelectionModel();

        selModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnMarkedCompleted.setEnabled(false);
                int[] selectedRows = table.getSelectedRows();
                if(selectedRows.length == 1)
                    btnMarkedCompleted.setEnabled(true);
            }
        });

        btnMarkedCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    int selIndex = selectedRows[i];
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    SessionWithUserData s = todaySessions.get(selIndex);

                    String year = s.getYear();
                    String month = s.getMonth();
                    month = month.length() == 1 ? "0".concat(month) : month;
                    String day = s.getDay();
                    day = day.length() == 1 ? "0".concat(day) : day;
                    String start_hour = s.getStartHour();
                    start_hour = start_hour.length() == 1 ? "0".concat(start_hour) : start_hour;

                    String start_time = year + "-" + month + "-" + day + " " + start_hour + ":00:00";
                    setSessionCompletedInDatabase( s.getBookName() ,start_time);
                    model.setValueAt("завершена", selIndex, 4);
                    btnMarkedCompleted.setEnabled(false);
                }
            }
        });

        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnlTodaySessions.add(sp, BorderLayout.CENTER);

    }

    private void setSessionCompletedInDatabase(String bookName, String startTime){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){
            String query = "update sessions set status = '" + SESSION_STATUS_COMPLETED + "' where start_time = '" + startTime + "' " +
                    "and book_id = (select id from books where name = '" + bookName + "');";
           statement.executeUpdate(query);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void createBookListPanel(){

            DefaultTableModel model = new DefaultTableModel();
            JTable bookList = new JTable(model){
                public boolean isCellEditable(int row, int col)
                {
                    return false;
                }
            };
            JPanel panel = new JPanel(new BorderLayout());

            model.addColumn("Название");
            model.addColumn("Автор");
            model.addColumn("Год издетельства");
            model.addColumn("Средння оценка");
            model.addColumn("Жанр");

            bookList.getColumnModel().getColumn(0).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(1).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(2).setPreferredWidth(100);
            bookList.getColumnModel().getColumn(3).setPreferredWidth(100);
            bookList.getColumnModel().getColumn(4).setPreferredWidth(400);

            bookList.setRowHeight(40);


            final String QUERY = "select b.name AS book, g.name AS genre, a.name AS author_name, a.surname AS author_surname," +
                    " b.rating AS rating" +
                    ", b.year_publish AS year, b.imagepath AS imagepath, b.description AS descr from book_genre bg join books b on" +
                    " bg.book_id = b.id join genres g on g.id = bg.genre_id join authors a on a.id = b.author_id;";
            books = getBookListFromDB(QUERY);

            for(Book book : books){
                String[] genres = book.getGenres();
                String genre = "";
                for(int i = 0; i < genres.length; i++){
                    if(i == genres.length - 1)
                        genre += genres[i];
                    else
                        genre += genres[i] + ", ";
                }

                Object[] row = {book.getName(), book.getAuthorName() + " " + book.getAuthorSurname(),
                    book.getYearOfPublishing(), book.getRating(), genre};
                model.addRow(row);
            }

            panel.add(bookList, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              pnlBookList.add(sp, BorderLayout.CENTER);
              sp.setWheelScrollingEnabled(false);


            JButton btnAddBook = new JButton("Добавить книгу");
            JButton btnDeleteBook = new JButton("Убрать книгу");
            btnAddBook.setFocusPainted(false);
            btnDeleteBook.setFocusPainted(false);
            btnDeleteBook.setEnabled(false);

            JPanel pnlButtons = new JPanel(new GridLayout());
            pnlButtons.add(btnAddBook);
            pnlButtons.add(btnDeleteBook);

            pnlBookList.add(pnlButtons, BorderLayout.SOUTH);

            btnAddBook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createAddBookForm(model, books);
                }
            });

            ListSelectionModel selModel = bookList.getSelectionModel();

            selModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    btnDeleteBook.setEnabled(false);
                    int[] selectedRows = bookList.getSelectedRows();
                    if(selectedRows.length == 1) {
                        btnDeleteBook.setEnabled(true);
                    }
                }
            });

            btnDeleteBook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedRows = bookList.getSelectedRows();
                    DefaultTableModel model = (DefaultTableModel) bookList.getModel();
                    for(int i = 0; i < selectedRows.length; i++){
                        int selIndex = selectedRows[i];
                        deleteBookFromDatabase( books.get(selIndex).getName());
                        model.removeRow(selIndex);
                        books.remove(selIndex);
                    }
                    btnDeleteBook.setEnabled(false);
                }
            });


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
                bookWithoutGenres.setRating(res.getFloat("rating"));
                bookWithoutGenres.setImagepath(res.getString("imagepath"));

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

    private void createTableUsers(){
        pnlUsers.removeAll();

        DefaultTableModel model = new DefaultTableModel(){

            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        usersTable = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());

        model.addColumn("ID");
        model.addColumn("Фамилия");
        model.addColumn("Имя");
        model.addColumn("Отчество");
        model.addColumn("Телефон");
        model.addColumn("Email");
        model.addColumn("Возраст");
        model.addColumn("Блокировка");

        usersTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(6).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(7).setPreferredWidth(250);
        usersTable.setRowHeight(30);

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            String query = "select * from users order by id asc;";
            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                Object[] rowData = {res.getInt("id"), res.getString("surname"),
                        res.getString("name"), res.getString("patronymic"),
                        res.getString("phone"), res.getString("email"), res.getInt("age"),
                res.getBoolean("is_blocked") ? "Заблокирован" : ""};
                model.addRow(rowData);
            }

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }


        panel.add(usersTable, BorderLayout.CENTER);

        JScrollPane spUsers = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnlUsers.add(spUsers, BorderLayout.CENTER);

        JButton btnBanUser = new JButton("Заблокировать пользователя");
        JButton btnUnbanUser = new JButton("Разблокировать пользователя");
        btnUnbanUser.setFocusPainted(false);
        btnBanUser.setFocusPainted(false);
        btnBanUser.setEnabled(false);
        btnUnbanUser.setEnabled(false);

        JPanel pnlButtons = new JPanel(new GridLayout());
        pnlButtons.add(btnBanUser);
        pnlButtons.add(btnUnbanUser);

        JPanel pnlSearch = new JPanel(new FlowLayout());
        JTextField tfSearch = new JTextField();
        JLabel labelSearch = new JLabel("Поиск по Фамилии");
        labelSearch.setPreferredSize(new Dimension(150, 20));

        tfSearch.setPreferredSize(new Dimension(100, 20));

        pnlSearch.add(labelSearch);
        pnlSearch.add(tfSearch);
        pnlUsers.add(pnlSearch, BorderLayout.NORTH);

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String surnameToFind = tfSearch.getText();
                    TableModel model = usersTable.getModel();

                    if(surnameToFind != null && !surnameToFind.isEmpty()) {
                        int indexToScrollTo = 0;
                        for (int i = 0; i < usersTable.getRowCount(); i++) {
                            if (surnameToFind.equals(model.getValueAt(i, 1))) {
                                indexToScrollTo = i;
                                break;
                            }
                        }
                        usersTable.changeSelection(indexToScrollTo, 0, false, false);
                        int position = indexToScrollTo * usersTable.getRowHeight();
                        JScrollBar sb = spUsers.getVerticalScrollBar();
                        sb.setValue(position);
                    }
                }
            }
        });

        ListSelectionModel selModel = usersTable.getSelectionModel();

        selModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnBanUser.setEnabled(false);
                btnUnbanUser.setEnabled(false);
                int[] selectedRows = usersTable.getSelectedRows();
                if(selectedRows.length == 1) {
                    int selIndex = selectedRows[0];
                    DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
                    if (model.getValueAt(selIndex, 7).equals("Заблокирован"))
                        btnUnbanUser.setEnabled(true);
                    else
                        btnBanUser.setEnabled(true);
                } else{
                    btnUnbanUser.setEnabled(false);
                    btnBanUser.setEnabled(false);
                }
            }
        });

        btnBanUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //должен поменять значение isblocked и удалить все сеансы этого пользователя в sessions
                int[] selectedRows = usersTable.getSelectedRows();
                int selIndex = selectedRows[0];
                DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
                int userid =  (Integer) model.getValueAt(selIndex, 0);
                banUser(userid);
                deletePlannedSessions(userid);
                model.setValueAt("Заблокирован", selIndex, 7);
                btnBanUser.setEnabled(false);
                pnlUsers.updateUI();
            }
        });

        btnUnbanUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = usersTable.getSelectedRows();
                int selIndex = selectedRows[0];
                DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
                int userid = (Integer) model.getValueAt(selIndex, 0);
                unbanUser(userid);
                model.setValueAt("", selIndex, 7);
                btnUnbanUser.setEnabled(false);
                pnlUsers.updateUI();
            }
        });

        pnlUsers.add(pnlButtons, BorderLayout.SOUTH);
        pnlUsers.updateUI();

    }

    private void createTableBlackList(){
        pnlBlackList.removeAll();

        DefaultTableModel model = new DefaultTableModel(){
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        tableBlackList = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Фамилия");
        model.addColumn("Имя");
        model.addColumn("Отчество");
        model.addColumn("Телефон");
        model.addColumn("Email");
        model.addColumn("Возраст");

        tableBlackList.getColumnModel().getColumn(0).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(2).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(3).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(4).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(5).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(6).setPreferredWidth(250);

        tableBlackList.setRowHeight(30);

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            final String QUERY = "select * from users where is_blocked = true;";
            ResultSet res = statement.executeQuery(QUERY);

            while(res.next()){
                Object[] rowData = {res.getInt("id"), res.getString("surname"),
                        res.getString("name"), res.getString("patronymic"),
                        res.getString("phone"), res.getString("email"), res.getInt("age"),};
                model.addRow(rowData);
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tableBlackList, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pnlBlackList.add(sp, BorderLayout.CENTER);

            JPanel pnlSearch = new JPanel(new FlowLayout());
            JLabel labelSearch = new JLabel("Поиск по Фамилии");
            labelSearch.setPreferredSize(new Dimension(150, 20));
            JTextField tfSearch = new JTextField();
            tfSearch.setPreferredSize(new Dimension(100, 20));

            pnlSearch.add(labelSearch);
            pnlSearch.add(tfSearch);

            pnlBlackList.add(pnlSearch, BorderLayout.NORTH);

            JButton btnUnbanUser = new JButton("Разблокировать");
            btnUnbanUser.setFocusPainted(false);
            btnUnbanUser.setEnabled(false);

            tfSearch.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        String surnameToFind = tfSearch.getText();
                        TableModel model = tableBlackList.getModel();

                        int indexToScrollTo = 0;
                        for(int i = 0; i < tableBlackList.getRowCount(); i++){
                            if( surnameToFind.equals(model.getValueAt(i, 1)))  {
                                indexToScrollTo = i;
                                break;
                            }
                        }
                        

                        tableBlackList.changeSelection(indexToScrollTo, 0, false, false);
                        int position = indexToScrollTo * tableBlackList.getRowHeight();
                        JScrollBar sb = sp.getVerticalScrollBar();
                        sb.setValue(position);
                    }
                }
            });


           // btnUnbanUser.setPreferredSize(new Dimension(800, 20));
            pnlBlackList.add(btnUnbanUser, BorderLayout.SOUTH);

            ListSelectionModel listSelectionModel = tableBlackList.getSelectionModel();
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    btnUnbanUser.setEnabled(false);
                    int[] selectedRows = tableBlackList.getSelectedRows();
                    if(selectedRows.length == 1){
                        btnUnbanUser.setEnabled(true);
                    }
                }
            });


            btnUnbanUser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedRows = tableBlackList.getSelectedRows();
                    int selIndex = selectedRows[0];
                    DefaultTableModel defaultTableModel = (DefaultTableModel) tableBlackList.getModel();
                    int userid = (Integer) defaultTableModel.getValueAt(selIndex, 0);
                    unbanUser(userid);
                    defaultTableModel.removeRow(selIndex);
                    btnUnbanUser.setEnabled(false);
                }
            });




        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }

        pnlBlackList.updateUI();
    }


    private void deletePlannedSessions(int userid){ // если пользователь имел сеансы на этот день, то их удаляем и обновляем todaySessions
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){

            final String QUERY = "delete from sessions where user_id = '" + userid + "' and start_time > current_timestamp;";
            statement.executeUpdate(QUERY);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }


    private void unbanUser(int userid){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "update users set is_blocked = false where id = '" + userid + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void banUser(int userid){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "update users set is_blocked = true where id = '" + userid + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private addBookForm createAddBookForm(DefaultTableModel model, ArrayList<Book> books){
        return new addBookForm(null,  model, books);
    }

    private void deleteBookFromDatabase(String name){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "delete from books where name = '" + name + "';";
            statement.executeUpdate(query);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }


    private ArrayList<SessionWithUserData> getSessionsFromDatabase(final String QUERY){
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet res = statement.executeQuery(QUERY);
            ArrayList<SessionWithUserData> sessions = new ArrayList<>();
            SessionWithUserData s;

            while (res.next()) {
                s = new SessionWithUserData();
                s.setBookName(res.getString("book"));
                s.setStartHour(res.getString("start_hour"));
                s.setEndHour(res.getString("end_hour"));
                s.setUserName(res.getString("user_name"));
                s.setUserSurname(res.getString("user_surname"));
                s.setYear(res.getString("year"));
                s.setMonth(res.getString("month"));
                s.setDay(res.getString("day"));
                String userPatronymic = res.getString("user_patronymic");
                s.setUserPatronymic(userPatronymic.equals("null") ? null : userPatronymic);
                s.setStatus(res.getString("status"));

                s.setUserPhone(res.getString("user_phone"));

                sessions.add(s);
            }
            return sessions;
        } catch (SQLException exception) {
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            exception.printStackTrace();
        }

        return null;
    }

    private JTable createSessionTable(ArrayList<SessionWithUserData> sessions){
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model){
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        model.addColumn("Книга");
        model.addColumn("Время");
        model.addColumn("ФИО");
        model.addColumn("Телефон");
        model.addColumn("Статус сеанса");

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);

        table.setRowHeight(30);

        int i = 0;
        for(SessionWithUserData s : sessions){
            String startHour = s.getStartHour().length() == 1 ? "0".concat(s.getStartHour()) : s.getStartHour();
            startHour += ":00";
            String endHour = s.getEndHour().length() == 1 ? "0".concat(s.getEndHour()) : s.getEndHour();
            endHour += ":00";
            String patronymic = s.getUserPatronymic().equals("null") ? null : s.getUserPatronymic();

            Object[] row = { s.getBookName(), startHour + " - " + endHour, s.getUserSurname() + " " + s.getUserName() + " " +
                     patronymic,  PHONE_NUM_BEGIN + s.getUserPhone(), s.getStatus()};

            model.addRow(row);

        }

        return table;
    }


    private void fillCbDay(){
        //создать элементы комбобокс
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(30);
        LocalDate buf = startDate.plusDays(1);
        while(!buf.equals(endDate)){
            String day = String.valueOf(buf.getDayOfMonth());
            String month = String.valueOf(buf.getMonthValue());
            if(day.length() == 1)
                day = "0".concat(day);
            if(month.length() == 1)
                month = "0".concat(month);
            String date = day + "." + month;
            cbDay.addItem(date);
            buf = buf.plusDays(1);
        }


    }


    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
