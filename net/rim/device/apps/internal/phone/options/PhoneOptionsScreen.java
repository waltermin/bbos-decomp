package net.rim.device.apps.internal.phone.options;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.options.OptionsOrderingComparator;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

public class PhoneOptionsScreen extends MainScreen implements ListFieldCallback {
   private ListField _listField;
   private Object[] _phoneOptionsList;
   private boolean _ssRequestInProgress;
   private static final int MAX_NUMBER_OF_OPTIONS = 13;
   private static PhoneOptionsScreen _instance;
   private static final long REGISTERED_PHONE_OPTIONS_LIST = 120495896240967438L;

   public PhoneOptionsScreen(Object context) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      this.setTitle(new LabelField(PhoneResources.getString(151)));
      _instance = this;
      int index = 0;
      this._phoneOptionsList = new Object[13];
      this._phoneOptionsList[index++] = new GeneralPhoneOption(context);
      VoiceMailOption defaultVoicemailOption = new VoiceMailOption(context);
      this._phoneOptionsList[index++] = defaultVoicemailOption;
      this._phoneOptionsList[index++] = new CallLogsOption(context);
      if (SSManager.callBarringSupported()) {
         this._phoneOptionsList[index++] = new CallBarringOption(context);
      }

      if (SSManager.callForwardingSupported()) {
         this._phoneOptionsList[index++] = new CallForwardingOption(context);
      }

      if (SSManager.callWaitingSupported()) {
         this._phoneOptionsList[index++] = new CallWaitingOption(context);
      }

      this._phoneOptionsList[index++] = new SmartDialingOption(context);
      if (EnhancedAcousticsOption.hasDisplayableFields()) {
         this._phoneOptionsList[index++] = new EnhancedAcousticsOption(context);
      }

      if (SSManager.isFDNAvailable()) {
         Runnable fdnPhoneListRunnable = (Runnable)appReg.get(7742709116935051721L);
         if (fdnPhoneListRunnable != null) {
            this._phoneOptionsList[index++] = new RunnablePhoneOptionsItem(fdnPhoneListRunnable);
         }
      }

      if (CallAlertingOption.hasDisplayableFields()) {
         this._phoneOptionsList[index++] = new CallAlertingOption(context);
      }

      int regdVoicemailOptionCount = 0;
      OptionsProviderRegistration$OptionsProvider optionsProvider = (OptionsProviderRegistration$OptionsProvider)appReg.get(-5286549153189976910L);
      if (optionsProvider != null) {
         Vector voicemailOptions = optionsProvider.getOptionsItems();
         if (voicemailOptions != null) {
            regdVoicemailOptionCount = voicemailOptions.size();
            if (regdVoicemailOptionCount > 0) {
               Array.resize(this._phoneOptionsList, this._phoneOptionsList.length + regdVoicemailOptionCount);
            }

            for (int i = 0; i < regdVoicemailOptionCount; i++) {
               Object optionsItem = voicemailOptions.elementAt(i);
               if (optionsItem != null) {
                  this._phoneOptionsList[index++] = optionsItem;
               }
            }
         }
      }

      if (regdVoicemailOptionCount > 0) {
         defaultVoicemailOption.setIsDefaultVoicemailOption(true);
      }

      if (TTYOption.hasDisplayableFields()) {
         this._phoneOptionsList[index++] = new TTYOption(context);
      }

      if (HACOption.hasDisplayableFields()) {
         this._phoneOptionsList[index++] = new HACOption(context);
      }

      if (index < 13) {
         Array.resize(this._phoneOptionsList, index);
      }

      Object[] registeredOptionsList = (Object[])appReg.get(120495896240967438L);
      if (registeredOptionsList != null) {
         int registeredCount = registeredOptionsList.length;
         Array.resize(this._phoneOptionsList, index + registeredCount);
         System.arraycopy(registeredOptionsList, 0, this._phoneOptionsList, index, registeredCount);
         index += registeredCount;
      }

      Arrays.sort(this._phoneOptionsList, new OptionsOrderingComparator());
      this._listField = new ListField(index);
      this.add(this._listField);
      this._listField.setCallback(this);
      SSManager.resetSuppressMessageDialogs();
   }

   public static void registerOptionsListItem(SaveableMainScreenOptionsListItem optionsItem) {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         Object[] optionsList = (Object[])registry.get(120495896240967438L);
         if (optionsList == null) {
            optionsList = new Object[]{optionsItem};
            registry.put(120495896240967438L, optionsList);
         } else {
            int count = optionsList.length;
            Array.resize(optionsList, count + 1);
            optionsList[count] = optionsItem;
         }
      }
   }

   public static boolean deregisterOptionsListItem(SaveableMainScreenOptionsListItem optionsItem) {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         Object[] optionsList = (Object[])registry.get(120495896240967438L);

         for (int i = 0; i < optionsList.length; i++) {
            if (optionsList[i] == optionsItem) {
               if (i < optionsList.length - 1) {
                  System.arraycopy(optionsList, i + 1, optionsList, i, optionsList.length - i - 1);
               }

               Array.resize(optionsList, optionsList.length - 1);
               return true;
            }
         }

         return false;
      }
   }

   static void setSSRequestInProgress(boolean inProgress) {
      _instance._ssRequestInProgress = inProgress;
   }

   private void openSelectedItem() {
      Object item = this._phoneOptionsList[this._listField.getSelectedIndex()];
      if (!(item instanceof PhoneOptionsItem)) {
         if (item instanceof ActionProvider) {
            ((ActionProvider)item).perform(6099736323056465049L, null);
         }
      } else {
         ((PhoneOptionsItem)item).onOpen();
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int height = listField.getFont().getHeight();
      Object item = this._phoneOptionsList[index];
      if (item instanceof PaintProvider) {
         PaintProvider paintProvider = (PaintProvider)item;
         paintProvider.paint(graphics, 0, y, width, height, null);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._phoneOptionsList[index];
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   protected boolean keyDown(int key, int time) {
      return this._ssRequestInProgress ? true : super.keyDown(key, time);
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (this._ssRequestInProgress) {
         return true;
      } else if (key == 27) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (key == '\n') {
         this.openSelectedItem();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this.invokeAction(1);
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this._ssRequestInProgress) {
               return true;
            }

            this.openSelectedItem();
            return true;
         default:
            return false;
      }
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      return this._ssRequestInProgress ? true : super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (!attached) {
         SSManager.resetSuppressMessageDialogs();
      }

      super.onUiEngineAttached(attached);
   }
}
