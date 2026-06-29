package net.rim.wica.versioning;

import java.util.Vector;

public class CommonFeatureVersion {
   protected String _name;
   public static final String DISCOVERY_VERSION;
   public static final String PROVISIONING_VERSION;
   public static final String CONTROL_CENTER_VERSION;
   public static final String SYSTEM_MSG_VERSION;
   public static final String TRANSPORT_VERSION;
   public static final String SECURITY_VERSION;
   public static final int NUM_VERSIONED_FEATURES;
   public static final String VERSION_SEPARATOR;
   public static final String VERSION_RANGE_SEPARATOR;
   public static final String NAME_VERSION_SEPARATOR;

   public String getFeatureName() {
      return this._name;
   }

   protected void setFeatureName(String name) {
      this._name = name;
   }

   public String serialize() {
      return ((StringBuffer)(new Object())).append(this._name).append("=").toString();
   }

   public static Vector tokenize(byte[] in) {
      Vector vec = (Vector)(new Object());
      String inStr = (String)(new Object(in));
      int ind1 = 0;

      for (int ind2 = inStr.indexOf(";"); ind2 > 0 && ind2 > ind1; ind2 = inStr.substring(ind1).indexOf(";") + ind1) {
         vec.addElement(inStr.substring(ind1, ind2));
         ind1 = ind2 + 1;
      }

      return vec;
   }
}
