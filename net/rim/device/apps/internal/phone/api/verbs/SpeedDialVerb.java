package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.quickcontact.QuickContactScreen;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneListItem;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class SpeedDialVerb extends Verb {
   private Object _speedDialData;
   private char _initialSpeedDialKey;
   private int _initialKeyCode;
   private int _resourceId;

   public SpeedDialVerb(Object data, int action, int ordering) {
      this(data, action, ordering, '\u0000');
   }

   public SpeedDialVerb(Object data, int resourceId, int ordering, char initialKey) {
      this(data, resourceId, ordering, initialKey, 0);
   }

   public SpeedDialVerb(Object data, int resourceId, int ordering, char initialKey, int keycode) {
      super(ordering, PhoneResources.getResourceBundle(), resourceId);
      this._resourceId = resourceId;
      this._speedDialData = data;
      this._initialSpeedDialKey = initialKey;
      this._initialKeyCode = keycode;
   }

   @Override
   public final Object invoke(Object parameter) {
      boolean programmaticInvocation = ContextObject.getFlag(parameter, 64);
      switch (this._resourceId) {
         case 6072:
            if (this._speedDialData instanceof PhoneListItem) {
               PhoneListItem pli = (PhoneListItem)this._speedDialData;
               if (this._initialSpeedDialKey == 0 && !PhoneUtilities.isQwertyReducedKeyboard()) {
                  String itemString = pli.toString();
                  if (!PhoneUtilities.isEmptyString(itemString)) {
                     char firstChar = CharacterUtilities.toUpperCase(itemString.charAt(0), 1701707776);
                     if (firstChar >= 'A' && firstChar <= 'Z' && !QuickContactList.getInstance().isQuickContactKeyInUse(firstChar)) {
                        this._initialSpeedDialKey = firstChar;
                     }
                  }
               }
            }

            assignSpeedDial(this._initialSpeedDialKey, this._initialKeyCode, this._speedDialData, !programmaticInvocation);
            return null;
         case 6073:
            QuickContactList.getInstance().removeQuickContactKey(this._initialSpeedDialKey);
            return null;
         case 6094:
            if (this._speedDialData instanceof PhoneListItem) {
               PhoneListItem phoneListItem = (PhoneListItem)this._speedDialData;
               QuickContactScreen.show(phoneListItem.getSpeedDialKey());
               return null;
            } else {
               QuickContactScreen.show();
            }
         default:
            return null;
      }
   }

   public static final void assignSpeedDial(char key, Object data) {
      assignSpeedDial(key, 0, data, false);
   }

   public static final void assignSpeedDial(char key, int keycode, Object data, boolean menuInvoked) {
      assignSpeedDial(key, keycode, data, menuInvoked, false);
   }

   public static final void assignSpeedDial(char key, int keycode, Object data, boolean menuInvoked, boolean showSpeedDialScreen) {
      CallerIDInfo cidi = null;
      boolean createdCidi = false;
      if (!(data instanceof PhoneListItem)) {
         if (!(data instanceof CallerIDInfo)) {
            if (data instanceof Object) {
               cidi = PhoneUtilities.createCallerIDInfo((RIMModel)data, 24, 0, null);
               createdCidi = true;
            }
         } else {
            cidi = (CallerIDInfo)data;
         }
      } else {
         cidi = ((PhoneListItem)data).getCallerIDInfo();
      }

      int confirmationKeycode = keycode;
      if (menuInvoked) {
         confirmationKeycode = 0;
      }

      if (cidi != null) {
         if (!AddSpeedDialConfirmationDialog.ask(PhoneResources.getString(6072), confirmationKeycode, cidi)) {
            return;
         }

         CallerIDInfo cidiCopy = null;
         if (!createdCidi) {
            cidiCopy = new CallerIDInfo(cidi, false);
         } else {
            cidiCopy = cidi;
         }

         Object item = QuickContactList.getInstance().createNewQuickContactItem(key, 4046126975918546978L, cidiCopy);
         if (item instanceof Object) {
            QuickContactItem qcItem = (QuickContactItem)item;
            if (keycode == 0 || menuInvoked) {
               QuickContactScreen.show(qcItem, 1);
               return;
            }

            QuickContactList.getInstance().add(qcItem);
            if (showSpeedDialScreen) {
               QuickContactScreen.show(key);
            }
         }
      }
   }
}
