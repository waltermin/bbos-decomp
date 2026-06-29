package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.util.Util;

final class PreloadedApplicationInfo {
   static final String[] SUPPORTED_LANGUAGES = new String[]{"en", "fr", "it", "de", "es"};
   static final String[] URI = new String[]{"rim.net/mds/provisioning", "rim.net/mds/discovery", "rim.net/mds/controlcentre"};
   static final long[] ID = new long[]{
      1L,
      2L,
      3L,
      24965152773L,
      12884901894L,
      9L,
      1291980505090L,
      219043332398L,
      432987045896L,
      468151435371L,
      476741369966L,
      2147483648112L,
      -3455949755463498776L,
      429496729705L,
      458756849666L,
      -3455949746873565082L,
      571230650497L,
      549755814018L,
      1738657103877L,
      1743756722579L,
      4294967296405L,
      862483775491L,
      867583393993L,
      2150973964292L
   };
   static final String[] FEATURE_VERSION = new String[]{"Provisioning", "Discovery", "ControlCenter"};
   static final String[] PACKAGE = new String[]{"installer-1.1.0.zip", "search-1.1.0.zip", "control-1.1.0.zip"};
   static final String[] VERSION = new String[]{"1.1.0", "1.1.0", "1.1.0"};
   static final boolean[] VISIBILITY = new boolean[]{false, false, true};

   static final int getIndex(String uri) {
      return Util.arrayFind(URI, uri);
   }
}
