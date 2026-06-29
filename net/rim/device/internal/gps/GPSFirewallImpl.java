package net.rim.device.internal.gps;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.MIDletSecurity;

public final class GPSFirewallImpl implements GPSFirewallInterface {
   private int _currentPrivacy;
   private static final int MAX_GPS_QUERY_ATTEMPT;
   private static final int GPS_WAIT_TIME;
   private static final long KEY;

   public GPSFirewallImpl() {
      GPS.addListener(Application.getApplication(), new GPSFirewallImpl$PrivacyListener(this));
      this._currentPrivacy = -1;
   }

   private final synchronized int getCurrentPrivacy() {
      if (!RadioInfo.areWAFsSupported(8)) {
         return 2;
      }

      if (this._currentPrivacy < 0) {
         int retryCount;
         for (retryCount = 0; retryCount < 20 && this._currentPrivacy < 0; retryCount++) {
            this._currentPrivacy = GPS.requestGetLPS();
            if (this._currentPrivacy < 0) {
               try {
                  this.wait(2500);
               } finally {
                  continue;
               }
            }
         }

         if (retryCount == 20 && this._currentPrivacy < 0) {
            return 0;
         }
      }

      return this._currentPrivacy;
   }

   @Override
   public final void setCurrentPrivacy(int privacy) {
      synchronized (this) {
         this._currentPrivacy = privacy;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean allowAccess(String permission) {
      int privacy = this.getCurrentPrivacy();
      synchronized (this) {
         switch (privacy) {
            case 0:
            case 2:
               int perm = -1;
               if (permission != null) {
                  if (permission.equals("lapi_location")) {
                     perm = 20;
                  } else if (permission.equals("lapi_orientation")) {
                     perm = 21;
                  } else if (permission.equals("lapi_proximitylistener")) {
                     perm = 22;
                  } else if (permission.equals("lapi_landmarkstore_read")) {
                     perm = 23;
                  } else if (permission.equals("lapi_landmarkstore_write")) {
                     perm = 24;
                  } else if (permission.equals("lapi_landmarkstore_category")) {
                     perm = 25;
                  } else if (permission.equals("lapi_landmarkstore_management")) {
                     perm = 26;
                  }

                  int midpAction = 1;
                  if (perm != -1) {
                     midpAction = MIDletSecurity.checkPermissionNoPrompt(perm);
                  }

                  if (midpAction == 0) {
                     return false;
                  }
               }

               int rc = ApplicationControl.isLocationApiAllowed(true);
               switch (rc) {
                  case 0:
                     if (privacy == 2 || ApplicationControl.isPromptResponseSaved(18)) {
                        return true;
                     } else {
                        rc = 2;
                     }
                  case -1:
                     boolean var10 = false /* VF: Semaphore variable */;

                     try {
                        var10 = true;
                        ApplicationControl.doPromptWork(
                           rc, ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall"), 16, 17, 18
                        );
                        if (perm != -1) {
                           if (CodeModuleManager.isMidlet()) {
                              MIDletSecurity.setPermission(perm, MIDletSecurity.checkRealPermissionNoPrompt(perm), true);
                              var10 = false;
                           } else {
                              var10 = false;
                           }
                        } else {
                           var10 = false;
                        }
                     } finally {
                        if (var10) {
                           if (perm != -1 && CodeModuleManager.isMidlet()) {
                              MIDletSecurity.setPermission(perm, MIDletSecurity.checkRealPermissionNoPrompt(perm), false);
                           }

                           return false;
                        }
                     }

                     return true;
                  case 1:
                  default:
                     return false;
               }
            default:
               return false;
         }
      }
   }

   @Override
   public final void reset() {
      ApplicationControl.resetAllPrompts(17, 18);
   }

   @Override
   public final boolean isEnabled() {
      return ITPolicyInternal.isITPolicyEnabled();
   }
}
