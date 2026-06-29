package net.rim.device.api.crypto.tls;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.vm.Array;

final class ProxyTLSOptionsProvider implements FieldProvider, VerbProvider, ListFieldCallback, KeyListener {
   private BooleanChoiceField _redirectionsOnProxyField;
   private ListField _trustedHostsField;
   private TLSOptionStore _optionStore = TLSOptionStore.getOptions();
   private Vector _trustedHosts;
   private VerticalFieldManager _vfm;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   @Override
   public final Field getField(Object context) {
      this._vfm = (VerticalFieldManager)(new Object());
      this._redirectionsOnProxyField = (BooleanChoiceField)(new Object(_rb.getString(33), 0, this._optionStore.getDoRedirection()));
      this._vfm.add(this._redirectionsOnProxyField);
      this._vfm.add((Field)(new Object(_rb.getString(37))));
      this._trustedHosts = this._optionStore.getTrustedHosts();
      this._trustedHostsField = (ListField)(new Object(this._trustedHosts.size()));
      this._trustedHostsField.setCallback(this);
      this._vfm.add(this._trustedHostsField);
      return this._vfm;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Array.resize(verbs, 1);
      verbs[0] = new ProxyTLSOptionsProvider$AddHostVerb(this);
      Field field = this._vfm.getLeafFieldWithFocus();
      if (field == this._trustedHostsField) {
         int selectedItem = this._trustedHostsField.getSelectedIndex();
         if (selectedItem != -1) {
            Array.resize(verbs, 2);
            verbs[1] = new ProxyTLSOptionsProvider$DeleteHostVerb(this, selectedItem);
         }
      }

      return null;
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      this._optionStore.setDoRedirection(this._redirectionsOnProxyField.isAffirmative());
      this._optionStore.setTrustedHosts(this._trustedHosts);
      return true;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      String trustedHost = (String)this.get(listField, index);
      if (trustedHost != null) {
         graphics.drawText(((StringBuffer)(new Object("  "))).append(trustedHost).toString(), 0, y, 0, width);
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
      return listField == this._trustedHostsField && index >= 0 && index < this._trustedHosts.size() ? this._trustedHosts.elementAt(index) : null;
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      if (key == 127) {
         Field field = this._vfm.getLeafFieldWithFocus();
         if (field == this._trustedHostsField) {
            int selectedItem = this._trustedHostsField.getSelectedIndex();
            if (selectedItem != -1) {
               new ProxyTLSOptionsProvider$DeleteHostVerb(this, selectedItem).invoke(null);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final String toString() {
      return _rb.getString(39);
   }

   public ProxyTLSOptionsProvider() {
   }
}
