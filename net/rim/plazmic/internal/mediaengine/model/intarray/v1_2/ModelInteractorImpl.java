package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.event.EventSubscriptionHelper;
import net.rim.plazmic.internal.mediaengine.service.EventSubscription;
import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.ContainerNode;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.SVGNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaManager;

public final class ModelInteractorImpl extends AnimationModel implements ModelInteractor, EventSubscription {
   private PME_Reader _pme12Reader = new PME_Reader();
   private MediaManager _manager;
   private Event _event = (Event)(new Object());
   private EventSubscriptionHelper _subscription = (EventSubscriptionHelper)(new Object());
   private EventEngine _engine;
   private Object _eventResolver;
   private Object _behaviorManager;
   private Node[] _nodeInstances;
   public static final int AVERAGE_NODE_CAPACITY;

   public final int deleteNodeHandle(boolean keepChildren, int nodeToDelete) {
      return NodeImpl.deleteNodeHandle(keepChildren, nodeToDelete, this);
   }

   public final int deleteNodeHandle(int nodeToDelete) {
      return NodeImpl.deleteNodeHandle(nodeToDelete, this);
   }

   public final int removeNodeHandle(boolean leaveChildren, int nodeToRemove) {
      return NodeImpl.removeNodeHandle(leaveChildren, nodeToRemove, this);
   }

   public final int removeNodeHandle(int nodeToRemove) {
      return NodeImpl.removeNodeHandle(nodeToRemove, this);
   }

   public final void triggerUpdate() {
      if (this._engine != null) {
         if (!this._engine.eventScheduled(24)) {
            this._event._event = 24;
            this._event._time = -100000;
            this._event._sender = this;
            this._event._listener = this._behaviorManager;
            this._engine.postEvent(this._event, true);
            this._event.clear();
         }
      }
   }

   public final void insertPreviousSibling(int nodeToInsert, int referenceNode) {
      NodeImpl.insertPreviousSibling(nodeToInsert, referenceNode, this);
   }

   public final void insertNextSibling(int nodeToInsert, int referenceNode) {
      NodeImpl.insertNextSibling(nodeToInsert, referenceNode, this);
   }

   public final void notify(Event event) {
      this._subscription.dispatchEvent(event);
   }

   public final void insertLastChild(int nodeToInsert, int referenceNode) {
      ContainerNodeImpl.insertLastChild(nodeToInsert, referenceNode, this);
   }

   public final void setMediaManager(MediaManager manager) {
      this._manager = manager;
   }

   public final void parseIntoModel(byte[] pme, ModelInteractorImpl$RootsHandles roots, int bitShift, int offsetX, int offsetY, int maxValue) {
      if (this._manager == null) {
         this._manager = (MediaManager)(new Object());
      }

      this._pme12Reader.readIntoModel(this, roots, pme, 0, ResourceContext.createContext(), this._manager, bitShift, offsetX, offsetY, maxValue);
   }

   public final void parseIntoModel(byte[] pme, ModelInteractorImpl$RootsHandles roots) {
      this.parseIntoModel(pme, roots, 0, 0, 0, 32768);
   }

   public final void insertFirstChild(int nodeToInsert, int referenceNode) {
      ContainerNodeImpl.insertFirstChild(nodeToInsert, referenceNode, this);
   }

   public final int getRootHandle() {
      return super._visualRoot;
   }

   public final String[] getCustomEventIds() {
      String[] result = new Object[0];

      for (int i = 0; i < super._ids.length; i++) {
         int handle = super._handlesWithId[i];
         if (super._nodes[handle + 1] == 98) {
            super._platform.arrayResize(result, result.length + 1);
            result[result.length - 1] = super._ids[i];
         }
      }

      return result;
   }

   final int createNodeHandle(int type, int size) {
      return this.addNode(type, size);
   }

   public final int createVisualNodeHandle(int type) {
      int handle = -1;
      switch (type) {
         case 32:
            handle = this.createNodeHandle(type, 30);
            TSpanNodeImpl.init(handle, this);
         default:
            return handle;
         case 34:
            handle = this.createNodeHandle(type, 25);
            EllipseNodeImpl.init(handle, this);
            return handle;
         case 36:
            handle = this.createNodeHandle(type, 25);
            RectNodeImpl.init(handle, this);
            return handle;
         case 40:
            handle = this.createNodeHandle(type, 27);
            PathNodeImpl.init(handle, this);
            return handle;
         case 42:
            handle = this.createNodeHandle(type, 30);
            ImageNodeImpl.init(handle, this);
            return handle;
         case 44:
            handle = this.createNodeHandle(type, 31);
            ForeignObjectNodeImpl.init(handle, this);
            return handle;
         case 46:
            handle = this.createNodeHandle(type, 30);
            SVGNodeImpl.init(handle, this);
            return handle;
         case 48:
            handle = this.createNodeHandle(type, 25);
            GroupNodeImpl.init(handle, this);
            return handle;
         case 50:
            handle = this.createNodeHandle(type, 30);
            TextNodeImpl.init(handle, this);
            return handle;
      }
   }

   final void removeNodeInstance(int UID) {
      if (this._nodeInstances != null && this._nodeInstances.length > UID) {
         this._nodeInstances[UID] = null;
      }
   }

   final void addNodeInstance(int UID, Node node) {
      if (this._nodeInstances == null) {
         this._nodeInstances = new Object[super._maxUID];
      } else if (this._nodeInstances.length < super._maxUID) {
         super._platform.arrayResize(this._nodeInstances, super._maxUID);
      }

      this._nodeInstances[UID] = node;
   }

   public final Node getNodeObject(int nodeHandle) {
      if (nodeHandle < 0) {
         return null;
      }

      int UID = super._nodes[nodeHandle + 9];
      if (this._nodeInstances != null && this._nodeInstances.length > UID && this._nodeInstances[UID] != null) {
         return this._nodeInstances[UID];
      }

      VisualNode node = null;
      switch (super._nodes[nodeHandle + 1]) {
         case 32:
            node = new TSpanNodeImpl(nodeHandle, this);
            break;
         case 34:
            node = new EllipseNodeImpl(nodeHandle, this);
            break;
         case 36:
            node = new RectNodeImpl(nodeHandle, this);
            break;
         case 40:
            node = new PathNodeImpl(nodeHandle, this);
            break;
         case 42:
            node = new ImageNodeImpl(nodeHandle, this);
            break;
         case 44:
            node = new ForeignObjectNodeImpl(nodeHandle, this);
            break;
         case 46:
            node = new SVGNodeImpl(nodeHandle, this);
            break;
         case 48:
            node = new GroupNodeImpl(nodeHandle, this);
            break;
         case 50:
            node = new TextNodeImpl(nodeHandle, this);
      }

      this.addNodeInstance(UID, node);
      return node;
   }

   @Override
   public final Node getNode(int handle) {
      return this.getNodeObject(handle);
   }

   @Override
   public final VisualNode createVisualNode(int type) {
      return (VisualNode)this.getNodeObject(this.createVisualNodeHandle(type));
   }

   @Override
   public final Node getNode(String id) {
      int handle = this.getHandle(id);
      return handle >= 0 ? this.getNodeObject(handle) : null;
   }

   @Override
   public final int getHandle(String id) {
      int nodeIdx = this.getIdIndex(id);
      return nodeIdx != -1 ? super._handlesWithId[nodeIdx] : -1;
   }

   @Override
   public final SVGNode getRoot() {
      return (SVGNode)this.getNodeObject(super._visualRoot);
   }

   @Override
   public final void initModel(int initialNodeCapacity) {
      super._nodes = new int[initialNodeCapacity * 50];
      super._isZoomAndPannable = true;
   }

   @Override
   public final void insertFirstChild(Node nodeToInsert, ContainerNode referenceNode) {
      referenceNode.insertFirstChild(nodeToInsert);
   }

   @Override
   public final void notify(int event, int eventParam, Object data) {
      this._subscription.dispatchEvent(this, event, eventParam, data);
   }

   @Override
   public final void insertLastChild(Node nodeToInsert, ContainerNode referenceNode) {
      referenceNode.insertFirstChild(nodeToInsert);
   }

   @Override
   public final void trigger(int event, int eventParam, Object data) {
      if (this._engine != null) {
         this._event._event = event;
         this._event._eventParam = eventParam;
         this._event._sender = this;
         this._event._data = data;
         this._event._time = this._engine.getTime();
         this.trigger(this._event);
         this._event.clear();
      }
   }

   @Override
   public final void insertNextSibling(Node nodeToInsert, Node referenceNode) {
      referenceNode.insertNextSibling(nodeToInsert);
   }

   @Override
   public final void trigger(Event event) {
      if (this._engine != null) {
         this.notify(event);
         Object originalListener = event._listener;
         event._listener = this._eventResolver;
         this._engine.postEvent(event, true);
         event._listener = originalListener;
      }
   }

   @Override
   public final void insertPreviousSibling(Node nodeToInsert, Node referenceNode) {
      referenceNode.insertPreviousSibling(nodeToInsert);
   }

   @Override
   public final void removeListener(MediaListener listener) {
      this._subscription.removeListener(listener);
   }

   @Override
   public final Node removeNode(Node nodeToRemove) {
      return nodeToRemove.removeNode();
   }

   @Override
   public final void addListener(MediaListener listener) {
      this._subscription.addListener(listener);
   }

   @Override
   public final Node removeNode(boolean leaveChildren, Node nodeToRemove) {
      return nodeToRemove.removeNode(leaveChildren);
   }

   @Override
   public final void addListener(int event, MediaListener listener) {
      this._subscription.addListener(event, listener);
   }

   @Override
   public final Node deleteNode(Node nodeToDelete) {
      return nodeToDelete.deleteNode();
   }

   @Override
   public final void addListener(int event, int eventParam, MediaListener listener) {
      this._subscription.addListener(event, eventParam, listener);
   }

   @Override
   public final Node deleteNode(boolean keepChildren, Node nodeToDelete) {
      return nodeToDelete.deleteNode(keepChildren);
   }

   @Override
   public final void dispose() {
      super.dispose();
   }

   @Override
   public final void setServices(MediaServices s) {
      if (s != super._services && s != null) {
         s.registerService("EventSubscription", this);
         s.registerService("ModelInteractor", this);
         this._engine = s.getEngine();
         this._eventResolver = s.getService("EventResolver");
         this._behaviorManager = s.getService("Behaviors");
      }

      super.setServices(s);
   }
}
