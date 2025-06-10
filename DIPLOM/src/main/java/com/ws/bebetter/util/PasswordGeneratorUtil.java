package com.ws.bebetter.util;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PasswordGeneratorUtil {

    public String generateRandomValidPassword() {
        PasswordGenerator generator = new PasswordGenerator();

        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
        lowerCaseRule.setNumberOfCharacters(3);

        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
        upperCaseRule.setNumberOfCharacters(3);

        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialCharacterData = new CharacterData() {
            @Override
            public String getErrorCode() {
                return "INSUFFICIENT_SPECIAL";
            }

            @Override
            public String getCharacters() {
                return "!\"#$%^&*()-+=?";
            }
        };

        CharacterRule specialCharRule = new CharacterRule(specialCharacterData);
        specialCharRule.setNumberOfCharacters(1);

        return generator.generatePassword(9,
                Arrays.asList(lowerCaseRule, upperCaseRule, digitRule, specialCharRule));
    }
}