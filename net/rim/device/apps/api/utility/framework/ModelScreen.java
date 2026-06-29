package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.OpenViewer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.vm.Array;

public class ModelScreen extends AppsMainScreen implements ModelUser, GlobalEventListener, OpenViewer {
   protected Verb[] _verbs = new Verb[0];
   protected Object _model;
   protected Object _context;
   protected Verb _defaultVerb;
   protected Verb _leaveScreenVerb;
   protected Object _returnValue;
   protected Application _application;
   private Manager _mainManager;

   public Field findField(Recognizer recognizer) {
      return this.findField(this.getMainManager(), recognizer);
   }

   public Field getModelFieldWithFocus() {
      Field field = this.getFieldWithFocus();

      while (field instanceof Object && field.getCookie() == null) {
         field = ((Manager)field).getFieldWithFocus();
      }

      return field;
   }

   public void setSecurityServiceColours(boolean itPolicyService) {
      String tagString;
      if (itPolicyService) {
         tagString = "security-service-message-itpolicy";
      } else {
         tagString = "security-service-message";
      }

      Tag tag = Tag.get(tagString);
      if (tag != null) {
         Manager manager = this.getMainManager();
         manager.setTag(tag);
      }
   }

   public int getFieldIndex(Field field) {
      return this.getFieldIndex(this.getMainManager(), field);
   }

   public void setLeaveScreenVerb(Verb leaveScreenVerb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void leaveScreen() {
      if (this._leaveScreenVerb != null) {
         this.invokeVerb(this._leaveScreenVerb, this._context);
      }
   }

   protected final void invokedVerb(Verb verb, Object returnValue) {
      ContextObject contextObject = null;
      Object previousModel = null;
      if (this._context instanceof ContextObject) {
         contextObject = (ContextObject)this._context;
         previousModel = contextObject.get(254);
         contextObject.put(254, this._model);
      }

      this._returnValue = returnValue;
      if (contextObject != null) {
         if (previousModel != null) {
            contextObject.put(254, previousModel);
         } else {
            contextObject.remove(254);
         }
      }

      if (ContextObject.getFlag(this._returnValue, 39)) {
         try {
            UiApplication.getUiApplication().popScreen(this);
         } finally {
            return;
         }
      }
   }

   protected Object loadFocusedFieldVerbs(SystemEnabledMenu menu, Field fieldWithFocus, Field leafFieldWithFocus) {
      Object fieldCookie = null;
      Object leafFieldCookie = null;
      Object returnCookie = null;
      ContextObject contextObject = ContextObject.castOrCreate(this._context);
      boolean oldFocus = contextObject.getFlag(2);
      contextObject.setFlag(2);
      boolean oldEditable = contextObject.getFlag(0);
      if (fieldWithFocus != null) {
         Field originalFieldWithFocus = fieldWithFocus;

         do {
            fieldCookie = fieldWithFocus.getCookie();
            if (fieldCookie == null) {
               if (!(fieldWithFocus instanceof Object)) {
                  break;
               }

               fieldWithFocus = ((Manager)fieldWithFocus).getFieldWithFocus();
            }
         } while (fieldCookie == null && fieldWithFocus != null);

         if (fieldWithFocus == null) {
            fieldWithFocus = originalFieldWithFocus;
         }

         if (fieldWithFocus.isEditable()) {
            contextObject.setFlag(0);
         } else {
            contextObject.clearFlag(0);
         }
      }

      if (leafFieldWithFocus != null) {
         leafFieldCookie = leafFieldWithFocus.getCookie();
      }

      if (leafFieldCookie instanceof RIMModel) {
         this.loadFieldVerbs(menu, leafFieldWithFocus, (RIMModel)leafFieldCookie, contextObject);
         returnCookie = leafFieldCookie;
      }

      if (oldEditable) {
         contextObject.setFlag(0);
      } else {
         contextObject.clearFlag(0);
      }

      if (!oldFocus) {
         contextObject.clearFlag(2);
      }

      if (fieldCookie instanceof RIMModel && fieldCookie != leafFieldCookie) {
         this.loadFieldVerbs(menu, fieldWithFocus, (RIMModel)fieldCookie, contextObject);
         returnCookie = fieldCookie;
      }

      return returnCookie;
   }

   protected boolean handleKeyChar(char key, int status, int time) {
      if (key == 27 && this._leaveScreenVerb != null) {
         this.leaveScreen();
         return true;
      } else {
         return false;
      }
   }

   public Object go() {
      return this.go(true);
   }

   public Object go(boolean modal) {
      if (modal) {
         UiApplication.getUiApplication().pushModalScreen(this);
      } else {
         UiApplication.getUiApplication().pushScreen(this);
      }

      return this._returnValue;
   }

   public Field findFieldByCookie(Object cookieToCheckFor) {
      return findFieldByCookie(this.getMainManager(), cookieToCheckFor);
   }

   @Override
   public Object getModel() {
      return this._model;
   }

   @Override
   public long getModelType(Object context) {
      return 0;
   }

   @Override
   public RIMModel getOpenedModel(Object context) {
      return (RIMModel)this._model;
   }

   @Override
   public void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
   }

   @Override
   public Object getModel(boolean removeEmpty) {
      return this._model;
   }

   @Override
   public void setModel(Object model) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 1202366544244619460L && object0 == this._model) {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   @Override
   protected Object invokeVerb(Verb verb, Object parameter) {
      this._returnValue = verb.invoke(parameter);
      this.invokedVerb(verb, this._returnValue);
      return this._returnValue;
   }

   @Override
   public void setContext(ContextObject context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance != 65537) {
         if (this._leaveScreenVerb != null && instance != 65536) {
            menu.add(this._leaveScreenVerb);
         }

         this.loadFocusedFieldVerbs(menu, this.getFieldWithFocus(), this.getLeafFieldWithFocus());
      }
   }

   protected static Field findFieldByCookie(Field field, Object cookieToCheckFor) {
      Object cookie = field.getCookie();
      if (cookie == cookieToCheckFor) {
         return field;
      }

      if (field instanceof Object) {
         Manager manager = (Manager)field;
         int size = manager.getFieldCount();
         Field fieldFound = null;

         for (int i = 0; i < size && fieldFound == null; i++) {
            Field f = manager.getField(i);
            fieldFound = findFieldByCookie(f, cookieToCheckFor);
            if (fieldFound != null) {
               return fieldFound;
            }
         }
      }

      return null;
   }

   private int getFieldIndex(Manager manager, Field field) {
      int size = manager.getFieldCount();
      int fieldIndex = 0;

      for (int i = 0; i < size; fieldIndex++) {
         Field f = manager.getField(i);
         if (f == field) {
            return fieldIndex;
         }

         if (f instanceof Object) {
            int fieldIndexWithinManager = this.getFieldIndex((Manager)f, field);
            if (fieldIndexWithinManager >= 0) {
               return fieldIndex + fieldIndexWithinManager + 1;
            }

            fieldIndex += -fieldIndexWithinManager;
         }

         i++;
      }

      return -size;
   }

   @Override
   protected ContextObject getContext() {
      return (ContextObject)this._context;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         UiApplication.getUiApplication().addGlobalEventListener(this);
      } else {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
         this._model = null;
         this._context = null;
      }

      super.onUiEngineAttached(attached);
   }

   private void loadFieldVerbs(SystemEnabledMenu menu, Field field, RIMModel model, Object context) {
      if (model instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)model;
         ContextObject.put(context, 9045827404276417370L, field);
         Verb defaultVerb = verbProvider.getVerbs(context, this._verbs);
         Recognizer verbRecognizer = (Recognizer)ContextObject.get(context, -409744358660961448L);
         if (verbRecognizer != null) {
            for (int i = 0; i < this._verbs.length; i++) {
               if (verbRecognizer.recognize(this._verbs[i])) {
                  menu.add(this._verbs[i]);
               }
            }
         } else {
            menu.add(this._verbs);
         }

         ContextObject.remove(context, 9045827404276417370L);
         Array.resize(this._verbs, 0);
         if (menu.getDefaultVerb() == null && defaultVerb != null) {
            this._defaultVerb = defaultVerb;
            menu.setDefault(defaultVerb);
         }
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean result = super.keyDown(keycode, time);
      if (!result && Keypad.key(keycode) == 21) {
         result = ControllerUtilities.invokeApplicationKeyVerb(this.getModel(false));
      }

      return result;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      return super.keyChar(key, status, time) ? true : this.handleKeyChar(key, status, time);
   }

   public ModelScreen(long style, String title, Object context) {
      super(validateStyle(style));
      if ((style & 8796093022208L) != 0) {
         ModelScreen$ScrollbarManager scrollbarManager = new ModelScreen$ScrollbarManager(style);
         this._mainManager = scrollbarManager.getContentManager();
         super.getMainManager().add(scrollbarManager);
      } else {
         this._mainManager = super.getMainManager();
      }

      this._application = Application.getApplication();
      this._context = context;
      if (title != null) {
         this.setTitle((Field)(new Object(title)));
      }
   }

   public ModelScreen(long style, boolean verticalScrollbar, String title, Object context) {
      this(style | (verticalScrollbar ? 17592186044416L : 0), title, context);
   }

   private Field findField(Manager manager, Recognizer recognizer) {
      int size = manager.getFieldCount();

      for (int i = 0; i < size; i++) {
         Field field = manager.getField(i);
         Object cookie = field.getCookie();
         if (recognizer.recognize(cookie)) {
            return field;
         }

         if (field instanceof Object) {
            field = this.findField((Manager)field, recognizer);
            if (field != null) {
               return field;
            }
         }
      }

      return null;
   }

   private static long validateStyle(long style) {
      if ((style & 8796093022208L) != 0) {
         style &= -8796093022209L;
         return style | 562949953421312L | 35184372088832L;
      } else {
         return style | 17592186044416L;
      }
   }

   @Override
   public Manager getMainManager() {
      return this._mainManager;
   }

   @Override
   public void deleteRange(int start, int count) {
      this.getMainManager().deleteRange(start, count);
   }

   @Override
   public void add(Field field) {
      this.getMainManager().add(field);
   }

   @Override
   public void delete(Field field) {
      this.getMainManager().delete(field);
   }

   @Override
   public void insert(Field field, int index) {
      this.getMainManager().insert(field, index);
   }
}
