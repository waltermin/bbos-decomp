package net.rim.device.apps.internal.setupwizard;

import java.util.Vector;
import net.rim.device.api.ui.GlyphMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.WizardController;
import net.rim.device.apps.api.setupwizard.WizardPage;

final class WizardListField extends ListField implements ListFieldCallback {
   private WizardController _pages;
   private Vector _filteredPages;
   private WizardCategory _currentCategory;
   private int _categoryMode;
   private ThemeAttributeSet _themeAttributesHeader;
   private int _themeGeneration;
   private Verb _defaultAction;
   private static Tag TAG_HEADER = Tag.create("header");

   public final int getCategoryMode() {
      return this._categoryMode;
   }

   public final void setCategoryFilter(WizardCategory category) {
      if (this._categoryMode == 2) {
         this._currentCategory = category;
         this._filteredPages.removeAllElements();
         int numPages = this._pages.size();
         if (category == null) {
            for (int i = 0; i < numPages; i++) {
               WizardPage page = this._pages.getPage(i);
               if (page.getCategory() == null || page instanceof Object) {
                  this._filteredPages.addElement(page);
               }
            }
         } else {
            this._filteredPages.addElement(category);

            for (int i = 0; i < numPages; i++) {
               WizardPage page = this._pages.getPage(i);
               if (category.equals(page.getCategory())) {
                  this._filteredPages.addElement(page);
               }
            }
         }

         this.setSize(this._filteredPages.size());
         this.invalidate();
      }
   }

   public final WizardCategory getCategoryFilter() {
      return this._currentCategory;
   }

   public final WizardPage getPage(int index) {
      return (WizardPage)this._filteredPages.elementAt(index);
   }

   public final void setSelectedPage(WizardPage page) {
      if (page != null && !page.isHidden()) {
         if (page instanceof Object) {
            this.selectCategory((WizardCategory)page);
         } else {
            int numPages = this._filteredPages.size();
            int firstPageIndex = -1;

            for (int i = 0; i < numPages; i++) {
               WizardPage itrPage = (WizardPage)this._filteredPages.elementAt(i);
               if (firstPageIndex == -1 && !(itrPage instanceof Object)) {
                  firstPageIndex = i;
               }

               if (itrPage == page) {
                  this.setSelectedIndex(i);
                  return;
               }
            }

            if (firstPageIndex != -1) {
               this.setSelectedIndex(firstPageIndex);
            }
         }
      }
   }

   protected final boolean doEscape() {
      boolean handled = false;
      if (this._categoryMode == 2) {
         WizardCategory category = this.getCategoryFilter();
         if (category != null) {
            this.setCategoryFilter(category.getCategory());
            this.setSelectedPage(category);
            handled = true;
         }
      }

      return handled;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      WizardPage page = this.getPage(index);
      if ((this._categoryMode != 1 || !(page instanceof Object))
         && (this._categoryMode != 2 || this._currentCategory == null || !this._currentCategory.equals(page))) {
         String title = page.getTitle();
         graphics.drawText(title, 0, y, 64, width);
         if (page instanceof Object) {
            GlyphMetrics metrics = (GlyphMetrics)(new Object());
            graphics.getFont().getGlyphMetrics('►', metrics);
            graphics.drawText("►", width - metrics.iBitmapWidth - 4, y);
         }
      } else {
         int height = graphics.getFont().getHeight();
         int themeGeneration = ThemeManager.getGeneration();
         if (themeGeneration != this._themeGeneration) {
            this._themeGeneration = themeGeneration;
            this._themeAttributesHeader = ThemeManager.getActiveTheme().getAttributeSet(TAG_HEADER);
         }

         listField.setThemeAttributesSpecialClear(true);
         graphics.pushRegion(0, y, width, height, 0, 0);
         listField.setThemeAttributesSpecial(this._themeAttributesHeader, graphics);
         String title = page.getTitle();
         graphics.setFont(listField.getFont().derive(1));
         graphics.drawText(title, 0, title.length(), 0, 0, 68, width);
         listField.setThemeAttributesSpecial(null, null);
         graphics.popContext();
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.getPage(index).getTitle();
   }

   private final int skipOverCategory(int amount, int originalIndex) {
      if (this._categoryMode != 1) {
         return 0;
      }

      int currentIndex = this.getSelectedIndex();
      int numPages = this.getSize();
      if (currentIndex >= 0 && this._filteredPages.elementAt(currentIndex) instanceof Object) {
         int newIndex = currentIndex;
         if (amount < 0) {
            do {
               newIndex--;
            } while (newIndex >= 0 && this._filteredPages.elementAt(newIndex) instanceof Object);
         }

         if (newIndex < 0 || amount > 0) {
            newIndex = currentIndex;

            do {
               newIndex++;
            } while (newIndex < numPages && this._filteredPages.elementAt(newIndex) instanceof Object);
         }

         if (newIndex >= 0 && newIndex < numPages) {
            this.setSelectedIndex(newIndex);
         }

         if (newIndex != originalIndex) {
            amount = 0;
         }

         return amount;
      } else {
         return 0;
      }
   }

   private final void selectCategory(WizardCategory category) {
      int numPages = this._filteredPages.size();

      for (int i = 0; i < numPages; i++) {
         WizardPage page = (WizardPage)this._filteredPages.elementAt(i);
         if (page instanceof Object && page == category) {
            this.setSelectedIndex(i);
            return;
         }
      }
   }

   public WizardListField(WizardController pages, int categoryMode, Verb defaultAction) {
      super(pages.size());
      this._defaultAction = defaultAction;
      this._categoryMode = categoryMode;
      this.setCallback(this);
      this._pages = pages;
      this._filteredPages = (Vector)(new Object());
      if (this._categoryMode == 2) {
         this.setCategoryFilter(null);
      } else {
         int numPages = this._pages.size();

         for (int i = 0; i < numPages; i++) {
            this._filteredPages.addElement(this._pages.getPage(i));
         }

         this.setSize(this._filteredPages.size());
         this.invalidate();
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 27 && this.doEscape()) {
         return true;
      } else if (key == 10 && this._defaultAction != null) {
         this._defaultAction.invoke(this);
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int originalIndex = this.getSelectedIndex();
      int moveFocusResult = super.moveFocus(amount, status, time);
      int skipResult = this.skipOverCategory(amount, originalIndex);
      return moveFocusResult + skipResult;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this._defaultAction != null) {
         this._defaultAction.invoke(this);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this._defaultAction != null) {
               this._defaultAction.invoke(this);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }
}
