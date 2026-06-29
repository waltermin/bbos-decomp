package net.rim.device.apps.internal.browser.debug;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class WMLD implements Decompiler {
   private PushbackInputStream _in;
   private int _inSize;
   private ByteArrayOutputStream _outBuf = (ByteArrayOutputStream)(new Object());
   private PrintStream _out = (PrintStream)(new Object(this._outBuf));
   private int _depth;
   private byte[] _stringTable;
   private int _stringTableLength;
   private String _encoding;
   private static final int WMLT_A = 28;
   private static final int WMLTC_ATTRIBUTES = 128;
   private static final int WMLTC_CONTENT = 64;
   private static final int WMLTC_END = 1;
   private static final int WMLTC_TAG_VALUE = -193;
   private static final int WMLC_INLINE_STRING = 3;
   private static final int WMLC_INLINE_STRING_END = 0;
   private static final int WMLG_ENTITY = 2;
   private static final int WMLG_EXT_I_0 = 64;
   private static final int WMLG_EXT_I_1 = 65;
   private static final int WMLG_EXT_I_2 = 66;
   private static final int WMLG_EXT_T_0 = 128;
   private static final int WMLG_EXT_T_1 = 129;
   private static final int WMLG_EXT_T_2 = 130;
   private static final int WMLG_STR_T = 131;

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final String decompile(byte[] data) {
      int wbxmlVersion = 0;
      int wmlVersion = 0;

      String wmlVersionString;
      try {
         ByteArrayInputStream inFile = (ByteArrayInputStream)(new Object(data));
         this._inSize = inFile.available();
         this._in = new PushbackInputStream(inFile);
         boolean var17 = false /* VF: Semaphore variable */;

         label154:
         try {
            var17 = true;
            this._outBuf.reset();
            var17 = false;
         } finally {
            if (var17) {
               this._outBuf = (ByteArrayOutputStream)(new Object());
               this._out = (PrintStream)(new Object(this._outBuf));
               break label154;
            }
         }

         this._depth = 0;

         try {
            wbxmlVersion = this._in.read();
            wmlVersion = this._in.readMBInt();
            if (wmlVersion == 0) {
               this._in.readMBInt();
            }
         } catch (Throwable var18) {
            this._out.println(e);
            return this._outBuf.toString();
         }

         switch (wbxmlVersion) {
            case 0:
            default:
               this._out
                  .println(
                     ((StringBuffer)(new Object("ERROR: Wrong WBXML version number (got 0x")))
                        .append(Integer.toHexString(wbxmlVersion))
                        .append("; expected 0x01, 0x02, or 0x03)")
                        .toString()
                  );
               this.crashBurn();
               return this._outBuf.toString();
            case 1:
            case 2:
            case 3:
               switch (wmlVersion) {
                  case 2:
                     wmlVersionString = "1.0";
                     break;
                  case 4:
                     wmlVersionString = "1.1";
                     break;
                  case 8:
                     wmlVersionString = "1.2";
                     break;
                  case 9:
                     wmlVersionString = "1.2";
                     break;
                  case 10:
                     wmlVersionString = "1.3";
                     break;
                  case 4360:
                     wmlVersionString = "OW1.1";
                     break;
                  default:
                     this._out
                        .println(
                           ((StringBuffer)(new Object("ERROR: Wrong WML version number (got 0x")))
                              .append(Integer.toHexString(wmlVersion))
                              .append("; expected 0x02, 0x04, 0x09, 0x0A, or 0x1108)")
                              .toString()
                        );
                     this.crashBurn();
                     return this._outBuf.toString();
               }

               int charset = this._in.readMBInt();
               if (charset == 0) {
                  charset = 106;
               }

               this._encoding = WSPHeaderDecoder.getCharsetName(charset);
               if ((this._stringTableLength = this._in.readMBInt()) > 0) {
                  this._stringTable = new byte[this._stringTableLength];

                  for (int i = 0; i < this._stringTableLength; i++) {
                     this._stringTable[i] = (byte)this._in.read();
                  }
               }
         }
      } catch (Throwable var20) {
         this._out.println(e);
         return this._outBuf.toString();
      }

      this._out.println("<?xml version=\"1.0\"?>");
      this._out
         .println(
            ((StringBuffer)(new Object("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML ")))
               .append(wmlVersionString)
               .append("//EN\" \"http://www.wapforum.org/DTD/wml_")
               .append(wmlVersionString)
               .append(".xml\">")
               .toString()
         );
      this.parseTags();
      this._stringTable = null;
      this._encoding = null;
      this._in = null;
      return this._outBuf.toString();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void crashBurn() {
      this._out.flush();
      this._out.println();

      try {
         this._out.println(((StringBuffer)(new Object("FATAL: Decompile halted at byte "))).append(this._inSize - this._in.available()).toString());
         this._in.close();
      } catch (Throwable var3) {
         this._out.println(e);
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean parseTags() {
      try {
         int input;
         while ((input = this._in.read()) != -1) {
            if (input == 3) {
               if (!this.parseString()) {
                  return false;
               }
            } else if (input == 131) {
               int ref = this._in.readMBInt();
               String st;
               if ((st = this.stringTableRef(ref)) != null) {
                  this._out.print(st);
               } else {
                  this._out.println(((StringBuffer)(new Object("Parse error: string table entry "))).append(ref).append(" referenced but invalid.").toString());
               }
            } else if (input == 130 || input == 129 || input == 128) {
               int ref = this._in.readMBInt();
               String st;
               if ((st = this.stringTableRef(ref)) != null) {
                  this._out.print("$(");
                  this._out.print(st);
                  if (input == 128) {
                     this._out.print(":escape");
                  } else if (input == 129) {
                     this._out.print(":unesc");
                  } else if (input == 130) {
                     this._out.print(":noesc");
                  }

                  this._out.println(')');
               } else {
                  this._out.println(((StringBuffer)(new Object("Parse error: string table entry "))).append(ref).append(" referenced but invalid.").toString());
               }
            } else if (input == 66 || input == 65 || input == 64) {
               this._out.print("$(");
               if (!this.parseString()) {
                  return false;
               }

               if (input == 64) {
                  this._out.print(":escape");
               } else if (input == 65) {
                  this._out.print(":unesc");
               } else if (input == 66) {
                  this._out.print(":noesc");
               }

               this._out.println(')');
            } else if (input == 2) {
               int c = this._in.readMBInt();
               this._out.print(((StringBuffer)(new Object("&#"))).append(String.valueOf(c)).append(';').toString());
            } else {
               int tag = input & -193;
               int content = input & 64;
               int attributes = input & 128;
               if (input == 1) {
                  return true;
               }

               if (WML_TAG_DESC(tag) == null) {
                  this._out
                     .println(
                        ((StringBuffer)(new Object("Parse error: expected tag, got 0x")))
                           .append(Integer.toHexString(input))
                           .append(" (0x")
                           .append(Integer.toHexString(tag))
                           .append(").")
                           .toString()
                     );
                  this.crashBurn();
                  return false;
               }

               this._out.println();

               for (int i = 0; i < this._depth; i++) {
                  this._out.print("  ");
               }

               this._out.print('<');
               this._out.print(WML_TAG_DESC(tag));
               if (attributes != 0 && !this.parseAttributes()) {
                  return false;
               }

               if (content != 0) {
                  this._out.print('>');
                  this._depth++;
                  if (!this.parseTags()) {
                     return false;
                  }

                  this._depth--;
                  if (tag != 28) {
                     this._out.println();

                     for (int i = 0; i < this._depth; i++) {
                        this._out.print("  ");
                     }
                  }

                  this._out.print("</");
                  this._out.print(WML_TAG_DESC(tag));
                  this._out.print('>');
               } else {
                  this._out.print("/>");
               }
            }
         }

         return true;
      } catch (Throwable var9) {
         this._out.println(e);
         return false;
      }
   }

   private final String stringTableRef(int idx) {
      if (idx >= this._stringTableLength) {
         return null;
      }

      for (int i = idx; i < this._stringTableLength; i++) {
         if (this._stringTable[i] == 0) {
            return (String)(new Object(this._stringTable, idx, i - idx, this._encoding));
         }
      }

      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean parseString() {
      try {
         ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object());

         int input;
         while ((input = this._in.read()) != 0) {
            out.write(input);
         }

         out.close();
         this._out.print((String)(new Object(out.toByteArray(), this._encoding)));
         return true;
      } catch (Throwable var4) {
         this._out.println(e);
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean parseAttributes() {
      try {
         int input;
         while ((input = this._in.read()) != 1) {
            if (input == 131) {
               int ref = this._in.readMBInt();
               String st;
               if ((st = this.stringTableRef(ref)) == null) {
                  this._out.println(((StringBuffer)(new Object("Parse error: string table entry "))).append(ref).append(" referenced but invalid.").toString());
                  this.crashBurn();
                  return false;
               }

               this._out.print(st);
            } else {
               String attr;
               if ((attr = WML_SATTR_DESC(input)) == null) {
                  this._out.println(((StringBuffer)(new Object("Parse error: expected attribute, got 0x"))).append(Integer.toHexString(input)).toString());
                  this.crashBurn();
                  return false;
               }

               if (attr.indexOf(34) == -1) {
                  this._out.print(((StringBuffer)(new Object())).append(' ').append(attr).append("=\"").toString());
                  if (!this.parseValueAttributes()) {
                     return false;
                  }

                  this._out.print('"');
               } else {
                  this._out.print(' ');
                  this._out.print(attr);
                  if (attr.charAt(attr.length() - 1) != '"') {
                     if (!this.parseValueAttributes()) {
                        return false;
                     }

                     this._out.print('"');
                  }
               }
            }
         }

         return true;
      } catch (Throwable var7) {
         this._out.println(e);
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean parseValueAttributes() {
      try {
         while (true) {
            int input = this._in.read();
            if (input == 131) {
               int ref = this._in.readMBInt();
               String st;
               if ((st = this.stringTableRef(ref)) == null) {
                  this._out.println(((StringBuffer)(new Object("Parse error: string table entry "))).append(ref).append(" referenced but invalid.").toString());
                  this.crashBurn();
                  return false;
               }

               this._out.print(st);
            } else if (input != 130 && input != 129 && input != 128) {
               if (input != 66 && input != 65 && input != 64) {
                  if (input == 3) {
                     if (!this.parseString()) {
                        return false;
                     }
                  } else {
                     String vattr;
                     if ((vattr = WML_VATTR_DESC(input)) == null) {
                        this._in.unread(input);
                        return true;
                     }

                     this._out.print(vattr);
                  }
               } else {
                  this._out.print("$(");
                  if (!this.parseString()) {
                     return false;
                  }

                  if (input == 64) {
                     this._out.print(":escape");
                  } else if (input == 65) {
                     this._out.print(":unesc");
                  } else if (input == 66) {
                     this._out.print(":noesc");
                  }

                  this._out.print(')');
               }
            } else {
               int ref = this._in.readMBInt();
               String st;
               if ((st = this.stringTableRef(ref)) == null) {
                  this._out.println(((StringBuffer)(new Object("Parse error: string table entry "))).append(ref).append(" referenced but invalid.").toString());
                  this.crashBurn();
                  return false;
               }

               this._out.print("$(");
               this._out.print(st);
               if (input == 128) {
                  this._out.print(":escape");
               } else if (input == 129) {
                  this._out.print(":unesc");
               } else if (input == 130) {
                  this._out.print(":noesc");
               }

               this._out.print(')');
            }
         }
      } catch (Throwable var7) {
         this._out.println(e);
         return false;
      }
   }

   private static final String WML_TAG_DESC(int a) {
      switch (a) {
         case 27:
         case 58:
            return null;
         case 28:
         default:
            return "a";
         case 29:
            return "td";
         case 30:
            return "tr";
         case 31:
            return "table";
         case 32:
            return "p";
         case 33:
            return "postfield";
         case 34:
            return "anchor";
         case 35:
            return "access";
         case 36:
            return "b";
         case 37:
            return "big";
         case 38:
            return "br";
         case 39:
            return "card";
         case 40:
            return "do";
         case 41:
            return "em";
         case 42:
            return "fieldset";
         case 43:
            return "go";
         case 44:
            return "head";
         case 45:
            return "i";
         case 46:
            return "img";
         case 47:
            return "input";
         case 48:
            return "meta";
         case 49:
            return "noop";
         case 50:
            return "prev";
         case 51:
            return "onevent";
         case 52:
            return "optgroup";
         case 53:
            return "option";
         case 54:
            return "refresh";
         case 55:
            return "select";
         case 56:
            return "small";
         case 57:
            return "strong";
         case 59:
            return "template";
         case 60:
            return "timer";
         case 61:
            return "u";
         case 62:
            return "setvar";
         case 63:
            return "wml";
      }
   }

   private static final String WML_SATTR_DESC(int a) {
      switch (a) {
         case 4:
         case 14:
         case 23:
         case 43:
         case 44:
         case 45:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 81:
            return null;
         case 5:
         default:
            return "accept-charset";
         case 6:
            return "align=\"bottom\"";
         case 7:
            return "align=\"center\"";
         case 8:
            return "align=\"left\"";
         case 9:
            return "align=\"middle\"";
         case 10:
            return "align=\"right\"";
         case 11:
            return "align=\"top\"";
         case 12:
            return "alt";
         case 13:
            return "content";
         case 15:
            return "domain";
         case 16:
            return "emptyok=\"false\"";
         case 17:
            return "emptyok=\"true\"";
         case 18:
            return "format";
         case 19:
            return "height";
         case 20:
            return "hspace";
         case 21:
            return "ivalue";
         case 22:
            return "iname";
         case 24:
            return "label";
         case 25:
            return "localsrc";
         case 26:
            return "maxlength";
         case 27:
            return "method=\"get\"";
         case 28:
            return "method=\"post\"";
         case 29:
            return "mode=\"nowrap\"";
         case 30:
            return "mode=\"wrap\"";
         case 31:
            return "multiple=\"false\"";
         case 32:
            return "multiple=\"true\"";
         case 33:
            return "name";
         case 34:
            return "newcontext=\"false\"";
         case 35:
            return "newcontext=\"true\"";
         case 36:
            return "onpick";
         case 37:
            return "onenterbackward";
         case 38:
            return "onenterforward";
         case 39:
            return "ontimer";
         case 40:
            return "optional=\"false\"";
         case 41:
            return "optional=\"true\"";
         case 42:
            return "path";
         case 46:
            return "scheme";
         case 47:
            return "sendreferer=\"false\"";
         case 48:
            return "sendreferer=\"true\"";
         case 49:
            return "size";
         case 50:
            return "src";
         case 51:
            return "ordered=\"true\"";
         case 52:
            return "ordered=\"false\"";
         case 53:
            return "tabindex";
         case 54:
            return "title";
         case 55:
            return "type";
         case 56:
            return "type=\"accept";
         case 57:
            return "type=\"delete";
         case 58:
            return "type=\"help";
         case 59:
            return "type=\"password\"";
         case 60:
            return "type=\"onpick\"";
         case 61:
            return "type=\"onenterbackward\"";
         case 62:
            return "type=\"onenterforward\"";
         case 63:
            return "type=\"ontimer\"";
         case 69:
            return "type=\"options";
         case 70:
            return "type=\"prev";
         case 71:
            return "type=\"reset";
         case 72:
            return "type=\"text\"";
         case 73:
            return "type=\"vnd.\"";
         case 74:
            return "href";
         case 75:
            return "href=\"http://";
         case 76:
            return "href=\"https://";
         case 77:
            return "value";
         case 78:
            return "vspace";
         case 79:
            return "width";
         case 80:
            return "xml:lang";
         case 82:
            return "align";
         case 83:
            return "columns";
         case 84:
            return "class";
         case 85:
            return "id";
         case 86:
            return "forua=\"false\"";
         case 87:
            return "forua=\"true\"";
         case 88:
            return "src=\"http://";
         case 89:
            return "src=\"https://";
         case 90:
            return "http-equiv";
         case 91:
            return "http-equiv=\"Content-Type\"";
         case 92:
            return "content=\"application/vnd.wap.wmlc;charset=";
         case 93:
            return "http-equiv=\"Expires\"";
         case 94:
            return "accesskey";
         case 95:
            return "enctype";
         case 96:
            return "enctype=\"application/x-www-form-urlencoded\"";
         case 97:
            return "enctype=\"multipart/form-data\"";
         case 98:
            return "xml:space=\"preserve\"";
         case 99:
            return "xml:space=\"default\"";
         case 100:
            return "cache-control=\"no-cache\"";
      }
   }

   private static final String WML_VATTR_DESC(int a) {
      switch (a) {
         case 132:
         case 146:
         case 156:
            return null;
         case 133:
         default:
            return ".com/";
         case 134:
            return ".edu/";
         case 135:
            return ".net/";
         case 136:
            return ".org/";
         case 137:
            return "accept";
         case 138:
            return "bottom";
         case 139:
            return "clear";
         case 140:
            return "delete";
         case 141:
            return "help";
         case 142:
            return "http://";
         case 143:
            return "http://www.";
         case 144:
            return "https://";
         case 145:
            return "https://www.";
         case 147:
            return "middle";
         case 148:
            return "nowrap";
         case 149:
            return "onpick";
         case 150:
            return "onenterbackward";
         case 151:
            return "onenterforward";
         case 152:
            return "ontimer";
         case 153:
            return "options";
         case 154:
            return "password";
         case 155:
            return "reset";
         case 157:
            return "text";
         case 158:
            return "top";
         case 159:
            return "unknown";
         case 160:
            return "wrap";
         case 161:
            return "www.";
      }
   }
}
