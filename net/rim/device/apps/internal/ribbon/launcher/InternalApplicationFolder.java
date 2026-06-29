package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.ApplicationFolder;

final class InternalApplicationFolder {
   private ApplicationFolder _applicationFolder;
   private ApplicationEntry[] _applications;
   private boolean _sorted;
   private ApplicationEntry _lastActiveApplication;
   private String _name;
   static String ROOT_FOLDER_NAME = "";
   private static Comparator _comparator = new InternalApplicationFolder$1();

   InternalApplicationFolder(ApplicationFolder folder) {
      this._name = folder.getName();
      this._applicationFolder = folder;
      this._applications = new ApplicationEntry[0];
   }

   InternalApplicationFolder(String name, ApplicationFolder folder) {
      this._name = name;
      this._applicationFolder = folder;
      this._applications = new ApplicationEntry[0];
   }

   final String getName() {
      return this._name;
   }

   final synchronized void addApplication(ApplicationEntry application) {
      if (this._sorted) {
         int count = this._applications.length;
         int index = Arrays.binarySearch(this._applications, application, _comparator, 0, count);
         if (index < 0) {
            index = -index - 1;
            Arrays.insertAt(this._applications, application, index);
            return;
         }

         this._sorted = false;
      }

      Arrays.add(this._applications, application);
   }

   final synchronized boolean removeApplication(ApplicationEntry application) {
      int index = Arrays.getIndex(this._applications, application);
      if (index != -1) {
         Arrays.removeAt(this._applications, index);
         return true;
      }

      String propertiesName = application.getPropertiesName();

      for (int i = this._applications.length - 1; i >= 0; i--) {
         String name = this._applications[i].getPropertiesName();
         if (StringUtilities.strEqual(propertiesName, name)) {
            Arrays.removeAt(this._applications, i);
            return true;
         }
      }

      return false;
   }

   final synchronized ApplicationEntry[] getApplications() {
      this.sort();
      return this._applications;
   }

   final synchronized ApplicationEntry getApplicationByIndex(int index) {
      this.sort();
      int count = 0;

      for (int i = 0; i < this._applications.length; i++) {
         if (this._applications[i].isVisible()) {
            if (count == index) {
               return this._applications[i];
            }

            count++;
         }
      }

      return null;
   }

   private final synchronized void sort() {
      if (!this._sorted) {
         Arrays.sort(this._applications, _comparator);
         int count = this._applications.length;

         for (int i = 1; i < count; i++) {
            ApplicationEntry previous = this._applications[i - 1];
            ApplicationEntry current = this._applications[i];
            int previousPriority = previous.getPriority();
            if (current.getPriority() <= previousPriority && !StringUtilities.strEqual(previous.getPropertiesName(), current.getPropertiesName())) {
               int newPriority = previousPriority + 1;
               if (i < count - 1) {
                  ApplicationEntry next = this._applications[i + 1];
                  if (next.getPriority() == newPriority) {
                     this._applications[i] = next;
                     this._applications[i + 1] = current;
                  }
               }

               current.setPriority(previousPriority + 1);
            }
         }

         this._sorted = true;
      }
   }

   private final int getApplicationEntryIndex(ApplicationEntry applicationEntry) {
      int count = this._applications.length;

      for (int i = 0; i < count; i++) {
         if (applicationEntry == this._applications[i]) {
            return i;
         }
      }

      return -1;
   }

   final synchronized void moveApplication(ApplicationEntry application, int startingMoveIconIndex, int index, ApplicationEntry lastEntry) {
      int lastPriority = application.getPriority();
      int toIndex;
      if (startingMoveIconIndex < index) {
         int fromIndex = this.getApplicationEntryIndex(application);
         toIndex = this.getApplicationEntryIndex(lastEntry);

         for (int i = fromIndex; i < toIndex; i++) {
            int nextPriority = this._applications[i + 1].getPriority();
            this._applications[i] = this._applications[i + 1];
            this._applications[i].setPriority(lastPriority);
            lastPriority = nextPriority;
         }
      } else {
         int fromIndex = this.getApplicationEntryIndex(application);
         toIndex = this.getApplicationEntryIndex(lastEntry);

         for (int i = fromIndex; i > toIndex; i--) {
            int nextPriority = this._applications[i - 1].getPriority();
            this._applications[i] = this._applications[i - 1];
            this._applications[i].setPriority(lastPriority);
            lastPriority = nextPriority;
         }
      }

      this._applications[toIndex] = application;
      application.setPriority(lastPriority);
      this._sorted = false;
   }

   final void setLastActiveApplication(ApplicationEntry application) {
      this._lastActiveApplication = application;
   }

   final ApplicationEntry getLastActiveApplication() {
      return this._lastActiveApplication;
   }

   public final boolean getAllowMoveIcons() {
      return this._applicationFolder.getAllowMoveIcons();
   }

   public final boolean getAllowHideIcons() {
      return this._applicationFolder.getAllowHideIcons();
   }

   public final String getResourceBundleName() {
      return this._applicationFolder.getResourceBundleName();
   }

   public final int getResourceId() {
      return this._applicationFolder.getResourceId();
   }
}
