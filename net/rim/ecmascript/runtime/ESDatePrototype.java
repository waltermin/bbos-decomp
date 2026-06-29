package net.rim.ecmascript.runtime;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.ecmascript.compiler.Tokenizer;
import net.rim.ecmascript.util.Misc;

class ESDatePrototype extends ESDate {
   private static final long msPerSecond = 1000L;
   private static final long secondsPerMinute = 60L;
   private static final long msPerMinute = 60000L;
   private static final long minutesPerHour = 60L;
   private static final long msPerHour = 3600000L;
   private static final long hoursPerDay = 24L;
   private static final long msPerDay = 86400000L;
   private static final long daysInRange = 100000000L;
   private static final long minTime = -8640000000000000L;
   private static final long maxTime = 8640000000000000L;
   private static final int yearsInRange = 273972;
   private static final int minYear = -272002;
   private static final int maxYear = 275942;
   private static final int[] monthDays = new int[]{
      0,
      31,
      59,
      90,
      120,
      151,
      181,
      212,
      243,
      273,
      304,
      334,
      365,
      -804650995,
      0,
      31,
      60,
      91,
      121,
      152,
      182,
      213,
      244,
      274,
      305,
      335,
      366,
      -804650987,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      10,
      11,
      11,
      0,
      0,
      12,
      12,
      13,
      13,
      14,
      14,
      51,
      1064304896,
      -977993472
   };
   private static final int[] monthDaysLeap = new int[]{
      0,
      31,
      60,
      91,
      121,
      152,
      182,
      213,
      244,
      274,
      305,
      335,
      366,
      -804650987,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      10,
      11,
      11,
      0,
      0,
      12,
      12,
      13,
      13,
      14,
      14,
      51,
      1064304896,
      -977993472,
      1634076160,
      1316252789,
      1971719133,
      1711669349,
      1953264993,
      6649222,
      1969317382,
      -2034207636,
      100689269,
      1164247660,
      9059180,
      1694657542,
      1812332730,
      12543234
   };
   private static final String[] dayStr = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
   static final String[] monthStr = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   private static final int FORMAT_DATE = 0;
   private static final int FORMAT_TIME = 1;
   private static final int FORMAT_BOTH = 2;
   static final String[] days = new String[]{
      "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
   };
   static final String[] months = new String[]{
      "JAN",
      "FEB",
      "MAR",
      "APR",
      "MAY",
      "JUN",
      "JUL",
      "AUG",
      "SEP",
      "OCT",
      "NOV",
      "DEC",
      "JANUARY",
      "FEBRUARY",
      "MARCH",
      "APRIL",
      "MAY",
      "JUNE",
      "JULY",
      "AUGUST",
      "SEPTEMBER",
      "OCTOBER",
      "NOVEMBER",
      "DECEMBER"
   };
   static final ESDatePrototype$DateKeyword[] _parseableTimeZones = new ESDatePrototype$DateKeyword[]{
      new ESDatePrototype$DateKeyword("GMT", 2, 0),
      new ESDatePrototype$DateKeyword("UTC", 2, 0),
      new ESDatePrototype$DateKeyword("UT", 2, 0),
      new ESDatePrototype$DateKeyword("EST", 2, -18000000),
      new ESDatePrototype$DateKeyword("EDT", 2, -14400000),
      new ESDatePrototype$DateKeyword("CST", 2, -21600000),
      new ESDatePrototype$DateKeyword("CDT", 2, -18000000),
      new ESDatePrototype$DateKeyword("MST", 2, -25200000),
      new ESDatePrototype$DateKeyword("MDT", 2, -21600000),
      new ESDatePrototype$DateKeyword("PST", 2, -28800000),
      new ESDatePrototype$DateKeyword("PDT", 2, -25200000),
      new ESDatePrototype$DateKeyword("AM", 3, 0),
      new ESDatePrototype$DateKeyword("PM", 3, 12)
   };
   private static ESDatePrototype$DateKeyword[] _keywords;

   private static TimeZone getTimeZone() {
      return GlobalObject.getInstance().timeZone;
   }

   static long fakeDaylightSavingTA(long t) {
      t -= Misc.getLocalTZA(getTimeZone());
      long dst_start = getFirstSundayInApril(t) + 7200000;
      long dst_end = getLastSundayInOctober(t) + 7200000;
      return t >= dst_start && t < dst_end ? 3600000 : 0;
   }

   static long getFirstSundayInApril(long t) {
      int year = yearFromTime(t);
      boolean leap = inLeapYear(t);
      long april = timeFromYear(year) + timeInMonth(0, leap) + timeInMonth(1, leap) + timeInMonth(2, leap);
      long first_sunday = april;

      while (weekDay(first_sunday) > 0) {
         first_sunday += 86400000;
      }

      return first_sunday;
   }

   static long getLastSundayInOctober(long t) {
      int year = yearFromTime(t);
      boolean leap = inLeapYear(t);
      long oct = timeFromYear(year);

      for (int m = 0; m < 9; m++) {
         oct += timeInMonth(m, leap);
      }

      long last_sunday = oct + 2592000000L;

      while (weekDay(last_sunday) > 0) {
         last_sunday -= 86400000;
      }

      return last_sunday;
   }

   static long timeInMonth(int month, boolean leap) {
      if (month == 3 || month == 5 || month == 8 || month == 10) {
         return 2592000000L;
      } else if (month == 0 || month == 2 || month == 4 || month == 6 || month == 7 || month == 9 || month == 11) {
         return 2678400000L;
      } else {
         return !leap ? 2419200000L : 2505600000L;
      }
   }

   private static long now() {
      return System.currentTimeMillis();
   }

   private static int day(long t) {
      return (int)ESMath.floorDivide(t, 86400000);
   }

   private static int timeWithinDay(long t) {
      return ESMath.positiveMod(t, 86400000);
   }

   private static int daysInYear(int y) {
      return (y % 4 != 0 || y % 100 == 0) && y % 400 != 0 ? 365 : 366;
   }

   private static int dayFromYear(int y) {
      return 365 * (y - 1970) + (int)ESMath.floorDivide(y - 1969, 4) - (int)ESMath.floorDivide(y - 1901, 100) + (int)ESMath.floorDivide(y - 1601, 400);
   }

   private static long timeFromYear(int y) {
      return (long)86400000 * dayFromYear(y);
   }

   private static int yearFromTime(long t) {
      int a = -272002;
      int b = 275942;

      while (true) {
         int guess = (a + b) / 2;
         if (guess == a || guess == b) {
            return timeFromYear(b) <= t ? b : a;
         }

         if (timeFromYear(guess) <= t) {
            a = guess;
         } else {
            b = guess;
         }
      }
   }

   private static boolean inLeapYear(long t) {
      return daysInYear(yearFromTime(t)) == 366;
   }

   private static int monthFromTime(long t) {
      int day = dayWithinYear(t);
      int[] monthStart = inLeapYear(t) ? monthDaysLeap : monthDays;

      for (int i = 0; i < 12; i++) {
         if (day < monthStart[i + 1]) {
            return i;
         }
      }

      return 0;
   }

   private static int dayWithinYear(long t) {
      return day(t) - dayFromYear(yearFromTime(t));
   }

   private static int dayFromMonth(int month, boolean inLeap) {
      int[] monthStart = inLeap ? monthDaysLeap : monthDays;
      return monthStart[month];
   }

   private static int dateFromTime(long t) {
      int day = dayWithinYear(t);
      int month = monthFromTime(t);
      return day - dayFromMonth(month, inLeapYear(t)) + 1;
   }

   private static int weekDay(long t) {
      return ESMath.positiveMod(day(t) + 4, 7);
   }

   private static long daylightSavingTA(long t) {
      TimeZone timeZone = GlobalObject.getInstance().timeZone;
      return timeZone == null
         ? fakeDaylightSavingTA(t)
         : timeZone.getOffset(1, yearFromTime(t), monthFromTime(t), dateFromTime(t), weekDay(t) + 1, timeWithinDay(t)) - timeZone.getRawOffset();
   }

   private static long localTime(long t) {
      return t + Misc.getLocalTZA(getTimeZone()) + daylightSavingTA(t);
   }

   private static double UTC(double t) {
      return t - Misc.getLocalTZA(getTimeZone()) - daylightSavingTA((long)t - Misc.getLocalTZA(getTimeZone()));
   }

   private static int hourFromTime(long t) {
      return ESMath.positiveMod(ESMath.floorDivide(t, 3600000), 24);
   }

   private static int minuteFromTime(long t) {
      return ESMath.positiveMod(ESMath.floorDivide(t, 60000), 60);
   }

   private static int secFromTime(long t) {
      return ESMath.positiveMod(ESMath.floorDivide(t, 1000), 60);
   }

   private static int msFromTime(long t) {
      return ESMath.positiveMod(t, 1000);
   }

   private static double makeTime(double hour, double min, double sec, double ms) {
      return hour * 4704985352480227328L + min * 4678479150791524352L + sec * 4652007308841189376L + ms;
   }

   private static boolean finite(double d) {
      if (Double.isNaN(d)) {
         return false;
      } else {
         return d == 9218868437227405312L ? false : d != -4503599627370496L;
      }
   }

   private static double makeDay(double param0, double param2, double param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: nop
      // 01: dload 0
      // 02: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 05: ifeq 29
      // 08: nop
      // 09: dload 2
      // 0a: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 0d: ifeq 29
      // 10: nop
      // 11: dload 4
      // 13: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 16: ifeq 29
      // 19: nop
      // 1a: dload 0
      // 1b: d2i
      // 1c: nop
      // 1d: dload 2
      // 1e: d2i
      // 1f: nop
      // 20: dload 4
      // 22: d2i
      // 23: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.makeDay (III)J
      // 26: l2d
      // 27: nop
      // 28: dreturn
      // 29: nop
      // 2a: ldc2_w 9221120237041090560
      // 2d: nop
      // 2e: dreturn
   }

   private static long makeDay(int year, int month, int date) {
      year += (int)ESMath.floorDivide(month, 12);
      month = ESMath.positiveMod(month, 12);
      long yearDay = ESMath.floorDivide(timeFromYear(year), 86400000);
      long monthDay = dayFromMonth(month, daysInYear(year) == 366);
      return yearDay + monthDay + date - 1;
   }

   private static double makeDate(double param0, double param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: nop
      // 01: dload 0
      // 02: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 05: ifeq 1c
      // 08: nop
      // 09: dload 2
      // 0a: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 0d: ifeq 1c
      // 10: nop
      // 11: dload 0
      // 12: d2l
      // 13: nop
      // 14: dload 2
      // 15: d2l
      // 16: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.makeDate (JJ)J
      // 19: l2d
      // 1a: nop
      // 1b: dreturn
      // 1c: nop
      // 1d: ldc2_w 9221120237041090560
      // 20: nop
      // 21: dreturn
   }

   private static long makeDate(long day, long time) {
      return day * 86400000 + time;
   }

   static double timeClip(double param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: nop
      // 01: dload 0
      // 02: invokestatic net/rim/ecmascript/runtime/ESDatePrototype.finite (D)Z
      // 05: ifne 0e
      // 08: nop
      // 09: ldc2_w 9221120237041090560
      // 0c: nop
      // 0d: dreturn
      // 0e: nop
      // 0f: dload 0
      // 10: nop
      // 11: ldc2_w 4845505999795912704
      // 14: dcmpl
      // 15: ifgt 22
      // 18: nop
      // 19: dload 0
      // 1a: nop
      // 1b: ldc2_w -4377866037058863104
      // 1e: dcmpg
      // 1f: ifge 28
      // 22: nop
      // 23: ldc2_w 9221120237041090560
      // 26: nop
      // 27: dreturn
      // 28: nop
      // 29: dload 0
      // 2a: d2l
      // 2b: l2d
      // 2c: nop
      // 2d: dreturn
   }

   private static void twoDigits(StringBuffer b, int value) {
      b.append((char)(value / 10 + 48));
      b.append((char)(value % 10 + 48));
   }

   private static String toString(long t, int format) {
      StringBuffer b = new StringBuffer();
      long localTime = localTime(t);
      if (format != 1) {
         b.append(dayStr[weekDay(localTime)]);
         b.append(' ');
         b.append(monthStr[monthFromTime(localTime)]);
         b.append(' ');
         twoDigits(b, dateFromTime(localTime));
         b.append(' ');
         int year = yearFromTime(localTime);
         if (year < 0) {
            b.append('-');
            year = -year;
         }

         b.append(Integer.toString(year));
      }

      if (format == 2) {
         b.append(' ');
      }

      if (format != 0) {
         twoDigits(b, hourFromTime(localTime));
         b.append(':');
         twoDigits(b, minuteFromTime(localTime));
         b.append(':');
         twoDigits(b, secFromTime(localTime));
         b.append(' ');
         int minutesFromGMT = (int)((Misc.getLocalTZA(getTimeZone()) + daylightSavingTA(t)) / 60000);
         b.append("GMT");
         if (minutesFromGMT < 0) {
            minutesFromGMT = -minutesFromGMT;
            b.append('-');
         } else {
            b.append('+');
         }

         twoDigits(b, minutesFromGMT / 60);
         twoDigits(b, minutesFromGMT % 60);
         b.append(" (");
         b.append(Misc.tzName(getTimeZone(), daylightSavingTA(t) != 0));
         b.append(")");
      }

      return b.toString();
   }

   private static String toLocaleString(GlobalObject global, long t, int format) {
      try {
         switch (format) {
            case -1:
               return toString(t, format);
            case 0:
               return global.dateFormat(t);
            case 1:
            default:
               return global.timeFormat(t);
            case 2:
               return global.dateTimeFormat(t);
         }
      } finally {
         return toString(t, format);
      }
   }

   private static String toUTCString(long t) {
      StringBuffer b = new StringBuffer();
      b.append(dayStr[weekDay(t)]);
      b.append(", ");
      twoDigits(b, dateFromTime(t));
      b.append(' ');
      b.append(monthStr[monthFromTime(t)]);
      b.append(' ');
      int year = yearFromTime(t);
      if (year < 0) {
         b.append('-');
         year = -year;
      }

      b.append(Integer.toString(year));
      b.append(' ');
      twoDigits(b, hourFromTime(t));
      b.append(':');
      twoDigits(b, minuteFromTime(t));
      b.append(':');
      twoDigits(b, secFromTime(t));
      b.append(" GMT");
      return b.toString();
   }

   private static ESDatePrototype$DateKeyword[] buildKeywords() {
      if (_keywords == null) {
         Vector timezones = new Vector();

         for (int i = 0; i < _parseableTimeZones.length; i++) {
            timezones.addElement(_parseableTimeZones[i]);
         }

         _keywords = new ESDatePrototype$DateKeyword[days.length + months.length + timezones.size()];
         int index = 0;

         for (int i = 0; i < days.length; i++) {
            _keywords[index++] = new ESDatePrototype$DateKeyword(days[i], 0);
         }

         for (int i = 0; i < months.length; i++) {
            _keywords[index++] = new ESDatePrototype$DateKeyword(months[i], 1, i % 12 + 1);
         }

         for (int i = 0; i < timezones.size(); i++) {
            _keywords[index++] = (ESDatePrototype$DateKeyword)timezones.elementAt(i);
         }
      }

      return _keywords;
   }

   static String getId(ESDatePrototype$ParseStringIndex s) {
      StringBuffer b = new StringBuffer();
      int start = s._functionType;

      while (!(s._functionType >= s._propertyNames)) {
         char ch = s._isRedirected.charAt(s._functionType);
         if (ch >= 'a' && ch <= 'z') {
            b.append((char)(ch - 'a' + 65));
            s.i = s._functionType + 1;
         } else {
            if (ch < 'A' || ch > 'Z') {
               break;
            }

            b.append(ch);
            s.i = s._functionType + 1;
         }
      }

      return s._functionType == start ? null : b.toString();
   }

   static int getNumber(ESDatePrototype$ParseStringIndex s) {
      int n = 0;
      s.follow = ' ';

      while (!(s._functionType >= s._propertyNames)) {
         char ch = s._isRedirected.charAt(s._functionType);
         switch (ch) {
            case '/':
               s.follow = ch;
               return n;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            default:
               s.i = s._functionType + 1;
               n *= 10;
               n += ch - '0';
         }
      }

      return n;
   }

   private static double parseDate(String str) {
      int year = -1;
      int month = -1;
      int date = -1;
      int hour = -1;
      int min = -1;
      int sec = -1;
      boolean haveMonthWord = false;
      boolean haveDay = false;
      ESDatePrototype$ParseStringIndex s = new ESDatePrototype$ParseStringIndex(null);
      s.str = str;
      s.i = 0;
      s.len = str.length();
      char ch = 0;
      int tzOffset = 0;
      boolean haveTZ = false;
      boolean haveAMPM = false;
      int ampm = 0;

      try {
         label280:
         while (true) {
            while (s.i < s.len) {
               ch = str.charAt(s.i);
               if (!Tokenizer.isWhiteSpace(ch)) {
                  break;
               }

               s.i++;
            }

            if (s.i >= s.len) {
               if (year == -1) {
                  year = yearFromTime(localTime(now()));
               }

               if (--month < 0) {
                  month = monthFromTime(localTime(now()));
               }

               if (date == -1) {
                  month = dateFromTime(localTime(now()));
               }

               if (hour == -1) {
                  hour = 0;
               }

               if (min == -1) {
                  min = 0;
               }

               if (sec == -1) {
                  sec = 0;
               }

               if (haveAMPM) {
                  if (hour > 12) {
                     throw new ESDatePrototype$CantParse(null);
                  }

                  if (ampm == 0) {
                     if (hour == 12) {
                        hour = 0;
                     }
                  } else if (hour < 12) {
                     hour += 12;
                  }
               }

               double day = makeDay(year, month, date);
               double time = makeTime(hour, min, sec, (double)0L);
               time = makeDate(day, time) + tzOffset;
               if (!haveTZ) {
                  time = UTC(time);
               }

               return timeClip(time);
            }

            switch (ch) {
               case '\'':
               case ')':
               case '*':
               case '+':
               case '.':
                  String id = getId(s);
                  if (id == null) {
                     throw new ESDatePrototype$CantParse(null);
                  }

                  ESDatePrototype$DateKeyword kw = null;
                  ESDatePrototype$DateKeyword[] keywords = buildKeywords();

                  for (int i = keywords.length - 1; i >= 0; i--) {
                     kw = keywords[i];
                     if (id.equals(kw.name)) {
                        switch (kw.type) {
                           case -1:
                              continue label280;
                           case 0:
                           default:
                              if (haveDay) {
                                 throw new ESDatePrototype$CantParse(null);
                              }

                              haveDay = true;
                              continue label280;
                           case 1:
                              if (haveMonthWord) {
                                 throw new ESDatePrototype$CantParse(null);
                              }

                              haveMonthWord = true;
                              if (month != -1) {
                                 if (date != -1) {
                                    throw new ESDatePrototype$CantParse(null);
                                 }

                                 date = month;
                              }

                              month = kw.value;
                              continue label280;
                           case 2:
                              if (haveTZ) {
                                 throw new ESDatePrototype$CantParse(null);
                              }

                              haveTZ = true;
                              tzOffset = -kw.value;
                              if (s.len - s.i >= 2) {
                                 ch = s.str.charAt(s.i);
                                 i = 0;
                                 boolean neg = false;
                                 if (ch == '+') {
                                    s.i++;
                                    i = getNumber(s);
                                 } else if (ch == '-') {
                                    s.i++;
                                    i = getNumber(s);
                                    neg = true;
                                 }

                                 if (i < 100) {
                                    i *= 60;
                                 } else {
                                    i = i / 100 * 60 + i % 100;
                                 }

                                 if (neg) {
                                    i = -i;
                                 }

                                 tzOffset = (int)(tzOffset - (long)i * 60 * 1000);
                              }
                              continue label280;
                           case 3:
                              if (haveAMPM) {
                                 throw new ESDatePrototype$CantParse(null);
                              }

                              haveAMPM = true;
                              ampm = kw.value;
                              continue label280;
                        }
                     }
                  }

                  throw new ESDatePrototype$CantParse(null);
               case '(':
                  do {
                     s.i++;
                     if (s.i >= s.len) {
                        throw new ESDatePrototype$CantParse(null);
                     }

                     ch = s.str.charAt(s.i);
                  } while (ch != ')');

                  s.i++;
                  break;
               case ',':
                  s.i++;
                  break;
               case '-':
               case '/':
                  s.i++;
                  break;
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  int var28 = getNumber(s);
                  if (s.follow == ':') {
                     if (hour != -1) {
                        throw new ESDatePrototype$CantParse(null);
                     }

                     hour = var28;
                  } else if (var28 > 31) {
                     if (year != -1) {
                        throw new ESDatePrototype$CantParse(null);
                     }

                     if (var28 < 100) {
                        var28 += 1900;
                     }

                     year = var28;
                  } else if (month == -1) {
                     month = var28;
                  } else {
                     if (date != -1) {
                        throw new ESDatePrototype$CantParse(null);
                     }

                     date = var28;
                  }
                  break;
               case ':':
               default:
                  s.i++;
                  int n = getNumber(s);
                  if (min == -1) {
                     min = n;
                  } else {
                     if (sec != -1) {
                        throw new ESDatePrototype$CantParse(null);
                     }

                     sec = n;
                  }
            }
         }
      } catch (ESDatePrototype$CantParse cp) {
         throw cp;
      } finally {
         ;
      }
   }

   ESDatePrototype() {
      super((double)9221120237041090560L, true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
      this.setGrowthIncrement(50);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().dateConstructor));
      this.addHostFunction(new ESDatePrototype$1(this, "toString"));
      this.addHostFunction(new ESDatePrototype$2(this, "toUTCString"));
      this.addHostFunction(new ESDatePrototype$3(this, "toGMTString"));
      this.addHostFunction(new ESDatePrototype$4(this, "toDateString"));
      this.addHostFunction(new ESDatePrototype$5(this, "toTimeString"));
      this.addHostFunction(new ESDatePrototype$6(this, "toLocaleString"));
      this.addHostFunction(new ESDatePrototype$7(this, "toLocaleDateString"));
      this.addHostFunction(new ESDatePrototype$8(this, "toLocaleTimeString"));
      this.addHostFunction(new ESDatePrototype$9(this, "valueOf"));
      this.addHostFunction(new ESDatePrototype$10(this, "getTime"));
      this.addHostFunction(new ESDatePrototype$11(this, "getFullYear"));
      this.addHostFunction(new ESDatePrototype$12(this, "getYear"));
      this.addHostFunction(new ESDatePrototype$13(this, "getUTCFullYear"));
      this.addHostFunction(new ESDatePrototype$14(this, "getMonth"));
      this.addHostFunction(new ESDatePrototype$15(this, "getUTCMonth"));
      this.addHostFunction(new ESDatePrototype$16(this, "getDate"));
      this.addHostFunction(new ESDatePrototype$17(this, "getUTCDate"));
      this.addHostFunction(new ESDatePrototype$18(this, "getDay"));
      this.addHostFunction(new ESDatePrototype$19(this, "getUTCDay"));
      this.addHostFunction(new ESDatePrototype$20(this, "getHours"));
      this.addHostFunction(new ESDatePrototype$21(this, "getUTCHours"));
      this.addHostFunction(new ESDatePrototype$22(this, "getMinutes"));
      this.addHostFunction(new ESDatePrototype$23(this, "getUTCMinutes"));
      this.addHostFunction(new ESDatePrototype$24(this, "getSeconds"));
      this.addHostFunction(new ESDatePrototype$25(this, "getUTCSeconds"));
      this.addHostFunction(new ESDatePrototype$26(this, "getMilliseconds"));
      this.addHostFunction(new ESDatePrototype$27(this, "getUTCMilliseconds"));
      this.addHostFunction(new ESDatePrototype$28(this, "getTimezoneOffset"));
      this.addHostFunction(new ESDatePrototype$29(this, "setTime", 1));
      this.addHostFunction(new ESDatePrototype$30(this, "setMilliseconds", 1));
      this.addHostFunction(new ESDatePrototype$31(this, "setUTCMilliseconds", 1));
      this.addHostFunction(new ESDatePrototype$32(this, "setSeconds", 2));
      this.addHostFunction(new ESDatePrototype$33(this, "setUTCSeconds", 2));
      this.addHostFunction(new ESDatePrototype$34(this, "setMinutes", 3));
      this.addHostFunction(new ESDatePrototype$35(this, "setUTCMinutes", 3));
      this.addHostFunction(new ESDatePrototype$36(this, "setHours", 4));
      this.addHostFunction(new ESDatePrototype$37(this, "setUTCHours", 4));
      this.addHostFunction(new ESDatePrototype$38(this, "setDate", 1));
      this.addHostFunction(new ESDatePrototype$39(this, "setUTCDate", 1));
      this.addHostFunction(new ESDatePrototype$40(this, "setMonth", 2));
      this.addHostFunction(new ESDatePrototype$41(this, "setUTCMonth", 2));
      this.addHostFunction(new ESDatePrototype$42(this, "setFullYear", 3));
      this.addHostFunction(new ESDatePrototype$43(this, "setYear", 1));
      this.addHostFunction(new ESDatePrototype$44(this, "setUTCFullYear", 3));
   }
}
