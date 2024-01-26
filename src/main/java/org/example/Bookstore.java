package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.example.MenuCodes.*;

public class Bookstore {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        Session session = sessionFactory.openSession();

        try {
            while (true) {
                System.out.println("Choose an option:");
                System.out.println(UPDATE_BOOK_DETAILS + ". Update Book Details");
                System.out.println(LIST_BOOKS_BY_GENRE + ". List Books by Genre");
                System.out.println(LIST_BOOKS_BY_AUTHOR + ". List Books by Author");
                System.out.println(UPDATE_CUSTOMER_INFORMATION + ". Update Customer Information");
                System.out.println(VIEW_CUSTOMER_PURCHASE_HISTORY + ". View Customer's Purchase History");
                System.out.println(PROCESS_NEW_SALE + ". Process New Sale");
                System.out.println(CALCULATE_TOTAL_REVENUE_BY_GENRE + ". Calculate Total Revenue by Genre");
                System.out.println(GENERATE_SALES_REPORT + ". Generate Sales Report");
                System.out.println(GENERATE_REVENUE_REPORT_BY_GENRE + ". Generate Revenue Report by Genre");
                System.out.println(EXIT + ". Exit");
                System.out.println();

                String choice = scanner.nextLine();

                switch (getMenuCodeFromValue(choice)) {
                    case UPDATE_BOOK_DETAILS -> updateBookDetails(session);
                    case LIST_BOOKS_BY_GENRE -> listBooksByGenre(session);
                    case LIST_BOOKS_BY_AUTHOR -> listBooksByAuthor(session);
                    case UPDATE_CUSTOMER_INFORMATION -> updateCustomerInformation(session);
                    case VIEW_CUSTOMER_PURCHASE_HISTORY -> viewCustomerPurchaseHistory(session);
                    case PROCESS_NEW_SALE -> handleNewSales(session);
                    case CALCULATE_TOTAL_REVENUE_BY_GENRE -> calculateTotalRevenueByGenre(session);
                    case GENERATE_SALES_REPORT -> reportOfAllSoldBooks(session);
                    case GENERATE_REVENUE_REPORT_BY_GENRE -> reportOfTotalRevenueFromEachGenre(session);
                    case EXIT -> {
                        System.out.println("Exiting.");
                        return;
                    }
                    case null -> System.out.println("Invalid choice. Please try again.");
                }
                System.out.println();
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    // Gets Menu Code From Value
    private static MenuCodes getMenuCodeFromValue(String value) {
        value = value.trim();
        for (MenuCodes menuCode : MenuCodes.values()) {
            if (menuCode.toString().equals(value)) {
                return menuCode;
            }
        }
        return null;
    }

    // Updates Book's Details
    private static void updateBookDetails(Session session) throws SQLException, NumberFormatException {
        Transaction transaction = null;

        try {
            System.out.println("Enter book ID:");
            Long bookID = Long.parseLong(scanner.nextLine());

            System.out.println("Enter new title or - if you don't want to change it:");
            String newTitle = scanner.nextLine();
            System.out.println("Enter new author name or - if you don't want to change it:");
            String newAuthor = scanner.nextLine();
            System.out.println("Enter new genre name or - if you don't want to change it:");
            String newGenre = scanner.nextLine();
            System.out.println("Enter new price or -1 if you don't want to change it:");
            float newPrice = Float.parseFloat(scanner.nextLine());
            System.out.println("Enter new quantity in stock or -1 if you don't want to change it:");
            int newQuantity = Integer.parseInt(scanner.nextLine());

            transaction = session.beginTransaction();

            Book book = session.get(Book.class, bookID);

            if (book != null) {
                if (!newTitle.trim().equals("-")) {
                    book.setTitle(newTitle);
                }
                if (!newAuthor.trim().equals("-")) {
                    book.setAuthor(newAuthor);
                }
                if (!newGenre.trim().equals("-")) {
                    book.setGenre(newGenre);
                }
                if (newPrice != -1) {
                    book.setPrice(newPrice);
                }
                if (newQuantity != -1) {
                    book.setQuantityInStock(newQuantity);
                }

                session.persist(book);
                session.flush();
                transaction.commit();
            } else {
                System.out.println("Book with ID " + bookID + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid numeric values.");
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    // Prints List of Books by Genre
    private static void listBooksByGenre(Session session) throws SQLException {
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                root.get("title"),
                root.get("author"),
                root.get("genre"),
                root.get("price"),
                root.get("quantityInStock")
        );

        criteriaQuery.where(criteriaBuilder.equal(root.get("genre"), genre));

        List<Book> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No books found for the given genre.");
        } else {
            printBooks(resultList);
        }
    }

    // Prints List of Books by Author
    private static void listBooksByAuthor(Session session) throws SQLException {
        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                root.get("title"),
                root.get("author"),
                root.get("genre"),
                root.get("price"),
                root.get("quantityInStock")
        );

        criteriaQuery.where(criteriaBuilder.equal(root.get("author"), author));

        List<Book> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No books found for the given author.");
        } else {
            printBooks(resultList);
        }
    }

    // Prints Books
    private static void printBooks(List<Book> resultList) {
        for (Book row : resultList) {
            String title = row.getTitle();
            String author = row.getAuthor();
            Float price = row.getPrice();
            int quantityInStock = row.getQuantityInStock();

            System.out.printf("Title: %s, Author: %s, Price: %.2f, QuantityInStock: %d%n",
                    title, author, price, quantityInStock);
        }
    }

    // Updates Customer's Information
    private static void updateCustomerInformation(Session session) throws SQLException, NumberFormatException {
        Transaction transaction = null;

        try {
            System.out.println("Enter customer ID:");
            Long customerID = Long.parseLong(scanner.nextLine());

            System.out.println("Enter new name or - if you don't want to change it:");
            String newName = scanner.nextLine();
            System.out.println("Enter new email or - if you don't want to change it:");
            String newEmail = scanner.nextLine();
            System.out.println("Enter new phone number or - if you don't want to change it:");
            String newPhone = scanner.nextLine();

            transaction = session.beginTransaction();

            Customer customer = session.get(Customer.class, customerID);

            if (customer != null) {
                if (!newName.trim().equals("-")) {
                    customer.setName(newName);
                }
                if (!newEmail.trim().equals("-")) {
                    customer.setEmail(newEmail);
                }
                if (!newPhone.trim().equals("-")) {
                    customer.setPhone(newPhone);
                }

                session.persist(customer);
                session.flush();
                transaction.commit();
            } else {
                System.out.println("Customer with ID " + customerID + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid numeric values.");
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    // Prints Customer's Purchase History
    private static void viewCustomerPurchaseHistory(Session session) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerID = Integer.parseInt(scanner.nextLine());

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Customer> customerRoot = criteriaQuery.from(Customer.class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                customerRoot.get("name").alias("CustomerName"),
                bookRoot.get("title"),
                bookRoot.get("author"),
                bookRoot.get("genre"),
                saleRoot.get("dateOfSale")
        );

        criteriaQuery.where(
                criteriaBuilder.equal(customerRoot.get("customerID"), saleRoot.get("customer").get("customerID")),
                criteriaBuilder.equal(bookRoot.get("bookID"), saleRoot.get("book").get("bookID")),
                criteriaBuilder.equal(customerRoot.get("customerID"), customerID)
        );

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String customerName = (String) row[0];
            String title = (String) row[1];
            String author = (String) row[2];
            String genre = (String) row[3];
            LocalDate dateOfSale = (LocalDate) row[4];

            System.out.printf("Customer: %s, Title: %s, Author: %s, Genre: %s, Date of Sale: %s%n",
                    customerName, title, author, genre, dateOfSale);
        }
    }

    // Processes New Sale
    private static void  handleNewSales(Session session) throws SQLException, NumberFormatException {
        Transaction transaction = null;

        try {
            System.out.println("Enter customer ID:");
            Long customerID = Long.parseLong(scanner.nextLine());
            System.out.println("Enter book ID:");
            Long bookID = Long.parseLong(scanner.nextLine());

            System.out.println("Enter books count:");
            int count = Integer.parseInt(scanner.nextLine());

            transaction = session.beginTransaction();

            String jpql = "SELECT b.price FROM Book b WHERE b.bookID = :bookID";
            float price = session.createQuery(jpql, Float.class)
                    .setParameter("bookID", bookID)
                    .getSingleResult() * count;

            Sale newSale = new Sale(session.get(Book.class, bookID),
                                    session.get(Customer.class, customerID),
                                    LocalDate.now(), count, price);

            session.persist(newSale);
            session.flush();
            transaction.commit();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format. Please enter valid numeric values.");
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    // Calculates Total Revenue by Genre
    private static void calculateTotalRevenueByGenre(Session session) throws SQLException {
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                bookRoot.get("genre"),
                criteriaBuilder.sum(saleRoot.get("totalPrice"))
        );

        criteriaQuery.where(
                criteriaBuilder.equal(saleRoot.get("book"), bookRoot),
                criteriaBuilder.equal(bookRoot.get("genre"), genre)
        );

        criteriaQuery.groupBy(bookRoot.get("genre"));

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            genre = (String) row[0];
            float totalRevenue = (Float) row[1];

            System.out.printf("Genre: %s, Total Revenue: %.2f%n", genre, totalRevenue);
        }
    }

    // Report of All Sold Books
    private static void reportOfAllSoldBooks(Session session) throws SQLException {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Customer> customerRoot = criteriaQuery.from(Customer.class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                customerRoot.get("name").alias("CustomerName"),
                bookRoot.get("title"),
                saleRoot.get("dateOfSale")
        );

        criteriaQuery.where(
                criteriaBuilder.equal(customerRoot.get("customerID"), saleRoot.get("customer").get("customerID")),
                criteriaBuilder.equal(bookRoot.get("bookID"), saleRoot.get("book").get("bookID"))
        );

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String customerName = (String) row[0];
            String bookTitle = (String) row[1];
            LocalDate dateOfSale = (LocalDate) row[2];

            System.out.printf("Customer: %s, Book Title: %s, Date of Sale: %s%n",
                    customerName, bookTitle, dateOfSale);
        }
    }

    // Report of Total Revenue from Each Genre
    private static void reportOfTotalRevenueFromEachGenre(Session session) throws SQLException {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Sale> saleRoot = criteriaQuery.from(Sale.class);
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        criteriaQuery.multiselect(
                bookRoot.get("genre"),
                criteriaBuilder.sum(saleRoot.get("totalPrice"))
        );

        criteriaQuery.where(
                criteriaBuilder.equal(saleRoot.get("book"), bookRoot)
        );

        criteriaQuery.groupBy(bookRoot.get("genre"));

        List<Object[]> resultList = session.createQuery(criteriaQuery).getResultList();

        if (resultList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Object[] row : resultList) {
            String genre = (String) row[0];
            float totalRevenue = (Float) row[1];

            System.out.printf("Genre: %s, Total Revenue: %.2f%n", genre, totalRevenue);
        }
    }
}