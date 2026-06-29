package net.rim.device.apps.internal.browser.css;

import java.util.Vector;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.markup.HTMLBinaryConstants;
import net.rim.device.apps.internal.browser.markup.HTMLUtilities;
import net.rim.vm.Array;

public final class CSSTextParser implements CSSParser {
   private CSSTokenizer _tokenizer;
   private DocumentHandler _handler;
   private String _url;
   private int _token;
   private int _pseudoElement;
   private int[] _stringStartIndicies;
   private int[] _stringEndIndicies;
   private int _stringCount;
   private String[] _urls;
   private int _urlCount;
   private Vector _freeList = (Vector)(new Object());
   private int[][] _currentSelector = new int[5][];
   private static final String[] DEFAULT_MEDIA_LIST = new Object[0];

   protected final void parseAtRule() {
      int braces = 0;

      while (true) {
         switch (this.nextTokenIgnoreWhitespace()) {
            case 0:
               return;
            case 39:
               if (braces == 0) {
                  return;
               }
               break;
            case 40:
               braces++;
               break;
            case 41:
               if (--braces == 0) {
                  return;
               }
         }
      }
   }

   @Override
   public final void parseStyleSheet(byte[] source) {
      this.parseStyleSheet((String)(new Object(source)));
   }

   @Override
   public final void parseStyleDeclaration(String source) {
      if (this._handler != null) {
         this._tokenizer = new CSSTokenizer(source);
         if (this.nextTokenIgnoreWhitespace() == 40) {
            this.nextTokenIgnoreWhitespace();
         }

         this.parseStyleDeclaration();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void parseStyleSheet(String source) {
      if (this._handler != null) {
         this._tokenizer = new CSSTokenizer(source);
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            this._handler.startDocument(this._url);
            this.nextTokenIgnoreWhitespaceCDOCDC();
            if (this._token == 14) {
               try {
                  this.parseCharsetRule();
               } catch (CSSParseException e) {
                  this.recover();
               }

               this.nextTokenIgnoreWhitespaceCDOCDC();
            }

            for (; this._token == 10; this.nextTokenIgnoreWhitespaceCDOCDC()) {
               try {
                  this.parseImportRule();
               } catch (CSSParseException e) {
                  this.recover();
               }
            }

            label71:
            while (true) {
               try {
                  switch (this._token) {
                     case 0:
                        var8 = false;
                        break label71;
                     case 11:
                     case 15:
                        this.parseAtRule();
                        break;
                     case 12:
                        this.parseMediaRule();
                        break;
                     default:
                        this.parseRuleSet();
                  }
               } catch (CSSParseException e) {
                  this.recover();
               }

               this.nextTokenIgnoreWhitespaceCDOCDC();
            }
         } finally {
            if (var8) {
               this._handler.endDocument(this._url);
            }
         }

         this._handler.endDocument(this._url);
      }
   }

   @Override
   public final String getSource() {
      return this._tokenizer.getSource();
   }

   @Override
   public final int getStringStartIndex(int stringIndex) {
      return this._stringStartIndicies[stringIndex];
   }

   @Override
   public final int getStringEndIndex(int stringIndex) {
      return this._stringEndIndicies[stringIndex];
   }

   @Override
   public final CSSString getString(int stringIndex) {
      return stringIndex < this._stringCount
         ? new CSSString(this._tokenizer.getSource(), this._stringStartIndicies[stringIndex], this._stringEndIndicies[stringIndex])
         : null;
   }

   @Override
   public final String getURL(int urlIndex) {
      return urlIndex < this._urlCount ? this._urls[urlIndex] : null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseRuleSet() throws CSSParseException {
      int selectorSize = this.parseSelectorList();
      if (this._token != 40) {
         throw new CSSParseException("rule-set");
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this._handler.startSelector(this._currentSelector, selectorSize);
         this.nextTokenIgnoreWhitespace();
         this.parseStyleDeclaration();
         var6 = false;
      } finally {
         if (var6) {
            this._handler.endSelector(this._currentSelector, selectorSize);

            for (int i = 0; i < selectorSize; i++) {
               this.deallocateArray(this._currentSelector[i]);
            }
         }
      }

      this._handler.endSelector(this._currentSelector, selectorSize);

      for (int i = 0; i < selectorSize; i++) {
         this.deallocateArray(this._currentSelector[i]);
      }
   }

   private final int parseSelectorList() {
      int offset = 0;

      for (this._currentSelector[offset++] = this.parseSelector(); this._token == 46; this._currentSelector[offset++] = this.parseSelector()) {
         this.nextTokenIgnoreWhitespace();
         int length = this._currentSelector.length;
         if (offset >= length) {
            Array.resize(this._currentSelector, length + 1);
         }
      }

      return offset;
   }

   private final int[] parseSelector() {
      this._pseudoElement = -1;
      int[] selector = this.parseSimpleSelector();

      while (true) {
         switch (this._token) {
            case 8:
            case 9:
            case 44:
            case 47:
            case 52:
            case 53:
               selector = this.createDescendantSelector(selector, this.parseSimpleSelector());
               break;
            case 49:
               this.nextTokenIgnoreWhitespace();
               selector = this.createDirectAdjacentSelector(selector, this.parseSimpleSelector());
               break;
            case 50:
               this.nextTokenIgnoreWhitespace();
               selector = this.createChildSelector(selector, this.parseSimpleSelector());
               break;
            default:
               if (this._pseudoElement != -1) {
                  selector = this.createChildSelector(selector, this.createPseudoElementSelector(this._pseudoElement));
               }

               return selector;
         }
      }
   }

   private final int[] parseSimpleSelector() throws CSSParseException {
      int[] simpleSelector = null;
      boolean validSelector;
      switch (this._token) {
         case 8:
            String source = this._tokenizer.getSource();
            int idStartIndex = this._tokenizer.getStartIndex();
            int idEndIndex = this._tokenizer.getEndIndex();
            int id = HTMLUtilities.resolveTag(source, idStartIndex, idEndIndex);
            if (id != -1) {
               simpleSelector = this.createElementSelector(id, true);
            } else {
               simpleSelector = this.createElementSelector(this.addString(), false);
            }

            this.nextToken();
            validSelector = true;
            break;
         case 53:
            simpleSelector = this.createElementSelector(0, true);
            this.nextToken();
            validSelector = true;
            break;
         default:
            simpleSelector = this.createElementSelector(0, true);
            validSelector = false;
      }

      int[] condition = null;
      boolean done = false;

      while (!done) {
         int[] c = null;
         switch (this._token) {
            case 1:
               this.nextTokenIgnoreWhitespace();
               c = null;
               done = true;
               break;
            case 9:
               c = this.createIdCondition(this.addString());
               this.nextToken();
               validSelector = true;
               break;
            case 44:
               c = this.parseAttribute();
               validSelector = true;
               break;
            case 47:
               c = this.parsePseudoClassOrElement();
               validSelector = true;
               break;
            case 52:
               if (this.nextToken() != 8) {
                  throw new CSSParseException("simple-selector");
               }

               c = this.createClassCondition(this.addString());
               this.nextToken();
               validSelector = true;
               break;
            default:
               c = null;
               done = true;
         }

         if (c != null) {
            if (condition == null) {
               condition = c;
            } else {
               condition = this.createAndCondition(condition, c);
            }
         }
      }

      if (!validSelector) {
         throw new CSSParseException("simple-selector");
      }

      if (condition != null) {
         simpleSelector = this.createConditionalSelector(simpleSelector, condition);
      }

      return simpleSelector;
   }

   private final int[] parseAttribute() throws CSSParseException {
      if (this.nextTokenIgnoreWhitespace() != 8) {
         throw new CSSParseException("attribute");
      }

      int[] attribute = null;
      int name = this.addString();
      int token = this.nextTokenIgnoreWhitespace();
      switch (token) {
         case 5:
         case 6:
         case 54:
            int value = -1;
            switch (this.nextTokenIgnoreWhitespace()) {
               case 6:
                  throw new CSSParseException("attribute");
               case 7:
               case 8:
               default:
                  value = this.addString();
                  if (this.nextTokenIgnoreWhitespace() != 45) {
                     throw new CSSParseException("attribute");
                  } else {
                     this.nextTokenIgnoreWhitespace();
                     switch (token) {
                        case 5:
                           return this.createOneOfAttributeCondition(name, value);
                        case 6:
                           return this.createBeginHyphenAttributeCondition(name, value);
                        case 54:
                           return this.createAttributeCondition(name, value);
                        default:
                           return attribute;
                     }
                  }
            }
         case 45:
            attribute = this.createAttributeCondition(name, -1);
            this.nextTokenIgnoreWhitespace();
            return attribute;
         default:
            throw new CSSParseException("attribute");
      }
   }

   private final int[] parsePseudoClassOrElement() throws CSSParseException {
      int[] pseudo = null;
      switch (this.nextTokenIgnoreWhitespace()) {
         case 8:
            int value = this.addString();
            if (this.isPseudoElement(value)) {
               if (this._pseudoElement != -1) {
                  throw new CSSParseException("pseudo-element");
               }

               this._pseudoElement = value;
            } else {
               pseudo = this.createPseudoClassCondition(value);
            }

            this.nextTokenIgnoreWhitespace();
            return pseudo;
         case 37:
            String source = this._tokenizer.getSource();
            int functionStartIndex = this._tokenizer.getStartIndex();
            int functionEndIndex = this._tokenizer.getEndIndex();
            int functionLength = functionEndIndex - functionStartIndex;
            if (this.nextTokenIgnoreWhitespace() != 8) {
               throw new CSSParseException("pseudo-class");
            } else {
               int language = this.addString();
               if (this.nextTokenIgnoreWhitespace() != 43) {
                  throw new CSSParseException("pseudo-class");
               } else {
                  if (functionLength == 4 && StringUtilities.regionMatches(source, true, functionStartIndex, "lang", 0, functionLength, 1701707776)) {
                     pseudo = this.createLangCondition(language);
                     this.nextTokenIgnoreWhitespace();
                     return pseudo;
                  }

                  throw new CSSParseException("pseudo-class");
               }
            }
         default:
            throw new CSSParseException("pseudo-class-element");
      }
   }

   private final boolean isPseudoElement(int value) {
      int startIndex = this.getStringStartIndex(value);
      int endIndex = this.getStringEndIndex(value);
      int length = endIndex - startIndex;
      String source = this._tokenizer.getSource();
      switch (source.charAt(startIndex)) {
         case 'A':
         case 'a':
            if (length == 5 && StringUtilities.regionMatches(source, true, startIndex, "after", 0, length, 1701707776)) {
               return true;
            }

            return false;
         case 'B':
         case 'b':
            if (length == 6 && StringUtilities.regionMatches(source, true, startIndex, "before", 0, length, 1701707776)) {
               return true;
            }

            return false;
         case 'F':
         case 'f':
            return length == 12 && StringUtilities.regionMatches(source, true, startIndex, "first-letter", 0, length, 1701707776)
               || length == 10 && StringUtilities.regionMatches(source, true, startIndex, "first-line", 0, length, 1701707776);
         default:
            return false;
      }
   }

   private final void parseStyleDeclaration() {
      boolean important = false;

      while (true) {
         try {
            switch (this._token) {
               case 0:
               case 41:
                  return;
               case 8:
                  int property = CSSUtilities.resolveStyleProperty(this._tokenizer.getSource(), this._tokenizer.getStartIndex(), this._tokenizer.getEndIndex());
                  boolean supported = CSSUtilities.isStylePropertySupported(property);
                  if (this.nextTokenIgnoreWhitespace() != 47) {
                     throw new CSSParseException("style-declaration");
                  }

                  this.nextTokenIgnoreWhitespace();
                  int[] expression = this.parseExpression(false, !supported);
                  if (this._token == 16) {
                     important = true;
                     this.nextTokenIgnoreWhitespace();
                  }

                  if (expression != null) {
                     if (supported && expression[expression.length - 1] > 0) {
                        this._handler.property(property, expression, important);
                     }

                     this.deallocateArray(expression);
                  }
                  break;
               case 39:
                  this.nextTokenIgnoreWhitespace();
                  break;
               default:
                  throw new CSSParseException("style-declaration");
            }
         } catch (CSSParseException e) {
            this.recover();
         }
      }
   }

   private final int[] parseExpression(boolean parameters, boolean ignore) throws CSSParseException {
      int[] expression = this.parseTerm(ignore);

      while (true) {
         boolean operator = false;
         switch (this._token) {
            case 46:
            case 48:
               operator = true;
               if (!ignore) {
                  expression = this.appendAdditional(expression, this._token);
               }

               this.nextTokenIgnoreWhitespace();
         }

         if (parameters) {
            if (this._token == 43) {
               if (operator) {
                  throw new CSSParseException("expression");
               }

               if (!ignore) {
                  expression = this.appendAdditional(expression, this._token);
               }

               this.nextTokenIgnoreWhitespace();
               return expression;
            }
         } else {
            switch (this._token) {
               case 0:
               case 16:
               case 39:
               case 41:
                  if (operator) {
                     throw new CSSParseException("expression");
                  }

                  return expression;
            }
         }

         int[] term = this.parseTerm(ignore);
         if (!ignore) {
            expression = this.appendAdditional(expression, term);
         }
      }
   }

   private final int[] parseTerm(boolean ignore) throws CSSParseException {
      boolean sign = false;
      boolean positive = true;
      switch (this._token) {
         case 49:
            sign = true;
            positive = true;
            this.nextToken();
            break;
         case 51:
            sign = true;
            positive = false;
            this.nextToken();
      }

      switch (this._token) {
         case 16:
         case 36:
            if (sign) {
               throw new CSSParseException("term");
            } else {
               switch (this._token) {
                  case 7:
                     if (!ignore) {
                        return this.parseString();
                     }

                     this.nextTokenIgnoreWhitespace();
                     return null;
                  case 8:
                     if (!ignore) {
                        return this.parseIdentifier();
                     }

                     this.nextTokenIgnoreWhitespace();
                     return null;
                  case 9:
                     if (!ignore) {
                        return this.parseHexColor();
                     }

                     this.nextTokenIgnoreWhitespace();
                     return null;
                  case 36:
                     if (!ignore) {
                        return this.parseURI();
                     }

                     this.nextTokenIgnoreWhitespace();
                     return null;
                  default:
                     throw new CSSParseException("term");
               }
            }
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         default:
            if (!ignore) {
               return this.parseNumber(positive, this._token);
            }

            this.nextTokenIgnoreWhitespace();
            return null;
         case 37:
            if (!ignore) {
               return this.parseFunction(positive);
            } else {
               this.nextTokenIgnoreWhitespace();
               this.parseExpression(true, true);
               return null;
            }
      }
   }

   private final int[] parseNumber(boolean positive, int units) {
      int[] number = this.allocateArray(2);
      int decimalIndex = this._tokenizer.getDecimalIndex();
      if (decimalIndex != -1) {
         number[0] = 7;
      } else {
         number[0] = 6;
      }

      int numberValue = this._tokenizer.getIntegerValue(decimalIndex, decimalIndex != -1);
      if (!positive) {
         numberValue = -numberValue;
      }

      switch (units) {
         case 16:
         case 32:
            number[1] = numberValue;
            break;
         case 17:
            number[0] |= 32;
            number[1] = numberValue;
            break;
         case 18:
            number[0] |= 48;
            number[1] = numberValue;
            break;
         case 19:
            number[0] |= 64;
            number[1] = numberValue;
            break;
         case 20:
            number[0] |= 64;
            number[1] = Ui.convertSize(numberValue, 4194308, 0);
            break;
         case 21:
            number[0] |= 64;
            number[1] = Ui.convertSize(numberValue, 2097156, 0);
            break;
         case 22:
            number[0] |= 64;
            number[1] = Ui.convertSize(numberValue * 5, 4194308, 0) / 2;
            break;
         case 23:
            number[0] |= 64;
            number[1] = Ui.convertSize(numberValue, 2, 0);
            break;
         case 24:
            number[0] |= 64;
            number[1] = Ui.convertSize(numberValue * 12, 2, 0);
            break;
         case 25:
            number[0] |= 160;
            number[1] = numberValue;
            break;
         case 26:
            number[0] |= 192;
            number[1] = numberValue;
            break;
         case 27:
            number[0] |= 176;
            number[1] = numberValue;
            break;
         case 28:
            number[0] |= 208;
            number[1] = numberValue;
            break;
         case 29:
            number[0] |= 224;
            number[1] = numberValue;
            break;
         case 30:
            number[0] |= 240;
            number[1] = numberValue;
            break;
         case 31:
            number[0] |= 256;
            number[1] = numberValue;
            break;
         case 33:
            number[0] |= 16;
            number[1] = numberValue;
            break;
         case 34:
         case 35:
         default:
            number[1] = numberValue;
      }

      this.nextTokenIgnoreWhitespace();
      return number;
   }

   private final int[] parseFunction(boolean positive) {
      int function = this.addString();
      this.nextTokenIgnoreWhitespace();
      int[] functionArray = this.allocateArray(2);
      functionArray[0] = 4;
      functionArray[1] = function;
      return this.appendAdditional(functionArray, this.parseExpression(true, false));
   }

   private final int[] parseString() {
      int identifier = this.addString();
      this.nextTokenIgnoreWhitespace();
      int[] result = this.allocateArray(2);
      result[0] = 2;
      result[1] = identifier;
      return result;
   }

   private final int[] parseIdentifier() {
      String source = this._tokenizer.getSource();
      int startIndex = this._tokenizer.getStartIndex();
      int endIndex = this._tokenizer.getEndIndex();
      int identifier = CSSUtilities.resolveStyleValue(source, startIndex, endIndex);
      if (identifier != -1) {
         this.nextTokenIgnoreWhitespace();
         int[] result = this.allocateArray(2);
         result[0] = 1;
         result[1] = identifier;
         return result;
      }

      label28:
      try {
         identifier = HTMLBinaryConstants.resolveColor(source, startIndex, endIndex);
      } finally {
         break label28;
      }

      if (identifier != -1) {
         this.nextTokenIgnoreWhitespace();
         int[] result = this.allocateArray(2);
         result[0] = 5;
         result[1] = identifier;
         return result;
      } else {
         return this.parseString();
      }
   }

   private final int[] parseURI() {
      String url = this._tokenizer.getStringValue();
      if (this._url != null) {
         url = URI.getAbsoluteURL(url, this._url);
      }

      int uri = this.addURL(url);
      this.nextTokenIgnoreWhitespace();
      int[] result = this.allocateArray(2);
      result[0] = 3;
      result[1] = uri;
      return result;
   }

   private final int[] parseHexColor() throws CSSParseException {
      String source = this._tokenizer.getSource();
      int colorStartIndex = this._tokenizer.getStartIndex();
      int colorEndIndex = this._tokenizer.getEndIndex();
      int length = colorEndIndex - colorStartIndex;
      if (length != 3 && length != 6) {
         throw new CSSParseException("hex-color");
      }

      int color = 0;
      int i = colorStartIndex;

      for (int shift = 20; i < colorEndIndex; shift -= 4) {
         char component = source.charAt(i);
         if (component >= '0' && component <= '9') {
            component = (char)(component - '0');
         } else if (component >= 'a' && component <= 'f') {
            component = (char)(component - 'W');
         } else {
            if (component < 'A' || component > 'F') {
               throw new CSSParseException("hex-color");
            }

            component = (char)(component - '7');
         }

         color |= component << shift;
         if (length == 3) {
            shift -= 4;
            color |= component << shift;
         }

         i++;
      }

      this.nextTokenIgnoreWhitespace();
      int[] result = this.allocateArray(2);
      result[0] = 5;
      result[1] = color;
      return result;
   }

   private final int nextToken() {
      try {
         while (true) {
            this._token = this._tokenizer.nextToken();
            switch (this._token) {
               case 2:
                  break;
               default:
                  return this._token;
            }
         }
      } catch (CSSParseException e) {
         this.recover();
         return this._token;
      }
   }

   private final int nextTokenIgnoreWhitespace() {
      try {
         while (true) {
            this._token = this._tokenizer.nextToken();
            switch (this._token) {
               case 0:
               default:
                  return this._token;
               case 1:
               case 2:
            }
         }
      } catch (CSSParseException e) {
         this.recover();
         return this._token;
      }
   }

   private final int nextTokenIgnoreWhitespaceCDOCDC() {
      try {
         while (true) {
            this._token = this._tokenizer.nextToken();
            switch (this._token) {
               case 0:
               default:
                  return this._token;
               case 1:
               case 2:
               case 3:
               case 4:
            }
         }
      } catch (CSSParseException e) {
         this.recover();
         return this._token;
      }
   }

   private final void recover() {
      int braces = 0;

      while (true) {
         switch (this._token) {
            case 0:
               return;
            case 39:
               if (braces == 0) {
                  return;
               }
               break;
            case 40:
               braces++;
               break;
            case 41:
               if (--braces <= 0) {
                  return;
               }
         }

         while (true) {
            try {
               this._token = this._tokenizer.nextToken();
               if (this._token != 1 && this._token != 2) {
                  break;
               }
            } catch (CSSParseException var3) {
            }
         }
      }
   }

   private final void parseCharsetRule() throws CSSParseException {
      if (this.nextTokenIgnoreWhitespace() != 7 || this.nextTokenIgnoreWhitespace() != 39) {
         throw new CSSParseException("charset-rule");
      }
   }

   private final void parseImportRule() throws CSSParseException {
      this.nextTokenIgnoreWhitespace();
      if (this._token == 7 || this._token == 36) {
         String uri = this._tokenizer.getStringValue();
         if (this._url != null) {
            uri = URI.getAbsoluteURL(uri, this._url);
         }

         String[] mediaList = DEFAULT_MEDIA_LIST;
         if (this.nextTokenIgnoreWhitespace() == 8) {
            mediaList = this.parseMediaList();
         }

         this._handler.importStyle(uri, mediaList);
         if (this._token == 39) {
            return;
         }
      }

      throw new CSSParseException("import-rule");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseMediaRule() throws CSSParseException {
      String[] mediaList;
      label98: {
         label97: {
            label104: {
               if (this.nextTokenIgnoreWhitespace() == 8) {
                  mediaList = this.parseMediaList();
                  boolean var6 = false /* VF: Semaphore variable */;

                  try {
                     var6 = true;
                     boolean processMediaRule = this._handler.startMedia(mediaList);
                     if (this._token == 40) {
                        if (processMediaRule) {
                           while (true) {
                              switch (this.nextTokenIgnoreWhitespace()) {
                                 case 0:
                                 case 41:
                                    var6 = false;
                                    break label98;
                                 default:
                                    this.parseRuleSet();
                              }
                           }
                        }

                        int braces = 1;

                        while (true) {
                           switch (this.nextTokenIgnoreWhitespace()) {
                              case 0:
                                 var6 = false;
                                 break label97;
                              case 40:
                                 braces++;
                                 break;
                              case 41:
                                 if (--braces == 0) {
                                    var6 = false;
                                    break label104;
                                 }
                           }
                        }
                     }

                     var6 = false;
                  } finally {
                     if (var6) {
                        this._handler.endMedia(mediaList);
                     }
                  }

                  this._handler.endMedia(mediaList);
               }

               throw new CSSParseException("media-rule");
            }

            this._handler.endMedia(mediaList);
            return;
         }

         this._handler.endMedia(mediaList);
         return;
      }

      this._handler.endMedia(mediaList);
   }

   public CSSTextParser(DocumentHandler handler, String url) {
      this._handler = handler;
      this._url = url;
      this._stringStartIndicies = new int[0];
      this._stringEndIndicies = new int[0];
      this._urls = new Object[0];
   }

   private final int addString() {
      int currentLength = this._stringStartIndicies.length;
      if (this._stringCount >= currentLength) {
         int newLength = currentLength + Array.getSectionSize(this._stringStartIndicies);
         Array.resize(this._stringStartIndicies, newLength);
         Array.resize(this._stringEndIndicies, newLength);
      }

      this._stringStartIndicies[this._stringCount] = this._tokenizer.getStartIndex();
      this._stringEndIndicies[this._stringCount] = this._tokenizer.getEndIndex();
      return this._stringCount++;
   }

   private final String[] parseMediaList() throws CSSParseException {
      String[] mediaList = new Object[1];
      mediaList[0] = this._tokenizer.getStringValue();

      while (this.nextTokenIgnoreWhitespace() == 46) {
         if (this.nextTokenIgnoreWhitespace() != 8) {
            throw new CSSParseException("media-list");
         }

         int length = mediaList.length;
         Array.resize(mediaList, length + 1);
         mediaList[length] = this._tokenizer.getStringValue();
      }

      return mediaList;
   }

   private final int addURL(String url) {
      int currentLength = this._urls.length;
      if (this._urlCount >= currentLength) {
         int newLength = currentLength + Array.getSectionSize(this._urls);
         Array.resize(this._urls, newLength);
      }

      this._urls[this._urlCount] = url;
      return this._urlCount++;
   }

   private final int[] appendAdditional(int[] original, int[] additional) {
      int originalLength = original[original.length - 1];
      int additionalLength = additional[additional.length - 1];
      int[] result;
      if (additionalLength + originalLength + 1 < original.length) {
         System.arraycopy(additional, 0, original, originalLength, additionalLength);
         original[original.length - 1] = originalLength + additionalLength;
         result = original;
      } else {
         result = this.allocateArray(originalLength + additionalLength);
         System.arraycopy(original, 0, result, 0, originalLength);
         System.arraycopy(additional, 0, result, originalLength, additionalLength);
         this.deallocateArray(original);
      }

      this.deallocateArray(additional);
      return result;
   }

   private final int[] appendAdditional(int[] original, int additional) {
      int originalLength = original[original.length - 1];
      if (originalLength + 2 < original.length) {
         original[originalLength] = additional;
         original[original.length - 1] = originalLength + 1;
         return original;
      } else {
         int[] result = this.allocateArray(originalLength + 1);
         result[originalLength] = additional;
         System.arraycopy(original, 0, result, 0, originalLength);
         this.deallocateArray(original);
         return result;
      }
   }

   private final int[] createAndCondition(int[] first, int[] second) {
      return this.appendItemAtElement0(0, first, second);
   }

   private final int[] createAttributeCondition(int name, int value) {
      int[] condition = this.allocateArray(3);
      condition[0] = 4;
      condition[1] = name;
      condition[2] = value;
      return condition;
   }

   private final int[] createIdCondition(int value) {
      int[] condition = this.allocateArray(2);
      condition[0] = 5;
      condition[1] = value;
      return condition;
   }

   private final int[] createLangCondition(int lang) {
      int[] condition = this.allocateArray(2);
      condition[0] = 6;
      condition[1] = lang;
      return condition;
   }

   private final int[] createOneOfAttributeCondition(int name, int value) {
      int[] condition = this.allocateArray(3);
      condition[0] = 7;
      condition[1] = name;
      condition[2] = value;
      return condition;
   }

   private final int[] createBeginHyphenAttributeCondition(int name, int value) {
      int[] condition = this.allocateArray(3);
      condition[0] = 8;
      condition[1] = name;
      condition[2] = value;
      return condition;
   }

   private final int[] createClassCondition(int value) {
      int[] condition = this.allocateArray(2);
      condition[0] = 9;
      condition[1] = value;
      return condition;
   }

   private final int[] createPseudoClassCondition(int value) {
      int[] condition = this.allocateArray(2);
      condition[0] = 10;
      condition[1] = value;
      return condition;
   }

   private final int[] createConditionalSelector(int[] selector, int[] condition) {
      return this.appendItemAtElement0(0, selector, condition);
   }

   private final int[] createElementSelector(int tagName, boolean defined) {
      int[] selector = this.allocateArray(3);
      selector[0] = 4;
      if (defined) {
         selector[1] = 1;
      } else {
         selector[1] = 2;
      }

      selector[2] = tagName;
      return selector;
   }

   private final int[] createPseudoElementSelector(int pseudoName) {
      int[] selector = this.allocateArray(2);
      selector[0] = 9;
      selector[1] = pseudoName;
      return selector;
   }

   private final int[] createDescendantSelector(int[] parent, int[] descendant) {
      return this.appendItemAtElement0(10, parent, descendant);
   }

   private final int[] appendItemAtElement0(int element, int[] first, int[] second) {
      int firstLength = first[first.length - 1];
      int secondLength = second[second.length - 1];
      int[] result;
      if (firstLength + secondLength + 2 < first.length) {
         System.arraycopy(first, 0, first, 1, firstLength);
         System.arraycopy(second, 0, first, firstLength + 1, secondLength);
         first[0] = element;
         first[first.length - 1] = firstLength + secondLength + 1;
         result = first;
      } else {
         result = this.allocateArray(firstLength + secondLength + 1);
         result[0] = element;
         System.arraycopy(first, 0, result, 1, firstLength);
         System.arraycopy(second, 0, result, 1 + firstLength, secondLength);
         this.deallocateArray(first);
      }

      this.deallocateArray(second);
      return result;
   }

   private final int[] createChildSelector(int[] parent, int[] child) {
      return this.appendItemAtElement0(11, parent, child);
   }

   private final int[] createDirectAdjacentSelector(int[] child, int[] directAdjacent) {
      return this.appendItemAtElement0(12, child, directAdjacent);
   }

   private final int[] allocateArray(int size) {
      int freeSize = this._freeList.size();
      if (size < 10 && freeSize >= 1) {
         int[] result = (int[])this._freeList.elementAt(freeSize - 1);
         this._freeList.removeElementAt(freeSize - 1);
         result[result.length - 1] = size;
         return result;
      } else {
         int[] result = new int[Math.max(size + 1, 11)];
         result[result.length - 1] = size;
         return result;
      }
   }

   private final void deallocateArray(int[] array) {
      if (array != null) {
         this._freeList.addElement(array);
      }
   }
}
