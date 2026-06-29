package net.rim.device.apps.internal.bis.launch.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.bis.launch.resource.ApplicationResources;

public class BaseScreen extends MainScreen implements FieldChangeListener {
   Hashtable _runnables = (Hashtable)(new Object());
   LabelField _titleField;
   protected MenuItem[] _menuItems;
   protected MenuItem _defaultMenuItem;
   private TitleBar _titleBar;

   public FormattedTextField createFormattedTextField(int resourceKey) {
      String label = ApplicationResources.getString(resourceKey);
      return new FormattedTextField(label, 0, this.getFont().derive(0));
   }

   public LabelField createLabelField(int resourceKey) {
      String label = ApplicationResources.getString(resourceKey);
      LabelField labelField = (LabelField)(new Object(label));
      labelField.setFont(this.getFont().derive(0));
      return labelField;
   }

   public ButtonField createButton(String label, FieldChangeRunnable runnable) {
      ButtonField button = new Button(label);
      if (runnable != null) {
         this.addFieldChangeRunnable(button, runnable);
      }

      return button;
   }

   public void addFieldChangeRunnable(Field field, FieldChangeRunnable runnable) {
      field.setChangeListener(this);
      this._runnables.put(field, runnable);
   }

   @Override
   public void fieldChanged(Field field, int context) {
      FieldChangeRunnable runnable = (FieldChangeRunnable)this._runnables.get(field);
      runnable.run(field, context);
   }

   public BaseScreen(String title) {
      this.setTitle(title);
      this.resetFontsInternal();
   }

   @Override
   public void setTitle(String title) {
      if (this._titleBar == null) {
         this._titleBar = new TitleBar(title);
      }

      this._titleBar.setTitle(title);
      this.setTitle(this._titleBar);
   }

   @Override
   protected boolean onSavePrompt() {
      return true;
   }

   private void resetFontsInternal() {
      Font defaultFont = Font.getDefault();
      int fontSize = Ui.convertSize(defaultFont.getHeight(), 0, 3);
      if (fontSize > 8) {
         fontSize--;
      }

      this.setFont(defaultFont.derive(defaultFont.getStyle(), fontSize, 3));
      if (fontSize > 7) {
         fontSize--;
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.deleteAll();
      MenuItem closeMenuItem = new BaseScreen$1(this, ApplicationResources.getString(15), 1, 1);
      if (this._menuItems != null && this._menuItems.length > 0) {
         for (int i = 0; i < this._menuItems.length; i++) {
            MenuItem menuItem = this._menuItems[i];
            menu.add(menuItem);
         }

         menu.addSeparator();
      }

      menu.add(closeMenuItem);
      if (this._defaultMenuItem != null) {
         menu.setDefault(this._defaultMenuItem);
      }
   }

   @Override
   public void close() {
   }
}
