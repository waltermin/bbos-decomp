package net.rim.blackberry.api.invoke;

import net.rim.blackberry.api.pdap.BlackBerryMemo;

public final class MemoArguments extends ApplicationArguments {
   private BlackBerryMemo _memo;
   public static final String ARG_NEW = "new";
   public static final String ARG_VIEW = "view";
   public static final String ARG_EDIT = "edit";

   public MemoArguments() {
   }

   public MemoArguments(String arg) {
      if (arg != null && arg.equals("new")) {
         super._args = new String[]{arg};
      } else {
         throw new IllegalArgumentException("Invalid argument specified");
      }
   }

   public MemoArguments(String arg, BlackBerryMemo memo) {
      if (arg == null || !arg.equals("new") && !arg.equals("view") && !arg.equals("edit")) {
         throw new IllegalArgumentException("Invalid argument specified");
      }

      super._args = new String[]{arg};
      this._memo = memo;
   }

   final BlackBerryMemo getMemoArg() {
      return this._memo;
   }
}
