package net.rim.device.apps.internal.options.items;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage$WizardVerb;
import net.rim.device.apps.api.setupwizard.ListWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public final class ThemeSetupWizard extends ListWizardPage {
   private String[] _themeNames;
   private String[] _themeNamesLocalized;
   private boolean _hideNone;
   private String _originalThemeName;
   private String _newThemeName;
   private int _originalThemeIndex;
   private boolean _themesPreLoaded;
   private boolean _pendingThemeChange;
   private boolean _revertingTheme;
   private Manager _header;
   private Hashtable _thumbCache;
   private BitmapField _previewField;
   private int _currentPreviewIndex;
   private Bitmap _emptyThumbnail;
   private static int _thumbWidth = (int)(Display.getWidth() * 4600877379321698714L);
   private static int _thumbHeight = (int)(Display.getHeight() * 4600877379321698714L);

   public ThemeSetupWizard() {
      super(OptionsResources.getResourceBundle(), 1439, 200, SetupWizardOrdering.THEME_CATEGORY, 131072);
      this._emptyThumbnail = new Bitmap(_thumbWidth, _thumbHeight);
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (this._thumbCache == null) {
         this._thumbCache = new Hashtable();
      }
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      return true;
   }

   @Override
   protected final void discardWizard() {
   }

   @Override
   protected final boolean canSkipWizardInternal() {
      this._themesPreLoaded = true;
      this.populateThemeNames();
      return this._themeNames.length == 1 && !this._hideNone || this._themeNames.length == 2 && this._hideNone;
   }

   @Override
   protected final void populateFields() {
      this._header = new VerticalFieldManager();
      this._header.setFont(this.getHeaderFont());
      LabelField label = new LabelField(OptionsResources.getString(2107));
      label.setBorder(0, 0, label.getFont().getHeight() >> 1, 0);
      this._header.add(label);
      this._header.add(new LabelField(OptionsResources.getString(2108)));
      this.setHeaderField(this._header);
      this.populateItemList();
      this._pendingThemeChange = false;
      this._revertingTheme = false;
      this.useSmallListFont(true);
   }

   protected final void populateItemList() {
      if (!this._themesPreLoaded) {
         this.populateThemeNames();
      }

      this._themesPreLoaded = false;
      int numThemes = this._themeNames.length;
      int indexOffset = this._hideNone ? 1 : 0;
      String[] displayNames = new String[numThemes - indexOffset];
      if (!this._hideNone) {
         displayNames[0] = OptionsResources.getString(1473);
      }

      for (int i = 1; i < numThemes; i++) {
         displayNames[i - indexOffset] = this._themeNamesLocalized[i];
      }

      this.setListItems(displayNames);
      this.setSelectedIndex(this._originalThemeIndex);
      Application.getApplication().invokeLater(new ThemeSetupWizard$1(this));
      this._currentPreviewIndex = this._originalThemeIndex;
      Bitmap previewBitmap = this.getPreviewBitmap(this._originalThemeIndex);
      this._previewField = new BitmapField(previewBitmap);
      this.setSideBar(this._previewField, previewBitmap.getWidth());
   }

   @Override
   protected final void selectedIndexChanged(int selectedIndex) {
      if (!this._revertingTheme) {
         this.setNewTheme(selectedIndex);
      }
   }

   @Override
   protected final void onRadioFocus(RadioButtonField radioButton) {
      if (radioButton != null) {
         this.setPreviewIndex(radioButton.getIndex());
      } else {
         this.setPreviewIndex(this.getSelectedIndex());
      }
   }

   private final int fixIndex(int index) {
      if (this._hideNone) {
         index++;
      }

      return index;
   }

   private final void setNewTheme(int uiIndex) {
      int rawIndex = this.fixIndex(uiIndex);
      String name = this._themeNames[rawIndex];
      this.log("Changing Themes: " + name);
      if (ThemeManager.isActivatable(name)) {
         Status.show(OptionsResources.getString(2109), Bitmap.getPredefinedBitmap(0), 2000, 0, false, false, 50);
         this._newThemeName = name;
         this._pendingThemeChange = true;
         Application.getApplication().invokeLater(new ThemeSetupWizard$2(this));
      }
   }

   private final int populateThemeNames() {
      this._themeNames = ThemeManager.getNameChoices();
      this._themeNamesLocalized = ThemeManager.getNameChoices(Locale.getDefault());
      int numNames = this._themeNames.length;
      this._originalThemeName = ThemeManager.getActiveName();
      this._originalThemeIndex = 0;

      for (int i = numNames - 1; i >= 0; i--) {
         if (this._themeNames[i].equals(this._originalThemeName)) {
            this._originalThemeIndex = i;
            break;
         }
      }

      this._hideNone = Ui.getMode() != 2 && this._originalThemeIndex != 0;
      if (this._hideNone) {
         this._originalThemeIndex--;
      }

      return this._originalThemeIndex;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (verb instanceof BasicWizardPage$WizardVerb && ((BasicWizardPage$WizardVerb)verb).canAutoSave()) {
         return true;
      }

      this.discardWizard();
      return true;
   }

   protected final void revertTheme() {
      if (this._originalThemeName != null && this._newThemeName != null && !this._newThemeName.equals(this._originalThemeName)) {
         this.log("Reverting theme: " + this._originalThemeName);
         this._newThemeName = null;
         this._revertingTheme = true;
         Status.show(OptionsResources.getString(2110));
         ThemeManager.setActiveTheme(this._originalThemeName);
         this.setSelectedIndex(this._originalThemeIndex);
         this._revertingTheme = false;
      }
   }

   protected final void confirmNewTheme() {
      int result = WizardDialog.ask(3, OptionsResources.getString(2111), false);
      if (result != 4) {
         this.revertTheme();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 2573494863350550132L && this._pendingThemeChange) {
         this._pendingThemeChange = false;
         Application.getApplication().invokeLater(new ThemeSetupWizard$3(this));
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
   }

   @Override
   protected final void resetFonts() {
      super.resetFonts();
      this._header.setFont(this.getHeaderFont());
   }

   private final void setPreviewIndex(int index) {
      this._currentPreviewIndex = index;
      this._previewField.setBitmap(this.getPreviewBitmap(index));
   }

   private final Bitmap getPreviewBitmap(int uiIndex) {
      int rawIndex = this.fixIndex(uiIndex);
      Bitmap bitmap = null;
      String resourceName = getPreviewResourceName(this._themeNamesLocalized[rawIndex]);
      if (resourceName != null) {
         synchronized (this._thumbCache) {
            if (this._thumbCache.containsKey(resourceName)) {
               bitmap = (Bitmap)this._thumbCache.get(resourceName);
            }
         }
      }

      if (bitmap == null) {
         bitmap = this._emptyThumbnail;
      }

      return bitmap;
   }

   private final void signalUpdatedThumbnail(int index, Bitmap bitmap) {
      if (index == this.fixIndex(this._currentPreviewIndex)) {
         synchronized (Application.getEventLock()) {
            if (index == this.fixIndex(this._currentPreviewIndex)) {
               this._previewField.setBitmap(bitmap);
            }
         }
      }
   }

   private static final String getPreviewResourceName(String name) {
      String resourceName = null;
      if (Display.getWidth() == 240) {
         if (name.indexOf("Dimension-Icon") >= 0) {
            resourceName = "ss-dimension-icon240.png";
         } else if (name.indexOf("Dimension-Zen") >= 0) {
            resourceName = "ss-dimension-zen.png";
         } else if (name.indexOf("Insight-List") >= 0) {
            resourceName = "ss-insight-list240.png";
         } else if (name.indexOf("Insight-Icon") >= 0) {
            resourceName = "ss-insight-icon240.png";
         } else if (name.indexOf("Cingular") >= 0) {
            resourceName = "ss-cingular240.png";
         } else if (name.indexOf("Verizon") >= 0) {
            resourceName = "ss-verizon240.png";
         }
      }

      if (resourceName == null) {
         if (name.indexOf("Dimension-Icon") >= 0) {
            return "ss-dimension-icon.png";
         }

         if (name.indexOf("Dimension-Zen") >= 0) {
            return "ss-dimension-zen.png";
         }

         if (name.indexOf("Dimension-Today") >= 0) {
            return "dimension-today.png";
         }

         if (name.indexOf("Dimension-List") >= 0) {
            return "dimension-list.png";
         }

         if (name.indexOf("Insight-List") >= 0) {
            return "ss-insight-list.png";
         }

         if (name.indexOf("Insight-Icon") >= 0) {
            return "ss-insight-icon.png";
         }

         if (name.indexOf("Cingular") >= 0) {
            return "ss-cingular.png";
         }

         if (name.indexOf("Orange") >= 0) {
            return "ss-orange.png";
         }

         if (name.indexOf("Vodafone Today") >= 0) {
            return "vodafone-today.png";
         }

         if (name.indexOf("Vodafone") >= 0) {
            resourceName = "vodafone.png";
         }
      }

      return resourceName;
   }

   private static final Bitmap loadPreviewBitmap(String resourceName) {
      String module = "net_rim_bb_setupwizard";
      Resource resource = Resource$Internal.getResourceClass(module);
      if (resource != null) {
         byte[] data = resource.getResource(resourceName);
         if (data != null) {
            EncodedImage image = EncodedImage.createEncodedImage(data, 0, -1);
            int scale = Math.max(image.getWidth() / _thumbWidth, 1);
            image.setScale(scale);
            Bitmap bitmap = new Bitmap(_thumbWidth, _thumbHeight);
            Graphics graphics = new Graphics(bitmap);
            graphics.drawImage(0, 0, _thumbWidth, _thumbHeight, image, 0, 0, 0);
            return bitmap;
         }
      }

      return null;
   }
}
