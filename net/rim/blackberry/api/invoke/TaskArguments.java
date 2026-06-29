package net.rim.blackberry.api.invoke;

import javax.microedition.pim.ToDo;

public final class TaskArguments extends ApplicationArguments {
   private ToDo _todo;
   public static final String ARG_NEW;
   public static final String ARG_VIEW;

   public TaskArguments() {
   }

   public TaskArguments(String arg) {
      if (arg != null && arg.equals("new")) {
         super._args = new Object[]{arg};
      } else {
         throw new Object("Invalid argument. Please use one of the TaskArguments constants.");
      }
   }

   public TaskArguments(String arg, ToDo todo) {
      if (arg == null || !arg.equals("new") && !arg.equals("view")) {
         throw new Object("Invalid argument. Please use one of the TaskArguments contstants.");
      }

      super._args = new Object[]{arg};
      this._todo = todo;
   }

   protected final ToDo getToDoArg() {
      return this._todo;
   }
}
