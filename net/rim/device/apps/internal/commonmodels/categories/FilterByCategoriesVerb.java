package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;

public final class FilterByCategoriesVerb extends DisplayCategoriesVerb {
   private int[] _selectedCategoryIds;
   private KeywordFilteredScreen _screen;
   private FilteredByCategoriesTitleField _title;

   public FilterByCategoriesVerb(KeywordFilteredScreen screen, int[] selectedCategoryIds) {
      super(CommonResources.getResourceBundle(), 9108);
      if (screen == null) {
         throw new IllegalArgumentException();
      }

      this._screen = screen;
      this._selectedCategoryIds = selectedCategoryIds;
   }

   public FilterByCategoriesVerb(KeywordFilteredScreen screen, FilteredByCategoriesTitleField title, int[] selectedCategoryIds) {
      this(screen, selectedCategoryIds);
      this._title = title;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this.displayCategories(this._selectedCategoryIds, (byte)2)) {
         filterByCategories(this._screen, this._title, this._selectedCategoryIds);
      }

      return null;
   }

   public static final void filterByCategories(KeywordFilteredScreen screen, FilteredByCategoriesTitleField title, int[] selectedCategoryIds) {
      screen.getKeywordFilterList().setSuffix(CategoryList.getInstance().getCategoryKeys(selectedCategoryIds));
      if (title != null) {
         title.updateCategories(selectedCategoryIds);
      }
   }
}
