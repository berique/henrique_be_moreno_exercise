package com.ecore.roles.web.validator;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

import static com.ecore.roles.utils.TestData.UUID_1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UUIDConstraintValidatorTest {

    @Test
    void validateRandomUUID() {
        UUIDConstraintValidator uuidConstraintValidator = new UUIDConstraintValidator();
        assertThat(uuidConstraintValidator.isValid(UUID.randomUUID(), mock(ConstraintValidatorContext.class))).isTrue();
    }

    @Test
    void validateInvalidUUID() {
        UUIDConstraintValidator uuidConstraintValidator = new UUIDConstraintValidator();
        assertThat(uuidConstraintValidator.isValid(UUID_1, mock(ConstraintValidatorContext.class))).isFalse();
    }
}
