package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;

class GlobalProperties$6 extends HostFunction {
   GlobalProperties$6(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      String str = Convert.toString(this.getParm(0));
      int len = str.length();
      StringBuffer b = (StringBuffer)(new Object());

      for (int i = 0; i < len; i++) {
         char c = str.charAt(i);
         switch (c) {
            case ')':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
               b.append('%');
               if (c >= 256) {
                  b.append('u');
                  b.append(Tokenizer.hexDigit(c >> '\f' & 15));
                  b.append(Tokenizer.hexDigit(c >> '\b' & 15));
               }

               b.append(Tokenizer.hexDigit(c >> 4 & 15));
               b.append(Tokenizer.hexDigit(c & 15));
               break;
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
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
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            default:
               b.append(c);
         }
      }

      return Value.makeStringValue(b.toString());
   }
}
