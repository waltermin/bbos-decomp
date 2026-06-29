package net.rim.device.apps.internal.bis.utils;

import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.blackberryemail.address.EmailAddressStringPattern;

public final class InputValidationUtils {
   private static final int NUM_DECIMAL_PIN_DIGITS = 7;
   private static final int NUM_HEX_PIN_DIGITS = 8;

   public static final boolean isValidEmailAddress(String emailAddress) {
      boolean result = false;
      if (emailAddress != null && emailAddress.length() > 0) {
         AbstractStringWrapper wrappedEmailAddress = AbstractStringWrapper.createInstance(emailAddress);
         StringPattern$Match matchResult = new StringPattern$Match();
         EmailAddressStringPattern emailPattern = new EmailAddressStringPattern();
         if (emailPattern.findMatch(wrappedEmailAddress, 0, wrappedEmailAddress.length(), matchResult) && matchResult.id == -2985347935260258684L) {
            result = true;
         }
      }

      return result;
   }

   public static final boolean isValidUsername(String username) {
      boolean valid = !hasTooFewUsernameCharacters(username)
         && !hasTooManyUsernameCharacters(username)
         && (CharacterUtilities.isLetter(username.charAt(0)) || CharacterUtilities.isDigit(username.charAt(0)));
      if (valid) {
         int numChars = username.length();

         for (int i = 1; i < numChars; i++) {
            if (!isValidUsernameCharacter(username.charAt(i))) {
               return false;
            }
         }
      }

      return valid;
   }

   public static final boolean isValidUsernameCharacter(char character) {
      return CharacterUtilities.isDigit(character) || CharacterUtilities.isLetter(character) || character == '-' || character == '_' || character == '.';
   }

   public static final boolean hasTooFewUsernameCharacters(String username) {
      return username == null || username.length() < 4;
   }

   public static final boolean hasTooManyUsernameCharacters(String username) {
      return username == null ? false : username.length() > 32;
   }

   public static final boolean isValidPassword(String password) {
      return !hasTooFewPasswordCharacters(password) && !hasTooManyPasswordCharacters(password);
   }

   public static final boolean hasTooFewPasswordCharacters(String password) {
      return password == null || password.length() < 6;
   }

   public static final boolean hasTooManyPasswordCharacters(String password) {
      return password == null ? false : password.length() > 16;
   }

   public static final boolean isValidPIN(String pin) {
      boolean result = false;
      if (pin != null) {
         int numPINDigits = pin.length();
         if (numPINDigits > 7 && numPINDigits <= 8) {
            boolean hasHexDigits = false;
            int digitCount = 0;

            for (int i = 0; i < numPINDigits; i++) {
               char c = CharacterUtilities.toUpperCase(pin.charAt(i));
               if (CharacterUtilities.isDigit(c)) {
                  digitCount++;
               } else if (c >= 'A' && c <= 'F') {
                  digitCount++;
                  hasHexDigits = true;
               }
            }

            if (!hasHexDigits && digitCount == 7) {
               return true;
            }

            if (digitCount == 8) {
               result = true;
            }
         }
      }

      return result;
   }

   public static final boolean validatePassword(BasicScreen screen, String password) {
      if (!isValidPassword(password)) {
         if (hasTooFewPasswordCharacters(password)) {
            screen.setError(ApplicationResources.getString(105));
            return false;
         } else if (hasTooManyPasswordCharacters(password)) {
            screen.setError(ApplicationResources.getString(104));
            return false;
         } else {
            screen.setError(ApplicationResources.getString(106));
            return false;
         }
      } else {
         return true;
      }
   }

   public static final boolean validateUsername(BasicScreen screen, String userName) {
      if (!isValidUsername(userName)) {
         if (hasTooFewUsernameCharacters(userName)) {
            screen.setError(ApplicationResources.getString(102));
            return false;
         } else if (hasTooManyUsernameCharacters(userName)) {
            screen.setError(ApplicationResources.getString(101));
            return false;
         } else {
            screen.setError(ApplicationResources.getString(103));
            return false;
         }
      } else {
         return true;
      }
   }
}
