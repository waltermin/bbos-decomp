package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public interface PageModel extends PersistableRIMModel {
   int STATUS_INITIAL;
   int STATUS_LOADING;
   int STATUS_PENDING;
   int STATUS_LOADED;
   int STATUS_OPENED;
   int STATUS_ERROR;

   long getLUID();

   void setLUID(long var1);

   long getTimeStamp();

   void setTimeStamp(long var1);

   long getLastAccessedTime();

   void setLastAccessedTime(long var1);

   int getStatus();

   String getTitle();

   void setTitle(String var1);

   ModelResult getModelResult();

   String getUrl();

   String getIconUrl();

   boolean isHomePage();

   void changeStatus(int var1);

   boolean isCompleted();

   int getUpdateStart();

   byte getUpdateFlags();

   int getUpdatePeriod();
}
