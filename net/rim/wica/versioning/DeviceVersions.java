package net.rim.wica.versioning;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class DeviceVersions {
   private Vector _devFeatureVers = (Vector)(new Object(4));

   private DeviceVersions() {
   }

   public Vector getFeatureVersions() {
      return this._devFeatureVers;
   }

   public byte[] serialize() {
      StringBuffer strBuf = (StringBuffer)(new Object());
      if (this._devFeatureVers != null) {
         Enumeration enumeration = this._devFeatureVers.elements();

         while (enumeration.hasMoreElements()) {
            strBuf.append(((DeviceFeatureVersion$Handler)enumeration.nextElement()).serialize());
            strBuf.append(";");
         }
      }

      return strBuf.toString().getBytes();
   }

   public static DeviceVersions createFromFeatures(Vector featureVers) {
      DeviceVersions devVers = new DeviceVersions();
      devVers._devFeatureVers = featureVers;
      return devVers;
   }

   public static DeviceVersions createByDeserializing(byte[] in) {
      Hashtable tbl = (Hashtable)(new Object());
      DeviceVersions devVers = new DeviceVersions();
      Vector vect = CommonFeatureVersion.tokenize(in);
      Enumeration enumeration = vect.elements();

      while (enumeration.hasMoreElements()) {
         String str = (String)enumeration.nextElement();
         int ind1 = str.indexOf("=");
         if (ind1 > 0 && ind1 < str.length()) {
            tbl.put(str.substring(0, ind1), str.substring(ind1 + 1));
         }
      }

      enumeration = tbl.keys();

      while (enumeration.hasMoreElements()) {
         String name = (String)enumeration.nextElement();
         DeviceFeatureVersion$Handler devVer = DeviceFeatureVersion.getHandlerForFeature(name);
         if (devVer != null) {
            devVer.deserialize(name, (String)tbl.get(name));
            devVers._devFeatureVers.addElement(devVer);
         }
      }

      return devVers;
   }
}
