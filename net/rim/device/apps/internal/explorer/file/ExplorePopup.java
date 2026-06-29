package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.internal.io.file.FileUtilities;

final class ExplorePopup extends PopupScreen implements ExploreCallback {
   private LabelField _pathField;
   private LabelField _titleField;
   private ExploreManager _explorer;
   private SelectionListener _selectionListener;

   ExplorePopup(SelectionListener selector, String title, Object ctx) {
      super(new VerticalFieldManager(), 196608);
      this._titleField = new LabelField(title);
      this.add(this._titleField);
      this._pathField = new LabelField(null, 1152921504606847168L);
      this._pathField.setEditable(false);
      this.add(this._pathField);
      this.add(new SeparatorField(65536));
      this._selectionListener = selector;
      this._explorer = new ExploreManager(this, ctx, true, 3459063580983296000L);
      this.add(this._explorer);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      Object selectedFileItem = null;
      Field field = this.getFieldWithFocus();
      if (field == this._pathField) {
         selectedFileItem = this._explorer.getCurrentView();
         this._explorer.makeMenuForPath(menu, instance);
      } else {
         selectedFileItem = this._explorer.getSelectedItem();
      }

      if (this._selectionListener instanceof VerbProvider) {
         Verb[] verbs = new Verb[0];
         ContextObject context = new ContextObject();
         if (selectedFileItem == null) {
            selectedFileItem = this._explorer.getCurrentView();
         }

         if (selectedFileItem instanceof FileItemField) {
            if (selectedFileItem instanceof UpAliasFileItemField && this._explorer.getCurrentView() instanceof FileItemField) {
               selectedFileItem = this._explorer.getCurrentView();
            }

            context.put(2765042845091913199L, ((FileItemField)selectedFileItem).getFullPath());
            context.put(6420606222376351919L, selectedFileItem);
         }

         VerbProvider verbProvider = (VerbProvider)this._selectionListener;
         Verb defaultVerb = verbProvider.getVerbs(context, verbs);
         if (verbs != null && verbs.length > 0) {
            for (int idx = 0; idx < verbs.length; idx++) {
               Verb verb = verbs[idx];
               VerbMenuItem menuItem = new VerbMenuItem(null, verb.getOrdering(), Integer.MAX_VALUE, verb, context);
               menu.add(menuItem);
               if (verb == defaultVerb) {
                  menu.setDefaultIgnoreContextMenuDefault(menuItem);
               }
            }
         }
      }
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (character == ' ' && this._selectionListener != null) {
         FileItemField field = this._explorer.getSelectedItem();
         if (field instanceof UpAliasFileItemField && this._explorer.getCurrentView() instanceof FileItemField) {
            field = (FileItemField)this._explorer.getCurrentView();
         }

         if (field != null) {
            this._selectionListener.selected(field.getFullPath());
            return true;
         }
      }

      return super.keyChar(character, status, time);
   }

   @Override
   public final void pathSet(Object path) {
      this._pathField.setText(FileUtilities.getDisplayName(path.toString()));
   }

   @Override
   public final void statusOn() {
   }

   @Override
   public final void statusOff() {
   }

   @Override
   public final void currentItemChanged(Field field, FileItemField item) {
   }

   @Override
   public final void close() {
      super.close();
      this._explorer.cleanup();
   }
}
