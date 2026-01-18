package com.example.controller;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.service.UserService;
import com.google.gson.Gson;
import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.http.HttpStatus;
import com.vcinsidedigital.webcore.validation.annotations.Annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController extends BaseController
{
    private final Gson gson = new Gson();

    @Inject
    private UserService service;

    @Get("/")
    public List<User> listAll(){
        return service.getAllUsers();
    }

    @Get("/{id}")
    public User getUser(@Path("id") Long id){
        return service.getUserById(id);
    }

    @Post("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpResponse createUser(@Valid UserDto userDto){
        User user = service.createUser(userDto);
        return executeIfValid(() -> {
            return created(user);
        });
    }



}
