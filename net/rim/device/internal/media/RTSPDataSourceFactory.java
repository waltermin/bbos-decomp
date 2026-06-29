package net.rim.device.internal.media;

public class RTSPDataSourceFactory {
   public static RTSPDataSource createNewDataSource(String locator, String userAgent) {
      try {
         Class c = Class.forName("net.rim.device.internal.media.RTSPDataSourceFactoryImpl");
         RTSPDataSourceFactory factory = (RTSPDataSourceFactory)c.newInstance();
         return factory.createDataSource(locator, userAgent);
      } catch (ClassNotFoundException var4) {
         return null;
      } catch (InstantiationException var5) {
         return null;
      } catch (IllegalAccessException var6) {
         return null;
      }
   }

   public RTSPDataSource createDataSource(String _1, String _2) {
      throw null;
   }
}
