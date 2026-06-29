package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

public class SkinEventProviderFactory {
   private static SkinEventProviderFactory _factory;

   public static SkinEventProviderFactory getFactory() {
      if (_factory == null) {
         _factory = new SkinEventProviderFactory();
         SkinEventProvider.initialize();
      }

      return _factory;
   }

   public SkinEventProvider createInstance() {
      return new SkinEventProvider();
   }
}
