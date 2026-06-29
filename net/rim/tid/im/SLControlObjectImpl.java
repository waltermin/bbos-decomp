package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.tid.im.layout.SLKeyLayout;

public class SLControlObjectImpl extends SLControlObject {
   private SLInputMethod _inputMethod;

   private SLControlObjectImpl() {
   }

   public SLControlObjectImpl(SLInputMethod inputMethod) {
      this._inputMethod = inputMethod;
   }

   @Override
   public Object getViewComponent(int component) {
      return this._inputMethod.getViewComponent(component);
   }

   @Override
   public int actionPerformed(int action, Object param) {
      return this._inputMethod.actionPerformed(null, action, param);
   }

   @Override
   public SLKeyLayout getKeyLayout() {
      return this._inputMethod.getKeyLayout();
   }

   @Override
   public byte[] getIMProperties(byte propID) {
      return this._inputMethod.getIMProperties(propID);
   }

   @Override
   public void setIMProperties(byte propID, byte[] IMProperties) {
      this._inputMethod.setIMProperties(propID, IMProperties);
   }

   @Override
   public boolean getIMStyleAsBoolean(int styleId) {
      return this._inputMethod.getIMStyleAsBoolean(styleId);
   }

   @Override
   public String[] getShortcuts() {
      return this._inputMethod.getShortcuts();
   }

   @Override
   public int removeShortcut(String replacedString, String replacementString) {
      return this._inputMethod.removeShortcut(replacedString, replacementString);
   }

   @Override
   public int addShortcut(String replacedString, String replacementStringPattern) {
      return this._inputMethod.addShortcut(replacedString, replacementStringPattern);
   }

   @Override
   public boolean setFilter(TextFilter filter) {
      return this._inputMethod.setFilter(filter);
   }

   @Override
   public int setTextInputStyle(int style) {
      return this._inputMethod.setTextInputStyle(style);
   }

   @Override
   public int getInputMode() {
      return this._inputMethod.getInputMode();
   }

   @Override
   public boolean setKeyLayoutLocale(Locale aLocale) {
      return false;
   }

   @Override
   public char getPeriodSymbol() {
      return this._inputMethod.getPeriodSymbol();
   }

   @Override
   public Object getAdditionalSymbolData(int type) {
      return this._inputMethod.getAdditionalSymbolData(type);
   }
}
