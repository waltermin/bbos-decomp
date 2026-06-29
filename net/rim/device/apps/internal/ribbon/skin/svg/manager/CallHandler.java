package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class CallHandler extends Handler {
   PhoneCallModelImpl[] _callModel = new PhoneCallModelImpl[this.MaxEntries];
   private static final int NUM_ENTRIES_PER_LOG = 2;

   CallHandler(ModelInteractorImpl mi, UiApplication app) {
      super(mi, app);
   }

   public boolean callSelected(int itemInFocus) {
      PhoneCallModelImpl callModel = this._callModel[itemInFocus];
      if (callModel != null) {
         CallerIDInfo callerInfo = callModel.getCallerIDInfo();
         if (callerInfo != null) {
            PersistableRIMModel numberModel = callerInfo.getNumber();
            if (numberModel != null) {
               String number = numberModel.toString();
               if (number != null && !number.equals("")) {
                  Invoke.invokeApplication(4, new PhoneArguments("call", number));
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public void invoke(int index) {
      if (index >= 0 && index < super.MaxEntries) {
         PhoneCallModelImpl callModel = this._callModel[index];
         if (callModel != null) {
            ContextObject context = new ContextObject();
            PhoneUtilities.setPrivateFlag(context, 38);
            PhoneUtilities.setPrivateFlag(context, 74);
            PhoneUtilities.setPrivateFlag(context, 72);
            Verb[] verbs = new Verb[0];
            Verb defaultVerb = callModel.getVerbs(context, verbs);
            SystemEnabledMenu menu = new SystemEnabledMenu(context, null);
            menu.add(verbs);
            menu.setDefault(defaultVerb);
            menu.coalesce(-3072555018635390988L, null);
            menu.show();
         }
      }
   }

   @Override
   public void update(boolean forceScreenUpdate) {
      if (super.MaxEntries >= 1) {
         synchronized (FolderHierarchies.getLockObject()) {
            label100: {
               synchronized (super._modelInteractor) {
                  for (int i = 0; i < super.MaxEntries; i++) {
                     this._callModel[i] = null;
                     this.setDisplayable("missedcall" + (i + 1) + "hotspot", false);
                  }

                  if (super._nodes != null) {
                     for (int i = super._nodes.length - 1; i >= 0; i--) {
                        if (super._nodes[i] != null) {
                           super._nodes[i].setString(super.BLANK);
                        }
                     }
                  }

                  PersistedSortedCollection missedCalls = PhoneFolders.getContainedItems(7042951934619290849L);
                  int numCalls = missedCalls.size();
                  if (numCalls > 0) {
                     int counter = 0;
                     boolean moreNew = true;
                     boolean somethingNew = false;

                     while (moreNew && numCalls > 0 && counter < super.MaxEntries) {
                        PhoneCallModelImpl callModel = (PhoneCallModelImpl)missedCalls.getAt(numCalls - 1);
                        if (!callModel.perform(-6072303684925088654L, null)) {
                           moreNew = false;
                        } else {
                           somethingNew = true;
                           this._callModel[counter] = callModel;
                           if (super._nodes != null) {
                              long time = callModel.getTimeStamp();
                              if (super._nodes[counter * 2] != null) {
                                 super._nodes[counter * 2].setString(this.formattedTime(time).toCharArray());
                              }

                              String name = callModel.getCallerIDInfo().getDisplayableString(0);
                              if (super._nodes[counter * 2 + 1] != null) {
                                 super._nodes[counter * 2 + 1].setString(name.toCharArray());
                              }
                           }

                           this.setDisplayable("missedcall" + (counter + 1) + "hotspot", true);
                           counter++;
                           numCalls--;
                        }
                     }

                     if (somethingNew) {
                        this.updateAlternateName();
                     }
                     break label100;
                  }
               }

               return;
            }
         }

         if (forceScreenUpdate) {
            super._application.repaint();
         }
      }
   }
}
