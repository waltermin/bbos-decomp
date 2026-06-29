package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

final class CallForwardingOption extends OptionsListItem implements PhoneOptionsItem, QuerySSOptions$Listener {
   private boolean _ssTestingHackEnabled = PhoneUtilities.getDebugFlag(5709883646465653063L, false);
   private int[] _forwardingStatusFlags;
   static final int[] CSP_TYPES_MAP = new int[]{
      128, 64, 32, 16, -804782076, 6554259, 8192880, -804651003, 154, 155, 146, 147, 148, -804782058, 21824438, 21824888
   };
   static final int[] SS_TYPES_MAP = new int[]{33, 41, 42, 43, 51, -804651005, 58, 17, 106, -804651007, 64, -804651007, 65, -804782078, 19661400, -804782066};
   private static final int NONE;
   private static final int GSM;
   private static final int CDMA;
   private static final int IDEN;
   private static final int[][][] MIN_MAX_FWDING_NUMBER_LENGTHS = new int[][][]{
      (int[][])({0, 0, -804651006, 0, 1, -804651003, 0, 1}),
      (int[][])({3, 28, -804651004, 3, 58, 4, 17, -804651007}),
      (int[][])({3, 20, -804651006, 3, 28, -804651004, 3, 58}),
      (int[][])({10, 20, -804651003, 16, 8, 128, 64, 32})
   };
   protected static final int[] _forwardingTypesMap = new int[]{
      33, 41, 42, 43, 51, -804651005, 58, 17, 106, -804651007, 64, -804651007, 65, -804782078, 19661400, -804782066
   };

   private static final int getNetworkIndex() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
         case 6:
            return 0;
         case 3:
         default:
            return 1;
         case 4:
            return 2;
         case 5:
            return 3;
         case 7:
            return 1;
      }
   }

   static final int mapForwardingType(int friendlyForwardingType) {
      return _forwardingTypesMap[friendlyForwardingType];
   }

   static final int[] getMinMaxForwardingNumberLengths() {
      return (int[])MIN_MAX_FWDING_NUMBER_LENGTHS[getNetworkIndex()];
   }

   public CallForwardingOption(Object context) {
      super(PhoneResources.getString(191));
   }

   private final int[] getProvisionedTypes(int[] fwdingStatusFlags) {
      int[] types = new int[4];
      int count = 0;

      for (int i = 0; i < this._forwardingStatusFlags.length; i++) {
         if (SSManager.isSSOptionProvisioned(this._forwardingStatusFlags[i])) {
            types[count++] = i;
         }
      }

      if (count < 4) {
         Array.resize(types, count);
      }

      return types;
   }

   private final Screen getScreen() {
      MainScreen screen = null;
      if (PhoneUtilities.cdmaWAFActive()) {
         int[] provisionedTypes = this.getProvisionedTypes(this._forwardingStatusFlags);
         if (provisionedTypes.length > 1) {
            screen = new CDMACallForwardingScreen(this._forwardingStatusFlags, provisionedTypes);
         }
      }

      if (screen == null) {
         screen = new CallForwardingScreen(this._forwardingStatusFlags);
      }

      screen.setTitle(PhoneResources.getString(191));
      return screen;
   }

   @Override
   protected final void open() {
      Screen screen = this.getScreen();
      if (screen != null) {
         UiApplication.getUiApplication().pushModalScreen(screen);
      }
   }

   @Override
   public final void onQueryFinished(int[] ssOptionFlags, boolean success) {
      if (!success) {
         if (ssOptionFlags != null && ssOptionFlags.length > 0 && ssOptionFlags[0] == 100) {
            Application.getApplication().invokeLater(new CallForwardingOption$1(this));
         }
      } else {
         this._forwardingStatusFlags = ssOptionFlags;
         boolean available = false;

         for (int i = 0; i < this._forwardingStatusFlags.length; i++) {
            if ((this._forwardingStatusFlags[i] & 1) != 0) {
               available = true;
               break;
            }
         }

         if (!available) {
            Application.getApplication().invokeLater(new CallForwardingOption$2(this));
         } else {
            Application.getApplication().invokeLater(new CallForwardingOption$3(this));
         }
      }
   }

   @Override
   public final void onOpen() {
      if (RadioInfo.getState() != 1) {
         synchronized (Application.getEventLock()) {
            Dialog.alert(((StringBuffer)(new Object())).append(CommonResources.getString(9153)).append(' ').append(PhoneResources.getString(143)).toString());
         }
      } else if (this._ssTestingHackEnabled) {
         this.open();
      } else {
         int[] forwardingTypes = ForwardingTypes.ALL_TYPES;
         int[] ssTypesMap = SS_TYPES_MAP;
         int[] cspTypesMap = CSP_TYPES_MAP;
         new QuerySSOptions(forwardingTypes, ssTypesMap, 0, cspTypesMap, this).start();
      }
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 6000;
   }
}
