package net.rim.device.apps.api.framework.model;

import net.rim.device.internal.ui.IconCollection;

public interface DescriptionProvider {
   long BRIEF_SUMMARY = -4581712257088750184L;
   long SUBJECT = 5649235763655597796L;
   long LOCATION = 9164664086580876244L;
   long TIME_SUMMARY = -8797898085576394050L;
   long STATUS_ICONS = 7380487202915104824L;
   long CATEGORY_BITMAP = 5975440733927886771L;
   long VERBOSE_SUMMARY = 1589658722817992360L;
   byte EMPHASIS_PROP = 1;

   String getStringForField(long var1);

   String getStringForField(long var1, long var3);

   int getIconsForField(long var1, IconCollection[] var3, int[][] var4);

   byte getProperties();
}
