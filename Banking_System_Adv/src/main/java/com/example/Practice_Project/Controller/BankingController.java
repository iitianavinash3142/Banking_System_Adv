package com.example.Practice_Project.Controller;

import com.example.Practice_Project.Entity.Transaction;
import com.example.Practice_Project.Entity.User;
import com.example.Practice_Project.Entity.TransactionInES;
import com.example.Practice_Project.Service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("Banking/")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @PostMapping("/add")
    public @ResponseBody String addUser(@RequestBody User user){
        return bankingService.addUser(user);
    }

    @RequestMapping("/all")
    public @ResponseBody Iterable<User> getAllUser(){
        return bankingService.getAllUser();
    }


    @PostMapping("/login")
    public @ResponseBody  String login(@RequestBody Map<String, Object> detail){
        return bankingService.login(detail);
    }


    @PostMapping("/deposit")
    public @ResponseBody  String depositAmount(@RequestBody Map<String, Object> detail){
        return bankingService.depositAmount(detail);
    }

    @PostMapping("/withdraw")
    public @ResponseBody  String withdrawAmount(@RequestBody Map<String, Object> detail){
        return bankingService.withdrawAmount(detail);
    }

    @PostMapping("/updatePassCode")
    public @ResponseBody  String updatePassCode(@RequestBody Map<String, Object> detail){
        return bankingService.updatePassCode(detail);
    }


    @PostMapping("/transferAmount")
    public @ResponseBody  String transferAmount(@RequestBody Map<String, Object> detail){
        return bankingService.transferAmount(detail);
    }

    @RequestMapping("/get/{id}")
    public @ResponseBody User getUser(@PathVariable("id") String mobileNo){
        return bankingService.getUser(mobileNo);

    }

    @RequestMapping(value = "/get/{id}" , method = RequestMethod.PUT)
    public @ResponseBody String updateUser(@RequestBody User user, @PathVariable("id") String mobileNo){
        return bankingService.updateUser(user,mobileNo);
    }

    @RequestMapping(value = "/get/{id}" , method = RequestMethod.DELETE)
    public @ResponseBody String deleteUser(@PathVariable("id") String mobileNo){
        return bankingService.deleteUser(mobileNo);
    }

    @PostMapping("/checkBalance")
    public @ResponseBody String checkBalance(@RequestBody Map<String, Object> detail){
        return bankingService.checkBalance(detail);
    }

    @RequestMapping(value = "/get_2/{id}" , method = RequestMethod.PUT)
    public @ResponseBody String updateUser2(@RequestBody Map<String, Object> detail, @PathVariable("id") String mobileNo){
        return bankingService.updateUser2(detail,mobileNo);
    }

    @PostMapping("/miniStatement")
    public @ResponseBody List<Transaction> miniStatement(@RequestBody Map<String, Object> detail){
        return bankingService.miniStatement(detail);
    }

    @PostMapping("/miniStatementES")
    public @ResponseBody Page<TransactionInES> miniStatementES(@RequestBody Map<String, Object> detail){
        return bankingService.miniStatementFromES(detail);
    }
}
