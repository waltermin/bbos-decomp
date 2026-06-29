package net.rim.device.apps.internal.options.items;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.WizardLayoutManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;

final class ThemeOptionsScreen extends AppsMainScreen implements ListFieldCallback, GlobalEventListener {
   private ListField _listField;
   private String[] _choices;
   private String[] _choicesLocalized;
   private boolean _showNone;
   private ThemeOptionsScreen$ThemePreviewField _previewField;
   private Hashtable _thumbnailCache;
   private static final double THUMBNAIL_SCALE;
   private static int _thumbnailWidth = (int)(Display.getWidth() * 4600877379321698714L);
   private static int _thumbnailHeight = (int)(Display.getHeight() * 4600877379321698714L);

   public ThemeOptionsScreen() {
      super(2814749767106560L);
      this.setTitle(OptionsResources.getString(1439));
      this.setHelp("display");
      this._thumbnailCache = (Hashtable)(new Object());
      Bitmap defaultBitmap = null;
      int formFactor = InternalServices.getFormFactor();
      if (formFactor != 9 && formFactor != 13) {
         defaultBitmap = Bitmap.getBitmapResource(ApplicationDescriptor.currentApplicationDescriptor().getModuleName(), "theme_preview_320x240.png");
      } else {
         defaultBitmap = Bitmap.getBitmapResource(ApplicationDescriptor.currentApplicationDescriptor().getModuleName(), "theme_preview_240x260.png");
      }

      this._previewField = new ThemeOptionsScreen$ThemePreviewField(OptionsResources.getString(1981), defaultBitmap);
      this._previewField.displayBitmap(null);
      this._listField = new ThemeOptionsScreen$1(this);
      this._listField.setCallback(this);
      VerticalFieldManager content = (VerticalFieldManager)(new Object(299067162755072L));
      content.setVerticalQuantization(-1);
      content.add(this._listField);
      WizardLayoutManager multiPaneLayout = (WizardLayoutManager)(new Object());
      multiPaneLayout.setContent(content);
      VerticalFieldManager footer = (VerticalFieldManager)(new Object(562949953421312L));
      footer.add((Field)(new Object()));
      footer.add(this._previewField);
      multiPaneLayout.setFooter(footer);
      this.add(multiPaneLayout);
      UiApplication.getUiApplication().addGlobalEventListener(this);
      this.refresh();
   }

   private final void refresh() {
      this._choices = ThemeManager.getNameChoices();
      this._choicesLocalized = ThemeManager.getNameChoices(Locale.getDefault());
      int count = this._choices.length;
      String activeName = ThemeManager.getActiveName();
      int activeIndex = 0;

      for (int lv = this._choices.length - 1; lv >= 0; lv--) {
         if (this._choices[lv].equals(activeName)) {
            activeIndex = lv;
            break;
         }
      }

      this._showNone = Ui.getMode() == 2 || activeIndex == 0;
      this._listField.setSize(this._showNone ? count : count - 1);
      this._listField.setSelectedIndex(this._showNone ? activeIndex : activeIndex - 1);
      Application.getApplication().invokeLater(new ThemeOptionsScreen$2(this));
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      int selectedIndex = this._listField.getSelectedIndex();
      if (selectedIndex != -1) {
         if (!this.isActive(selectedIndex)) {
            Verb defaultVerb = null;
            if (ThemeManager.isActivatable(this.getName(selectedIndex))) {
               defaultVerb = new ThemeOptionsScreen$ThemeVerb(
                  this.getLocalizedName(selectedIndex), this.getName(selectedIndex), 16865360, OptionsResources.getResourceBundle(), 1850
               );
               menu.add(defaultVerb);
               menu.setDefault(defaultVerb);
            }

            if (ThemeManager.isRemoveable(this.getName(selectedIndex))) {
               menu.add(
                  new ThemeOptionsScreen$ThemeVerb(this.getLocalizedName(selectedIndex), this.getName(selectedIndex), 611472, CommonResource.getBundle(), 17)
               );
            }
         }

         menu.add(
            new ThemeOptionsScreen$ThemeVerb(
               this.getLocalizedName(selectedIndex), this.getName(selectedIndex), 33554432, OptionsResources.getResourceBundle(), 2037
            )
         );
      }

      VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
      if (verbRepository != null) {
         Verb[] verbs = verbRepository.getVerbs(-4064892972611285119L);
         if (verbs != null && verbs.length > 0) {
            menu.add(verbs);
         }
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (result != null) {
         if (ContextObject.getFlag(result, 39)) {
            this.close();
            return;
         }

         this.refresh();
      }
   }

   private final boolean isActive(int index) {
      String activeTheme = ThemeManager.getActiveName();
      return activeTheme != null ? activeTheme.equals(this.getName(index)) : false;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      Font oldFont = null;
      StringBuffer buffer = (StringBuffer)(new Object(this.getLocalizedName(index)));
      if (this.isActive(index)) {
         oldFont = graphics.getFont();
         graphics.setFont(oldFont.derive(1));
         buffer.append(OptionsResources.getString(1851));
      }

      graphics.drawText(buffer, 0, buffer.length(), 0, y, 64, width);
      if (oldFont != null) {
         graphics.setFont(oldFont);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.getLocalizedName(index);
   }

   private final String getLocalizedName(int index) {
      return this._showNone && index == 0 ? OptionsResources.getString(1473) : this._choicesLocalized[index + (this._showNone ? 0 : 1)];
   }

   private final String getName(int index) {
      return this._choices[index + (this._showNone ? 0 : 1)];
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      int selectedIndex = this._listField.getSelectedIndex();
      if (selectedIndex != -1 && !this.isActive(selectedIndex)) {
         if (key == '\n' || key == ' ') {
            this.invokeActivateAndCloseAction(selectedIndex);
            return true;
         }

         if (key == '\b') {
            if (ThemeManager.isRemoveable(this.getName(selectedIndex))) {
               String prompt = CommonResource.format(10025, this.getLocalizedName(selectedIndex));
               if (Dialog.ask(2, prompt, -1) == 3) {
                  ThemeManager.remove(this.getName(selectedIndex));
                  this.refresh();
               }
            }

            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               int selectedIndex = this._listField.getSelectedIndex();
               if (selectedIndex != -1 && !this.isActive(selectedIndex)) {
                  this.invokeActivateAndCloseAction(selectedIndex);
               }

               return true;
         }
      }

      return handled;
   }

   private final void invokeActivateAndCloseAction(int selectedIndex) {
      String name = this.getName(selectedIndex);
      if (ThemeManager.isActivatable(name)) {
         ThemeManager.setActiveTheme(name);
         this.close();
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 9057101852544553212L || guid == -7464003439710973532L) {
         this.refresh();
      }
   }

   private final void listFocusChanged(int selectedIndex) {
      this._previewField.displayBitmap(this.getPreviewBitmap(this.fixIndex(selectedIndex)));
   }

   private final Bitmap getPreviewBitmap(int rawIndex) {
      Bitmap bitmap = null;
      String themeName = this._choices[rawIndex];
      synchronized (this._thumbnailCache) {
         if (this._thumbnailCache.containsKey(themeName)) {
            bitmap = (Bitmap)this._thumbnailCache.get(themeName);
         }

         return bitmap;
      }
   }

   private final int fixIndex(int index) {
      if (!this._showNone) {
         index++;
      }

      return index;
   }
}
