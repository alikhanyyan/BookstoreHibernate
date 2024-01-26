package org.example;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "sale_id")
    Long saleID;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Column(name = "date_of_sale")
    LocalDate dateOfSale;

    @Column(name = "quantity_sold", nullable = false)
    Integer quantitySold;

    @Column(name = "total_price", nullable = false)
    Float totalPrice;

    Sale() {

    }

    public Sale(Book book, Customer customer, LocalDate dateOfSale, Integer quantitySold, Float totalPrice) {
        this.book = book;
        this.customer = customer;
        this.dateOfSale = dateOfSale;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public Long getSaleID() {
        return saleID;
    }

    public Long getBookID() {
        return book.getBookID();
    }

    public Long getCustomerID() {
        return customer.getCustomerID();
    }


    public LocalDate getDateOfSale() {
        return dateOfSale;
    }
    public void setDateOfSale(LocalDate dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }
    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
