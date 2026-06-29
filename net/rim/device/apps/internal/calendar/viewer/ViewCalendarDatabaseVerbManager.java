package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.service.ServiceIdentifier;

final class ViewCalendarDatabaseVerbManager {
   private CalendarKey[] _folders;
   private ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");

   ViewCalendarDatabaseVerbManager() {
      this.createFolderKeys();
   }

   public final Verb getChooseCalendarVerb() {
      return this._folders.length > 1 ? new ViewCalendarDatabaseVerbManager$ChooseCalendarVerb(this) : null;
   }

   protected final void createFolderKeys() {
      ServiceIdentifier[] calendarServices = CalendarServiceManager.getInstance().getCalendarServices(false, true);
      Vector folders = new Vector();

      for (int i = 0; i < calendarServices.length; i++) {
         CalendarService calendarService = (CalendarService)calendarServices[i];
         LongHashtable calendarFolders = calendarService.getCalendarFolders();
         LongEnumeration folderIDs = calendarFolders.keys();

         while (folderIDs.hasMoreElements()) {
            CalendarKey calendarKey = new CalendarKey(calendarService.getUniqueServiceID(), folderIDs.nextElement());
            folders.addElement(calendarKey);
         }
      }

      this._folders = new CalendarKey[folders.size()];
      folders.copyInto(this._folders);
   }
}
