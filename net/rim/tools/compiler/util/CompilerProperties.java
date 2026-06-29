package net.rim.tools.compiler.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.tools.compiler.exec.MyArrays;

public class CompilerProperties extends Hashtable {
   private String _seps = "()<>@,;:'\"/[]?={} \t";
   private char[] _buffer;

   public Object setProperty(String key, String value) {
      return this.put(key, value);
   }

   public String getProperty(String key) {
      Object o = this.get(key);
      if (o == null) {
         return null;
      } else if (!(o instanceof String)) {
         Vector v = (Vector)o;
         return !v.isEmpty() ? (String)v.firstElement() : null;
      } else {
         return (String)o;
      }
   }

   public String getQuotedProperty(String key) {
      String result = this.getProperty(key);
      if (result != null && result.length() != 0) {
         return result.charAt(0) == '"' ? result.substring(1, result.length() - 1) : result;
      } else {
         return null;
      }
   }

   public void putVector(String key, Vector values) {
      this.put(key, values);
   }

   public Vector getVector(String key) {
      Object o = this.get(key);
      if (o == null) {
         Vector v = new Vector();
         this.putVector(key, v);
         return v;
      }

      if (!(o instanceof String)) {
         return (Vector)o;
      }

      Vector v = vectorize((String)o);
      this.putVector(key, v);
      return v;
   }

   public static Vector vectorize(String value) {
      Vector result = new Vector();
      if (value != null) {
         StringBuffer buffer = new StringBuffer(value);
         int num = buffer.length();
         int index = 0;

         while (index < num) {
            char ch = buffer.charAt(index);
            if (ch == '"') {
               buffer.deleteCharAt(index);
               num--;
            } else if (ch == ';') {
               value = buffer.toString().substring(0, index);
               buffer.delete(0, ++index);
               num = buffer.length();
               index = 0;
               if (!result.contains(value)) {
                  result.addElement(value);
               }
            } else {
               index++;
            }
         }

         if (num > 0) {
            value = buffer.toString();
            if (!result.contains(value)) {
               result.addElement(value);
            }
         }
      }

      return result;
   }

   public Vector parseVector(String propertyName, boolean stripWhitespace) {
      Vector result = this.getVector(propertyName);
      if (!result.isEmpty() && stripWhitespace) {
         int num = result.size();
         StringBuffer newValue = new StringBuffer();

         for (int index = 0; index < num; index++) {
            String value = (String)result.elementAt(index);
            if (value != null) {
               boolean found = false;
               newValue.setLength(0);
               int length = value.length();

               for (int i = 0; i < length; i++) {
                  char ch = value.charAt(i);
                  switch (ch) {
                     case '\t':
                     case ' ':
                        found = true;
                        break;
                     default:
                        newValue.append(ch);
                  }
               }

               if (found) {
                  result.setElementAt(newValue.toString(), index);
               }
            }
         }
      }

      this.remove(propertyName);
      return result;
   }

   private int readConfigLine(InputStream rdr) {
      boolean prefix = true;
      boolean comment = false;
      int percent = -1;
      int index = 0;

      while (true) {
         int ch = rdr.read();
         if (ch < 0) {
            return -1;
         }

         if (ch != 13) {
            if (ch == 10) {
               while (index > 0 && this._buffer[index - 1] <= ' ') {
                  index--;
               }

               return index;
            }

            if (!prefix || ch > 32) {
               prefix = false;
               if (ch == 35) {
                  comment = true;
               }

               if (!comment) {
                  if (ch == 37) {
                     if (percent != -1) {
                        if (percent != index) {
                           String key = new String(this._buffer, percent, index - percent);
                           index = percent - 1;
                           String value = this.getProperty(key);
                           if (value != null) {
                              int num = value.length();
                              if (index + num >= this._buffer.length) {
                                 this._buffer = MyArrays.resize(this._buffer, this._buffer.length + num);
                              }

                              for (int i = 0; i < num; i++) {
                                 this._buffer[index++] = value.charAt(i);
                              }
                           }
                        }

                        percent = -1;
                        continue;
                     }

                     percent = index + 1;
                  }

                  if (index == this._buffer.length) {
                     this._buffer = MyArrays.resize(this._buffer, this._buffer.length * 2);
                  }

                  this._buffer[index++] = (char)ch;
               }
            }
         }
      }
   }

   private Vector storeProperty(String key, Vector values) {
      if (key != null && !values.isEmpty()) {
         if (values.size() == 1) {
            this.setProperty(key, (String)values.firstElement());
            values.setSize(0);
            return values;
         }

         this.putVector(key, values);
         values = null;
      }

      return values;
   }

   public void readDefFile(String fileName, InputStream rdr) throws IOException {
      String propertyName = null;
      Vector values = null;
      if (this._buffer == null) {
         this._buffer = new char[32];
      }

      int lineNum = 0;

      while (true) {
         int lineLen = this.readConfigLine(rdr);
         if (lineLen == -1) {
            values = this.storeProperty(propertyName, values);
            return;
         }

         lineNum++;
         if (lineLen != 0) {
            if (this._buffer[0] != '[') {
               if (propertyName == null) {
                  throw new IOException(fileName + "(" + lineNum + "): Error!: Expecting '[' but found '" + new String(this._buffer, 0, lineLen) + "'");
               }

               String line = new String(this._buffer, 0, lineLen);
               if (values.isEmpty() || !values.contains(line)) {
                  values.addElement(line);
               }
            } else {
               if (this._buffer[--lineLen] != ']') {
                  throw new IOException(fileName + "(" + lineNum + "): Error!: Expecting ']' but found '" + new String(this._buffer, 0, ++lineLen) + "'");
               }

               lineLen--;
               values = this.storeProperty(propertyName, values);
               propertyName = new String(this._buffer, 1, lineLen);
               Object obj = this.get(propertyName);
               if (obj == null) {
                  if (values == null) {
                     values = new Vector();
                  }
               } else if (obj instanceof String) {
                  if (values == null) {
                     values = new Vector();
                  }

                  values.addElement(obj);
               } else {
                  values = (Vector)obj;
               }
            }
         }
      }
   }

   public void load(String input) throws IOException {
      int offset = 0;
      int end = input.length();
      boolean badKey = false;
      boolean badValue = false;
      StringBuffer buffer = new StringBuffer();
      boolean done = false;

      while (!done) {
         int length = 0;
         int ch = 0;
         buffer.setLength(0);

         while (true) {
            if (offset == end) {
               if (buffer.length() == 0) {
                  return;
               }

               badKey = true;
               break;
            }

            int var16 = input.charAt(offset++);
            if (var16 != '\r' && var16 != '\n') {
               length++;
               if (var16 == ':' || var16 == '=') {
                  break;
               }

               if (var16 > 31 && var16 != 127 && this._seps.indexOf((char)var16) == -1) {
                  buffer.append((char)var16);
               } else {
                  badKey = true;
               }
            } else {
               if (buffer.length() != 0) {
                  badKey = true;
                  break;
               }

               badKey = false;
            }
         }

         if (buffer.length() == 0) {
            throw new IOException("Invalid empty key name in property");
         }

         if (badKey) {
            throw new IOException("Invalid key name in property: '" + buffer.toString() + "'.");
         }

         String key = buffer.toString();
         buffer.setLength(0);

         while (true) {
            if (offset == end) {
               done = true;
               break;
            }

            ch = input.charAt(offset++);
            if (ch != 13) {
               if (ch == 10) {
                  if (length != 72 && length != 70 || offset >= end || input.charAt(offset) != ' ') {
                     break;
                  }

                  offset++;
                  length = 1;
               } else {
                  length++;
                  if (ch != 32 && ch != 9 || buffer.length() != 0) {
                     if (ch == 92 && offset < end && input.charAt(offset) == 'u') {
                        offset++;
                        ch = 0;

                        for (int i = 0; i < 4 && offset < end; i++) {
                           int x = input.charAt(offset++);
                           int h = 0;
                           if (48 <= x && x <= 57) {
                              h = x - 48;
                           } else if (97 <= x && x <= 102) {
                              h = x - 97 + 10;
                           } else {
                              if (65 > x || x > 70) {
                                 done = true;
                                 break;
                              }

                              h = x - 65 + 10;
                           }

                           length++;
                           ch = (ch << 4) + h;
                        }
                     }

                     if ((ch <= 31 || ch == 127) && ch != 9) {
                        badValue = true;
                     }

                     buffer.append((char)ch);
                  }
               }
            }
         }

         length = buffer.length();
         boolean trim = false;

         while (true) {
            length--;
            char var18;
            if (length < 0 || (var18 = buffer.charAt(length)) != ' ' && var18 != '\t') {
               if (trim) {
                  buffer.setLength(++length);
               }

               String value = buffer.toString();
               if (badValue) {
                  throw new IOException("Value for key: '" + key + "', is invalid: '" + value + "'.");
               }

               this.put(key, value);
               break;
            }

            trim = true;
         }
      }
   }
}
