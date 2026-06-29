package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.api.ui.DialogWithBackgroundThreadRunnable;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class PasswordKeeperScreen extends KeywordFilteredScreen implements CollectionListener {
   private PasswordKeeperSync _collection;
   private PasswordKeeperScreen$PasswordVerb _newVerb;
   private PasswordKeeperScreen$PasswordVerb _openVerb;
   private PasswordKeeperScreen$PasswordVerb _deleteVerb;
   private PasswordKeeperScreen$PasswordVerb _copyUsernameVerb;
   private PasswordKeeperScreen$PasswordVerb _copyPasswordVerb;
   private PasswordKeeperScreen$PasswordVerb _clearVerb;
   private PasswordKeeperScreen$PasswordVerb _changeVerb;
   private PasswordKeeperScreen$PasswordVerb _optionVerb;
   private PasswordKeeperScreen$PasswordVerb _closeVerb;
   private static final int MENU_ORDERING_NEW = 1052672;
   private static final int MENU_ORDERING_OPEN = 1056768;
   private static final int MENU_ORDERING_DELETE = 1064960;
   private static final int MENU_ORDERING_COPY_USERNAME = 16846848;
   private static final int MENU_ORDERING_COPY_PASSWORD = 16847104;
   private static final int MENU_ORDERING_CLEAR = 16850944;
   private static final int MENU_ORDERING_CHANGE = 16912384;
   private static final int MENU_ORDERING_OPTION = 16977920;
   private static final int MENU_ORDERING_CLOSE = 17043456;

   public PasswordKeeperScreen(String title, KeywordFilterList list, ListFieldCallback listCallback, PasswordKeeperSync collection) {
      super(title, list, listCallback, false);
      this.setHelp(34120);
      this.setDefaultClose(false);
      this._collection = collection;
      this._collection.addCollectionListener(this);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\b':
            if (!this.isSearchStringEmpty()) {
               break;
            }
         case '\u007f':
            PasswordKeeperElement element = (PasswordKeeperElement)this.getSelectedElement();
            if (element != null) {
               this.deletePassword(element);
            }

            return true;
         case '\n':
            PasswordKeeperElement element = (PasswordKeeperElement)this.getSelectedElement();
            if (element == null) {
               KeywordFilteredListFinder finder = this.getFinderField();
               String searchPattern = finder.getSearchPattern();
               if (searchPattern == null || searchPattern.length() == 0) {
                  searchPattern = this.getFinderField().getText();
               }

               element = new PasswordKeeperElement(new Object[0], new Object[]{searchPattern == null ? "" : searchPattern, "", "", "", ""});
            }

            this.showScreen(element);
            return true;
         case '\u001b':
            if (this.isSearchStringEmpty()) {
               this.close();
               return true;
            }

            this.setSearchPattern(null);
            return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         PasswordKeeperElement element = (PasswordKeeperElement)this.getSelectedElement();
         if (this._collection.getSyncObjectCount() > 0 && element != null) {
            this.showScreen(element);
            return true;
         }
      }

      return super.invokeAction(action);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      ResourceBundleFamily rb = CommonResource.getBundle();
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      if (instance != 65537) {
         if (this._newVerb == null) {
            this._newVerb = new PasswordKeeperScreen$PasswordVerb(this, 1, 1052672, rb, 13);
         }

         menu.add(this._newVerb);
         menu.setDefault(this._newVerb);
         if (this._changeVerb == null) {
            this._changeVerb = new PasswordKeeperScreen$PasswordVerb(this, 7, 16912384, PasswordKeeper.getBundle(), 2010);
         }

         menu.add(this._changeVerb);
         if (instance == 0) {
            PasswordKeeperElement element = (PasswordKeeperElement)this.getSelectedElement();
            if (this._collection.getSyncObjectCount() > 0 && element != null) {
               if (this._openVerb == null) {
                  this._openVerb = new PasswordKeeperScreen$PasswordVerb(this, 2, 1056768, rb, 15);
               }

               menu.add(this._openVerb);
               menu.setDefault(this._openVerb);
               String username = null;
               String password = null;

               try {
                  username = element.getField(1);
                  password = element.getField(2);
               } catch (DecryptionException var9) {
               } catch (PasswordKeeperLockedException var10) {
               }

               if (options.getAllowCopy() && PasswordKeeperUtilities.isAvailable(username)) {
                  if (this._copyUsernameVerb == null) {
                     this._copyUsernameVerb = new PasswordKeeperScreen$PasswordVerb(this, 3, 16846848, PasswordKeeper.getBundle(), 2012);
                  }

                  menu.add(this._copyUsernameVerb);
               }

               if (options.getAllowCopy() && PasswordKeeperUtilities.isAvailable(password)) {
                  if (this._copyPasswordVerb == null) {
                     this._copyPasswordVerb = new PasswordKeeperScreen$PasswordVerb(this, 4, 16847104, PasswordKeeper.getBundle(), 3041);
                  }

                  menu.add(this._copyPasswordVerb);
               }

               if (this._deleteVerb == null) {
                  this._deleteVerb = new PasswordKeeperScreen$PasswordVerb(this, 5, 1064960, rb, 17);
               }

               menu.add(this._deleteVerb);
               if (options.getAllowCopy() && Clipboard.getClipboard().get() != null) {
                  if (this._clearVerb == null) {
                     this._clearVerb = new PasswordKeeperScreen$PasswordVerb(this, 6, 16850944, PasswordKeeper.getBundle(), 2013);
                  }

                  menu.add(this._clearVerb);
               }
            }

            if (this._optionVerb == null) {
               this._optionVerb = new PasswordKeeperScreen$PasswordVerb(this, 8, 16977920, rb, 20);
            }

            menu.add(this._optionVerb);
            if (this._closeVerb == null) {
               this._closeVerb = new PasswordKeeperScreen$PasswordVerb(this, 9, 17043456, rb, 9);
            }

            menu.add(this._closeVerb);
         }
      }
   }

   private final void showScreen(PasswordKeeperElement element) {
      PasswordKeeperElementScreen screen = new PasswordKeeperElementScreen(this, element);
      UiApplication app = UiApplication.getUiApplication();
      app.pushScreen(screen);
   }

   public final void copyUsername(PasswordKeeperElement element) {
      if (element != null) {
         try {
            Clipboard clipboard = Clipboard.getClipboard();
            clipboard.put(element.getField(1));
            Dialog.inform(PasswordKeeper.getString(3019));
         } catch (DecryptionException e) {
            Dialog.inform(PasswordKeeper.getString(4000));
         } catch (PasswordKeeperLockedException e) {
            Dialog.inform(PasswordKeeper.getString(4000));
         }
      }
   }

   public final void copyPassword(PasswordKeeperElement element) {
      if (element != null) {
         try {
            Clipboard clipboard = Clipboard.getClipboard();
            clipboard.put(element.getField(2));
            Dialog.inform(PasswordKeeper.getString(3042));
         } catch (DecryptionException e) {
            Dialog.inform(PasswordKeeper.getString(4000));
         } catch (PasswordKeeperLockedException e) {
            Dialog.inform(PasswordKeeper.getString(4000));
         }
      }
   }

   private final void deletePassword(PasswordKeeperElement element) {
      if (element != null) {
         PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
         boolean prompt = options.getConfirmDelete();
         if (prompt) {
            String title = null;

            try {
               title = element.getTitle();
            } catch (DecryptionException e) {
               title = PasswordKeeper.getString(3022);
            } catch (PasswordKeeperLockedException e) {
               title = PasswordKeeper.getString(3022);
            }

            if (!SimpleChoiceDialog.askYesNoQuestion(PasswordKeeper.getString(3023), title)) {
               return;
            }
         }

         this._collection.removeSyncObject(element);
      }
   }

   public final void cleanClipboard() {
      Clipboard clipboard = Clipboard.getClipboard();
      clipboard.put(null);
      Dialog.inform(PasswordKeeper.getString(3018));
   }

   private final void changePassword() {
      PasswordKeeperManager manager = PasswordKeeperManager.getInstance();
      manager.changePassword(this._collection.getSource());
   }

   public final void showOptions() {
      PasswordKeeperOptionsScreen screen = new PasswordKeeperOptionsScreen();
      UiApplication app = UiApplication.getUiApplication();
      app.pushScreen(screen);
   }

   public final void setFocus(PasswordKeeperElement element) {
      this.setElementWithFocus(element);
   }

   @Override
   public final void close() {
      DialogWithBackgroundThreadRunnable runnable = new PasswordKeeperThread(false);
      DialogWithBackgroundThread dialog = (DialogWithBackgroundThread)(new Object());
      dialog.initialize(PasswordKeeper.getString(3010), runnable);
      dialog.run();
      this._collection.removeCollectionListener(this);
   }

   final PasswordKeeperSync getCollection() {
      return this._collection;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      ((CollectionListener)this.getKeywordFilterList()).elementAdded(this._collection.getSource(), element);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      ((CollectionListener)this.getKeywordFilterList()).elementRemoved(this._collection.getSource(), element);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   static final KeywordFilteredListFinder access$000(PasswordKeeperScreen x0) {
      return x0.getFinderField();
   }

   static final KeywordFilteredListFinder access$100(PasswordKeeperScreen x0) {
      return x0.getFinderField();
   }

   static final Object access$300(PasswordKeeperScreen x0) {
      return x0.getSelectedElement();
   }

   static final Object access$400(PasswordKeeperScreen x0) {
      return x0.getSelectedElement();
   }

   static final Object access$500(PasswordKeeperScreen x0) {
      return x0.getSelectedElement();
   }

   static final Object access$600(PasswordKeeperScreen x0) {
      return x0.getSelectedElement();
   }
}
