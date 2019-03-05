/**
 * Copyright 2017- Mark C. Slee, Heron Arts LLC
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
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package heronarts.glx.ui.component;

import heronarts.glx.event.KeyEvent;
import heronarts.glx.event.MouseEvent;
import heronarts.glx.ui.UI;
import heronarts.glx.ui.UI2dContainer;
import heronarts.glx.ui.UIMouseFocus;
import heronarts.glx.ui.vg.VGraphics;

/**
 * Section with a title which can collapse/expand
 */
public class UICollapsibleSection extends UI2dContainer implements UIMouseFocus {

  private static final int PADDING = 4;
  private static final int TITLE_LABEL_HEIGHT = 12;
  private static final int CHEVRON_PADDING = 20;
  private static final int CLOSED_HEIGHT = TITLE_LABEL_HEIGHT + 2*PADDING;
  private static final int CONTENT_Y = CLOSED_HEIGHT;

  private final UILabel title;
  private boolean expanded = true;
  private float expandedHeight;

  private final UI2dContainer content;

  /**
   * Constructs a new collapsible section
   *
   * @param ui UI
   * @param x Xpos
   * @param y Ypos
   * @param w Width
   * @param h Height
   */
  public UICollapsibleSection(UI ui, float x, float y, float w, float h) {
    super(x, y, w, h);
    setBackgroundColor(ui.theme.getDeviceBackgroundColor());
    setFocusBackgroundColor(ui.theme.getDeviceFocusedBackgroundColor());
    setBorderRounding(4);

    this.title = new UILabel(PADDING, PADDING + 1, this.width - PADDING - CHEVRON_PADDING, TITLE_LABEL_HEIGHT);
    this.title.setTextAlignment(VGraphics.Align.LEFT, VGraphics.Align.MIDDLE);
    addTopLevelComponent(this.title);

    setHeight(this.expandedHeight = (int) Math.max(CLOSED_HEIGHT, h));
    this.content = new UI2dContainer(PADDING, CONTENT_Y, this.width - 2*PADDING, Math.max(0, this.expandedHeight - PADDING - CONTENT_Y)) {
      @Override
      public void onResize() {
        expandedHeight = (this.height <= 0 ? CLOSED_HEIGHT : CONTENT_Y + this.height + PADDING);
        if (expanded) {
          UICollapsibleSection.this.setHeight(expandedHeight);
        }
      }
    };
    setContentTarget(this.content);
  }

  public boolean isExpanded() {
    return this.expanded;
  }

  protected UICollapsibleSection setTitleX(float x) {
    this.title.setX(x);
    this.title.setWidth(this.width - CHEVRON_PADDING - x);
    return this;
  }

  /**
   * Sets the title of the section
   *
   * @param title Title
   * @return this
   */
  public UICollapsibleSection setTitle(String title) {
    this.title.setLabel(title);
    return this;
  }

  @Override
  public void onDraw(UI ui, VGraphics vg) {
    vg.fillColor(0xff333333);
    vg.beginPath();
    vg.rect(width-16, PADDING, 12, 12, 4);
    vg.fill();

    vg.fillColor(ui.theme.getControlTextColor());
    if (this.expanded) {
      vg.beginPath();
      vg.moveTo(this.width-7, 8.5f);
      vg.lineTo(this.width-13, 8.5f);
      vg.lineTo(this.width-10, 12.5f);
      vg.closePath();
      vg.fill();
    } else {
      vg.beginPath();
      vg.circle(this.width-10, 10, 2);
      vg.fill();
    }
  }

  /**
   * Toggles the expansion state of the section
   *
   * @return this
   */
  public UICollapsibleSection toggle() {
    return setExpanded(!this.expanded);
  }

  /**
   * Sets the expanded state of this section
   *
   * @param expanded Whether section is expanded
   * @return this
   */
  public UICollapsibleSection setExpanded(boolean expanded) {
    if (this.expanded != expanded) {
      this.expanded = expanded;
      this.content.setVisible(this.expanded);
      setHeight(this.expanded ? this.expandedHeight : CLOSED_HEIGHT);
      redraw();
    }
    return this;
  }

  @Override
  public void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    if (my < CONTENT_Y) {
      if ((mx >= this.width - CHEVRON_PADDING) || (mx >= this.title.getX() && mouseEvent.getCount() == 2)) {
        toggle();
      }
    }
  }

  @Override
  public void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    super.onKeyPressed(keyEvent, keyChar, keyCode);
    if (keyCode == KeyEvent.VK_SPACE) {
      keyEvent.consume();
      toggle();
    }
  }

  @Override
  public UI2dContainer getContentTarget() {
    return this.content;
  }
}
