package net.rim.device.apps.internal.options.items;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.VoiceDataUsage;
import net.rim.vm.Array;

public final class StatusOptionsItem extends MainScreenOptionsListItem implements ListFieldCallback, RadioStatusListener, SystemListener {
   private ListField _listField;
   private final String DEVICE_SELF_TEST_MODULE_NAME = "net_rim_bb_device_selftest";
   private static Object[] _listItems;
   private static final int SIGNAL_LEVEL = 0;
   private static final int BATTERY_LEVEL = 1;

   public StatusOptionsItem() {
      super(OptionsResources.getString(400), new Object(2, 3));
   }

   private static final Object[] getSerials(int waf) {
      Vector v = (Vector)(new Object());
      if ((waf & 1) != 0) {
         v.addElement(new StatusSerialNumber(true, 1));
      }

      if ((waf & 2) != 0) {
         v.addElement(new StatusSerialNumber(false, 2));
         v.addElement(new StatusSerialNumber(true, 2));
      }

      if ((waf & 8) != 0) {
         v.addElement(new StatusSerialNumber(true, 8));
      }

      if ((waf & 4) != 0) {
         v.addElement(new StatusSerialNumber(true, 4));
      }

      Object[] list = new Object[v.size()];
      v.copyInto(list);
      return list;
   }

   private static final Object[] createStatusItemList() {
      int numItems = 7;
      int supportedWafs = RadioInfo.getSupportedWAFs();
      boolean isIDENOrWLAN = (supportedWafs & 12) != 0;
      Object[] serials = getSerials(supportedWafs);
      numItems += serials.length;
      if (isIDENOrWLAN) {
         numItems++;
      }

      Object[] listItems = new Object[numItems];
      int ix = 0;
      listItems[ix++] = new StatusSignalLevel();
      listItems[ix++] = new StatusBatteryLevel();
      listItems[ix++] = new StatusFile(false);
      listItems[ix++] = new StatusFile(true);
      listItems[ix++] = new StatusPIN();
      System.arraycopy(serials, 0, listItems, ix, serials.length);
      ix += serials.length;
      if (isIDENOrWLAN) {
         listItems[ix++] = new StatusIP((supportedWafs & 8) != 0 ? 8 : 4);
      }

      Array.resize(listItems, ix);
      return listItems;
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (_listItems == null) {
         _listItems = createStatusItemList();
      }
   }

   @Override
   protected final void open() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private final void populateMenuFromRepository(MainScreen mainScreen) {
      VerbRepository statusRepository = VerbRepository.getVerbRepository(-2430495530417912432L);
      Verb[] verbs = statusRepository.getVerbs(null);
      if (verbs != null) {
         for (int i = verbs.length - 1; i >= 0; i--) {
            Verb verbToAdd = verbs[i];
            if (verbToAdd != null) {
               mainScreen.addMenuItem((MenuItem)(new Object(verbToAdd, Integer.MAX_VALUE)));
            }
         }
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      ObjectListField olf = (ObjectListField)(new Object());
      olf.set(_listItems);
      olf.setCallback(this);
      mainScreen.add(olf);
      this._listField = olf;
      Application UI = Application.getApplication();
      UI.addSystemListener(this);
      UI.addRadioListener(this);
      this.populateMenuFromRepository(mainScreen);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new StatusOptionsItem$DatabaseStatisticsVerb());
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      StatusListItem item = (StatusListItem)_listItems[index];
      int leftWidth = graphics.drawText(item.getDisplayName(), 0, y, 70, width);
      graphics.drawText(item.getDisplayValue(), leftWidth, y, 69, width - leftWidth);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return _listItems[index];
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void signalLevel(int level) {
      this._listField.invalidate(0);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      this._listField.invalidate(1);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (!super.confirm(verb, context)) {
         return false;
      }

      Application UI = Application.getApplication();
      UI.removeSystemListener(this);
      UI.removeRadioListener(this);
      return true;
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1112889682:
            this.addBuyersRemorseFields();
            return true;
         case 1413829460:
            this.launchSelfTest();
            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1128547153:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1128547154:
         default:
            this.resetBuyersRemorseFields();
            return true;
      }
   }

   private final void launchSelfTest() {
      ApplicationDescriptor testAppDescriptor = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle("net_rim_bb_device_selftest"))[0];
      if (testAppDescriptor != null) {
         try {
            ApplicationManager.getApplicationManager().runApplication(testAppDescriptor, true);
         } finally {
            return;
         }
      }
   }

   private final void addBuyersRemorseFields() {
      int itemCount = _listItems.length;
      if (itemCount != 0 && _listItems[itemCount - 1] instanceof StatusVoiceDataUsage) {
         if (itemCount >= 2) {
            this._listField.invalidate(itemCount - 2);
            this._listField.invalidate(itemCount - 1);
         }
      } else {
         Array.resize(_listItems, itemCount + 2);
         _listItems[itemCount] = new StatusVoiceDataUsage(true);
         _listItems[itemCount + 1] = new StatusVoiceDataUsage(false);
         ((ObjectListField)this._listField).set(_listItems);
      }
   }

   private final void resetBuyersRemorseFields() {
      VoiceDataUsage.reset();
      int itemCount = _listItems.length;
      if (itemCount >= 2 && _listItems[itemCount - 1] instanceof StatusVoiceDataUsage) {
         this._listField.invalidate(itemCount - 2);
         this._listField.invalidate(itemCount - 1);
      }
   }
}
