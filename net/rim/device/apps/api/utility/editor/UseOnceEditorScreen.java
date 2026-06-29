package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.SendKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.vm.Array;

public class UseOnceEditorScreen extends AppsMainScreen {
   private RIMModel _model;
   private boolean _modelComplete;
   private FieldProvider _fieldProvider;
   private Field _modelField;
   private RIMModel _tmpValidationModel;
   private Verb _continueVerb = new UseOnceEditorScreen$ContinueVerb(this);
   private Verb _cancelVerb = new UseOnceEditorScreen$CancelVerb(this);
   private Verb[] _verbs = new Verb[0];
   private Object _context;
   static final long USE_ONCE_REGISTRY;

   private UseOnceEditorScreen(String title, RIMModel model, FieldProvider fieldProvider, Field modelField, RIMModel tmpValidationModel, Object context) {
      super(0);
      this.setDefaultClose(false);
      this.setTitle(title);
      this.add(modelField);
      this._model = model;
      this._fieldProvider = fieldProvider;
      this._modelField = modelField;
      this._tmpValidationModel = tmpValidationModel;
      this._context = context;
      modelField.setFocus();
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof Object) {
         EditField editField = (EditField)field;
         editField.setCursorPosition(editField.getText().length());
      }
   }

   private RIMModel getModel() {
      return this._modelComplete ? this._model : null;
   }

   public static RIMModel showUseOnceScreen(String title, long objectId, String initialValue) {
      return showUseOnceScreen(title, objectId, initialValue, null);
   }

   public static RIMModel showUseOnceScreen(String title, long objectId, String initialValue, Object context) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      UseOnceEditorScreen$MyLongHashtable useOnceRegistry = (UseOnceEditorScreen$MyLongHashtable)ar.getOrWaitFor(-1985753716985742011L);
      if (useOnceRegistry == null) {
         useOnceRegistry = new UseOnceEditorScreen$MyLongHashtable();
         ar.put(-1985753716985742011L, useOnceRegistry);
      }

      Factory factory = (Factory)ar.waitFor(objectId);
      ContextObject contextObject = new ContextObject();
      RIMModel previousModel = (RIMModel)useOnceRegistry.get(objectId);
      if (initialValue != null) {
         contextObject.put(253, initialValue);
      } else if (previousModel != null) {
         contextObject.put(254, previousModel);
      }

      RIMModel model = (RIMModel)factory.createInstance(contextObject);
      contextObject.remove(253);
      contextObject.remove(254);
      if (model instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)model;
         context = ContextObject.clone(context);
         ContextObject.setFlag(context, 0);
         Field modelField = fieldProvider.getField(context);
         if (modelField != null) {
            contextObject.put(254, model);
            UseOnceEditorScreen screen = new UseOnceEditorScreen(
               title, model, fieldProvider, modelField, (RIMModel)factory.createInstance(contextObject), context
            );
            UiApplication.getUiApplication().pushModalScreen(screen);
            model = screen.getModel();
            if (model != null) {
               useOnceRegistry.put(objectId, model);
               contextObject.put(254, model);
               model = (RIMModel)factory.createInstance(contextObject);
            }

            return model;
         }
      }

      return null;
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               return this.invokeContinueAction();
         }
      }

      return handled;
   }

   @Override
   protected boolean handleSendKey() {
      if (this._tmpValidationModel instanceof FieldProvider) {
         FieldProvider fp = (FieldProvider)this._tmpValidationModel;
         if (fp.grabDataFromField(this._modelField, null) && this._tmpValidationModel instanceof VerbProvider) {
            ContextObject context = ContextObject.clone(this._context);
            context.setFlag(119, 2, 125);
            Verb[] verbs = new Verb[0];
            Verb verb = ((VerbProvider)this._tmpValidationModel).getVerbs(context, verbs);
            if (verb instanceof SendKeyInvocableVerb) {
               this._continueVerb.invoke(null);
            }
         }
      }

      return true;
   }

   protected boolean invokeContinueAction() {
      if (this._tmpValidationModel instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)this._tmpValidationModel;
         if (fieldProvider.grabDataFromField(this._modelField, null)) {
            this._continueVerb.invoke(null);
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = super.keyChar(key, status, time);
      if (!result) {
         if (key == '\n') {
            return this.invokeContinueAction();
         }

         if (key == 27) {
            this._model = null;
            this._modelComplete = false;
            UiApplication.getUiApplication().popScreen(this);
            return true;
         }
      }

      return result;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._fieldProvider instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)this._fieldProvider;
         verbProvider.getVerbs(new ContextObject(0), this._verbs);
         menu.add(this._verbs);
         Array.resize(this._verbs, 0);
      }

      menu.add(this._cancelVerb);
      menu.setDefault(this._cancelVerb);
      if (this._tmpValidationModel instanceof FieldProvider) {
         FieldProvider fieldProvider = (FieldProvider)this._tmpValidationModel;
         menu.add(this._continueVerb);
         menu.setDefault(this._continueVerb);
      }
   }
}
