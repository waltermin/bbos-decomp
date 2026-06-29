package net.rim.device.apps.internal.secureemail.sendmethods;

public class DoNotEncryptException extends Exception {
   private int _newEncodingAction;

   public DoNotEncryptException(int newEncodingAction) {
      this._newEncodingAction = newEncodingAction;
   }

   public int getNewEncodingAction() {
      return this._newEncodingAction;
   }
}
