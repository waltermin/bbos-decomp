package net.rim.device.apps.internal.memo;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.memo.resources.MemoResources;
import net.rim.device.internal.ui.component.PropertyField;

final class MemoOptionsScreen extends SaveableMainScreenOptionsListItem {
   private BooleanChoiceField _deleteConfirm;
   private BooleanChoiceField _allowWirelessSync;
   private MemoOptions _memoOptions = MemoOptions.getOptions();
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");

   private MemoOptionsScreen() {
      super(MemoResources.getString(240));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Manager section = this.createSection(CommonResources.getString(9179), mainScreen);
      this._deleteConfirm = new BooleanChoiceField(CommonResources.getString(2008), 0, this._memoOptions.getConfirmDelete());
      section.add(this._deleteConfirm);
      if (SyncManager.getInstance().isOTASyncAvailable(MemoCollectionImpl.getInstance(), false)) {
         this._allowWirelessSync = new BooleanChoiceField(CommonResources.getString(9117), 0, this._memoOptions.isWirelessSyncAllowed());
         section.add(this._allowWirelessSync);
      }

      section = this.createSection(null, mainScreen);
      section.add(new PropertyField(CommonResources.getString(9133), Integer.toString(MemoCollectionImpl.getInstance().size())));
   }

   protected final Manager createSection(String title, Manager parent) {
      Manager section = new VerticalFieldManager(1153484454560268288L);
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      if (title != null) {
         LabelField titleField = new LabelField(title, 1152921504606846976L);
         titleField.setTag(OPTIONS_SECTION_HEADER_TAG);
         section.add(titleField);
      }

      section.add(new SeparatorField());
      parent.add(section);
      return section;
   }

   @Override
   protected final boolean save() {
      this._memoOptions.setConfirmDelete(this._deleteConfirm.isAffirmative());
      if (this._allowWirelessSync != null) {
         boolean allowWirelessSync = this._allowWirelessSync.isAffirmative();
         if (this._memoOptions.isWirelessSyncAllowed() != allowWirelessSync) {
            if (allowWirelessSync) {
               MemoCollectionImpl memoCollection = MemoCollectionImpl.getInstance();
               if (memoCollection.isDirty()) {
                  if (Dialog.ask(3, CommonResources.getString(9157), -1) == -1) {
                     return false;
                  }

                  memoCollection.markDirty(false);
               }
            }

            this._memoOptions.allowWirelessSync(allowWirelessSync);
         }
      }

      this._memoOptions.commit();
      return super.save();
   }

   public static final void showEditOptionsScreen() {
      MemoOptionsScreen optionsScreen = new MemoOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }
}
