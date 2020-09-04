package com.example.Practice_Project.Service;

import com.example.Practice_Project.Entity.Transaction;
import com.example.Practice_Project.Entity.User;
import com.example.Practice_Project.Flink.KafkaFlink;
import com.example.Practice_Project.Kafka.Producer;
import com.example.Practice_Project.Entity.TransactionInES;
import com.example.Practice_Project.Kafka.ProducerWithAsync;
import com.example.Practice_Project.Reposotory.TransactionRepository;
import com.example.Practice_Project.Reposotory.TransactionRepositoryInES;
import com.example.Practice_Project.Reposotory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class BankingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Producer producer;

    @Autowired
    ProducerWithAsync producerWithAsync;

    @Autowired
    private TransactionRepositoryInES transactionRepositoryInES;

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    public String addUser(User user){
        user.setAccountNo(user.getMobileNo());
        user.setAccountBalance(0);
        userRepository.save(user);

        return "User added successfully";
    }


    public Iterable<User> getAllUser(){
        return userRepository.findAll();
    }



    public String login( Map<String, Object> detail){
        String username = (String) detail.get("username");
        String password = (String) detail.get("password");

        User user = userRepository.findById(username).orElse(null);
        if(user == null) return "User does not exist";
        else if( ! user.getPassCode().equals(password)) {
            return "Password is wrong";
        }

        return "Successfully Login";
    }

    @Transactional
    public String depositAmount( Map<String, Object> detail){
        String accountNo = (String) detail.get("accountNo");
        Integer money = (Integer) detail.get("money");

        if(money < 0) return "Enter Valid Amount";
        else {
            User user = userRepository.findById(accountNo).orElse(null);
            if(user == null) return "Account No does not exist";
            else{
                Integer accountBalance = user.getAccountBalance();
                accountBalance = accountBalance + money;
                user.setAccountBalance(accountBalance);
                userRepository.save(user);
            }

            String message = "Deposit Money, Credit, " + money.toString() + ", " + user.getAccountBalance()+ ", " + accountNo;
            System.out.println("Reached here 1");
            producer.sendMessage(message);
            System.out.println("Reached here 2");

//            System.out.println("Reached here 1");
//            producerWithAsync.sendMessage(message);
//            System.out.println("Reached here 2");

            Transaction transaction = new Transaction("Deposit Money","Credit",money, user.getAccountBalance(), accountNo);
            transactionRepository.save(transaction);
        }

        KafkaFlink.getMessage();
        return "Success Deposit";
    }

    @Transactional
    public String withdrawAmount( Map<String, Object> detail){
        String accountNo = (String) detail.get("accountNo");
        Integer money = (Integer) detail.get("money");

        if(money < 0) return "Enter Valid Amount";
        else {
            User user = userRepository.findById(accountNo).orElse(null);
            if(user == null) return "Account No does not exist";
            else{
                Integer accountBalance = user.getAccountBalance();
                if(accountBalance < money) return "Insufficient Account Balance";
                else {
                    accountBalance = accountBalance - money;
                    user.setAccountBalance(accountBalance);
                    userRepository.save(user);
                }

                String message = "Withdraw Money, Debit, " + money.toString() + ", " + user.getAccountBalance()+ ", " + accountNo;
                producer.sendMessage(message);

                Transaction transaction = new Transaction("Withdraw Money","Debit",money, user.getAccountBalance(), accountNo);
                transactionRepository.save(transaction);
            }
        }

        return "Success Withdraw";
    }

    public String updatePassCode(Map<String, Object> detail){
        String accountNo = (String) detail.get("accountNo");
        String oldPassCode = (String) detail.get("oldPassCode");
        String newPassCode = (String) detail.get("newPassCode");

        User user = userRepository.findById(accountNo).orElse(null);
        if(user == null) return "Account No does not exist";
        else{
            if( !oldPassCode.equals(user.getPassCode())) return "Current pass code is wrong";
            else{
                user.setPassCode(newPassCode);
                userRepository.save(user);
            }
        }

        return "Pass Code updated Successfully";
    }

    @Transactional
    public String transferAmount( Map<String, Object> detail){
        String toAccountNo = (String) detail.get("toAccountNo");
        String fromAccountNo = (String) detail.get("fromAccountNo");
        Integer money = (Integer) detail.get("money");

        if(money < 0) return "Enter Valid Amount";
        else {
            User toUser = userRepository.findById(toAccountNo).orElse(null);
            User fromUser = userRepository.findById(fromAccountNo).orElse(null);

            if(toUser == null) return "Sender Account No does not exist";
            else if(fromUser == null) return "Reciept Account No does not exist";
            else{

                Integer toAccountBalance = toUser.getAccountBalance();

                if(toAccountBalance < money) return "Insufficient Account Balance in Sender Account";
                else {
                    toAccountBalance -= money;
                    toUser.setAccountBalance(toAccountBalance);
                    fromUser.setAccountBalance(fromUser.getAccountBalance() + money);

                    String message = "Transfer Money to " + fromAccountNo + ", Debit, " + money.toString() + ", " + toUser.getAccountBalance()+ ", " + toAccountNo;
                    producer.sendMessage(message);

                    Transaction toTransaction = new Transaction("Transfer Money to " + fromAccountNo,"Debit",money, toUser.getAccountBalance(), toAccountNo);
                    transactionRepository.save(toTransaction);

                    message = "Get Money from " + toAccountNo + ", Credit, " + money.toString() + ", " + fromUser.getAccountBalance()+ ", " + fromAccountNo;
                    producer.sendMessage(message);

                    Transaction fromTransaction = new Transaction("Get Money From " + toAccountNo,"Credit",money, fromUser.getAccountBalance(), fromAccountNo);
                    transactionRepository.save(fromTransaction);

                    userRepository.save(toUser);
                    userRepository.save(fromUser);
                }
            }
        }

        return "Amount successfully transferred";
    }

    public User getUser(String mobileNo){
        return userRepository.findById(mobileNo).orElse(null);

    }

    public String updateUser(User user, String mobileNo){
        userRepository.save(user);
        return "Update Successfully";
    }

    public String deleteUser( String mobileNo ){
        userRepository.deleteById(mobileNo);
        return "Delete Successfully";
    }

    public String checkBalance(Map<String, Object> detail){
        String accountNo = (String) detail.get("accountNo");

        User user = userRepository.findById(accountNo).orElse(null);
        if(user == null) return "Account No does not exist";

        return "Account Balance : " + String.valueOf(user.getAccountBalance());
    }

    public String updateUser2( Map<String, Object> detail, String mobileNo){
        String name = (String) detail.get("name");

        User user = userRepository.findById(mobileNo).orElse(null);
        if(user == null) return "Account No does not exist";
        user.setName(name);
        userRepository.save(user);
        return "Update Successfully";
    }

    public List<Transaction> miniStatement( Map<String, Object> detail){
        String mobileNo  = (String) detail.get("mobileNo");

        User user = userRepository.findById(mobileNo).orElse(null);
        if(user == null) return null;

        List<Transaction> transactions = new ArrayList<>();

        transactionRepository.findByUserMobileNo(mobileNo).forEach(transactions::add);

        return transactions.subList(Math.max(transactions.size()-10,0),transactions.size());
    }

    public Page<TransactionInES> miniStatementFromES(Map<String, Object> detail){
        String mobileNo  = (String) detail.get("mobileNo");

        User user = userRepository.findById(mobileNo).orElse(null);
        if(user == null) return null;
        //return transactionRepositoryInES.findAll();
        List<TransactionInES> transactions = new ArrayList<>();

        transactionRepositoryInES.findByUserMobileNo(mobileNo).forEach(transactions::add);
        List<TransactionInES> miniTransactions = transactions.subList(Math.max(transactions.size()-2,0),transactions.size());
        Collections.reverse(miniTransactions);

//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("user.mobileNo", " 9799124092" ))
//                .build();
//        SearchHits<TransactionInES> trans = elasticsearchOperations.search(searchQuery, TransactionInES.class, IndexCoordinates.of("bankdetail"));
//        TransactionInES transaction = trans.getSearchHit(0).getContent();
//        System.out.println(transaction.getUser().getMobileNo());
//        miniTransactions.add(transaction);
        Page<TransactionInES> pages = transactionRepositoryInES.findMiniStatement(" 9799124092", PageRequest.of(0,2, Sort.by(Sort.Direction.DESC,"createdDate")));
        System.out.println(pages);
        return pages;
        //return miniTransactions;
//        return transactionRepositoryInES.findByUserMobileNo(mobileNo);
    }

}
