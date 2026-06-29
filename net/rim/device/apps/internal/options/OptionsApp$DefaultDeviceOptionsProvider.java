package net.rim.device.apps.internal.options;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.options.IEuropeanOptions;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.internal.options.items.AboutOptionsItem;
import net.rim.device.apps.internal.options.items.AppPermissionsOptionsItem;
import net.rim.device.apps.internal.options.items.ApplicationOptionsItem;
import net.rim.device.apps.internal.options.items.AutoOnOffOptionsItem;
import net.rim.device.apps.internal.options.items.AutoTextOptionsItem;
import net.rim.device.apps.internal.options.items.DateTimeOptionsItem;
import net.rim.device.apps.internal.options.items.FirewallOptionsItem;
import net.rim.device.apps.internal.options.items.LocalizationOptionsItem;
import net.rim.device.apps.internal.options.items.LocationServicesOptionsItem;
import net.rim.device.apps.internal.options.items.OwnerOptionsItem;
import net.rim.device.apps.internal.options.items.SIMCardOptionsItem;
import net.rim.device.apps.internal.options.items.ScreenKeyboardOptionsItem;
import net.rim.device.apps.internal.options.items.SecurityOptionsItem;
import net.rim.device.apps.internal.options.items.SpellCheckOptionsItem;
import net.rim.device.apps.internal.options.items.StatusOptionsItem;
import net.rim.device.apps.internal.options.items.StorageModeOptionsItem;
import net.rim.device.apps.internal.options.items.ThemeOptionsItem;
import net.rim.device.apps.internal.options.items.network.NetworkOptionsItem;
import net.rim.tid.awt.im.InputContext;

final class OptionsApp$DefaultDeviceOptionsProvider implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector optionsItems = new Vector();
      optionsItems.addElement(new AboutOptionsItem());
      optionsItems.addElement(new ApplicationOptionsItem());
      optionsItems.addElement(new AutoOnOffOptionsItem());
      optionsItems.addElement(new AutoTextOptionsItem());
      optionsItems.addElement(new DateTimeOptionsItem());
      optionsItems.addElement(new FirewallOptionsItem());
      if (RadioInfo.areWAFsSupported(-5)) {
         optionsItems.addElement(new NetworkOptionsItem());
      }

      optionsItems.addElement(new OwnerOptionsItem());
      optionsItems.addElement(new ScreenKeyboardOptionsItem());
      optionsItems.addElement(new SecurityOptionsItem());
      optionsItems.addElement(new StatusOptionsItem());
      optionsItems.addElement(new AppPermissionsOptionsItem());
      if (SIMCard.isSupported()) {
         optionsItems.addElement(new SIMCardOptionsItem());
      }

      if (!DeviceInfo.isBatteryRemovable()) {
         optionsItems.addElement(new StorageModeOptionsItem());
      }

      if (Locale.getAvailableLocales().length > 2 || Locale.getAvailableInputLocales().length > 1) {
         optionsItems.addElement(new LocalizationOptionsItem());
      }

      if (LocationServicesOptionsItem.isAvailable()) {
         optionsItems.addElement(LocationServicesOptionsItem.createInstance());
      }

      if (ThemeManager.getCount() > 1) {
         optionsItems.addElement(new ThemeOptionsItem());
      }

      if (SpellCheckOptionsItem.isSpellCheckingAvailable()) {
         optionsItems.addElement(new SpellCheckOptionsItem());
      }

      boolean isSureType = InputContext.getInstance().getActiveInputMethodID() == 4096;
      if (isSureType) {
         IEuropeanOptions item = null;

         label69:
         try {
            item = (IEuropeanOptions)Class.forName("net.rim.device.apps.internal.options.items.FastEuropean.FastEuropeanOptionsItem").newInstance();
         } finally {
            break label69;
         }

         if (item != null) {
            item.init(2);
            optionsItems.addElement(item);
         }
      }

      return optionsItems;
   }
}
