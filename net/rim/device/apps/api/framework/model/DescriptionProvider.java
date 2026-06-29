package net.rim.device.apps.api.framework.model;

import net.rim.device.internal.ui.IconCollection;

public interface DescriptionProvider {
   long BRIEF_SUMMARY;
   long SUBJECT;
   long LOCATION;
   long TIME_SUMMARY;
   long STATUS_ICONS;
   long CATEGORY_BITMAP;
   long VERBOSE_SUMMARY;
   byte EMPHASIS_PROP;

   String getStringForField(long var1);

   String getStringForField(long var1, long var3);

   int getIconsForField(long var1, IconCollection[] var3, int[][][] var4);

   byte getProperties();
}
