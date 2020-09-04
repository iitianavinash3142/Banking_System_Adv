package com.example.Practice_Project.Entity;

import org.elasticsearch.search.DocValueFormat;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Document(indexName = "bankdetail", type = "transaction")
public class TransactionInES {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    private String description;
    private String amount;
    private String status;
    private String accountBalance;

    @Field(type = FieldType.Date, store = true)
    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Field(type = FieldType.Nested, includeInParent = true)
    private User user;


    public TransactionInES(){

    }

    public TransactionInES(String description, String status, String amount, String accountBalance, String mobileNo){
        super();
        this.description = description;
        this.status = status;
        this.amount = amount;
        this.accountBalance = accountBalance;
        this.user = new User(mobileNo,"","","",0);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }
}
