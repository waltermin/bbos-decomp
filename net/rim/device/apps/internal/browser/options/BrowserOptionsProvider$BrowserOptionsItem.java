package net.rim.device.apps.internal.browser.options;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.SBInjector;
import net.rim.device.internal.EScreens.EScreenAccess;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class BrowserOptionsProvider$BrowserOptionsItem extends SaveableMainScreenOptionsListItem {
   private Vector _mdsHotkeyConfigs = new Vector();
   private Vector _wapHotkeyConfigs = new Vector();
   private Vector _browserConfigs = new Vector();
   private ObjectChoiceField _mdsHotkeyConfigChoiceField;
   private ObjectChoiceField _wapHotkeyConfigChoiceField;
   private ObjectChoiceField _browserConfigChoiceField;

   public BrowserOptionsProvider$BrowserOptionsItem() {
      super(BrowserResources.getString(232), -1514481539159318190L);
      ContextObject.put(super._context, 244, new Integer(27786));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      String defaultUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
      String mdsHotkeyUid = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
      String wapHotkeyUid = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
      BrowserConfigRecord[] browserConfigs = BrowserConfigRecord.getValidBrowserConfigRecords();
      this._mdsHotkeyConfigs.removeAllElements();
      this._wapHotkeyConfigs.removeAllElements();
      this._browserConfigs.removeAllElements();
      int mdsHotkeyConfigIndex = 0;
      int wapHotkeyConfigIndex = 0;
      int browserConfigIndex = 0;

      for (int i = 0; i < browserConfigs.length; i++) {
         BrowserConfigRecord browserConfig = browserConfigs[i];
         String transportUid = browserConfig.getPropertyAsString(4);
         String transportCid = browserConfig.getPropertyAsString(3);
         int configType = browserConfig.getPropertyAsInt(12);
         if (browserConfig.getPropertyAsInt(30) != 1
            && configType != 2
            && configType != 6
            && BrowserConfigRecord.getTransportServiceRecord(transportCid, transportUid) != null) {
            if (configType == 7 || StringUtilities.strEqualIgnoreCase(transportCid, WAPServiceRecord.SERVICE_CID, 1701707776)) {
               this._wapHotkeyConfigs.addElement(browserConfig);
               if (StringUtilities.strEqualIgnoreCase(browserConfig.getUid(), wapHotkeyUid, 1701707776)) {
                  wapHotkeyConfigIndex = this._wapHotkeyConfigs.size() - 1;
               }

               if (browserConfig.getPropertyAsInt(7) == 0) {
                  this._browserConfigs.addElement(browserConfig);
                  if (StringUtilities.strEqualIgnoreCase(browserConfig.getUid(), defaultUid, 1701707776)) {
                     browserConfigIndex = this._browserConfigs.size() - 1;
                  }
               }
            } else if (StringUtilities.strEqualIgnoreCase(transportCid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
               this._mdsHotkeyConfigs.addElement(browserConfig);
               if (StringUtilities.strEqualIgnoreCase(browserConfig.getUid(), mdsHotkeyUid, 1701707776)) {
                  mdsHotkeyConfigIndex = this._mdsHotkeyConfigs.size() - 1;
               }

               if (browserConfig.getPropertyAsInt(7) == 0) {
                  this._browserConfigs.addElement(browserConfig);
                  if (StringUtilities.strEqualIgnoreCase(browserConfig.getUid(), defaultUid, 1701707776)) {
                     browserConfigIndex = this._browserConfigs.size() - 1;
                  }
               }
            } else if (browserConfig.getPropertyAsInt(7) == 0) {
               this._browserConfigs.addElement(browserConfig);
               if (StringUtilities.strEqualIgnoreCase(browserConfig.getUid(), defaultUid, 1701707776)) {
                  browserConfigIndex = this._browserConfigs.size() - 1;
               }
            }
         }
      }

      int browserConfigsNum = this._browserConfigs.size();
      if (browserConfigsNum >= 0) {
         String[] browserConfigNames = new String[browserConfigsNum];

         for (int i = 0; i < browserConfigsNum; i++) {
            browserConfigNames[i] = this.getBrowserName((BrowserConfigRecord)this._browserConfigs.elementAt(i));
         }

         this._browserConfigChoiceField = new ObjectChoiceField(BrowserResources.getString(200), browserConfigNames, browserConfigIndex);
         mainScreen.add(this._browserConfigChoiceField);
      }

      int mdsConfigsNum = this._mdsHotkeyConfigs.size();
      if (mdsConfigsNum >= 1) {
         String[] mdsHotkeyConfigNames = new String[mdsConfigsNum];

         for (int i = 0; i < mdsConfigsNum; i++) {
            mdsHotkeyConfigNames[i] = this.getBrowserName((BrowserConfigRecord)this._mdsHotkeyConfigs.elementAt(i));
         }

         this._mdsHotkeyConfigChoiceField = new ObjectChoiceField(BrowserResources.getString(717), mdsHotkeyConfigNames, mdsHotkeyConfigIndex);
         mainScreen.add(this._mdsHotkeyConfigChoiceField);
      }

      int wapConfigsNum = this._wapHotkeyConfigs.size();
      if (wapConfigsNum >= 1) {
         String[] wapHotkeyConfigNames = new String[wapConfigsNum];

         for (int i = 0; i < wapConfigsNum; i++) {
            wapHotkeyConfigNames[i] = this.getBrowserName((BrowserConfigRecord)this._wapHotkeyConfigs.elementAt(i));
         }

         this._wapHotkeyConfigChoiceField = new ObjectChoiceField(BrowserResources.getString(718), wapHotkeyConfigNames, wapHotkeyConfigIndex);
         mainScreen.add(this._wapHotkeyConfigChoiceField);
      }
   }

   private final String getBrowserName(BrowserConfigRecord rec) {
      return rec != null ? rec.getLocalizedString(11) : null;
   }

   @Override
   protected final boolean save() {
      if (this._mdsHotkeyConfigChoiceField != null) {
         int index = this._mdsHotkeyConfigChoiceField.getSelectedIndex();
         if (index >= 0) {
            GeneralProperty.setDefaultMdsBrowserConfigServiceUID(((BrowserConfigRecord)this._mdsHotkeyConfigs.elementAt(index)).getUid());
            GeneralProperty.setCurrentProperty(33, true);
         }
      }

      if (this._wapHotkeyConfigChoiceField != null) {
         int index = this._wapHotkeyConfigChoiceField.getSelectedIndex();
         if (index >= 0) {
            GeneralProperty.setDefaultWapBrowserConfigServiceUID(((BrowserConfigRecord)this._wapHotkeyConfigs.elementAt(index)).getUid());
            GeneralProperty.setCurrentProperty(32, true);
         }
      }

      if (this._browserConfigChoiceField != null) {
         int index = this._browserConfigChoiceField.getSelectedIndex();
         if (index >= 0) {
            GeneralProperty.setDefaultBrowserConfigServiceUID(((BrowserConfigRecord)this._browserConfigs.elementAt(index)).getUid());
         }
      }

      GeneralProperty.setPreferredBrowserConfigServiceUID(null);
      GeneralProperty.setPreferredMdsBrowserConfigServiceUID(null);
      GeneralProperty.setPreferredWapBrowserConfigServiceUID(null);
      return super.save();
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1380076882:
            if (BackdoorKeyProcessor.isDevelopmentDevice() || EScreenAccess.isAllowed()) {
               SBInjector.injectWAP1xPush();
               SBInjector.injectMMS();
               RIMGlobalMessagePoster.postGlobalEvent(-2734094174038131697L);
               Dialog.alert("MMS over WAP Service Record successfully injected.");
            }

            return true;
         case 1380076884:
            if (BackdoorKeyProcessor.isDevelopmentDevice() || EScreenAccess.isAllowed()) {
               SBInjector.injectWAP1xPush();
               SBInjector.injectTCPMMS(
                  "10.1.20.203:9201:8205", null, null, null, "MMS rimnet", "MMS Transport (RIM)", null, null, "http://mknowles-temp.rim.net:82/", null
               );
               RIMGlobalMessagePoster.postGlobalEvent(-2734094174038131697L);
               Dialog.alert("MMS over TCP/IP Service Record successfully injected.");
            }

            return true;
         case 1380079430:
            GeneralProperty.setCurrentProperty(12, false);
            Dialog.alert("Browser service records will *NOT* be deleted after a SIM change.");
            return true;
         case 1380079438:
            GeneralProperty.setCurrentProperty(12, true);
            Dialog.alert("Browser service records will be deleted after a SIM change.");
            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1380073796:
            Dialog.alert(SBInjector.injectAppDownload() ? "App Download successfully injected." : "App Download injection failed.");
            return true;
         case 1380075842:
            SBInjector.injectSBWithBookmarks(true);
            Dialog.alert("Browser Service Record with Bookmarks successfully injected.");
            this.clearScreen();
            return true;
         case 1380075860:
            SBInjector.injectTCPBrowser();
            Dialog.alert("Browser TCP Service Record injected.");
            this.clearScreen();
            return true;
         case 1380077378:
            SBInjector.injectSBWithBookmarks(false);
            Dialog.alert("Browser Service Record without Bookmarks successfully injected.");
            this.clearScreen();
            return true;
         case 1380077634:
            SBInjector.injectWAP1xPush();
            Dialog.alert("Browser Push Service Records successfully injected.");
            return true;
         case 1380078402:
            SBInjector.inject();
            Dialog.alert("Browser Service Records successfully injected.");
            this.clearScreen();
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   private final void clearScreen() {
      synchronized (Application.getEventLock()) {
         Screen mainScreen = UiApplication.getUiApplication().getActiveScreen();
         if (mainScreen instanceof MainScreen) {
            mainScreen.deleteRange(0, mainScreen.getFieldCount());
            this.populateMainScreen((MainScreen)mainScreen);
         }
      }
   }
}
