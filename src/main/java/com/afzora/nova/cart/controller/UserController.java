package com.afzora.nova.cart.controller;

import com.afzora.nova.cart.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

//    List<Users> usersList = new ArrayList<>(List.of(
//            new Users( 15L, "Muhammad", "Afzal"),
//            new Users(16L, "Muhammad", "Akram")
//    ));

    @GetMapping("/getUser")
    public String getWelcome()
    {
        return "Welcome My Name is Muhammad Afzal";
    }

    @GetMapping("/serssion-id")
    public String servletRequest(HttpServletRequest httpServletRequest)
    {
        return "Session Id: " + httpServletRequest.getSession().getId();
    }

//    @GetMapping("")
//    public ResponseEntity<List<Users>> getAllUsers()
//    {
//        return ResponseEntity.status(200).body(usersList);
//    }

    @GetMapping("/csrf-token")
    public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest httpServletRequest)
    {
        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        return ResponseEntity.status(200).body(csrfToken);
    }
}
