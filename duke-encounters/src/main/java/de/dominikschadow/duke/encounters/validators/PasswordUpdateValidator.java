/*
 * Copyright (C) 2015 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Application Intrusion Detection project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.duke.encounters.validators;

import de.dominikschadow.duke.encounters.Constants;
import de.dominikschadow.duke.encounters.domain.PasswordUpdate;
import de.dominikschadow.duke.encounters.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.inject.Named;

/**
 * Validates a request to change the users password. Makes sure that the current password is correct and that the new
 * password matches its confirmation.
 *
 * @author Dominik Schadow
 */
@Named
public class PasswordUpdateValidator implements Validator {
    @Autowired
    private SpringValidatorAdapter validator;
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordUpdate.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validator.validate(target, errors);

        PasswordUpdate passwordUpdate = (PasswordUpdate) target;

        if (!userService.confirmPassword(passwordUpdate.getCurrentPassword())) {
            errors.rejectValue("newPassword", Constants.PASSWORD_ERROR_CODE, Constants
                    .PASSWORD_NOT_CORRECT_ERROR_MESSAGE);
        }

        if (StringUtils.length(passwordUpdate.getNewPassword()) < 10) {
            errors.rejectValue("newPassword", Constants.PASSWORD_ERROR_CODE, Constants.PASSWORD_UNSAFE_ERROR_MESSAGE);
        }

        if (!passwordUpdate.getNewPassword().equals(passwordUpdate.getNewPasswordConfirmation())) {
            errors.rejectValue("newPassword", Constants.PASSWORD_ERROR_CODE, Constants.PASSWORD_MATCH_ERROR_MESSAGE);
        }
    }
}
