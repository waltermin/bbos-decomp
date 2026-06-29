package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.vm.Array;

final class MemoryCleanerOptionsItem extends SaveableMainScreenOptionsListItem implements ListFieldCallback, FieldChangeListener {
   private BooleanChoiceField _statusField;
   private TimeChoiceField _idleTimeoutField;
   private LabelField _registeredCleanersLabelField;
   private BooleanChoiceField _cleanWhenHolsteredField;
   private BooleanChoiceField _cleanWhenIdleField;
   private BooleanChoiceField _showAppOnRibbonField;
   private ListField _listField;
   private MemoryCleanerListener[] _listeners;
   MemoryCleanerManager _manager = MemoryCleanerManager.getInstance();
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7878136828847559798L, "net.rim.device.apps.internal.resource.MemoryCleaner");
   private static final int LIST_INDENT_PIXELS;

   public MemoryCleanerOptionsItem() {
      super(_rb.getString(3), 5294015899860238835L);
      ContextObject.put(super._context, 244, "net_rim_bb_secureemail_help/memory_cleaning");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      boolean forceCleanWhenHolstered = ITPolicy.getBoolean(27, 3, false);
      this._cleanWhenHolsteredField = (BooleanChoiceField)(new Object(
         _rb.getString(4), 0, forceCleanWhenHolstered || this._manager.getCleanWhenHolstered(), 268435456
      ));
      this._cleanWhenHolsteredField.setEditable(!forceCleanWhenHolstered);
      boolean forceCleanWhenIdle = ITPolicy.getBoolean(27, 2, false);
      this._cleanWhenIdleField = (BooleanChoiceField)(new Object(_rb.getString(5), 0, forceCleanWhenIdle || this._manager.getCleanWhenIdle(), 268435456));
      this._cleanWhenIdleField.setEditable(!forceCleanWhenIdle);
      long[] times = new long[]{
         60000L,
         120000L,
         300000L,
         600000L,
         1200000L,
         1800000L,
         3600000L,
         5138132777706979740L,
         936748722662541837L,
         2161727822518044745L,
         6631966375936L,
         548820485376L,
         7309979312151950379L,
         7589726930713801825L,
         3539939693527328109L,
         3472330427675660064L,
         4195438653875494963L,
         3472333793734438960L,
         504403161249626338L,
         75104371985959284L,
         70486318372490256L,
         32467789973880832L,
         77136238247807744L,
         288230380387884498L,
         -8092687054312488601L,
         -864691128364827637L,
         6248140268390925385L,
         -152204260211880367L,
         799933353438661807L,
         903623242139265581L,
         -2264856781434862796L,
         7892278667395119059L,
         7837619582284490586L,
         -5551613583307027212L,
         -567976619093484949L,
         -6153237705648891774L,
         -2358613302782245388L,
         2750810775769356529L,
         5953940653667058589L,
         690429278306045661L,
         4227868481076461121L,
         -8239309115511116664L,
         -3273925405716222400L,
         4739171045197733032L,
         -2155680665271832714L,
         4807216810702068197L,
         -3204108936727230804L,
         2368220470422240017L,
         -6969754995301336514L,
         7875214882303564886L,
         -3429141754941728619L,
         4235535529506643368L,
         8829119687207333079L,
         -3996651156963418970L,
         -233618424128250045L,
         -6888933387085454925L
      };
      long maxTimeout = (long)ITPolicy.getInteger(27, 1, 60) * 60000;
      this._idleTimeoutField = (TimeChoiceField)(new Object(_rb.getString(6), 268435456));
      this._idleTimeoutField.setTimeChoicesIncludeMaxTimeChoice(times, 0, maxTimeout);
      this._idleTimeoutField.setSelectedTimeInMillis(this._manager.getIdleTimeout(), false);
      boolean showAppOnRibbon = this._manager.getShowAppOnRibbon();
      this._showAppOnRibbonField = (BooleanChoiceField)(new Object(_rb.getString(13), 0, showAppOnRibbon));
      this._showAppOnRibbonField.setEditable(true);
      MemoryCleanerManager manager = MemoryCleanerManager.getInstance();
      MemoryCleanerListener[] tempListeners = manager.getListeners();
      int numTempListeners = tempListeners.length;
      this._listeners = new Object[numTempListeners];
      int numNonNullListeners = 0;

      for (int i = 0; i < numTempListeners; i++) {
         if (tempListeners[i].getDescription() != null) {
            this._listeners[numNonNullListeners] = tempListeners[i];
            numNonNullListeners++;
         }
      }

      Array.resize(this._listeners, numNonNullListeners);
      Font defaultFont = Font.getDefault();
      Font boldFont = defaultFont.derive(defaultFont.getStyle() | 1);
      this._registeredCleanersLabelField = (LabelField)(new Object(_rb.getString(9), 64));
      this._registeredCleanersLabelField.setFont(boldFont);
      this._listField = (ListField)(new Object(this._listeners.length));
      this._listField.setCallback(this);
      this._statusField = (BooleanChoiceField)(new Object(_rb.getString(16), 2, this._manager.enabled(), 268435456));
      if (this._manager.enabled()) {
         this._statusField.setEditable(this._manager.userEnabled());
         this._statusField.setChangeListener(this);
         mainScreen.add(this._statusField);
         this.showEnabledOptions(mainScreen);
      } else {
         this._statusField.setEditable(true);
         this._statusField.setChangeListener(this);
         mainScreen.add(this._statusField);
      }
   }

   private final void showEnabledOptions(MainScreen mainScreen) {
      mainScreen.add((Field)(new Object()));
      mainScreen.add(this._cleanWhenHolsteredField);
      mainScreen.add(this._cleanWhenIdleField);
      mainScreen.add(this._idleTimeoutField);
      mainScreen.add(this._showAppOnRibbonField);
      mainScreen.add((Field)(new Object()));
      mainScreen.add(this._registeredCleanersLabelField);
      mainScreen.add(this._listField);
   }

   @Override
   protected final boolean save() {
      if (this._manager.userEnabled() || !this._manager.enabled()) {
         this._manager.setUserCleanEnabled(this._statusField.isAffirmative());
      }

      this._manager.setIdleTimeout(this._idleTimeoutField.getSelectedTimeInMillis());
      this._manager.setCleanWhenHolstered(this._cleanWhenHolsteredField.isAffirmative());
      this._manager.setCleanWhenIdle(this._cleanWhenIdleField.isAffirmative());
      this._manager.setShowAppOnRibbon(this._showAppOnRibbonField.isAffirmative());
      return super.save();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._statusField) {
         super._mainScreen.deleteAll();
         super._mainScreen.add(this._statusField);
         if (this._statusField.isAffirmative()) {
            this.showEnabledOptions(super._mainScreen);
         }
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new MemoryCleanerOptionsItem$CleanNowVerb(_rb.getString(8)));
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Field field = super._mainScreen.getFieldWithFocus();
      if (field != this._listField) {
         return null;
      }

      int selectedItem = this._listField.getSelectedIndex();
      if (selectedItem < 0) {
         return null;
      }

      MemoryCleanerOptionsItem$CleanItemVerb cleanItemVerb = new MemoryCleanerOptionsItem$CleanItemVerb(this, selectedItem);
      verbToMenu.addVerb(cleanItemVerb);
      return cleanItemVerb;
   }

   private static final String getListenerDescription(MemoryCleanerListener listener) {
      try {
         return listener.getDescription();
      } finally {
         ;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (listField == this._listField && index >= 0 && index < this._listeners.length) {
         graphics.drawText(getListenerDescription(this._listeners[index]), 5, y, 64, width);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return listField == this._listField && index >= 0 && index < this._listeners.length ? this._listeners[index] : null;
   }
}
