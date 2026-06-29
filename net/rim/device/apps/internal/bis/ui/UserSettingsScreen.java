package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItem;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public class UserSettingsScreen extends BasicScreen {
   private int _menuOptions;
   public static final int EMAIL_ACCOUNTS_OPTION;
   public static final int CHANGE_DEVICE_OPTION;
   public static final int CHANGE_LANGUAGE_OPTION;
   public static final int CHANGE_PASSWORD_OPTION;
   public static final int SEND_SB_OPTION;
   public static final int ALL_OPTIONS;

   public UserSettingsScreen() {
      this(0);
   }

   public UserSettingsScreen(int menuOptions) {
      this._menuOptions = menuOptions;
   }

   @Override
   protected void addCustomMenuItems(Menu menu, int instance) {
      super.addCustomMenuItems(menu, instance);
      if (instance != 65536) {
         if (menu.getInstance() == 65538) {
            menu.add(MenuItem.separator(30000));
         }

         boolean hasOptions = false;
         if ((this._menuOptions & 1) != 0) {
            LinkEvent emailAccountsLinkEvent = null;
            if (ClientSessionState.getInstance().getUserInfo().isBBMail()) {
               emailAccountsLinkEvent = new LinkEvent(272, 7);
            } else {
               emailAccountsLinkEvent = new LinkEvent(57, 7);
            }

            NotificationMenuItem emailAccountsMenuItem = new NotificationMenuItem(emailAccountsLinkEvent.getLabel(), 30000, 30000);
            menu.add(emailAccountsMenuItem);
            this.attachEventToMenuItem(emailAccountsMenuItem, emailAccountsLinkEvent);
            hasOptions = true;
         }

         if (ClientSessionState.getInstance().getUserInfo().isAutoAuth() && this._menuOptions > 0) {
            LinkEvent usernameLinkEvent = new LinkEvent(230, 49);
            NotificationMenuItem usernameMenuItem = new NotificationMenuItem(usernameLinkEvent.getLabel(), 30000, 30000);
            menu.add(usernameMenuItem);
            this.attachEventToMenuItem(usernameMenuItem, usernameLinkEvent);
            hasOptions = true;
         }

         if ((this._menuOptions & 2) != 0 && !ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
            BrandingInfo brandInfo = ClientSessionState.getInstance().getBrandingInfo();
            if (brandInfo.isDevicePINChangeEnabled()) {
               LinkEvent changeDeviceLinkEvent = new LinkEvent(4, 27);
               NotificationMenuItem changeDeviceMenuItem = new NotificationMenuItem(changeDeviceLinkEvent.getLabel(), 30000, 30000);
               menu.add(changeDeviceMenuItem);
               this.attachEventToMenuItem(changeDeviceMenuItem, changeDeviceLinkEvent);
               hasOptions = true;
            }
         }

         if ((this._menuOptions & 4) != 0) {
            UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
            if (userInfo.isUserPersonalizationEnabled()) {
               LinkEvent changeLanguageLinkEvent = new LinkEvent(173, 11);
               NotificationMenuItem changeLanguageMenuItem = new NotificationMenuItem(changeLanguageLinkEvent.getLabel(), 30000, 30000);
               menu.add(changeLanguageMenuItem);
               this.attachEventToMenuItem(changeLanguageMenuItem, changeLanguageLinkEvent);
               hasOptions = true;
            }
         }

         if ((this._menuOptions & 8) != 0 && !ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
            LinkEvent changePasswordLinkEvent = new LinkEvent(171, 28);
            NotificationMenuItem changePasswordMenuItem = new NotificationMenuItem(changePasswordLinkEvent.getLabel(), 30000, 30000);
            menu.add(changePasswordMenuItem);
            this.attachEventToMenuItem(changePasswordMenuItem, changePasswordLinkEvent);
            hasOptions = true;
         }

         if ((this._menuOptions & 16) != 0) {
            LinkEvent serviceBooksLinkEvent = new LinkEvent(172, 29);
            NotificationMenuItem serviceBooksMenuItem = new NotificationMenuItem(serviceBooksLinkEvent.getLabel(), 30000, 30000);
            menu.add(serviceBooksMenuItem);
            this.attachEventToMenuItem(serviceBooksMenuItem, serviceBooksLinkEvent);
            hasOptions = true;
         }

         if (hasOptions) {
            menu.add(MenuItem.separator(30001));
         }
      }
   }

   protected void setMenuOptions(int options) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected String getString(int key, Object[] params) {
      String text = ApplicationResources.getString(key);
      return params != null ? MessageFormat.format(text, params) : text;
   }
}
