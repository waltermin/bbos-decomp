package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassification;
import net.rim.device.internal.ui.component.PleaseWaitDialog;

public class SecureEmailOptionsItem extends SaveableMainScreenOptionsListItem {
   private SecureEmailOptionsModel _optionsModel;
   private SecureEmailFactory _factory;
   private static final long INJECTED_MESSAGE_CLASSIFICATIONS;

   public SecureEmailOptionsItem(SecureEmailFactory factory) {
      super(factory.getEncodingString(), 5294015899860238835L);
      this._factory = factory;
      ContextObject.put(super._context, 244, this._factory.getHelpContextString());
   }

   @Override
   protected void open() {
      this._optionsModel = this._factory.createOptionsModel(null);
      synchronized (Application.getEventLock()) {
         super.open();
      }
   }

   @Override
   protected void populateMainScreen(MainScreen mainScreen) {
      Field f = this._optionsModel.getField(null);
      if (f != null) {
         mainScreen.add(f);
      }
   }

   @Override
   protected boolean save() {
      if (!this._optionsModel.validate(null, null)) {
         return false;
      } else {
         return !this._optionsModel.grabDataFromField(null, null) ? false : super.save();
      }
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      boolean result = super.confirm(verb, context);
      if (result) {
         synchronized (this) {
            this.notifyAll();
         }

         this._optionsModel = null;
         super._mainScreen.deleteAll();
      }

      return result;
   }

   @Override
   protected Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Verb[] verbs = new Object[0];
      Verb defaultVerb = this._optionsModel.getVerbs(null, verbs);
      verbToMenu.addVerbs(verbs);
      return defaultVerb;
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1297303362:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1297303363:
         default:
            MessageClassification[] classifications = new Object[5];
            classifications[0] = (MessageClassification)(new Object("Unclassified/Non classé", "(U)/(N)", 0));
            classifications[1] = (MessageClassification)(new Object("Identified", "(I)", 1));
            classifications[2] = (MessageClassification)(new Object("Protected/Protégé", "(P)", 2));
            classifications[3] = (MessageClassification)(new Object("Secret/Secret", "(S)", 3));
            classifications[4] = (MessageClassification)(new Object("Confidential/Confidentiel", "(C)", 3));
            ApplicationRegistry.getApplicationRegistry().replace(-4543606409829069159L, classifications);
            Status.show("Default set of message classifications injected.");
            return true;
      }
   }

   public void doBlocking(Object context) {
      if (Application.getApplication().isEventThread()) {
         PleaseWaitDialog pwd = (PleaseWaitDialog)(new Object(new SecureEmailOptionsItem$BackgroundOptionsWorkerThread(this)));
         pwd.display();
      } else {
         this.perform(6099736323056465049L, context);

         try {
            synchronized (this) {
               this.wait();
            }
         } finally {
            return;
         }
      }
   }
}
