package net.rim.blackberry.api.invoke;

public class SearchArguments extends ApplicationArguments {
   public SearchArguments() {
      this(null, null);
   }

   public SearchArguments(String text, String name) {
      super._args = new Object[3];
      super._args[0] = "invokeapi";
      super._args[1] = text;
      super._args[2] = name;
   }
}
