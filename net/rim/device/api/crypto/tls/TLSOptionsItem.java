package net.rim.device.api.crypto.tls;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.cldc.io.ssl.SSLOptionsRegistration;
import net.rim.device.cldc.io.ssl.TLSOptionStore;

final class TLSOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private boolean _deviceSideTLSExists;
   private TLSOptionStore _tlsOptions;
   private ChoiceField _defaultImplField;
   private FieldProvider[] _fieldProviders;
   private Field[] _fields;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");
   private static final int PROXY_INDEX;
   private static final int DEVICE_INDEX;

   public TLSOptionsItem() {
      super(_rb.getFamily(), 32, 5294015899860238835L);
      ContextObject.put(super._context, 244, new Object(32785));
      this._tlsOptions = TLSOptionStore.getOptions();
      this._deviceSideTLSExists = SSLOptionsRegistration.doesDeviceSideExist();
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean result = super.confirm(verb, context);
      if (!result) {
         return false;
      }

      super._mainScreen.deleteAll();

      for (int i = 0; i < this._fields.length; i++) {
         this._fields[i] = null;
         this._fieldProviders[i] = null;
      }

      return result;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      mainScreen.deleteAll();
      int numProviders = this._deviceSideTLSExists ? 2 : 1;
      this._fieldProviders = new Object[numProviders];
      this._fieldProviders[0] = new ProxyTLSOptionsProvider();
      if (this._deviceSideTLSExists) {
         this._fieldProviders[1] = new DeviceTLSOptionsProvider();
      }

      this._fields = new Object[numProviders];
      String[] tlsChoices = new Object[this._fieldProviders.length];

      for (int i = 0; i < numProviders; i++) {
         Field f = this._fieldProviders[i].getField(null);
         if (f != null) {
            this._fields[i] = f;
            tlsChoices[i] = this._fieldProviders[i].toString();
         }
      }

      boolean deviceSideOnly = ITPolicy.getBoolean(28, 9, false);
      int defaultImplementation = deviceSideOnly ? 2 : this._tlsOptions.getDefaultImplementation();
      int defaultImplIndex = defaultImplementation == 1 ? 0 : 1;
      this._defaultImplField = (ChoiceField)(new Object(_rb.getString(36), tlsChoices, defaultImplIndex, 268435456));
      this._defaultImplField.setEditable(!deviceSideOnly);
      this._defaultImplField.setChangeListener(this);
      mainScreen.add(this._defaultImplField);
      mainScreen.add((Field)(new Object()));
      mainScreen.add(this._fields[defaultImplIndex]);
   }

   @Override
   protected final boolean save() {
      int defaultIndex = this._defaultImplField.getSelectedIndex();
      int defaultImplementation = defaultIndex == 0 ? 1 : 2;
      this._tlsOptions.setDefaultImplementation(defaultImplementation);

      for (int i = 0; i < this._fieldProviders.length; i++) {
         this._fieldProviders[i].grabDataFromField(this._fields[i], null);
      }

      return super.save();
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      int defaultIndex = this._defaultImplField.getSelectedIndex();
      FieldProvider var10000 = this._fieldProviders[defaultIndex];
      if (!(this._fieldProviders[defaultIndex] instanceof Object)) {
         return null;
      }

      VerbProvider verbProvider = (VerbProvider)var10000;
      Verb[] verbs = new Object[0];
      Verb defaultVerb = verbProvider.getVerbs(null, verbs);
      verbToMenu.addVerbs(verbs);
      return defaultVerb;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._defaultImplField) {
         int newFieldIndex = this._defaultImplField.getSelectedIndex();
         int oldFieldIndex = 1 - newFieldIndex;
         super._mainScreen.delete(this._fields[oldFieldIndex]);
         super._mainScreen.add(this._fields[newFieldIndex]);
      }
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
   public final boolean keyChar(char key, int time, int status) {
      if (!super.keyChar(key, time, status)) {
         int defaultIndex = this._defaultImplField.getSelectedIndex();
         FieldProvider var10000 = this._fieldProviders[defaultIndex];
         if (this._fieldProviders[defaultIndex] instanceof Object) {
            KeyListener keyListener = (KeyListener)var10000;
            return keyListener.keyChar(key, time, status);
         }
      }

      return true;
   }
}
