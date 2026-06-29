package net.rim.ecmascript.runtime;

import java.util.Vector;

public final class MDSHelper {
   public static final Vector enumerateProperties(ESObject obj) {
      Vector properties = (Vector)(new Object(4));
      obj.enumerate(properties);
      return properties;
   }
}
