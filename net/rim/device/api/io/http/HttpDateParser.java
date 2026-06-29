package net.rim.device.api.io.http;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.util.CalendarExtensions;

public final class HttpDateParser {
   private static String[] _months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

   private HttpDateParser() {
   }

   private static final long parseANSIC(String param0, Calendar param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 3
      // 02: istore 2
      // 03: aload 0
      // 04: iload 2
      // 05: iinc 2 1
      // 08: invokevirtual java/lang/String.charAt (I)C
      // 0b: bipush 32
      // 0d: if_icmpeq 14
      // 10: bipush -1
      // 12: i2l
      // 13: lreturn
      // 14: aload 0
      // 15: iload 2
      // 16: aload 1
      // 17: invokestatic net/rim/device/api/io/http/HttpDateParser.readMonth (Ljava/lang/String;ILjava/util/Calendar;)V
      // 1a: iinc 2 3
      // 1d: aload 0
      // 1e: iload 2
      // 1f: iinc 2 1
      // 22: invokevirtual java/lang/String.charAt (I)C
      // 25: bipush 32
      // 27: if_icmpeq 2e
      // 2a: bipush -1
      // 2c: i2l
      // 2d: lreturn
      // 2e: aload 0
      // 2f: iload 2
      // 30: aload 1
      // 31: invokestatic net/rim/device/api/io/http/HttpDateParser.readDate1Or2 (Ljava/lang/String;ILjava/util/Calendar;)V
      // 34: iinc 2 2
      // 37: aload 0
      // 38: iload 2
      // 39: iinc 2 1
      // 3c: invokevirtual java/lang/String.charAt (I)C
      // 3f: bipush 32
      // 41: if_icmpeq 48
      // 44: bipush -1
      // 46: i2l
      // 47: lreturn
      // 48: aload 0
      // 49: iload 2
      // 4a: aload 1
      // 4b: invokestatic net/rim/device/api/io/http/HttpDateParser.readTime (Ljava/lang/String;ILjava/util/Calendar;)V
      // 4e: iinc 2 8
      // 51: aload 0
      // 52: iload 2
      // 53: iinc 2 1
      // 56: invokevirtual java/lang/String.charAt (I)C
      // 59: bipush 32
      // 5b: if_icmpeq 62
      // 5e: bipush -1
      // 60: i2l
      // 61: lreturn
      // 62: aload 0
      // 63: iload 2
      // 64: aload 1
      // 65: invokestatic net/rim/device/api/io/http/HttpDateParser.readYear4 (Ljava/lang/String;ILjava/util/Calendar;)V
      // 68: iinc 2 4
      // 6b: aload 1
      // 6c: bipush 14
      // 6e: bipush 0
      // 6f: invokevirtual java/util/Calendar.set (II)V
      // 72: goto 7d
      // 75: astore 3
      // 76: bipush 0
      // 77: i2l
      // 78: lreturn
      // 79: astore 3
      // 7a: bipush 0
      // 7b: i2l
      // 7c: lreturn
      // 7d: aload 1
      // 7e: checkcast net/rim/device/cldc/util/CalendarExtensions
      // 81: invokeinterface net/rim/device/cldc/util/CalendarExtensions.getTimeLong ()J 1
      // 86: lreturn
      // try (11 -> 24): 63 null
      // try (25 -> 38): 63 null
      // try (39 -> 52): 63 null
      // try (53 -> 62): 63 null
      // try (11 -> 24): 67 null
      // try (25 -> 38): 67 null
      // try (39 -> 52): 67 null
      // try (53 -> 62): 67 null
   }

   private static final int readDigits(String str, int offset, int numDigits) {
      return Integer.parseInt(str.substring(offset, offset + numDigits));
   }

   private static final void readDate2(String dateString, int index, Calendar calendar) {
      calendar.set(5, readDigits(dateString, index, 2));
   }

   private static final void readDate1Or2(String dateString, int index, Calendar calendar) {
      if (dateString.charAt(index) == ' ') {
         calendar.set(5, readDigits(dateString, index + 1, 1));
      } else {
         readDate2(dateString, index, calendar);
      }
   }

   private static final void readYear4(String dateString, int index, Calendar calendar) {
      calendar.set(1, readDigits(dateString, index, 4));
   }

   private static final void readMonth(String dateString, int index, Calendar calendar) throws IOException {
      int month = 0;

      while (month < 12 && !dateString.regionMatches(true, index, _months[month], 0, 3)) {
         month++;
      }

      if (month >= 12) {
         throw new IOException("No valid month was specified");
      }

      calendar.set(2, month);
   }

   private static final void readTime(String dateString, int index, Calendar calendar) throws IOException {
      if (dateString.charAt(index + 2) == ':' && dateString.charAt(index + 5) == ':') {
         int hour = readDigits(dateString, index, 2);
         int minutes = readDigits(dateString, index + 3, 2);
         int seconds = readDigits(dateString, index + 6, 2);
         if (hour >= 0 && hour <= 23 && minutes >= 0 && minutes <= 59 && seconds >= 0 && seconds <= 59) {
            calendar.set(11, hour);
            calendar.set(12, minutes);
            calendar.set(13, seconds);
         } else {
            throw new IOException("The time was not specified correctly");
         }
      } else {
         throw new IOException("The time was not specified correctly");
      }
   }

   private static final long parseISO8601(String dateString, Calendar calendar) {
      int dateLength = dateString.length();
      int index = 0;
      int year = 1978;
      int month = 0;
      int day = 1;
      int hour = 0;
      int minutes = 0;
      int seconds = 0;
      int milliseconds = 0;
      long timeZoneOffset = 0;
      if (dateLength >= 4) {
         year = readDigits(dateString, index, 4);
         index += 5;
      }

      if (dateLength >= 7) {
         month = readDigits(dateString, index, 2) - 1;
         index += 3;
      }

      if (dateLength >= 10) {
         day = readDigits(dateString, index, 2);
         index += 3;
      }

      if (dateLength >= 16) {
         hour = readDigits(dateString, index, 2);
         index += 3;
         minutes = readDigits(dateString, index, 2);
         index += 2;
      }

      if (dateLength >= 17) {
         if (dateString.charAt(16) != ':') {
            timeZoneOffset = readTimeZone(dateString, index, calendar);
         } else {
            seconds = readDigits(dateString, ++index, 2);
            index += 2;
            if (dateLength >= 20 && dateString.charAt(19) == '.') {
               int endIndex = ++index;

               while (endIndex < dateLength && Character.isDigit(dateString.charAt(endIndex))) {
                  endIndex++;
               }

               int numDigits = endIndex - index;
               if (numDigits > 0 && numDigits <= 3) {
                  milliseconds = readDigits(dateString, index, endIndex - index);
                  switch (numDigits) {
                     case 0:
                        break;
                     case 1:
                     default:
                        milliseconds *= 100;
                        break;
                     case 2:
                        milliseconds *= 10;
                  }
               }

               index = endIndex;
               if (dateLength > endIndex) {
                  timeZoneOffset = readTimeZone(dateString, index, calendar);
               }
            } else {
               timeZoneOffset = readTimeZone(dateString, index, calendar);
            }
         }
      }

      calendar.set(1, year);
      calendar.set(2, month);
      calendar.set(5, day);
      calendar.set(11, hour);
      calendar.set(12, minutes);
      calendar.set(13, seconds);
      calendar.set(14, milliseconds);
      return ((CalendarExtensions)calendar).getTimeLong() + timeZoneOffset;
   }

   private static final long readTimeZone(String dateString, int index, Calendar calendar) {
      int dateLength = dateString.length();
      if (dateLength > index && dateString.charAt(index) != 'Z' && index + 5 <= dateLength) {
         boolean positive = dateString.charAt(index) == '+';
         long value = (long)(readDigits(dateString, index + 1, 2) * 60 * 60) * 1000;
         value += (long)(readDigits(dateString, index + 4, 2) * 60) * 1000;
         return positive ? value : -value;
      } else {
         return 0;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final long parse(String date) {
      if (date != null && date.length() != 0) {
         try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            boolean isFirstDigit = false;
            if (Character.isDigit(date.charAt(0))) {
               isFirstDigit = true;
               if (Character.isDigit(date.charAt(2))) {
                  return parseISO8601(date, calendar);
               }
            }

            date = date.trim();
            int dateLength = date.length();
            int position = 0;
            if (!isFirstDigit) {
               int commaIndex = date.indexOf(44);
               int spacePos = date.indexOf(32);
               if (commaIndex == -1 && spacePos != -1 || spacePos < commaIndex) {
                  return parseANSIC(date, calendar);
               }

               if (commaIndex == -1) {
                  return 0;
               }

               position = Math.min(commaIndex, spacePos) + 1;
            }

            position = trimHead(date, position, dateLength);
            int spacePos = date.indexOf(32, position);
            int dashPos = date.indexOf(45, position);
            int nextTokenPos = dashPos == -1 ? spacePos : (spacePos == -1 ? dateLength : Math.min(spacePos, dashPos));
            int dayOfMonth = 0;
            int month = -1;
            boolean var23 = false /* VF: Semaphore variable */;

            try {
               var23 = true;
               dayOfMonth = NumberUtilities.parseInt(date, trimHead(date, position, dateLength), nextTokenPos, 10);
               position = nextTokenPos + 1;
               if (position >= dateLength) {
                  return 0;
               }

               spacePos = date.indexOf(32, position);
               dashPos = date.indexOf(45, position);
               nextTokenPos = dashPos == -1 ? spacePos : (spacePos == -1 ? dateLength : Math.min(spacePos, dashPos));
               month = getMonth(date, trimHead(date, position, dateLength), nextTokenPos);
               position = nextTokenPos + 1;
               var23 = false;
            } finally {
               if (var23) {
                  label374: {
                     month = getMonth(date, trimHead(date, position, dateLength), nextTokenPos);
                     if (month == -1) {
                        return 0;
                     }

                     position = nextTokenPos + 1;
                     if (position >= dateLength) {
                        return 0;
                     }

                     spacePos = date.indexOf(32, position);
                     dashPos = date.indexOf(45, position);
                     nextTokenPos = dashPos == -1 ? spacePos : (spacePos == -1 ? dateLength : Math.min(spacePos, dashPos));
                     dayOfMonth = NumberUtilities.parseInt(date, trimHead(date, position, dateLength), nextTokenPos, 10);
                     position = nextTokenPos + 1;
                     break label374;
                  }
               }
            }

            if (month == -1) {
               return 0;
            }

            if (position >= dateLength) {
               return 0;
            }

            spacePos = date.indexOf(32, position);
            int commaIndex = date.indexOf(44, position);
            int yearEndIndex;
            if (commaIndex == -1 && spacePos == -1) {
               yearEndIndex = dateLength;
            } else if (commaIndex == -1) {
               yearEndIndex = spacePos;
            } else {
               yearEndIndex = Math.min(spacePos, commaIndex);
            }

            int year = NumberUtilities.parseInt(date, trimHead(date, position, dateLength), yearEndIndex, 10);
            if (year < 100) {
               calendar.setTime(new Date());
               int defaultCenturyStartYear = calendar.get(1) - 80;
               int twoDigitDefaultCenturyStartYear = defaultCenturyStartYear % 100;
               year += defaultCenturyStartYear / 100 * 100 + (year < twoDigitDefaultCenturyStartYear ? 100 : 0);
            }

            for (position = yearEndIndex; position < dateLength; position++) {
               char ch = date.charAt(position);
               if (ch != ' ' && ch != ',') {
                  break;
               }
            }

            int minute = 0;
            int secondEndIndex = dateLength;
            int second = 0;
            int hourOfDay = 0;
            if (position < dateLength) {
               int hourEndIndex = date.indexOf(58, position);
               if (hourEndIndex == -1) {
                  secondEndIndex = date.indexOf(32, position);
                  if (secondEndIndex == -1) {
                     secondEndIndex = dateLength;
                  }

                  if (secondEndIndex - position == 6) {
                     hourOfDay = NumberUtilities.parseInt(date, position, position + 2, 10);
                     minute = NumberUtilities.parseInt(date, position + 2, position + 4, 10);
                     second = NumberUtilities.parseInt(date, position + 4, position + 6, 10);
                  }
               } else {
                  hourOfDay = NumberUtilities.parseInt(date, position, hourEndIndex, 10);
                  int minuteEndIndex = date.indexOf(58, hourEndIndex + 1);
                  if (minuteEndIndex == -1) {
                     minuteEndIndex = date.indexOf(32, hourEndIndex + 1);
                     hourOfDay = NumberUtilities.parseInt(date, hourEndIndex + 1, minuteEndIndex, 10);
                     secondEndIndex = minuteEndIndex;
                     second = 0;
                  } else {
                     minute = NumberUtilities.parseInt(date, hourEndIndex + 1, minuteEndIndex, 10);
                     secondEndIndex = date.indexOf(32, minuteEndIndex + 1);
                     if (secondEndIndex == -1) {
                        secondEndIndex = dateLength;
                     }

                     second = NumberUtilities.parseInt(date, minuteEndIndex + 1, secondEndIndex, 10);
                  }
               }
            }

            if (hourOfDay >= 0 && hourOfDay <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59) {
               TimeZone timeZone;
               if (secondEndIndex < dateLength) {
                  timeZone = TimeZone.getTimeZone(mapTimeZone(date, secondEndIndex + 1, Math.min(secondEndIndex + 4, dateLength)));
               } else {
                  timeZone = TimeZone.getDefault();
               }

               calendar.setTimeZone(timeZone);
               calendar.set(1, year);
               calendar.set(2, month);
               calendar.set(5, dayOfMonth);
               calendar.set(11, hourOfDay);
               calendar.set(12, minute);
               calendar.set(13, second);
               calendar.set(14, 0);
               return ((CalendarExtensions)calendar).getTimeLong();
            } else {
               return 0;
            }
         } finally {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static final int trimHead(String date, int position, int dateLength) {
      while (position < dateLength) {
         if (date.charAt(position) != ' ') {
            return position;
         }

         position++;
      }

      return position;
   }

   private static final int getMonth(String monthString, int fromPos, int toPos) {
      if (toPos - fromPos < 3) {
         return -1;
      }

      char char0 = monthString.charAt(fromPos);
      switch (char0) {
         case 'A':
         case 'a':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Apr", 0, 3, 1701707776)) {
               return 3;
            }

            if (StringUtilities.regionMatches(monthString, true, fromPos, "Aug", 0, 3, 1701707776)) {
               return 7;
            }
            break;
         case 'D':
         case 'd':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Dec", 0, 3, 1701707776)) {
               return 11;
            }
            break;
         case 'F':
         case 'f':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Feb", 0, 3, 1701707776)) {
               return 1;
            }
            break;
         case 'J':
         case 'j':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Jan", 0, 3, 1701707776)) {
               return 0;
            }

            if (StringUtilities.regionMatches(monthString, true, fromPos, "Jun", 0, 3, 1701707776)) {
               return 5;
            }

            if (StringUtilities.regionMatches(monthString, true, fromPos, "Jul", 0, 3, 1701707776)) {
               return 6;
            }
            break;
         case 'M':
         case 'm':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Mar", 0, 3, 1701707776)) {
               return 2;
            }

            if (StringUtilities.regionMatches(monthString, true, fromPos, "May", 0, 3, 1701707776)) {
               return 4;
            }
            break;
         case 'N':
         case 'n':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Nov", 0, 3, 1701707776)) {
               return 10;
            }
            break;
         case 'O':
         case 'o':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Oct", 0, 3, 1701707776)) {
               return 9;
            }
            break;
         case 'S':
         case 's':
            if (StringUtilities.regionMatches(monthString, true, fromPos, "Sep", 0, 3, 1701707776)) {
               return 8;
            }
      }

      return -1;
   }

   private static final String mapTimeZone(String tz, int fromIndex, int toIndex) {
      if (tz == null) {
         return tz;
      }

      int tzLength = toIndex - fromIndex;
      if (tzLength >= 1 && tzLength <= 4) {
         String testValue1 = null;
         String testValue2 = null;
         String guessValue = null;
         char ch = CharacterUtilities.toLowerCase(tz.charAt(fromIndex), 1701707776);
         switch (ch) {
            case 'a':
               if (tzLength == 4) {
                  guessValue = "America/Anchorage";
                  testValue1 = "AKST";
                  testValue2 = "AKDT";
               } else {
                  guessValue = "America/Halifax";
                  testValue1 = "AST";
                  testValue2 = "ADT";
               }
               break;
            case 'b':
               guessValue = "Pacific/Midway";
               testValue1 = "BEST";
               testValue2 = "BEDT";
               break;
            case 'c':
               guessValue = "America/Chicago";
               testValue1 = "CST";
               testValue2 = "CDT";
               break;
            case 'e':
               guessValue = "America/New_York";
               testValue1 = "EST";
               testValue2 = "EDT";
               break;
            case 'h':
               guessValue = "Pacific/Honolulu";
               testValue1 = "HAST";
               testValue2 = "HADT";
               break;
            case 'm':
               guessValue = "America/Denver";
               testValue1 = "MST";
               testValue2 = "MDT";
               break;
            case 'n':
               guessValue = "America/St_Johns";
               testValue1 = "NST";
               testValue2 = "NDT";
               break;
            case 'p':
               guessValue = "America/Los_Angeles";
               testValue1 = "PST";
               testValue2 = "PDT";
         }

         return (testValue1 == null || !StringUtilities.regionMatches(tz, true, fromIndex, testValue1, 0, testValue1.length(), 1701707776))
               && (testValue2 == null || !StringUtilities.regionMatches(tz, true, fromIndex, testValue2, 0, testValue2.length(), 1701707776))
            ? tz
            : guessValue;
      } else {
         return tz;
      }
   }
}
