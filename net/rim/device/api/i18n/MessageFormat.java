package net.rim.device.api.i18n;

import java.util.Date;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public class MessageFormat extends Format {
   private Locale _locale = Locale.getDefault();
   private String pattern = "";
   private Format[] _formats = new Format[0];
   private int[] offsets = new int[15];
   private int[] argumentNumbers = new int[15];
   private int maxOffset = -1;
   private static final String[] typeList = new String[]{"", "number", "date", "time", "choice"};
   private static final String[] dateModifierList = new String[]{"", "short", "medium", "long", "full"};
   private static final int MAX_ARGUMENTS = 15;

   public MessageFormat(String pattern) {
      this.applyPattern(pattern);
   }

   private MessageFormat(String pattern, Locale locale) {
      this._locale = locale;
      this.applyPattern(pattern);
   }

   public void applyPattern(String newPattern) {
      StringBuffer[] segments = new StringBuffer[4];

      for (int i = 0; i < segments.length; i++) {
         segments[i] = new StringBuffer();
      }

      int part = 0;
      int formatNumber = 0;
      boolean inQuote = false;
      int braceStack = 0;
      this.maxOffset = -1;

      for (int i = 0; i < newPattern.length(); i++) {
         char ch = newPattern.charAt(i);
         if (part == 0) {
            if (ch == '\'') {
               if (i + 1 < newPattern.length() && newPattern.charAt(i + 1) == '\'') {
                  segments[part].append(ch);
                  i++;
               } else {
                  inQuote = !inQuote;
               }
            } else if (ch == '{' && !inQuote) {
               part = 1;
            } else {
               segments[part].append(ch);
            }
         } else if (inQuote) {
            segments[part].append(ch);
            if (ch == '\'') {
               inQuote = false;
            }
         } else {
            switch (ch) {
               case '\'':
                  inQuote = true;
               default:
                  segments[part].append(ch);
                  break;
               case ',':
                  if (part < 3) {
                     part++;
                  } else {
                     segments[part].append(ch);
                  }
                  break;
               case '{':
                  braceStack++;
                  segments[part].append(ch);
                  break;
               case '}':
                  if (braceStack == 0) {
                     part = 0;
                     this.makeFormat(i, formatNumber, segments);
                     formatNumber++;
                  } else {
                     braceStack--;
                     segments[part].append(ch);
                  }
            }
         }
      }

      if (braceStack == 0 && part != 0) {
         this.maxOffset = -1;
         throw new IllegalArgumentException("Unmatched braces");
      }

      this.pattern = segments[0].toString();
   }

   private static final int findKeyword(String s, String[] list) {
      for (int i = 0; i < list.length; i++) {
         if (s.equals(list[i])) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final StringBuffer format(Object arguments, StringBuffer result, FieldPosition ignored) {
      Object[] objs;
      try {
         objs = (Object[])arguments;
      } catch (ClassCastException cce) {
         throw new IllegalArgumentException("argument should be an array");
      }

      return this.format(objs, result, ignored, 0);
   }

   public static String format(String pattern, Object[] arguments) {
      MessageFormat temp = new MessageFormat(pattern);
      return temp.format(arguments);
   }

   private StringBuffer format(Object[] arguments, StringBuffer result, FieldPosition status, int recursionProtection) {
      int lastOffset = 0;

      for (int i = 0; i <= this.maxOffset; i++) {
         StringUtilities.append(result, this.pattern, lastOffset, this.offsets[i] - lastOffset);
         lastOffset = this.offsets[i];
         int argumentNumber = this.argumentNumbers[i];
         if (arguments != null && argumentNumber < arguments.length) {
            Object obj = arguments[argumentNumber];
            boolean tryRecursion = false;
            Format subformat = null;
            if (this._formats != null && this._formats.length > i) {
               subformat = this._formats[i];
            }

            String arg;
            if (obj == null) {
               arg = "null";
            } else if (subformat != null) {
               arg = subformat.format(obj);
            } else if (obj instanceof Date) {
               arg = DateFormat.getInstance(63).format(obj);
            } else if (!(obj instanceof String)) {
               arg = obj.toString();
               if (arg == null) {
                  arg = "null";
               }
            } else {
               arg = (String)obj;
            }

            if (tryRecursion && arg.indexOf(123) >= 0) {
               MessageFormat temp = new MessageFormat(arg, this._locale);
               temp.format(arguments, result, status, recursionProtection);
            } else {
               result.append(arg);
            }
         } else {
            result.append('{');
            result.append(argumentNumber);
            result.append('}');
         }
      }

      StringUtilities.append(result, this.pattern, lastOffset, this.pattern.length() - lastOffset);
      return result;
   }

   @Override
   public int[] getFields() {
      int[] arguments = new int[this.maxOffset + 1];
      System.arraycopy(this.argumentNumbers, 0, arguments, 0, this.maxOffset + 1);
      return arguments;
   }

   public Format[] getFormats() {
      int length = this._formats.length;
      Format[] formats = new Format[length];
      System.arraycopy(this._formats, 0, formats, 0, length);
      return formats;
   }

   public Locale getLocale() {
      return this._locale;
   }

   @Override
   public int hashCode() {
      return this.pattern.hashCode();
   }

   private void makeFormat(int position, int offsetNumber, StringBuffer[] segments) {
      int oldMaxOffset = this.maxOffset;

      try {
         int argumentNumber = Integer.parseInt(segments[1].toString());
         if (argumentNumber < 0 || argumentNumber >= 15) {
            throw new NumberFormatException();
         }

         this.maxOffset = offsetNumber;
         this.offsets[offsetNumber] = segments[0].length();
         this.argumentNumbers[offsetNumber] = argumentNumber;
      } catch (Exception e) {
         throw new IllegalArgumentException("argument number too large at ");
      }

      Format newFormat;
      newFormat = null;
      label44:
      switch (findKeyword(segments[2].toString(), typeList)) {
         case -1:
         case 1:
            this.maxOffset = oldMaxOffset;
            throw new IllegalArgumentException("Unknown format type");
         case 0:
            break;
         case 2:
         default:
            switch (findKeyword(segments[3].toString(), dateModifierList)) {
               case -1:
                  newFormat = DateFormat.getInstance(48);

                  try {
                     ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                     break label44;
                  } catch (Exception e) {
                     this.maxOffset = oldMaxOffset;
                     throw new IllegalArgumentException("Invalid pattern");
                  }
               case 0:
               default:
                  newFormat = DateFormat.getInstance(48);
                  break label44;
               case 1:
                  newFormat = DateFormat.getInstance(56);
                  break label44;
               case 2:
                  newFormat = DateFormat.getInstance(48);
                  break label44;
               case 3:
                  newFormat = DateFormat.getInstance(40);
                  break label44;
               case 4:
                  newFormat = DateFormat.getInstance(32);
                  break label44;
            }
         case 3:
            switch (findKeyword(segments[3].toString(), dateModifierList)) {
               case -1:
                  newFormat = DateFormat.getInstance(6);

                  try {
                     ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                     break;
                  } catch (Exception e) {
                     this.maxOffset = oldMaxOffset;
                     throw new IllegalArgumentException("Invalid pattern");
                  }
               case 0:
               default:
                  newFormat = DateFormat.getInstance(6);
                  break;
               case 1:
               case 2:
                  newFormat = DateFormat.getInstance(7);
                  break;
               case 3:
               case 4:
                  newFormat = DateFormat.getInstance(6);
                  break;
               case 5:
               case 6:
                  newFormat = DateFormat.getInstance(5);
                  break;
               case 7:
               case 8:
                  newFormat = DateFormat.getInstance(4);
            }
      }

      this.setFormat(offsetNumber, newFormat);
      segments[1].setLength(0);
      segments[2].setLength(0);
      segments[3].setLength(0);
   }

   public void setFormats(Format[] formats) {
      int length = formats == null ? 0 : formats.length;
      this._formats = new Format[length];
      if (formats != null) {
         System.arraycopy(formats, 0, this._formats, 0, length);
      }
   }

   public void setFormat(int variable, Format format) {
      if (this._formats.length <= variable) {
         Array.resize(this._formats, variable + 1);
      }

      this._formats[variable] = format;
   }

   public void setLocale(Locale locale) {
      this._locale = locale;
   }
}
