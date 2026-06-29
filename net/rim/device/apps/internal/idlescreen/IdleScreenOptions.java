package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.idlescreen.IdleScreenOptionsProvider;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.TimeChoiceField;

public final class IdleScreenOptions extends IdleScreenOptionsProvider {
   private BooleanChoiceField _enabledField;
   private TimeChoiceField _timeoutField;
   private long[] _timeouts = new long[]{
      60000L,
      120000L,
      300000L,
      600000L,
      1200000L,
      1800000L,
      3600000L,
      4993736432481992959L,
      729594190584873472L,
      15812378781615360L,
      -1152691706676641792L,
      3386734411868274555L,
      72058693666998016L,
      1099511628032L,
      842225910203093L,
      -72056906674077688L,
      173878083583L,
      96029147066794611L,
      25356937176547348L,
      -2594072363767169014L,
      3675631095220537069L,
      3956130801678216706L,
      -69241804889784314L,
      -1729382256910270213L,
      7665811756822757410L,
      7020347861333468005L,
      8303902631152018791L,
      7002867873441017716L,
      8227902196707258480L,
      8315171465526734185L,
      7017504795567420003L,
      8242202436415615602L,
      8319393576171892321L,
      7236828795133322098L,
      7593683801589902953L,
      7599584889991687540L,
      2751756762706637924L,
      3548120073198200168L,
      119915503693874L,
      -3457637518689890303L,
      2200185950419L,
      6076768769934363149L,
      3681606L,
      -236491583232L,
      504410855635425536L,
      1099528405248L,
      14091160632951040L,
      726205474273426944L,
      -3967389796753473376L,
      2233788108120260648L,
      72057679959632384L,
      -2522015748371900928L,
      -4899909213980983161L,
      -1224154455478208767L,
      -4503570352766976L,
      1082315636977L
   };
   private static final int ONE_MINUTE = 60;
   private static final int DEFAULT_TIMEOUT = 600;
   static final long TIMEOUT_KEY = 5986625456595542661L;
   static final long IDLE_SCREEN_FILE_KEY = 964187185309765141L;
   static final long OLD_DISABLED_VALUE = -1L;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(8585934785835124063L, "net.rim.device.apps.internal.resource.IdleScreen");

   static final int getTimeout() {
      return Options.getInstance().getInt(-8475525960600792234L);
   }

   static final void setTimeout(int timeout) {
      Options.getInstance().setInt(-8475525960600792234L, timeout);
   }

   static final String getIdleScreenFilename() {
      String result = Options.getInstance().getString(5872329845182774168L);
      if (result == null) {
         result = Options.getInstance().getString(845737328485785096L);
         if (result != null) {
            return "/store" + result;
         }
      }

      return result;
   }

   static final void setIdleScreenFilename(String filename) {
      String shortFilename = filename;
      if (filename != null) {
         int slashIndex = filename.indexOf(47, 1);
         if (slashIndex != -1) {
            shortFilename = filename.substring(slashIndex, filename.length());
         }
      }

      Options.getInstance().setString(845737328485785096L, shortFilename);
      Options.getInstance().setString(5872329845182774168L, filename);
   }

   static final String getIdleScreenAttribute(long attrib) {
      return Options.getInstance().getString(attrib);
   }

   static final void setIdleScreenAttribute(long attrib, String value) {
      Options.getInstance().setString(attrib, value);
   }

   static final boolean isDisabled() {
      return getTimeout() <= 0;
   }

   static final void register() {
      IdleScreenOptionsProvider.register(new IdleScreenOptions());
   }

   @Override
   public final void populateMainScreen(MainScreen screen) {
      int timeout = getTimeout();
      this._enabledField = new BooleanChoiceField(_rb.getString(1), 2, timeout > 0);
      if (-1 == timeout || 0 == timeout) {
         timeout = 600;
      }

      screen.add(this._enabledField);
      this._timeoutField = new TimeChoiceField(_rb.getString(2), this._timeouts, (timeout < 0 ? -timeout : timeout) * 1000);
      screen.add(this._timeoutField);
      screen.add(new IdleScreenOptions$PreviewButtonField());
   }

   @Override
   public final void save() {
      setTimeout(
         (int)((this._enabledField.isAffirmative() ? this._timeoutField.getSelectedTimeInMillis() : -this._timeoutField.getSelectedTimeInMillis()) / 1000)
      );
   }
}
