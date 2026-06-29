package net.rim.blackberry.api.invoke;

import java.util.Calendar;
import javax.microedition.pim.Event;

public final class CalendarArguments extends ApplicationArguments {
   private Calendar _date;
   private Event _event;
   protected static final String ARG_VIEW = "newview";
   public static final String ARG_VIEW_DAY = "2";
   public static final String ARG_VIEW_WEEK = "3";
   public static final String ARG_VIEW_MONTH = "1";
   public static final String ARG_VIEW_AGENDA = "4";
   public static final String ARG_VIEW_DEFAULT = "-1";
   public static final String ARG_NEW = "newappt";

   public CalendarArguments() {
   }

   public CalendarArguments(String arg) {
      if (arg == null || !arg.equals("2") && !arg.equals("3") && !arg.equals("1") && !arg.equals("4") && !arg.equals("-1")) {
         if (arg != null && arg.equals("newappt")) {
            super._args = new Object[1];
            super._args[0] = "newappt";
         } else {
            throw new Object("Invalid argument. Please use one of the CalendarArguments Class members.");
         }
      } else {
         super._args = new Object[2];
         super._args[0] = "newview";
         super._args[1] = arg;
      }
   }

   public CalendarArguments(String arg, Calendar date) {
      this(arg);
      this._date = date;
   }

   public CalendarArguments(String arg, Event event) {
      this(arg);
      this._event = event;
   }

   protected final Calendar getDateArg() {
      return this._date;
   }

   protected final Event getEventArg() {
      return this._event;
   }
}
