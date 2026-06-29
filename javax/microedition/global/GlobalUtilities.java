package javax.microedition.global;

class GlobalUtilities {
   private GlobalUtilities() {
   }

   public static String convertUnderscoreToHyphens(String locale) {
      return locale == null ? null : locale.replace('_', '-');
   }

   public static boolean isValidLocale(String locale) {
      int length = locale.length();
      boolean expectingMoreCharacters = false;

      for (int i = 0; i < length; i++) {
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               if (!Character.isLowerCase(locale.charAt(i))) {
                  return false;
               }

               expectingMoreCharacters = true;
               break;
            case 1:
               if (!Character.isLowerCase(locale.charAt(i))) {
                  return false;
               }

               expectingMoreCharacters = false;
               break;
            case 2:
            case 5:
               if (locale.charAt(i) != '-') {
                  return false;
               }

               expectingMoreCharacters = true;
               break;
            case 3:
               if (!Character.isUpperCase(locale.charAt(i))) {
                  return false;
               }
               break;
            case 4:
               if (!Character.isUpperCase(locale.charAt(i))) {
                  return false;
               }

               expectingMoreCharacters = false;
               break;
            case 6:
               expectingMoreCharacters = false;
         }
      }

      return !expectingMoreCharacters;
   }
}
