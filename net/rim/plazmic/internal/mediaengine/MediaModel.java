package net.rim.plazmic.internal.mediaengine;

import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

public interface MediaModel {
   Class getMediaClass();

   int getVersionNumber();

   String getContentType();

   int getNumMetaInfo();

   String getMetaInfo(String var1);

   String getMetaKey(int var1);

   String getMetaValue(int var1);

   String getSource();

   void setSource(String var1);

   String[] getExternalResources(int var1);

   String getMissingExtURLs();

   int getExternalResourcesCount(int var1);

   void disposeModel();

   Object getResource(String var1);

   ForeignObject[] getForeignObjects();
}
