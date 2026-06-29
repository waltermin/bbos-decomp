package net.rim.device.apps.internal.bis.data;

public final class SecretQuestion {
   private int _id;
   private String _text;
   public static final int CUSTOM_ID = -1;

   public SecretQuestion(int id, String text) {
      this._id = id;
      this._text = text;
   }

   public final int getId() {
      return this._id;
   }

   public final String getText() {
      return this._text;
   }

   @Override
   public final String toString() {
      return this.getText();
   }

   public final boolean isCustom() {
      return this._id == -1;
   }
}
