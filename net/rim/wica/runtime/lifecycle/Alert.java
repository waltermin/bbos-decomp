package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;

public final class Alert implements Persistable {
   private String _text;
   private int _messageCode;
   private boolean _profile;
   private boolean _ribbon;

   public Alert() {
   }

   public Alert(int messageCode, boolean profile, boolean ribbon, String text) {
      this._messageCode = messageCode;
      this._profile = profile;
      this._ribbon = ribbon;
      this._text = text;
   }

   public final int getMessageCode() {
      return this._messageCode;
   }

   public final String getText() {
      return this._text;
   }

   public final boolean hasDialog() {
      return this._text != null;
   }

   public final boolean hasProfile() {
      return this._profile;
   }

   public final boolean hasRibbonIndicator() {
      return this._ribbon;
   }

   public final void setMessageCode(int messageCode) {
      this._messageCode = messageCode;
   }

   public final void setProfile(boolean profile) {
      this._profile = profile;
   }

   public final void setRibbon(boolean ribbon) {
      this._ribbon = ribbon;
   }

   public final void setText(String text) {
      this._text = text;
   }
}
