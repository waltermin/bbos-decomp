package net.rim.device.apps.api.utility.framework;

public class VerbToMenuFactory {
   static VerbToMenu _instance;

   public static VerbToMenu createInstance() {
      if (_instance == null) {
         _instance = new VerbToMenuImpl();
      }

      _instance.clear();
      return _instance;
   }
}
