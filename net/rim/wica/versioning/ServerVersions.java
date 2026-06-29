package net.rim.wica.versioning;

import java.util.Enumeration;
import java.util.Vector;

public class ServerVersions {
   private Vector _srvVers = new Vector(4);

   private ServerVersions() {
   }

   public Vector getFeatureVersions() {
      return this._srvVers;
   }

   public byte[] serialize() {
      if (this._srvVers == null) {
         return null;
      }

      StringBuffer strBuf = new StringBuffer();
      Enumeration enumeration = this._srvVers.elements();

      while (enumeration.hasMoreElements()) {
         strBuf.append(((ServerFeatureVersion)enumeration.nextElement()).serialize());
         strBuf.append(";");
      }

      return strBuf.toString().getBytes();
   }

   public static ServerVersions createByDeserializing(byte[] in) {
      ServerVersions srvVers = new ServerVersions();
      Vector vect = CommonFeatureVersion.tokenize(in);
      Enumeration enumeration = vect.elements();

      while (enumeration.hasMoreElements()) {
         ServerFeatureVersion srvVer = ServerFeatureVersion.createByDeserializing((String)enumeration.nextElement());
         srvVers._srvVers.addElement(srvVer);
      }

      return srvVers;
   }

   public static ServerVersions createFromFeatures(Vector in) {
      ServerVersions srvVers = new ServerVersions();
      srvVers._srvVers = in;
      return srvVers;
   }
}
