package net.rim.device.apps.internal.globalsearch;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.api.ui.InvokeLaterRunnable;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.vm.WeakReference;

final class SearchResultsFieldManager extends VerticalFieldManager implements CollectionListener {
   private boolean _expanded;
   private boolean _firstExpansion = true;
   private SearchResultCollection _collection;
   private String _label;
   private LabelField _nodeField;
   private ImageField _expansionIcon = (ImageField)(new Object());
   private Field _resultsField;
   private Field _separatorField;
   private Field _emptyLabelField;
   private Image _treePlus = SystemIcon.COLLECTION.getImage(6);
   private Image _treeMinus = SystemIcon.COLLECTION.getImage(5);
   private HorizontalFieldManager _hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
   private WeakReference _nodeFieldLabelWR = (WeakReference)(new Object(null));
   private Application _application = Application.getApplication();
   private SearchResultsFieldManager$UpdateNodeFieldRunnable _updateNodeFieldRunnable = new SearchResultsFieldManager$UpdateNodeFieldRunnable(this, null);
   private InvokeLaterRunnable _invokeLaterRunnable = (InvokeLaterRunnable)(new Object());

   SearchResultsFieldManager(Searchable searchable, long id, SearchResultCollection collection, boolean displayIcon) {
      super(0);
      this._label = searchable.getName(id);
      this._collection = collection;
      this._collection.addCollectionListener(new Object(this));
      this._nodeField = new SearchResultsFieldManager$LabelFieldWithoutClipboard(this._label);
      this._nodeField.setFont(this.getFont().derive(1));
      this.updateNodeField();
      EncodedImage icon = searchable.getIcon(id);
      if (displayIcon && icon != null) {
         this._nodeField.setImage(Bitmap.createBitmapFromBytes(icon.getData(), 0, -1, icon.getHeight() / this.getFont().getHeight()));
      }

      this._expansionIcon.setPreferredSize(this.getFont().getHeight(), this.getFont().getHeight());
      this._expansionIcon.setImage(this._treePlus);
      this._hfm.add(this._expansionIcon);
      this._hfm.add(this._nodeField);
      this.add(this._hfm);
      if (collection instanceof Object) {
         this._resultsField = ((FieldProvider)collection).getField(new Object(78));
      }

      this._separatorField = (Field)(new Object());
      this._emptyLabelField = (Field)(new Object(null));
   }

   final boolean getExpanded() {
      return this._expanded;
   }

   final void toggleExpansion() {
      this._expanded = !this._expanded;
      this._hfm.setFieldWithFocus(this._nodeField);
      this.setFieldWithFocus(this._hfm);
      if (this._expanded) {
         this._expansionIcon.setImage(this._treeMinus);
         this.add(this._resultsField);
         this.add(this._separatorField);
         this.add(this._emptyLabelField);
         if (this._firstExpansion && this._resultsField instanceof Object) {
            ListField listField = (ListField)this._resultsField;
            this._firstExpansion = false;
            listField.setSelectedIndex(0);
         }
      } else {
         this._expansionIcon.setImage(this._treePlus);
         this.delete(this._resultsField);
         this.delete(this._separatorField);
         this.delete(this._emptyLabelField);
      }

      UiApplication.getUiApplication().relayout();
      this.invalidate();
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (super.keyChar(character, status, time)) {
         return true;
      } else if ((character == ' ' || character == '\n') && this.getFieldWithFocus() == this._hfm) {
         this.toggleExpansion();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.getFieldWithFocus() == this._hfm) {
               this.toggleExpansion();
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   public final void focusChangeNotify(int action) {
      if (action == 1 && !this._expanded) {
         this._hfm.setFieldWithFocus(this._nodeField);
         this.setFieldWithFocus(this._hfm);
      } else {
         super.focusChangeNotify(action);
      }
   }

   private final void updateNodeField() {
      synchronized (this._invokeLaterRunnable) {
         this._invokeLaterRunnable.setRunnable(this._updateNodeFieldRunnable);
         if (this._invokeLaterRunnable.doneProcessing()) {
            this._invokeLaterRunnable.resetDoneProcessing();
            this._application.invokeLater(this._invokeLaterRunnable);
         } else {
            this._invokeLaterRunnable.resetDoneProcessing();
         }
      }
   }

   private final void doUpdateNodeField() {
      StringBuffer sb = WeakReferenceUtilities.getStringBuffer(this._nodeFieldLabelWR);
      sb.setLength(0);
      sb.append(this._label);
      sb.append(' ');
      sb.append('(');
      sb.append(this._collection.size());
      sb.append(')');
      this._nodeField.setText(sb);
   }

   @Override
   public final void reset(Collection collection) {
      this.updateNodeField();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.updateNodeField();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.updateNodeField();
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.updateNodeField();
   }
}
