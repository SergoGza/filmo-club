package com.videoclub.filmoapp.film.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.CustomValidatorBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
@RequiredArgsConstructor
public class FilmMvcDTOValidator extends CustomValidatorBean {


    private final LocalValidatorFactoryBean localValidatorFactoryBean;

    @Override
    public void validate(Object target, Errors errors) {

        localValidatorFactoryBean.validate(target, errors);

    }
}
