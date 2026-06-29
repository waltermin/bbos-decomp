package net.rim.device.apps.internal.task;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.internal.ui.component.PropertyField;

final class TaskOptionsScreen extends SaveableMainScreenOptionsListItem {
   private ObjectChoiceField _sortOrder;
   private ObjectChoiceField _deleteConfirm;
   private TimeChoiceField _snooze;
   private BooleanChoiceField _allowWirelessSync;
   private TaskOptions _taskOptions = TaskOptions.getOptions();
   static final String[] _sortOrderChoices = new String[]{
      TaskResources.getString(34), TaskResources.getString(35), TaskResources.getString(39), TaskResources.getString(21)
   };
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");

   private TaskOptionsScreen() {
      super(TaskResources.getString(31));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Manager section = this.createSection(CommonResources.getString(9178), mainScreen);
      int sortOrder = this._taskOptions.getSortOrderIndex();
      if (sortOrder < 0 || sortOrder >= _sortOrderChoices.length) {
         sortOrder = 0;
      }

      this._sortOrder = new ObjectChoiceField(TaskResources.getString(5), _sortOrderChoices, sortOrder);
      section.add(this._sortOrder);
      section = this.createSection(CommonResources.getString(9179), mainScreen);
      ReminderManager reminderManager = ReminderManager.getInstance();
      if (reminderManager != null) {
         this._snooze = reminderManager.getSnoozeOptionsField(this._taskOptions.getSnoozeMillis());
         section.add(this._snooze);
      }

      this._deleteConfirm = new ObjectChoiceField(CommonResources.getString(2008), CommonResources.getYesNoArray(0));
      this._deleteConfirm.setSelectedIndex(this._taskOptions.getConfirmDelete() ? 0 : 1);
      section.add(this._deleteConfirm);
      if (SyncManager.getInstance().isOTASyncAvailable(TaskCollectionImpl.getInstance(), false)) {
         this._allowWirelessSync = new BooleanChoiceField(CommonResources.getString(9117), 0, this._taskOptions.isWirelessSyncAllowed());
         section.add(this._allowWirelessSync);
      }

      section = this.createSection(null, mainScreen);
      section.add(new PropertyField(CommonResources.getString(9133), Integer.toString(TaskCollectionImpl.getInstance().size())));
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
      this._taskOptions.setSortOrderIndex(this._sortOrder.getSelectedIndex());
      this._taskOptions.setConfirmDelete(this._deleteConfirm.getSelectedIndex() != 1);
      if (this._snooze != null) {
         this._taskOptions.setSnoozeMillis(this._snooze.getSelectedTimeInMillis());
      }

      if (this._allowWirelessSync != null) {
         boolean allowWirelessSync = this._allowWirelessSync.isAffirmative();
         if (this._taskOptions.isWirelessSyncAllowed() != allowWirelessSync) {
            if (allowWirelessSync) {
               TaskCollectionImpl taskCollection = TaskCollectionImpl.getInstance();
               if (taskCollection.isDirty()) {
                  if (Dialog.ask(3, CommonResources.getString(9157), -1) == -1) {
                     return false;
                  }

                  taskCollection.markDirty(false);
               }
            }

            this._taskOptions.allowWirelessSync(allowWirelessSync);
         }
      }

      this._taskOptions.commit();
      return super.save();
   }

   public static final void showEditOptionsScreen() {
      TaskOptionsScreen optionsScreen = new TaskOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }
}
