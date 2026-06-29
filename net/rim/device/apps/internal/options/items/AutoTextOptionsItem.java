package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class AutoTextOptionsItem extends MainScreenOptionsListItem {
   private AutoTextOptionsItem$AutoTextScreen _autoTextScreen;
   private static AutoText _autoTextEngine;
   private static Verb _newAutoTextUnitVerb;

   public AutoTextOptionsItem() {
      super(OptionsResources.getString(302), new Object(3, 5, 2));
   }

   public static final void initializeAutoTextOptions() {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(1247995981604341294L);
      verbRepository.register(new AutoTextOptionsItem$EditAutoTextVerb(), 4738722199580714034L);
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (_autoTextEngine == null) {
         _autoTextEngine = AutoText.getAutoText();
      }

      if (_newAutoTextUnitVerb == null) {
         _newAutoTextUnitVerb = new AutoTextOptionsItem$NewAutoTextUnitVerb(this);
      }
   }

   @Override
   protected final MainScreen createMainScreen() {
      ReadableList sortedList = new AutoTextOptionsItem$AutoTextSortedReadableList(_autoTextEngine, new AutoTextOptionsItem$AutoTextComparator());
      AutoTextOptionsItem$AutoTextScreen autoTextScreen = new AutoTextOptionsItem$AutoTextScreen(
         this, this.getDisplayName(), (KeywordFilterList)(new Object(sortedList, new AutoTextOptionsItem$AutoTextIndexHelper()))
      );
      this._autoTextScreen = autoTextScreen;
      return this._autoTextScreen;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }

   static final Object access$200(AutoTextOptionsItem x0) {
      return x0._context;
   }

   static final Object access$400(AutoTextOptionsItem x0) {
      return x0._context;
   }

   static final Object access$500(AutoTextOptionsItem x0) {
      return x0._context;
   }

   static final void access$600(AutoTextOptionsItem x0) {
      x0.open();
   }
}
