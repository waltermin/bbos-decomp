package net.rim.blackberry.api.invoke;

public final class PhoneArguments extends ApplicationArguments {
   public static final String ARG_CALL;
   public static final String VOICEMAIL;
   private static final String ARG_NUMBER;
   private static final String ARG_SMART_DIAL;

   public PhoneArguments() {
   }

   public PhoneArguments(String actionArg, String dialStringArg) {
      this(actionArg, dialStringArg, true);
   }

   public PhoneArguments(String actionArg, String dialStringArg, boolean smartDialing) {
      if (actionArg.equals("call") && dialStringArg != null && dialStringArg.length() > 0) {
         if (smartDialing) {
            super._args = new Object[3];
         } else {
            super._args = new Object[2];
         }

         super._args[0] = actionArg;
         if (dialStringArg.equals("voicemail")) {
            super._args[1] = dialStringArg;
         } else {
            super._args[1] = ((StringBuffer)(new Object("number="))).append(dialStringArg).toString();
         }

         if (smartDialing) {
            super._args[2] = "smartdial";
         }
      } else {
         throw new Object("Invalid argument");
      }
   }
}
