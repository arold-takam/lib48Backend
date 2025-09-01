package com.k48.lib48.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "History")
public class History {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String users;

    private String bookTitle;

    private String operation;

    private String etatOperation;

    private LocalDateTime dateTime;

    private String details;

    public History(String users,String bookTitle, String operation,
                   String etatOperation, LocalDateTime dateTime, String details) {
        this.users=users;
        this.bookTitle=bookTitle;
        this.operation = operation;
        this.etatOperation = etatOperation;
        this.dateTime = dateTime;
        this.details = details;
    }

    public History() {}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return users;
    }

    public void setUser(String user) {
        this.users = user;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEtatOperation() {
        return etatOperation;
    }

    public void setEtatOperation(String etatOperation) {
        this.etatOperation = etatOperation;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }


}
