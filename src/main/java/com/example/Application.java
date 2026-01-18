package com.example;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;
import com.vcinsidedigital.webcore.validation.ValidationPlugin;

@WebApplication
public class Application extends WebServerApplication
{
    public static void main(String[] args){
        registerPlugin(new ValidationPlugin());
        WebServerApplication.run(Application.class, args);
    }
}
