package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.resource.RibbonResource;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;

final class FolderDialog extends Dialog implements RibbonResource, FocusChangeListener {
   private int _mode;
   private boolean _editFolderDirty;
   private RichTextField _labelTitle;
   private LabelField _labelFolder;
   private EditField _editFolder;
   private LabelField _labelIcon;
   private FilmstripField _filmstripFolder;
   private ButtonField _buttonAdd;
   private ButtonField _buttonCancel;
   private String _folderName;
   private String _customImageName;
   public static final int CREATE_MODE = 0;
   public static final int EDIT_MODE = 1;
   private static FolderDialog _instance;
   private static int _folderSuffix;

   public final void setFolderName(String name) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setFolderDescription(String description) {
      this._editFolder.setText(description);
      this._editFolder.setSelection(0, true, description.length());
      this._editFolder.setFocus();
      this._editFolderDirty = true;
   }

   public final void setCustomImageName(String customImageName) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getFolderImageName() {
      return this._filmstripFolder.getFolderImageName();
   }

   public final void setMode(int mode) {
      this._mode = mode;
      if (mode == 1) {
         this._labelFolder.setText(RibbonResources.getString(170));
         this._labelIcon.setText(RibbonResources.getString(171));
         this._labelTitle.setText(RibbonResources.getString(164));
         this._buttonAdd.setLabel(RibbonResources.getString(175));
         this._buttonCancel.setLabel(CommonResources.getString(9042));
      } else {
         this._labelFolder.setText(RibbonResources.getString(170));
         this._labelIcon.setText(RibbonResources.getString(171));
         this._labelTitle.setText(RibbonResources.getString(162));
         this._buttonAdd.setLabel(CommonResources.getString(9045));
         this._buttonCancel.setLabel(CommonResources.getString(9042));
      }
   }

   public final void loadImages() {
      this._filmstripFolder.loadImages();
      this._filmstripFolder.setCurrentImage(this._customImageName);
   }

   public final String getResult() {
      return this._editFolder.getText().trim();
   }

   @Override
   public final void focusChanged(Field field, int event) {
      if (field == this._editFolder) {
         this._editFolderDirty = false;
      }
   }

   public static final void resetFolderSuffix() {
      _folderSuffix = 1;
   }

   static final FolderDialog getInstance() {
      if (_instance == null) {
         _instance = new FolderDialog();
      }

      _instance.setMode(0);
      _instance.setCustomImageName(null);
      String name = RibbonResources.getString(169) + _folderSuffix;
      _instance.setFolderName(name);
      _instance.setFolderDescription(name);
      _instance.loadImages();
      return _instance;
   }

   static final FolderDialog getInstance(ApplicationEntry entry) {
      String name = null;
      if (_instance == null) {
         _instance = new FolderDialog();
      }

      _instance.setMode(1);
      if (entry != null) {
         FolderEntryPointDescriptor descriptor = (FolderEntryPointDescriptor)entry.getDescriptor();
         name = descriptor.get(1, "");
         String description = entry.getDescription(true);
         _instance.setCustomImageName(entry.getCustomImageName());
         _instance.setFolderDescription(description);
      }

      _instance.setFolderName(name);
      _instance.loadImages();
      return _instance;
   }

   private FolderDialog() {
      super("", null, null, 0, null, 35184372088832L);
      _folderSuffix = 1;
      this._labelTitle = new RichTextField("", 36028797086072832L);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(this._labelTitle);
      this.add(new SeparatorField());
      this._labelFolder = new LabelField(RibbonResources.getString(170));
      this._editFolder = new EditField("", "", 1000000, 4503602043289600L);
      this._editFolder.setPadding(0, 15, 0, 0);
      this._editFolder.setFocusListener(this);
      HorizontalFieldManager editManager = new HorizontalFieldManager(4294967296L);
      editManager.add(this._labelFolder);
      editManager.add(this._editFolder);
      this.add(editManager);

      label20:
      try {
         this._labelIcon = new LabelField(RibbonResources.getString(171));
         this._filmstripFolder = new FilmstripField(18014411394383872L, "file:///store/samples/folder icons/");
         HorizontalFieldManager folderManager = new HorizontalFieldManager(4294967296L);
         this._labelIcon.setPadding(0, 15, 0, 0);
         folderManager.add(this._labelIcon);
         folderManager.add(this._filmstripFolder);
         this.add(folderManager);
      } finally {
         break label20;
      }

      this._buttonAdd = new ButtonField(CommonResources.getString(9045));
      this._buttonAdd.setChangeListener(this);
      this._buttonCancel = new ButtonField(CommonResources.getString(9042));
      this._buttonCancel.setChangeListener(this);
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      buttonManager.add(this._buttonAdd);
      buttonManager.add(this._buttonCancel);
      this.add(buttonManager);
      this.setEscapeEnabled(false);
      this._editFolderDirty = true;
      this._editFolder.setFocus();
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (this._buttonAdd.isFocus()) {
         if (this.isValidFolder()) {
            _folderSuffix++;
            this.close();
            return true;
         }

         this.onInvalidFolder();
      }

      return super.navigationClick(status, time);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this._editFolderDirty = false;
      return super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (this._editFolderDirty && this._editFolder.isFocus()) {
         this._editFolderDirty = false;

         label44:
         try {
            if (key != 127 && key != '\b') {
               this._editFolder.setText(String.valueOf(key));
            } else {
               this._editFolder.setText("");
            }
         } finally {
            break label44;
         }
      }

      if (!handled && key == 27) {
         this.cancel();
      }

      return handled;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._buttonCancel) {
         this.cancel();
      } else {
         if (field == this._buttonAdd) {
            if (!this.isValidFolder()) {
               this.onInvalidFolder();
               return;
            }

            _folderSuffix++;
            this.close();
         }
      }
   }

   private final void onInvalidFolder() {
      if (this._editFolder != null && this._editFolder.getText() != null && this._editFolder.getText().length() == 0) {
         Dialog.alert(RibbonResources.getString(174));
      } else {
         Dialog.alert(RibbonResources.getString(165));
      }

      this.setFolderName(this._folderName);
   }

   private final boolean isValidFolder() {
      String folderName = this._editFolder.getText().trim();
      if (this._folderName != null && this._folderName.equals(folderName)) {
         return true;
      }

      HierarchyManager manager = HierarchyManager.getInstance();
      return folderName.length() > 0 && (this._mode == 0 || this._mode == 1) ? !manager.folderNameExists(this._folderName, folderName) : false;
   }
}
