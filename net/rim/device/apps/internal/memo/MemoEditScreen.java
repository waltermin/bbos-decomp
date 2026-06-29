package net.rim.device.apps.internal.memo;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.FindVerbManager;
import net.rim.device.apps.internal.memo.resources.MemoResources;

final class MemoEditScreen extends EditorUsingRIMModelFactory {
   private int _initialTextPosition;
   private int _initialScrollPosition;
   private MemoModelImpl _originalMemo;
   private MemoModelImpl _savedMemo;
   private Field _categoriesField;
   private FindVerbManager _findVerbManager = (FindVerbManager)(new Object(this.getDelegate()));
   boolean _infoVisible;

   MemoEditScreen(MemoModelImpl memo, int initialTextPosition, int initialScrollPosition, boolean newMemo) {
      this(memo, null, initialTextPosition, initialScrollPosition, newMemo);
   }

   MemoEditScreen(MemoModelImpl memo, String pattern, int initialTextPosition, int initialScrollPosition, boolean newMemo) {
      super(new Object(0, 8), null, 8809206174646860213L, -1);
      this._originalMemo = newMemo ? null : memo;
      if (memo == null) {
         memo = new MemoModelImpl();
         if (pattern != null && pattern.length() > 0) {
            memo.add(FactoryUtil.createInstance(-4904857078378172834L, pattern));
         }
      } else if (ObjectGroup.isInGroup(memo)) {
         memo = (MemoModelImpl)ObjectGroup.expandGroup(memo);
      }

      this.setModel(memo);
      this._initialTextPosition = initialTextPosition;
      this._initialScrollPosition = initialScrollPosition;
      this._categoriesField = this.findField(RecognizerRepository.getRecognizers(-537018776823173138L));
      this.getMainManager().setTag(ThemeUtilities.MEMO_SCREEN_TAG);
   }

   @Override
   protected final Manager createManagerForField(Field f, int order) {
      Manager manager = null;
      if (order <= 0) {
         manager = (Manager)(new Object());
         manager.setTag(ThemeUtilities.MEMO_TITLE_AREA_TAG);
      }

      if (order > 0) {
         manager = (Manager)(new Object(4611686018427387904L));
         manager.setTag(ThemeUtilities.MEMO_DATA_AREA_TAG);
      }

      return manager;
   }

   @Override
   protected final int getOrderForManagerForField(Field field, int order) {
      if (order <= 0) {
         return 0;
      } else {
         return order > 0 ? 5500 : -1;
      }
   }

   @Override
   public final Object go(boolean modal) {
      super.go(modal);
      return this._savedMemo;
   }

   @Override
   public final boolean isDataValid() {
      if (!this.validateDataFromEdit()) {
         Status.show(MemoResources.getString(170));
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         AutoTextEditField bodyField = (AutoTextEditField)this.findField(new MemoEditScreen$BodyModelRecognizer(null));
         if (bodyField != null && this._initialTextPosition >= 0) {
            bodyField.setCursorPosition(this._initialTextPosition);
            Manager manager = bodyField.getManager();

            while (manager != null && !manager.isStyle(281474976710656L)) {
               manager = manager.getManager();
            }

            if (manager != null) {
               manager.setVerticalScroll(Math.max(this._initialScrollPosition - 3, 0));
            }

            bodyField.setFocus();
            return;
         }
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance != 65537) {
         super.makeMenu(menu, instance);
         MenuItem saveItem = MenuItem.getPrefab(15);
         menu.add(saveItem);
         if (instance == 0) {
            menu.add(this._findVerbManager.getVerbs());
            if (this._originalMemo != null) {
               menu.add(new DeleteMemoVerb(this._originalMemo));
            }

            VerbRepository vr = VerbRepository.getVerbRepository(1967755168374878363L);
            if (vr != null) {
               menu.add(vr.getVerbs(null));
            }

            if (this._categoriesField != null) {
               menu.add((Verb)(new Object(this._categoriesField)));
            }
         }

         if (this.isDirty()) {
            menu.setDefault(saveItem);
         }
      }
   }

   @Override
   public final void save() {
      MemoCollectionImpl memodb = MemoCollectionImpl.getInstance();
      if (this._originalMemo != null && !memodb.contains(this._originalMemo)) {
         Status.show(MemoResources.getString(171));
      } else {
         this._savedMemo = (MemoModelImpl)this.getModel();
         if (this._originalMemo == null) {
            memodb.add(this._savedMemo);
         } else {
            memodb.update(this._originalMemo, this._savedMemo);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      ContextObject context = ContextObject.castOrCreate(parameter);
      boolean var7 = false /* VF: Semaphore variable */;

      Object var4;
      try {
         var7 = true;
         context.put(3696141428889703675L, super._model);
         var4 = super.invokeVerb(verb, context);
         var7 = false;
      } finally {
         if (var7) {
            context.remove(3696141428889703675L);
         }
      }

      context.remove(3696141428889703675L);
      return var4;
   }

   @Override
   protected final boolean openProductionBackdoor(int backDoor) {
      switch (backDoor) {
         case 1447642454:
            return super.openProductionBackdoor(backDoor);
         case 1447642455:
         default:
            if (!this._infoVisible) {
               this.insertInfo();
            }

            return true;
      }
   }

   private final void insertInfo() {
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      vfm.add((Field)(new Object(((StringBuffer)(new Object("RefId: "))).append(((MemoModelImpl)this.getModel()).getUID()).toString(), 18014398509481984L)));
      vfm.add((Field)(new Object()));
      this.insert(vfm, 0);
      vfm.setFocus();
      this._infoVisible = true;
   }
}
