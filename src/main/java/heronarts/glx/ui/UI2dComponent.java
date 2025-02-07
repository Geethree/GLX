/**
 * Copyright 2013- Mark C. Slee, Heron Arts LLC
 *
 * This file is part of the LX Studio software library. By using
 * LX, you agree to the terms of the LX Studio Software License
 * and Distribution Agreement, available at: http://lx.studio/license
 *
 * Please note that the LX license is not open-source. The license
 * allows for free, non-commercial use.
 *
 * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
 * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
 * PURPOSE, WITH RESPECT TO THE SOFTWARE.
 *
 * @author Mark C. Slee <mark@heronarts.com>
 */

package heronarts.glx.ui;

import java.util.Stack;

import heronarts.glx.event.Event;
import heronarts.glx.ui.vg.VGraphics;
import heronarts.lx.modulation.LXParameterModulation;
import heronarts.lx.parameter.LXParameterListener;

public abstract class UI2dComponent extends UIObject {

  /**
   * Position of the object, relative to parent, top left corner
   */
  protected float x;

  /**
   * Position of the object, relative to parent, top left corner
   */
  protected float y;

  /**
   * Width of the object
   */
  protected float width;

  /**
   * Height of the object
   */
  protected float height;

  protected float topMargin = 0, rightMargin = 0, bottomMargin = 0, leftMargin = 0;

  float scrollX = 0;

  float scrollY = 0;

  private boolean hasBackground = false;

  private int backgroundColor = 0xFF000000;

  private boolean hasFocusBackground = false;

  private int focusBackgroundColor = 0xFF000000;

  private boolean hasBorder = false;

  private int borderColor = 0xFF000000;

  private int borderWeight = 1;

  private int borderRounding = 0;

  private boolean hasFocusCorners = true;

  private boolean hasFocusColor = false;

  private int focusColor = 0;

  private VGraphics.Font font = null;

  private boolean hasFontColor = false;

  private int fontColor = 0xff000000;

  protected VGraphics.Align textAlignHorizontal = VGraphics.Align.LEFT;

  protected VGraphics.Align textAlignVertical = VGraphics.Align.BASELINE;

  protected float textOffsetX = 0;

  protected float textOffsetY = 0;

  private boolean mappable = true;

  protected boolean debug = false;
  protected String debugName = "";

  boolean needsRedraw = true;
  boolean childNeedsRedraw = true;
  boolean needsBlit = false;

  public final LXParameterListener redraw = (p) -> { redraw(); };

  protected UI2dComponent() {
    this(0, 0, 0, 0);
  }

  protected UI2dComponent(float x, float y, float width, float height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public UI2dComponent setDebug(boolean debug) {
    return setDebug(debug, this.getClass().getName());
  }

  public UI2dComponent setDebug(boolean debug, String debugName) {
    this.debug = debug;
    this.debugName = debugName;
    return this;
  }

  @Override
  public UI2dComponent setDescription(String description) {
    super.setDescription(description);
    return this;
  }

  /**
   * X position
   *
   * @return x position
   */
  @Override
  public final float getX() {
    return this.x;
  }

  /**
   * Y position
   *
   * @return y position
   */
  @Override
  public final float getY() {
    return this.y;
  }

  /**
   * Width
   *
   * @return width
   */
  @Override
  public final float getWidth() {
    return this.width;
  }

  /**
   * Height
   *
   * @return height
   */
  @Override
  public final float getHeight() {
    return this.height;
  }

  /**
   * Whether the given coordinate, in the parent-space, is contained
   * by this object.
   *
   * @param x X-coordinate in parent's coordinate space
   * @param y Y-coordinate in parent's coordinate space
   * @return Whether this object's bounds contain that point
   */
  @Override
  public boolean contains(float x, float y) {
    return
      (x >= this.x && x < (this.x + this.width)) &&
      (y >= this.y && y < (this.y + this.height));
  }

  /**
   * Set the visibility state of this component
   *
   * @param visible Whether this should be visible
   * @return this
   */
  @Override
  public UI2dComponent setVisible(boolean visible) {
    if (isVisible() != visible) {
      super.setVisible(visible);
      if (this.parent instanceof UI2dContainer) {
        ((UI2dContainer) this.parent).reflow();
      }
      if (visible) {
        // Redraw ourselves, in the space we take up
        redraw();
      } else {
        // We're invisible now, the container needs to redraw so
        // our background or whatever was underneath can
        // be filled in
        redrawContainer();
      }
    }
    return this;
  }

  /**
   * Set the position of this component in its parent coordinate space
   *
   * @param x X-position in parents coordinate space
   * @return this
   */
  public UI2dComponent setX(float x) {
    return setPosition(x, this.y);
  }

  /**
   * Set the position of this component in its parent coordinate space
   *
   * @param y Y-position in parents coordinate space
   * @return this
   */
  public UI2dComponent setY(float y) {
    return setPosition(this.x, y);
  }

  /**
   * Set the position of this component in its parent coordinate space
   *
   * @param x X-position in parents coordinate space
   * @param y Y-position in parents coordinate space
   * @return this
   */
  public UI2dComponent setPosition(float x, float y) {
    if ((this.x != x) || (this.y != y)) {
      this.x = x;
      this.y = y;
      if (this.parent instanceof UI2dContainer) {
        ((UI2dContainer) this.parent).reflow();
      }
      // We redraw from our container instead of just
      // ourselves because the background needs to be
      // refreshed. If we only redrew ourself, there
      // could be remnants of our old position in the
      // buffer
      redrawContainer();
    }
    return this;
  }

  /**
   * Sets position based upon an array of either 2 coordinates or 4
   *
   * @param position length 2 array or x/y, or length 4 of x/y/width/height
   * @return this
   */
  public UI2dComponent setPosition(float[] position) {
    if (position.length == 2) {
      return setPosition(position[0], position[1]);
    } else if (position.length == 4) {
      return setPosition(position[0], position[1], position[2], position[3]);
    }
    throw new IllegalArgumentException("Wrong length array to setPosition: " + position);
  }

  /**
   * Set the position of this component in its parent coordinate space
   *
   * @param x X-position in parents coordinate space
   * @param y Y-position in parents coordinate space
   * @param width Width of object
   * @param height Height of object
   * @return this
   */
  public UI2dComponent setPosition(float x, float y, float width, float height) {
    boolean move = false;
    boolean resize = false;
    if ((this.x != x) || (this.y != y)) {
      this.x = x;
      this.y = y;
      move = true;
    }
    if ((this.width != width) || (this.height != height)) {
      this.width = width;
      this.height = height;
      resize = true;
    }
    if (move || resize) {
      if (this.parent instanceof UI2dContainer) {
        ((UI2dContainer) this.parent).reflow();
      }
      if (resize) {
        onResize();
      }
      // Redraw from our container because our bounds are
      // different and we don't want to leave remnants of
      // our old position in the buffer
      redrawContainer();
    }
    return this;
  }

  /**
   * Sets the position of this object in the global space, relative to a parent object
   * with a defined offset
   *
   * @param parent Parent object
   * @param offsetX X offset
   * @param offsetY Y offset
   * @return this
   */
  public UI2dComponent setPosition(UIObject parent, float offsetX, float offsetY) {
    float x = offsetX, y = offsetY;
    while (parent != null) {
      x += parent.getX();
      y += parent.getY();
      if (parent instanceof UI2dScrollContext) {
        UI2dScrollContext scrollContext = (UI2dScrollContext) parent;
        x += scrollContext.getScrollX();
        y += scrollContext.getScrollY();
      }
      parent = parent.getParent();
    }
    setPosition(x, y);
    return this;
  }

  /**
   * Sets the height of this component
   *
   * @param height Height
   * @return this
   */
  public UI2dComponent setHeight(float height) {
    return setSize(this.width, height);
  }

  /**
   * Sets the width of this component
   *
   * @param width Width of the component
   * @return Width of this component
   */
  public UI2dComponent setWidth(float width) {
    return setSize(width, this.height);
  }

  /**
   * Set the dimensions of this component
   *
   * @param width Width of component
   * @param height Height of component
   * @return this
   */
  public UI2dComponent setSize(float width, float height) {
    if ((this.width != width) || (this.height != height)) {
      this.width = width;
      this.height = height;
      if (this.parent instanceof UI2dContainer) {
        ((UI2dContainer) this.parent).reflow();
      }
      onResize();

      // Our bounds have changed, we could be smaller.
      // Redraw whole container to erase any remnants
      // of our former position
      redrawContainer();
    }
    return this;
  }

  /**
   * Sets the margins around this object when inside of a UI2dContainer with layout
   *
   * @param margin Margin on all sides
   * @return this
   */
  public UI2dComponent setMargin(float margin) {
    return setMargin(margin, margin, margin, margin);
  }

  /**
   * Sets the margins around this object when inside of a UI2dContainer with layout
   *
   * @param yMargin Vertical margins
   * @param xMargin Horizontal margins
   * @return this
   */
  public UI2dComponent setMargin(float yMargin, float xMargin) {
    return setMargin(yMargin, xMargin, yMargin, xMargin);
  }

  /**
   * Sets the top margin around this object when inside a UI2dContainer with layout
   *
   * @param topMargin Top margin
   * @return this
   */
  public UI2dComponent setTopMargin(float topMargin) {
    return setMargin(topMargin, this.rightMargin, this.bottomMargin, this.leftMargin);
  }

  /**
   * Sets the bottom margin around this object when inside a UI2dContainer with layout
   *
   * @param bottomMargin Bottom margin
   * @return this
   */
  public UI2dComponent setBottomMargin(float bottomMargin) {
    return setMargin(this.topMargin, this.rightMargin, bottomMargin, this.leftMargin);
  }

  /**
   * Sets the left margin around this object when inside a UI2dContainer with layout
   *
   * @param leftMargin Left margin
   * @return this
   */
  public UI2dComponent setLeftMargin(float leftMargin) {
    return setMargin(this.topMargin, this.rightMargin, this.bottomMargin, leftMargin);
  }

  /**
   * Sets the right margin around this object when inside a UI2dContainer with layout
   *
   * @param rightMargin Right margin
   * @return this
   */
  public UI2dComponent setRightMargin(float rightMargin) {
    return setMargin(this.topMargin, rightMargin, this.bottomMargin, this.leftMargin);
  }

  /**
   * Sets the margins around this object when inside of a UI2dContainer with layout
   *
   * @param topMargin Top margin
   * @param rightMargin Right margin
   * @param bottomMargin Bottom margin
   * @param leftMargin Left margin
   * @return this
   */
  public UI2dComponent setMargin(float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
    boolean reflow = false;
    if (this.topMargin != topMargin) {
      this.topMargin = topMargin;
      reflow = true;
    }
    if (this.rightMargin != rightMargin) {
      this.rightMargin = rightMargin;
      reflow = true;
    }
    if (this.bottomMargin != bottomMargin) {
      this.bottomMargin = bottomMargin;
      reflow = true;
    }
    if (this.leftMargin != leftMargin) {
      this.leftMargin = leftMargin;
      reflow = true;
    }
    if (reflow && (this.parent instanceof UI2dContainer)) {
      ((UI2dContainer) this.parent).reflow();
    }
    return this;
  }

  /**
   * Subclasses may override this method, invoked when the component is resized
   */
  protected void onResize() {

  }

  /**
   * Whether this object has a background
   *
   * @return true or false
   */
  public boolean hasBackground() {
    return this.hasBackground;
  }

  /**
   * The background color, if there is a background
   *
   * @return color
   */
  public int getBackgroundColor() {
    return this.backgroundColor;
  }

  /**
   * Sets whether the object has a background
   *
   * @param hasBackground true or false
   * @return this
   */
  public UI2dComponent setBackground(boolean hasBackground) {
    if (this.hasBackground != hasBackground) {
      this.hasBackground = hasBackground;
      redraw();
    }
    return this;
  }

  /**
   * Sets a background color
   *
   * @param backgroundColor color
   * @return this
   */
  public UI2dComponent setBackgroundColor(int backgroundColor) {
    if (!this.hasBackground || (this.backgroundColor != backgroundColor)) {
      this.hasBackground = true;
      this.backgroundColor = backgroundColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets whether a focus background color is used
   *
   * @param focusBackground Focus background color
   * @return this
   */
  public UI2dComponent setFocusBackground(boolean focusBackground) {
    if (this.hasFocusBackground != focusBackground) {
      this.hasFocusBackground = focusBackground;
      if (hasFocus()) {
        redraw();
      }
    }
    return this;
  }

  /**
   * Sets a background color to be used when the component is focused
   *
   * @param focusBackgroundColor Color
   * @return this
   */
  public UI2dComponent setFocusBackgroundColor(int focusBackgroundColor) {
    if (!this.hasFocusBackground || (this.focusBackgroundColor != focusBackgroundColor)) {
      this.hasFocusBackground = true;
      this.focusBackgroundColor = focusBackgroundColor;
      if (hasFocus()) {
        redraw();
      }
    }
    return this;
  }

  /**
   * Whether this object has a border
   *
   * @return true or false
   */
  public boolean hasBorder() {
    return this.hasBorder;
  }

  /**
   * Current border color
   *
   * @return color
   */
  public int getBorderColor() {
    return this.borderColor;
  }

  /**
   * The weight of the border
   *
   * @return weight
   */
  public int getBorderWeight() {
    return this.borderWeight;
  }

  /**
   * Sets whether there is a border
   *
   * @param hasBorder true or false
   * @return this
   */
  public UI2dComponent setBorder(boolean hasBorder) {
    if (this.hasBorder != hasBorder) {
      this.hasBorder = hasBorder;
      redraw();
    }
    return this;
  }

  /**
   * Sets the color of the border
   *
   * @param borderColor color
   * @return this
   */
  public UI2dComponent setBorderColor(int borderColor) {
    if (!this.hasBorder || (this.borderColor != borderColor)) {
      this.hasBorder = true;
      this.borderColor = borderColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets the weight of the border
   *
   * @param borderWeight weight
   * @return this
   */
  public UI2dComponent setBorderWeight(int borderWeight) {
    if (!this.hasBorder || (this.borderWeight != borderWeight)) {
      this.hasBorder = true;
      this.borderWeight = borderWeight;
      redraw();
    }
    return this;
  }

  public UI2dComponent setBorderRounding(int borderRounding) {
    if (this.borderRounding != borderRounding) {
      this.borderRounding = borderRounding;
      redraw();
    }
    return this;
  }

  public int getBorderRounding() {
    return this.borderRounding;
  }

  public UI2dComponent setFocusCorners(boolean focusCorners) {
    this.hasFocusCorners = focusCorners;
    return this;
  }

  public UI2dComponent setFocusColor(int focusColor) {
    this.hasFocusColor = true;
    this.focusColor = focusColor;
    return this;
  }

  /**
   * Whether a font is set on this object
   *
   * @return true or false
   */
  public boolean hasFont() {
    return this.font != null;
  }

  /**
   * Get default font, may be null
   *
   * @return The default font, or null
   */
  public VGraphics.Font getFont() {
    return this.font;
  }

  /**
   * Sets the default font for this object to use, null indicates component may
   * use its own default behavior.
   *
   * @param font Font
   * @return this
   */
  public UI2dComponent setFont(VGraphics.Font font) {
    if (this.font != font) {
      this.font = font;
      redraw();
    }
    return this;
  }

  /**
   * Whether this object has a specific color
   *
   * @return true or false
   */
  public boolean hasFontColor() {
    return this.hasFontColor;
  }

  /**
   * The font color, if there is a color specified
   *
   * @return color
   */
  public int getFontColor() {
    return this.fontColor;
  }

  /**
   * Sets whether the object has a font color
   *
   * @param hasFontColor true or false
   * @return this
   */
  public UI2dComponent setFontColor(boolean hasFontColor) {
    if (this.hasFontColor != hasFontColor) {
      this.hasFontColor = hasFontColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets a font color
   *
   * @param fontColor color
   * @return this
   */
  public UI2dComponent setFontColor(int fontColor) {
    if (!this.hasFontColor || (this.fontColor != fontColor)) {
      this.hasFontColor = true;
      this.fontColor = fontColor;
      redraw();
    }
    return this;
  }

  /**
   * Sets the text alignment
   *
   * @param horizontalAlignment From VGraphics.Align
   * @return this
   */
  public UI2dComponent setTextAlignment(VGraphics.Align horizontalAlignment) {
    return setTextAlignment(horizontalAlignment, this.textAlignVertical);
  }

  /**
   * Sets an offset for text rendering position relative to alignment. Note that
   * adherence to this offset is not strictly enforced by all subclasses, it is
   * up to them to implement it.
   *
   * @param textOffsetX Text position x offset
   * @param textOffsetY Text position y offset
   * @return this
   */
  public UI2dComponent setTextOffset(float textOffsetX, float textOffsetY) {
    if (this.textOffsetX != textOffsetX || this.textOffsetY != textOffsetY) {
      this.textOffsetX = textOffsetX;
      this.textOffsetY = textOffsetY;
      redraw();
    }
    return this;
  }

  /**
   * Sets the text alignment of this component
   *
   * @param horizontalAlignment From VGrahpics.Align
   * @param verticalAlignment From VGrahpics.Align
   * @return this
   */
  public UI2dComponent setTextAlignment(VGraphics.Align horizontalAlignment, VGraphics.Align verticalAlignment) {
    if (!horizontalAlignment.isHorizontal()) {
      throw new IllegalArgumentException("Cannot set horizontal alignment to vertical value: " + horizontalAlignment);
    }
    if (verticalAlignment.isHorizontal()) {
      throw new IllegalArgumentException("Cannot set vertical alignment to horizontal value: " + verticalAlignment);
    }
    if (this.textAlignHorizontal != horizontalAlignment || this.textAlignVertical != verticalAlignment) {
      this.textAlignHorizontal = horizontalAlignment;
      this.textAlignVertical = verticalAlignment;
      redraw();
    }
    return this;
  }

  /**
   * Clip a text to fit in the given width
   *
   * @param vg PGraphics
   * @param str String
   * @param width Width to fit in
   * @return Clipped version of the string that will fit in the bounds
   */
  public static String clipTextToWidth(VGraphics vg, String str, float width) {
    while ((str.length() > 0) && (vg.textWidth(str) > width)) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

  /**
   * Sets whether this component can ever be used for mapping control
   *
   * @param mappable Whether this component is a mappable control
   * @return this
   */
  public UI2dComponent setMappable(boolean mappable) {
    this.mappable = mappable;
    return this;
  }

  /**
   * Determines whether component is permitted to be a mappable control
   *
   * @return Whether this component is a mappable control
   */
  protected boolean isMappable() {
    return this.mappable;
  }

  /**
   * Removes this components from the container is is held by
   *
   * @return this
   */
  public UI2dComponent removeFromContainer() {
    return removeFromContainer(true);
  }

  /**
   * Removes this components from the container is is held by
   *
   * @param redraw Whether to reflow and redraw the container
   * @return this
   */
  public UI2dComponent removeFromContainer(boolean redraw) {
    if (this.parent == null) {
      throw new IllegalStateException("Cannot remove parentless UIObject from container");
    }
    boolean hadFocus = hasFocus();
    if (hadFocus) {
      blur();
    }
    int index = this.parent.mutableChildren.indexOf(this);
    this.parent.mutableChildren.remove(index);
    if (this.parent instanceof UI2dContainer) {
      UI2dContainer container = (UI2dContainer) this.parent;

      if (redraw) {
        container.reflow();
      }

      // If container does auto-keyfocus, focus the neighbor
      if (hadFocus && (container.arrowKeyFocus != UI2dContainer.ArrowKeyFocus.NONE)) {
        int maxIndex = container.mutableChildren.size() - 1;
        if (index > maxIndex) {
          index = maxIndex;
        }
        while (index >= 0) {
          UIObject neighbor = container.children.get(index--);
          if (neighbor instanceof UIKeyFocus) {
            neighbor.focus(Event.SIBLING_REMOVED);
            break;
          }
        }
      }
    }

    // Blammo, we are gone. Need to redraw the container.
    if (redraw) {
      redrawContainer();
    }
    this.parent = null;
    return this;
  }

  /**
   * Get the parent object that this is in
   *
   * @return Parent of this component
   */
  @Override
  public UIObject getParent() {
    return this.parent;
  }

  /**
   * Returns the adjacent object in the hierarchy
   *
   * @return The previous UI object in the hierarchy adjacent to this one
   */
  public UI2dComponent getPrevSibling() {
    UI2dContainer container = getContainer();
    UI2dComponent prev = null;
    if (container != null) {
      for (UIObject child : container) {
        if (child == this) {
          return prev;
        }
        prev = (UI2dComponent) child;
      }
    }
    return null;
  }

  /**
   * Returns the adjacent object in the hierarchy
   *
   * @return The next UI object in the hierarchy adjacent to this one
   */
  public UI2dComponent getNextSibling() {
    UI2dContainer container = getContainer();
    if (container != null) {
      boolean next = false;
      for (UIObject child : container) {
        if (next) {
          return (UI2dComponent) child;
        } else if (child == this) {
          next = true;
        }
      }
    }
    return null;
  }

  /**
   * Returns the 2d container that this is in
   *
   * @return Container of this component, or null if not in a 2d container
   */
  public UI2dContainer getContainer() {
    if (this.parent instanceof UI2dContainer) {
      return (UI2dContainer) this.parent;
    }
    return null;
  }

  /**
   * Adds this component to a container, also removing it from any other container that
   * is currently holding it.
   *
   * @param container Container to place in
   * @return this
   */
  public final UI2dComponent addToContainer(UIContainer container) {
    return addToContainer(container, -1);
  }

  /**
   * Adds this component to a container at a specified index, also removing it from any
   * other container that is currently holding it.
   *
   * @param container Container to place in
   * @param index At which index to place this object in parent container
   * @return this
   */
  public UI2dComponent addToContainer(UIContainer container, int index) {
    return addToContainer(container, index, true);
  }

  /**
   * Adds this component to a container at a specified index, also removing it from any
   * other container that is currently holding it. Reflow behavior is controlled by a
   * flag.
   *
   * @param container Container to place in
   * @param index At which index to place this object in parent container
   * @param redraw Whether to reflow and redraw the parent container
   * @return this
   */
  public UI2dComponent addToContainer(UIContainer container, int index, boolean redraw) {
    if (this.parent != null) {
      removeFromContainer();
    }
    UIObject containerObject = container.getContentTarget();
    if (containerObject == this) {
      throw new IllegalArgumentException("Cannot add an object to itself");
    }
    if (index < 0) {
      containerObject.mutableChildren.add(this);
    } else {
      containerObject.mutableChildren.add(index, this);
    }
    this.parent = containerObject;
    setUI(containerObject.ui);
    if (redraw) {
      if (this.parent instanceof UI2dContainer) {
        ((UI2dContainer) this.parent).reflow();
      }
      redraw();
    }
    return this;
  }

  /**
   * Sets the index of this object in its container.
   *
   * @param index Desired index
   * @return this
   */
  public UI2dComponent setContainerIndex(int index) {
    if (this.parent == null) {
      throw new UnsupportedOperationException("Cannot setContainerIndex() on an object not in a container");
    }
    this.parent.mutableChildren.remove(this);
    if (index < 0) {
      this.parent.mutableChildren.add(this);
    } else {
      this.parent.mutableChildren.add(index, this);
    }
    if (this.parent instanceof UI2dContainer) {
      ((UI2dContainer) this.parent).reflow();
    }
    // Overlaps could have changed, everything in the container needs
    // to be redone now
    redrawContainer();
    return this;
  }

  /**
   * Redraws this object.
   *
   * @return this object
   */
  public final UI2dComponent redraw() {
    if (this.ui != null && this.parent != null && this.isVisible()) {
      this.ui.redraw(this);
    }
    return this;
  }

  private void redrawContainer() {
    if ((this.parent != null) && (this.parent instanceof UI2dComponent)) {
      ((UI2dComponent) this.parent).redraw();
    }
  }

  final void _redraw() {
    // Mark object and children as needing redraw
    _redrawChildren();

    // Mark parent containers as needing a child redrawn
    UIObject p = this.parent;
    while ((p != null) && (p instanceof UI2dComponent)) {
      UI2dComponent p2d = (UI2dComponent) p;
      p2d.childNeedsRedraw = true;
      p = p2d.parent;
    }
  }

  /**
   * Internal helper. Marks this object and all of its children as needing to be
   * redrawn.
   */
  private final void _redrawChildren() {
    this.needsRedraw = true;
    this.childNeedsRedraw = false;
    for (UIObject child : this.mutableChildren) {
      if ((child instanceof UI2dContext) && (((UI2dContext) child).isOffscreen)) {
        // If this is an offscreen 2d context that didn't get a direct redraw
        // call, don't update it.
        continue;
      }
      this.childNeedsRedraw = true;
      ((UI2dComponent) child)._redrawChildren();
    }
  }

  protected final void populateRenderStack(Stack<UI2dContext> renderStack) {
    if (!isVisible()) {
      return;
    }
    // Before the main drawing loop, the tree is checked for all
    // contexts that need a redraw. If we fall into this category
    // then we push ourselves on the render stack, which will be
    // serviced before the draw() methods are invoked.
    if (this instanceof UI2dContext) {
      if (this.needsRedraw || this.childNeedsRedraw) {
        renderStack.push((UI2dContext) this);
      }
    }
    // Iterate through children
    for (UIObject child : this.children) {
      ((UI2dComponent)child).populateRenderStack(renderStack);
    }
  }

  /**
   * Subclasses should override this method to perform their drawing functions.
   *
   * @param ui UI context
   * @param vg Graphics context
   */
  protected void onDraw(UI ui, VGraphics vg) {}

  /**
   * Draws this object using the given graphics context
   *
   * @param ui UI
   * @param vg View to draw into
   */
  void draw(UI ui, VGraphics vg) {
    if (!isVisible()) {
      return;
    }
    boolean needsBorder = this.needsRedraw || this.childNeedsRedraw;
    boolean needsMappingOverlay = this.needsRedraw;
    int sx = (int) this.scrollX;
    int sy = (int) this.scrollY;
    if (this.needsRedraw) {
      this.needsRedraw = false;
      drawBackground(ui, vg);
      vg.translate(sx, sy);
      onDraw(ui, vg);
      vg.translate(-sx, -sy);
    }
    if (this.childNeedsRedraw) {
      this.childNeedsRedraw = false;
      vg.translate(sx, sy);
      for (UIObject childObject : this.mutableChildren) {
        UI2dComponent child = (UI2dComponent) childObject;
        if (child.isVisible()) {
          if (child.needsRedraw || child.childNeedsRedraw || child.needsBlit) {
            float cx = child.x;
            float cy = child.y;
            vg.translate(cx, cy);
            child.draw(ui, vg);
            vg.translate(-cx, -cy);
          }
        }
      }
      vg.translate(-sx, -sy);
    }
    if (needsBorder) {
      drawBorder(ui, vg);
      if (isModulationSource() || isTriggerSource()) {
        drawMappingBorder(ui, vg);
      }
    }
    if (needsMappingOverlay) {
      drawMappingOverlay(ui, vg, 0, 0, this.width, this.height);
    }
  }

  private void drawMappingBorder(UI ui, VGraphics vg) {
    vg.beginPath();
    vg.rect(0.5f, 0.5f, this.width - 1, this.height - 1, this.borderRounding);
    vg.strokeColor(0xff000000 | ui.theme.getModulationTargetMappingColor());
    vg.stroke();
  }

  private void drawMappingOverlay(UI ui, VGraphics vg, float x, float y, float w, float h) {
    if (isModulationSource() || isTriggerSource()) {
      // Do nothing! Handled by drawMappingBorder
    } else if (isMidiMapping()) {
      vg.beginPath();
      vg.rect(x, y, w, h);
      vg.fillColor(ui.theme.getMidiMappingColor());
      vg.fill();
      if (isControlTarget()) {
        drawFocusCorners(ui, vg, 0xccff0000);
      }
    } else if (isModulationSourceMapping() || isTriggerSourceMapping()) {
      vg.beginPath();
      vg.rect(x, y, w, h);
      vg.fillColor(ui.theme.getModulationSourceMappingColor());
      vg.fill();
    } else if (isModulationTargetMapping() || isTriggerTargetMapping()) {
      vg.beginPath();
      vg.rect(x, y, w, h);
      vg.fillColor(ui.theme.getModulationTargetMappingColor());
      vg.fill();
    } else if (isModulationHighlight()) {
      LXParameterModulation modulation = this.ui.highlightParameterModulation;
      if (modulation != null) {
        int color = modulation.color.getColor();
        color = (color & 0x00ffffff) | (0x33000000);
        vg.beginPath();
        vg.rect(x, y, w, h);
        vg.fillColor(color);
        vg.fill();
      }
    }
  }

  private void drawBackground(UI ui, VGraphics vg) {
    if (this.width == 0 || this.height == 0) {
      return;
    }

    boolean ownBackground = this.hasBackground || (this.hasFocus && this.hasFocusBackground);

    if (!ownBackground || (this.borderRounding > 0)) {
      // If we don't have our own background, or our borders are rounded,
      // then we need to walk up the UI tree to figure out how to paint
      // in the background.
      UIObject component = this.parent;
      while ((component != null) && (component instanceof UI2dComponent)) {
        UI2dComponent component2d = (UI2dComponent) component;
        if (component2d.hasBackground || (component2d.hasFocus && component2d.hasFocusBackground)) {
          vg.beginPath();
          vg.rect(0, 0, this.width, this.height);
          vg.fillColor((component2d.hasFocus && component2d.hasFocusBackground) ? component2d.focusBackgroundColor : component2d.backgroundColor);
          vg.fill();
          break;
        }
        component = component.parent;
      }
    }

    if (ownBackground) {
      vg.beginPath();
      vg.rect(0, 0, this.width, this.height, this.borderRounding);
      vg.fillColor((this.hasFocus && this.hasFocusBackground) ? this.focusBackgroundColor : this.backgroundColor);
      vg.fill();
    }

  }

  protected void drawBorder(UI ui, VGraphics vg) {
    if (this.width == 0 || this.height == 0) {
      return;
    }

    if (this.hasBorder) {
      int border = this.borderWeight;
      vg.beginPath();
      vg.rect(border * .5f, border * .5f, this.width - border, this.height - border, this.borderRounding);
      vg.strokeWidth(border);
      vg.strokeColor(this.borderColor);
      vg.stroke();

      // Reset stroke weight
      vg.strokeWidth(1);
    }
    if (hasFocus() && (this instanceof UIFocus)) {
      drawFocus(ui, vg);
    }
  }

  protected int getFocusColor(UI ui) {
    return this.hasFocusColor ? this.focusColor : ui.theme.getFocusColor();
  }

  /**
   * Focus size for hashes drawn on the outline of the object. May be overridden.
   *
   * @return Focus hash line size
   */
  protected int getFocusSize() {
    return (int) Math.min(8, Math.min(this.width, this.height) / 8);
  }

  /**
   * Draws focus on this object. May be overridden by subclasses to provide
   * custom focus-drawing behavior.
   *
   * @param ui UI
   * @param vg VGraphics
   */
  protected void drawFocus(UI ui, VGraphics vg) {
    if (this.hasFocusCorners) {
      drawFocusCorners(ui, vg, getFocusColor(ui));
    }
  }

  protected void drawFocusCorners(UI ui, VGraphics vg, int color) {
    drawFocusCorners(ui, vg, color, 0, 0, this.width, this.height, getFocusSize());
  }

  public static void drawFocusCorners(UI ui, VGraphics vg, int color, float x, float y, float width, float height, float focusSize) {
    x += .5f;
    y += .5f;
    focusSize += .5f;

    // Top left
    vg.beginPath();
    vg.strokeColor(color);

    vg.moveTo(x, y + focusSize);
    vg.lineTo(x, y);
    vg.lineTo(x + focusSize, y);

    // Top right
    vg.moveTo(x + width - focusSize - 1, y);
    vg.lineTo(x + width - 1, y);
    vg.lineTo(x + width - 1, y + focusSize);

    // Bottom right
    vg.moveTo(x + width - 1, y + height - 1 - focusSize);
    vg.lineTo(x + width - 1, y + height - 1);
    vg.lineTo(x + width - 1 - focusSize, y + height - 1);

    // Bottom left
    vg.moveTo(x + focusSize, y + height - 1);
    vg.lineTo(x, y + height - 1);
    vg.lineTo(x, y + height - 1 - focusSize);

    // Stroke it!
    vg.stroke();
  }

}
