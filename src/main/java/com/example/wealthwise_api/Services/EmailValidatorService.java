package com.example.wealthwise_api.Services;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidatorService  implements Predicate<String> {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    @Override
    public boolean test(String s) {

        if(s.matches(EMAIL_REGEX)){
            return true;
        }
        return false;
    }

}
