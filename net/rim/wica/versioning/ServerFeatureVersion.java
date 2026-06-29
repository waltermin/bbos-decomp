package net.rim.wica.versioning;

public class ServerFeatureVersion extends CommonFeatureVersion {
   private String _curVersion;

   private ServerFeatureVersion() {
   }

   public String getCurrentVersion() {
      return this._curVersion;
   }

   @Override
   public String serialize() {
      return ((StringBuffer)(new Object())).append(super.serialize()).append(this._curVersion).toString();
   }

   public static ServerFeatureVersion createByDeserializing(String inStr) {
      ServerFeatureVersion srvVer = new ServerFeatureVersion();
      int ind1 = inStr.indexOf("=");
      if (ind1 > 0) {
         srvVer.setFeatureName(inStr.substring(0, ind1));
         if (ind1 < inStr.length()) {
            srvVer._curVersion = inStr.substring(ind1 + 1);
         }
      }

      return srvVer;
   }

   public static ServerFeatureVersion createFromValues(String name, int ver) {
      ServerFeatureVersion srvVer = new ServerFeatureVersion();
      srvVer.setFeatureName(name);
      srvVer._curVersion = ((StringBuffer)(new Object(""))).append(ver).toString();
      return srvVer;
   }

   public static ServerFeatureVersion createFromValues(String name, String ver) {
      ServerFeatureVersion srvVer = new ServerFeatureVersion();
      srvVer.setFeatureName(name);
      srvVer._curVersion = ver;
      return srvVer;
   }
}
