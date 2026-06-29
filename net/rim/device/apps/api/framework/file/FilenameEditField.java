package net.rim.device.apps.api.framework.file;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.FolderIcons;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public class FilenameEditField extends VerticalFieldManager implements FieldChangeListener {
   private String _fileExtension;
   private BasicEditField _editField;
   private LabelField _dirLabel;
   private String _currentPath;
   private FileSelector _fileSelector;
   private ButtonField _dirButton;
   public static final long DISABLE_SMART_EXTENSION_MODE = 32768L;
   private static final int MAX_NAME_LENGTH = 256;
   private static ResourceBundle _rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
   public static final long SAVE = 134217728L;

   public String getFilename() {
      if (this._editField == null) {
         return null;
      }

      String result = ((StringBuffer)(new Object())).append(this._editField.getText().trim()).append(this._fileExtension).toString();
      int endPos = result.length() - 1;

      while (endPos >= 0 && result.charAt(endPos) == '.') {
         endPos--;
      }

      return result.substring(0, endPos + 1);
   }

   public String getPath() {
      return this._currentPath;
   }

   public void setPath(String path) {
      this._currentPath = path;
      this._dirLabel.setText(FileUtilities.getDisplayName(path));
   }

   public FileSelector getFileSelector() {
      return this._fileSelector;
   }

   public String validate() {
      String name = this.getFilename();
      String error = null;
      if (name != null && name.length() != 0) {
         if (name.length() > 256) {
            return _rb.getString(43);
         }

         if (name.charAt(0) == '.') {
            return _rb.getString(115);
         }

         for (int lv = name.length() - 1; lv >= 0; lv--) {
            if (!validateNameChar(name.charAt(lv))) {
               return _rb.getString(44);
            }
         }

         return error;
      } else {
         return _rb.getString(42);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._editField) {
         this.validate();
      } else {
         if (field == this._dirButton) {
            String selectedFile = this._fileSelector.selectFile(this.getPath());
            if (selectedFile != null) {
               if (!FileUtilities.isDirectory(selectedFile)) {
                  String path = FileUtilities.getPath(selectedFile);
                  if (this._editField != null) {
                     this.addOrReplaceFilenameField(path, FileUtilities.getName(selectedFile));
                  }

                  selectedFile = path;
               }

               this.setPath(selectedFile);
            }

            this.fieldChangeNotify(0);
         }
      }
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      if (this._editField != null) {
         this._editField.setFocus();
      }
   }

   public FilenameEditField(String path, String filename, int mediaType, long style, boolean allowPathChange) {
      this(path, filename, new FileSelector(path, null, mediaType, (style & 134217728) != 0, null), style, allowPathChange);
   }

   public FilenameEditField(String path, String filename, FileSelector fileSelector, long style, boolean allowPathChange) {
      super(style);
      this._fileSelector = fileSelector;
      HorizontalFieldManager hfm = null;
      if (allowPathChange) {
         hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
         this._dirButton = (ButtonField)(new Object(98304));
         this._dirButton.setImage(FolderIcons.ICONS.getImage(2));
         if (allowPathChange) {
            this._dirButton.setChangeListener(this);
         }

         hfm.add(this._dirButton);
         this.add(hfm);
      }

      if (path != null) {
         this._currentPath = path;
         this._dirLabel = (LabelField)(new Object(FileUtilities.getDisplayName(path), 192));
         if (hfm != null) {
            hfm.add(this._dirLabel);
         } else {
            this.add(this._dirLabel);
         }
      }

      if (filename != null) {
         int endPos = this.getSuffixStartPos(filename);
         this._fileExtension = endPos < 0 ? "" : filename.substring(endPos, filename.length());
         this.addOrReplaceFilenameField(path, filename);
      }
   }

   private void addOrReplaceFilenameField(String path, String filename) {
      if (this._editField != null) {
         this.delete(this._editField);
         this._editField = null;
      }

      filename = filterFilename(filename);
      int pathLength = path == null ? 0 : path.length();
      this._editField = (BasicEditField)(new Object(CommonResource.getString(10074), "", 256 - this._fileExtension.length() - pathLength, 4503602043289601L));
      this.add(this._editField);
      this.setFilename(filename);
   }

   private int getSuffixStartPos(String filename) {
      int endPos = -1;
      if ((this.getFieldStyle() & 32768) == 0) {
         int searchIndex = filename.length() - 1;
         if (StringUtilities.endsWithIgnoreCase(filename, FileUtilities.ENCRYPTED_MEDIA_EXTENSION, 1701707776)) {
            searchIndex -= FileUtilities.ENCRYPTED_MEDIA_EXTENSION.length();
         }

         endPos = filename.lastIndexOf(46, searchIndex);
      }

      return endPos;
   }

   private static String filterFilename(String name) {
      name = FileUtilities.getDisplayName(name);
      char[] nameChars = null;
      FilenameTextFilter filter = null;

      for (int i = name.length() - 1; i >= 0; i--) {
         if (!FilenameTextFilter.staticValidate(name.charAt(i))) {
            if (nameChars == null) {
               nameChars = name.toCharArray();
               filter = new FilenameTextFilter();
            }

            nameChars[i] = filter.convert(name.charAt(i), 0);
            if (!FilenameTextFilter.staticValidate(nameChars[i])) {
               nameChars[i] = '_';
            }
         }
      }

      if (nameChars != null) {
         name = (String)(new Object(nameChars));
      }

      return name;
   }

   public static boolean validateNameChar(char character) {
      return FilenameTextFilter.staticValidate(character);
   }

   private void setFilename(String filename) {
      if (this._editField != null) {
         int endPos = this.getSuffixStartPos(filename);
         if (endPos < 0) {
            endPos = filename.length();
         }

         filename = filename.substring(0, endPos);
         this._editField.setText(filename);
         this._editField.autoSelectFullText();
      }
   }
}
