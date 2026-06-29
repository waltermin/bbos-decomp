package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.ui.CommonResources;

final class MapsOptionsScreen extends MainScreenOptionsListItem {
   private ObjectChoiceField _defaultService;
   public static final ResourceBundleFamily _resources = ResourceBundle.getBundle(6514774203079918781L, "net.rim.device.apps.internal.lbs.LBS");

   static final void registerOptionsProvider() {
      OptionsProviderRegistration.registerOptionsProvider(new MapsOptionsScreen$MapsOptionsProvider());
   }

   MapsOptionsScreen() {
      super(_resources.getString(7), null, -1514481539159318190L);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._defaultService = new ObjectChoiceField(_resources.getString(484), null);
      this._defaultService.setEmptyString(CommonResources.getResourceBundle(), 9061);
      mainScreen.add(this._defaultService);
      this.getMapsServiceRecords();
   }

   private final void getMapsServiceRecords() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] serviceRecords = sb.findRecordsByCid("LbsConfig");
      int count = serviceRecords.length;
      MapsOptionsScreen$MapsServiceRecord[] mapSR = new MapsOptionsScreen$MapsServiceRecord[0];
      String selection = LBSOptions.getString(7706156913208477511L, "");
      int selectionIx = -1;

      for (int i = 0; i < count; i++) {
         ServiceRecord sr = serviceRecords[i];
         if (sr.getType() == 0) {
            Arrays.add(mapSR, new MapsOptionsScreen$MapsServiceRecord(this, sr, null));
            if (StringUtilities.strEqualIgnoreCase(sr.getName(), selection, 1701707776)) {
               selectionIx = mapSR.length - 1;
            }
         }
      }

      this._defaultService.setChoices(mapSR);
      if (selectionIx > -1) {
         this._defaultService.setSelectedIndex(selectionIx);
      }
   }

   @Override
   protected final boolean save() {
      boolean changed = false;
      if (this._defaultService.isDirty()) {
         String nameCurrent = LBSOptions.getString(7706156913208477511L, "");
         MapsOptionsScreen$MapsServiceRecord sr = (MapsOptionsScreen$MapsServiceRecord)this._defaultService.getChoice(this._defaultService.getSelectedIndex());
         String nameSelected = sr.toString();
         if (!StringUtilities.strEqualIgnoreCase(nameSelected, nameCurrent, 1701707776)) {
            changed = true;
            MapsServices.getInstance().checkAppData(sr._sr);
         }
      }

      return changed;
   }
}
