package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.im.conv.repository.IDataSearchRepository;
import net.rim.tid.im.layout.SLKeyLayout;

public class SLControlObject {
   private MinimalInputMethod _inputMethod;

   protected SLControlObject() {
   }

   public SLControlObject(MinimalInputMethod inputMethod) {
      this._inputMethod = inputMethod;
   }

   public Object getViewComponent(int component) {
      return null;
   }

   public int actionPerformed(int action, Object param) {
      return this._inputMethod.actionPerformed(null, action, param);
   }

   public SLKeyLayout getKeyLayout() {
      return this._inputMethod.getKeyLayout();
   }

   public byte[] getIMProperties(byte propID) {
      return this._inputMethod.getIMProperties(propID);
   }

   public boolean getIMStyleAsBoolean(int styleId) {
      return false;
   }

   public void setIMProperties(byte propID, byte[] IMProperties) {
      this._inputMethod.setIMProperties(propID, IMProperties);
   }

   public String[] getShortcuts() {
      return null;
   }

   public int removeShortcut(String replacedString, String replacementString) {
      return -1;
   }

   public int addShortcut(String replacedString, String replacementStringPattern) {
      return -1;
   }

   public void addDataRepository(IDataSearchRepository repository, int flag) {
   }

   public boolean setFilter(TextFilter filter) {
      return this._inputMethod.setFilter(filter);
   }

   public int setTextInputStyle(int style) {
      return this._inputMethod.setTextInputStyle(style);
   }

   public boolean setKeyLayoutLocale(Locale aLocale) {
      return this._inputMethod.setKeyLayoutLocale(aLocale);
   }

   public boolean isCorrectWord(StringBufferGap sbg, int startIndex, int length) {
      return this._inputMethod.isCorrectWord(sbg, startIndex, length);
   }

   public int getInputMode() {
      return -1;
   }

   public char getPeriodSymbol() {
      return this._inputMethod.getPeriodSymbol();
   }

   public Object getAdditionalSymbolData(int type) {
      return null;
   }
}
