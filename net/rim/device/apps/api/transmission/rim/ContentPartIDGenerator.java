package net.rim.device.apps.api.transmission.rim;

public class ContentPartIDGenerator {
   private int _currentValue;

   public synchronized int generateContentPartID() {
      return this._currentValue++;
   }
}
