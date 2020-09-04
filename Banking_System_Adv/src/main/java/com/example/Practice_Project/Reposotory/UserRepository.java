package com.example.Practice_Project.Reposotory;

import com.example.Practice_Project.Entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String > {

}
