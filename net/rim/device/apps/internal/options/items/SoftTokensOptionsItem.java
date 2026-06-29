package net.rim.device.apps.internal.options.items;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.impl.api.SoftToken;
import net.rim.device.cldc.impl.api.SoftTokenListener;
import net.rim.device.cldc.impl.api.SoftTokenManager;
import net.rim.device.internal.ui.component.PasswordDialog;

public final class SoftTokensOptionsItem
   extends MainScreenOptionsListItem
   implements OptionsProviderRegistration$OptionsProvider,
   ListFieldCallback,
   SoftTokenListener {
   private UiApplication _uiApp;
   private SoftTokenManager _softTokenMgr;
   private ListField _tokenListField;
   private Vector _tokenList;
   private FileSelector _fileSelector;
   private SoftTokensOptionsItem$OpenTokenVerb _openTokenVerb;
   private SoftTokensOptionsItem$InstallTokenVerb _installTokenVerb;
   private SoftTokensOptionsItem$UninstallTokenVerb _uninstallTokenVerb;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-984250137466033667L, "net.rim.device.apps.internal.resource.SoftTokensOptionsResources");

   public final String getString(int stringID) {
      return _rb.getString(stringID);
   }

   public final void saveToken(String seedString, String passwordString, boolean promptForPassword) throws IOException {
      int retCode = this._softTokenMgr.save(seedString, passwordString, -1, false, null);
      if (retCode != 0 && retCode != -2) {
         int errorStringID;
         switch (retCode) {
            case -9:
               errorStringID = 16;
               break;
            case -8:
            case -7:
               errorStringID = 14;
               break;
            case -6:
               if (passwordString.length() == 0) {
                  errorStringID = 12;
                  if (promptForPassword) {
                     PasswordDialog passwordDialog = new PasswordDialog(this.getString(22));
                     passwordDialog.show();
                     if (passwordDialog.getCloseReason() != -1) {
                        passwordString = new String(passwordDialog.getPassword());
                        this.saveToken(seedString, passwordString, false);
                        return;
                     }
                  }
               } else {
                  errorStringID = 13;
               }
               break;
            case -5:
               errorStringID = 11;
               break;
            case -4:
               errorStringID = 10;
               break;
            case -3:
            default:
               errorStringID = 9;
         }

         throw new IOException(this.getString(errorStringID));
      } else {
         this._tokenList = this._softTokenMgr.getSoftTokens();
         this._tokenListField.setSize(this._tokenList.size());
         this._tokenListField.setSelectedIndex(this._tokenList.size() - 1);
      }
   }

   public final Object getOptionsItem(String itemID) {
      return null;
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector();
      items.addElement(new SoftTokensOptionsItem());
      return items;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      SoftToken token = (SoftToken)this._tokenList.elementAt(index);
      if (token != null) {
         if (token.getNickName() != null) {
            graphics.drawText(token.getNickName(), 0, y, 6, width);
         }

         if (token.getSerialNum() != null) {
            graphics.drawText(token.getSerialNum(), 0, y, 5, width);
         }
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return (SoftToken)this._tokenList.elementAt(index);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void softTokensUpdated() {
      synchronized (this._uiApp.getAppEventLock()) {
         this._tokenList = this._softTokenMgr.getSoftTokens();
         this._tokenListField.setSize(this._tokenList.size());
      }
   }

   public SoftTokensOptionsItem() {
      super(_rb.getString(2), new ContextObject(0, 2), 5294015899860238835L);
      if (WLAN.isSupported()) {
         ContextObject.put(super._context, 244, new Integer(100772));
      }

      this._softTokenMgr = SoftTokenManager.getInstance();
      this._openTokenVerb = new SoftTokensOptionsItem$OpenTokenVerb(this);
      if (BackdoorKeyProcessor.isDevelopmentDevice()) {
         this._installTokenVerb = new SoftTokensOptionsItem$InstallTokenVerb(this);
         this._uninstallTokenVerb = new SoftTokensOptionsItem$UninstallTokenVerb(this);
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._softTokenMgr != null && BackdoorKeyProcessor.isDevelopmentDevice()) {
         verbToMenu.addVerb(this._installTokenVerb);
      }
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      if (this._tokenListField != null
         && this._tokenList != null
         && this._tokenListField.getSelectedIndex() < this._tokenList.size()
         && this._tokenList.size() > 0) {
         if (BackdoorKeyProcessor.isDevelopmentDevice()) {
            verbToMenu.addVerb(this._uninstallTokenVerb);
         }

         verbToMenu.addVerb(this._openTokenVerb);
         verbToMenu.setDefaultVerb(this._openTokenVerb);
         return this._openTokenVerb;
      } else {
         return null;
      }
   }

   @Override
   protected final boolean doCloseVerb() {
      if (this._softTokenMgr != null) {
         this._softTokenMgr.removeListener(this);
      }

      return super.doCloseVerb();
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      if (this._softTokenMgr != null) {
         this._softTokenMgr.addListener(this);
         this._uiApp = UiApplication.getUiApplication();
         this._tokenListField = new ListField();
         this._tokenList = this._softTokenMgr.getSoftTokens();
         this._tokenListField.setCallback(this);
         this._tokenListField.setSize(this._tokenList.size());
         this._tokenListField.setEmptyString(this.getString(5), 4);
         mainScreen.add(this._tokenListField);
      }
   }

   public static final void libMain(String[] args) {
      if (SoftTokenManager.getInstance() != null) {
         OptionsProviderRegistration.registerOptionsProvider(new SoftTokensOptionsItem());
      }
   }
}
