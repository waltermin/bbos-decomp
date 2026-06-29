package net.rim.ecmascript.runtime;

import java.util.Vector;

public final class MDSHelper {
   public static final Vector enumerateProperties(ESObject obj) {
      Vector properties = new Vector(4);
      obj.enumerate(properties);
      return properties;
   }
}
