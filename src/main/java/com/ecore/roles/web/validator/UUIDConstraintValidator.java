package com.ecore.roles.web.validator;

import com.ecore.roles.web.annotation.UUIDValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class UUIDConstraintValidator implements ConstraintValidator<UUIDValidator, UUID> {

    @Override
    public void initialize(UUIDValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext constraintValidatorContext) {
        List<String> uuidChars = asList(uuid.toString().replaceAll("-", "").split(""));
        Set<String> count = uuidChars.stream().collect(Collectors.toSet());
        return count.size() > 1;

    }
}
