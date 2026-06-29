package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioException;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.system.CodeModuleGroupProperties;
import net.rim.device.internal.system.CodeModuleGroupPropertiesCollection;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.device.internal.ui.component.MIDletPlatformRequestDialog;
import net.rim.vm.TraceBack;

public class MIDlet {
   private MIDletMain _main;
   private CodeModuleGroupProperties _properties;
   private boolean _lookForGroupProperties = true;
   static boolean _instantiationAllowed;
   private static ResourceBundle _resources = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");

   protected MIDlet() {
      if (!_instantiationAllowed) {
         throw new SecurityException();
      }

      MIDletSecurity.checkMIDletCreation();
   }

   protected void startApp() {
      throw null;
   }

   protected void pauseApp() {
      throw null;
   }

   protected void destroyApp(boolean _1) {
      throw null;
   }

   public final void notifyDestroyed() {
      MIDletApplication ma = (MIDletApplication)Application.getApplication();
      ma.exit();
   }

   public final void notifyPaused() {
      this._main.requestBackground();
   }

   public final String getAppProperty(String key) {
      String moduleName = TraceBack.getCallingModuleName(2);
      String property = this.getGroupProperty(moduleName, key);
      return property != null ? property : MIDletApplication.getAppProperty(moduleName, key, true);
   }

   public final void resumeRequest() {
      this._main.requestForeground();
   }

   public final boolean platformRequest(String URL) throws ConnectionNotFoundException {
      if (URL != null && !URL.equals("")) {
         try {
            String moduleName = ApplicationDescriptor.currentApplicationDescriptor().getName();
            if (URL.indexOf("http:") != 0 && URL.indexOf("https:") != 0) {
               if (URL.indexOf("tel:") != 0 || !Phone.isSupported()) {
                  throw new ConnectionNotFoundException();
               }

               String phoneNumber = URL.substring(4);
               MIDletPlatformRequestDialog dialog;
               synchronized (Application.getEventLock()) {
                  String msg = MessageFormat.format(_resources.getString(10099), new String[]{moduleName, phoneNumber});
                  if (Display.getDisplay(this).getCurrent() instanceof Alert) {
                     ((Alert)Display.getDisplay(this).getCurrent()).setTimeout(-2);
                  }

                  dialog = new MIDletPlatformRequestDialog(msg);
               }

               if (dialog.requestGranted()) {
                  Phone.getInstance();
                  Phone.initiateCall(phoneNumber, 0);
               }
            } else {
               MIDletPlatformRequestDialog dialog;
               synchronized (Application.getEventLock()) {
                  String requestDomain = this.getRequestDomain(URL);
                  String msg = MessageFormat.format(_resources.getString(10098), new String[]{moduleName, requestDomain});
                  if (Display.getDisplay(this).getCurrent() instanceof Alert) {
                     ((Alert)Display.getDisplay(this).getCurrent()).setTimeout(-2);
                  }

                  dialog = new MIDletPlatformRequestDialog(msg);
               }

               if (dialog.requestGranted()) {
                  int handle = CodeModuleManager.getModuleHandle("net_rim_bb_browser_daemon");
                  if (handle <= 0) {
                     throw new ConnectionNotFoundException();
                  }

                  ApplicationDescriptor[] browserDescriptors = CodeModuleManager.getApplicationDescriptors(handle);
                  if (browserDescriptors == null) {
                     throw new ConnectionNotFoundException();
                  }

                  if (browserDescriptors.length <= 0) {
                     throw new ConnectionNotFoundException();
                  }

                  String[] args = new String[]{"url", URL};
                  ApplicationDescriptor descriptor = new ApplicationDescriptor(browserDescriptors[0], "url invocation", args, null, -1, null, -1, 2);

                  try {
                     ApplicationManager.getApplicationManager().runApplication(descriptor);
                  } catch (ApplicationManagerException e) {
                     throw new ConnectionNotFoundException();
                  }
               }
            }

            return false;
         } catch (RadioException e) {
            throw new ConnectionNotFoundException();
         }
      } else {
         return false;
      }
   }

   public final int checkPermission(String permission) {
      return MIDletSecurity.checkSymbolicPermission(permission);
   }

   void setMain(MIDletMain main) {
      this._main = main;
   }

   private String getRequestDomain(String URL) {
      String uri = URIDecoder.decode(URL, "utf-8");

      try {
         URL url = new URL(uri);
         return url.getHost();
      } catch (MalformedURLException var4) {
         return URL;
      } catch (IllegalArgumentException var5) {
         return URL;
      }
   }

   private String getGroupProperty(String moduleName, String key) {
      if (this._properties == null) {
         if (!this._lookForGroupProperties) {
            return null;
         }

         CodeModuleGroup[] groups = CodeModuleGroupManager.loadAll();
         if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
               if (groups[i].containsModule(moduleName)) {
                  CodeModuleGroupPropertiesCollection collection = CodeModuleGroupPropertiesCollection.getInstance();
                  if (collection != null) {
                     this._properties = (CodeModuleGroupProperties)collection.getSyncObject(
                        CodeModuleGroupPropertiesCollection.getGroupUID(groups[i].getName())
                     );
                  }
                  break;
               }
            }
         }

         if (this._properties == null) {
            this._lookForGroupProperties = false;
         }
      }

      return this._properties == null ? null : (String)this._properties.get(key);
   }
}
