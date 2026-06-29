package net.rim.tid.im.conv;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.Arrays;
import net.rim.tid.awt.event.NavigationEvent;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.im.ISupplementaryInputData;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.itie.LinguisticData;
import net.rim.vm.Array;

public class IConversion {
   protected SLInputMethod inputMethod;
   protected SLComposedText composedText;
   protected String logicalName;
   protected TextFilter _filter;
   protected int _inputStyle;
   private char[] _filterVerifyBuffer;
   protected byte[] _properties;
   protected static final int[] control = new int[]{
      0,
      10,
      27,
      8,
      127,
      9,
      131,
      132,
      129,
      130,
      32,
      133,
      134,
      137,
      128,
      -805043114,
      1090521089,
      134241024,
      452987392,
      805314560,
      -1677689088,
      1281,
      1107304704,
      -2080349440,
      -738151680,
      402781952,
      1778465025,
      -1409185023,
      -301871871,
      805441282,
      1912754434,
      -1274899710,
      -33366782,
      1241721091,
      -1778158333,
      -1509712381,
      -1241272829,
      -805060861,
      62467,
      3146753,
      1049601,
      1049601,
      2752513,
      70255618,
      65552,
      65629,
      42,
      822345984,
      285475072,
      285475072,
      553648384,
      822346240,
      16781572,
      16785952,
      8448,
      67174400,
      67174470,
      67174438,
      65574,
      67239993
   };

   public void setInputMethod(SLInputMethod inputMethod) {
      this.inputMethod = inputMethod;
      this.composedText = inputMethod.getComposedText();
   }

   public int setTextInputStyle(int style) {
      this._inputStyle = style;
      return -1;
   }

   public void setLogicalName(String logicalName) {
      this.logicalName = logicalName;
   }

   public String getLogicalName() {
      return this.logicalName;
   }

   public void inputMethodKeyLayoutChanged(SLKeyLayout layout) {
   }

   protected boolean applyControlInUnconverted(ConversionEvent event) {
      SLVariants current = this.composedText.getCurrentVariant(true);
      switch (event.getKeyCode()) {
         case 8:
            current.deleteChar(false, -1);
            break;
         case 10:
            this.composedText.commitAll();
            break;
         case 27:
            current.setOriginal("");
            current.setCaretPosition(0);
            break;
         case 32:
            return this.applySpace(event);
         case 127:
            current.deleteChar(true, -1);
            break;
         case 129:
            current.setCaretPosition(0);
            break;
         case 130:
            current.setCaretPosition(current.getOriginal().length());
            break;
         case 131:
            current.moveCaret(false);
            break;
         case 132:
            current.moveCaret(true);
            break;
         default:
            return false;
      }

      event.consume();
      event.setID(1);
      return true;
   }

   protected boolean applySpace(ConversionEvent event) {
      return false;
   }

   protected boolean isPunctuation(char ch) {
      switch (ch) {
         case '!':
         case ',':
         case '.':
         case '?':
         case '、':
         case '。':
            return true;
         default:
            return false;
      }
   }

   protected boolean isAutotextON() {
      return (this._inputStyle & 65536) != 0;
   }

   public boolean setLocale(Locale _1) {
      throw null;
   }

   public void dispatchConversionEvent(ConversionEvent _1) {
      throw null;
   }

   public void dispatchNavigationEvent(NavigationEvent event) {
   }

   public void endComposition() {
      throw null;
   }

   public int loadLinguisticData(LinguisticData data) {
      return 1;
   }

   public int unloadLinguisticData(int id) {
      return 4;
   }

   public void composedTextChanged() {
   }

   public void setIMProperties(byte propID, byte[] IMProperties) {
      switch (propID) {
         case 3:
            this._properties = IMProperties;
      }
   }

   public byte[] getIMProperties(byte propID) {
      switch (propID) {
         case 3:
            return this._properties;
         default:
            return null;
      }
   }

   public int actionPerformed(int actionCommand, Object param) {
      return 4;
   }

   public boolean isControl(int code) {
      return Arrays.getIndex(control, code) != -1;
   }

   public String[] getShortcuts() {
      return null;
   }

   public boolean setFilter(TextFilter filter) {
      this._filter = filter;
      if (filter != null) {
         this._inputStyle = (int)(this._inputStyle | filter.getPreferredInputStyle());
      }

      return true;
   }

   protected boolean verify(StringBuffer chars, boolean convert, int modifier, int position) {
      if (this._filter == null) {
         return true;
      }

      int charsCount = chars.length();
      if (this._filterVerifyBuffer == null) {
         this._filterVerifyBuffer = new char[charsCount];
      }

      if (charsCount > this._filterVerifyBuffer.length) {
         Array.resize(this._filterVerifyBuffer, charsCount);
      }

      chars.getChars(0, charsCount, this._filterVerifyBuffer, 0);
      int count = 0;

      for (int i = 0; i < charsCount; i++) {
         char charToVerify = this._filterVerifyBuffer[i];
         if (convert) {
            charToVerify = this._filter.convert(charToVerify, SLKeyLayout.convertModifiersToStatus(modifier));
            if (this.getIndex(this._filterVerifyBuffer, charToVerify, 0, count) != -1) {
               continue;
            }
         }

         if (this._filter.validate(charToVerify, null, position)) {
            this._filterVerifyBuffer[count++] = charToVerify;
         }
      }

      chars.setLength(0);
      if (count != 0) {
         chars.append(this._filterVerifyBuffer, 0, count);
         return true;
      } else {
         chars.append('\u0000');
         return false;
      }
   }

   private int getIndex(char[] array, char element, int start, int end) {
      for (int i = start; i < end; i++) {
         if (array[i] == element) {
            return i;
         }
      }

      return -1;
   }

   public boolean isConsumeNEXTKey() {
      return true;
   }

   public void reset() {
   }

   public int removeShortcut(String replacedString, String replacementString) {
      return 4;
   }

   public int addShortcut(String replacedString, String replacementStringPattern) {
      return 4;
   }

   public CustomWordsRepository getRepository(int type) {
      return null;
   }

   public CustomDictionary getCustomDictionary(int type) {
      return null;
   }

   public ISupplementaryInputData getSuplementaryInputData() {
      return null;
   }

   public void protectContent(int flag) {
   }
}
