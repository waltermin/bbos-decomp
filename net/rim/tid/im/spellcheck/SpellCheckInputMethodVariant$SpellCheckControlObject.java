package net.rim.tid.im.spellcheck;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.conv.repository.IDataSearchRepository;
import net.rim.tid.im.layout.SLKeyLayout;

class SpellCheckInputMethodVariant$SpellCheckControlObject extends SLControlObject {
   SpellCheckInputMethodVariant _im;
   SLControlObject _joinedControlObject;

   SpellCheckInputMethodVariant$SpellCheckControlObject(SpellCheckInputMethodVariant im, SLControlObject jo) {
      this._im = im;
      this._joinedControlObject = jo;
   }

   @Override
   public int actionPerformed(int action, Object param) {
      InputMethodContext inputMethodContext = this._im._inputMethodContext;
      if (inputMethodContext == null) {
         return 0;
      }

      synchronized (inputMethodContext) {
         return this._im.actionPerformed(this, action, param);
      }
   }

   @Override
   public void addDataRepository(IDataSearchRepository repos, int flag) {
      this._joinedControlObject.addDataRepository(repos, flag);
   }

   @Override
   public int addShortcut(String replacedString, String replacementStringPattern) {
      return this._joinedControlObject.addShortcut(replacedString, replacementStringPattern);
   }

   @Override
   public byte[] getIMProperties(byte propID) {
      return this._im.getIMProperties(propID);
   }

   @Override
   public int getInputMode() {
      return this._joinedControlObject.getInputMode();
   }

   @Override
   public SLKeyLayout getKeyLayout() {
      return this._joinedControlObject.getKeyLayout();
   }

   @Override
   public char getPeriodSymbol() {
      return this._joinedControlObject.getPeriodSymbol();
   }

   @Override
   public String[] getShortcuts() {
      return this._joinedControlObject.getShortcuts();
   }

   @Override
   public Object getViewComponent(int component) {
      return this._joinedControlObject.getViewComponent(component);
   }

   @Override
   public boolean isCorrectWord(StringBufferGap sbg, int startIndex, int length) {
      return this._joinedControlObject.isCorrectWord(sbg, startIndex, length);
   }

   @Override
   public int removeShortcut(String replacedString, String replacementString) {
      return this._joinedControlObject.removeShortcut(replacedString, replacementString);
   }

   @Override
   public boolean setFilter(TextFilter filter) {
      return this._joinedControlObject.setFilter(filter);
   }

   @Override
   public void setIMProperties(byte propID, byte[] IMProperties) {
      this._im.setIMProperties(propID, IMProperties);
   }

   @Override
   public boolean setKeyLayoutLocale(Locale locale) {
      return this._joinedControlObject.setKeyLayoutLocale(locale);
   }

   @Override
   public int setTextInputStyle(int style) {
      return this._joinedControlObject.setTextInputStyle(style);
   }
}
