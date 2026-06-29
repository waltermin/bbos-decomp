package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.ui.ScreenView;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.ResourceProvider;
import net.rim.wica.runtime.ui.internal.StyleFactory;
import net.rim.wica.runtime.ui.internal.UiServiceImpl;
import net.rim.wica.runtime.ui.internal.WicaScreen;

public final class ScreenContext implements ScreenView {
   private UiServiceImpl _uiService;
   private ScreenModel _model;
   private View _layout;
   private Menu _menu = (Menu)(new Object(65536));
   private Field _savedFocus;
   private boolean _suspendLayout;

   public final Field getSavedFocus() {
      return this._savedFocus;
   }

   public final void setSavedFocus(Field savedFocus) {
      this._savedFocus = savedFocus;
   }

   public final Manager getLayout() {
      return (Manager)this._layout;
   }

   public final String getTitle() {
      return this._model.getTitle();
   }

   public final void close() {
      if (this._menu.isDisplayed()) {
         this.closeMenu();
      }
   }

   public final void requestMenuShow(int instance) {
      this._uiService.forceModelUpdate();
      this._uiService.getRuntime().requestMenuShow(instance);
   }

   public final void showMenu(int instance) {
      if (this._model.isDisplayed()) {
         synchronized (this._menu) {
            this._menu.deleteAll();
            this._menu.setInstance(instance);
            if (instance == 65536 || instance == 65537) {
               this._menu.setAlignment(12884901888L, 34359738368L);
            } else if ((instance & 1073741824) != 0) {
               this._menu.setAlignment(4294967296L, 34359738368L);
            }

            instance &= -1073741825;
            WicaScreen screen = (WicaScreen)this.getLayout().getScreen();
            screen.populateMenu(this._menu, instance);
            MenuModel menuModel = this._model.getMenuOnShow();
            if (menuModel != null) {
               MenuItemModel[] items = menuModel.getItems();
               int itemCount = items.length;

               for (int i = 0; i < itemCount; i++) {
                  if (items[i].isVisible()) {
                     this._menu.add(new ScreenMenuItem(items[i], i));
                  }
               }
            }

            this._menu.addSeparator();
            boolean defaultMenu = true;
            defaultMenu = instance == 0;
            if (defaultMenu) {
               if (this._uiService.isBackAvailable()) {
                  this._menu.add(new ScreenContext$BackMenuItem(this));
               }

               this._menu.add(new ScreenContext$CloseMenuItem(this));
            }
         }

         if (this._menu.getSize() > 0) {
            UiApplication.getUiApplication().invokeLater(new ScreenContext$1(this));
         }
      }
   }

   final void setTitle(String title) {
      WicaScreen screen = (WicaScreen)this.getLayout().getScreen();
      if (screen != null) {
         screen.setTitle(title);
      }
   }

   final ResourceProvider getResourceProvider() {
      return this._uiService.getResourceProvider();
   }

   final StyleFactory getStyleFactory() {
      return this._uiService.getStyleFactory();
   }

   final void setSuspendLayout(boolean suspendLayout) {
      this._suspendLayout = suspendLayout;
   }

   final boolean getSuspendLayout() {
      return this._suspendLayout;
   }

   @Override
   public final void setVisibility(byte visibility) {
      this._layout.setVisibility(visibility);
   }

   @Override
   public final View getCurrentFocus() {
      View focusedView = null;
      Field focusedField = ((Manager)this._layout).getFieldWithFocus();

      while (focusedField != null && !(focusedField instanceof View)) {
         focusedField = focusedField.getManager();
      }

      if (focusedField != null) {
         focusedView = (View)focusedField;
      }

      return focusedView;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._layout.setModel(model);
   }

   @Override
   public final UIComponent getModel() {
      return this._layout.getModel();
   }

   @Override
   public final void update(int row) {
      UiApplication.getUiApplication().invokeAndWait(new ScreenContext$2(this));
   }

   private final void closeMenu() {
      this._menu.close();
   }

   public ScreenContext(UiServiceImpl uiService, ScreenModel model, int row, long style) {
      this._uiService = uiService;
      this._model = model;
      this._model.setView(this);
      this._layout = ComponentFactory.getView(this, model, row, style);
      this._layout.setModel(model);
   }
}
