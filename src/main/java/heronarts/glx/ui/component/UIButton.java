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

package heronarts.glx.ui.component;

import java.util.Objects;

import heronarts.glx.event.KeyEvent;
import heronarts.glx.event.MouseEvent;
import heronarts.glx.ui.UI;
import heronarts.glx.ui.UIControlTarget;
import heronarts.glx.ui.UIFocus;
import heronarts.glx.ui.UITriggerSource;
import heronarts.glx.ui.UITriggerTarget;
import heronarts.glx.ui.vg.VGraphics;
import heronarts.lx.command.LXCommand;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.LXListenableNormalizedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;

public class UIButton extends UIParameterComponent implements UIControlTarget, UITriggerSource, UITriggerTarget, UIFocus {

  public static class Action extends UIButton {
    public Action(float w, float h) {
      this(0, 0, w, h);
    }

    public Action(float w, float h, String label) {
      this(0, 0, w, h, label);
    }

    public Action(float x, float y, float w, float h) {
      super(x, y, w, h);
      setBorderRounding(8);
      setMomentary(true);
    }

    public Action(float x, float y, float w, float h, String label) {
      this(x, y, w, h);
      setLabel(label);
    }

  }

  public static class Trigger extends UIButton {

    public static final int HEIGHT = 12;
    public static final int WIDTH = 18;

    public Trigger(UI ui, float x, float y) {
      this(ui, null, x, y);
    }

    public Trigger(UI ui, BooleanParameter trigger, float x, float y) {
      super(x, y, WIDTH, HEIGHT);
      setIcon(ui.theme.iconTrigger);
      setMomentary(true);
      setBorderRounding(4);
      if (trigger != null) {
        setParameter(trigger);
      }
    }
  }

  private LXParameter controlSource = null;
  private LXParameter controlTarget = null;

  protected boolean active = false;
  protected boolean isMomentary = false;

  protected int inactiveColor = UI.get().theme.getControlBackgroundColor();
  protected int activeColor = UI.get().theme.getPrimaryColor();

  private String activeLabel = "";
  private String inactiveLabel = "";

  private boolean hasIconColor = false;
  private int iconColor = UI.WHITE;

  private int activeFontColor = UI.WHITE;

  private VGraphics.Image activeIcon = null;
  private VGraphics.Image inactiveIcon = null;

  private boolean triggerable = false;
  protected boolean enabled = true;

  protected boolean momentaryPressValid = false;
  private boolean momentaryPressEngaged = false;

  private EnumParameter<? extends Object> enumParameter = null;
  private BooleanParameter booleanParameter = null;

  private float iconOffsetX = 0, iconOffsetY = 0;

  private final LXParameterListener booleanParameterListener = (p) -> {
    setActive(booleanParameter.isOn(), false);
  };

  private final LXParameterListener enumParameterListener = (p) -> {
    setLabel(enumParameter.getEnum().toString());
  };

  public UIButton() {
    this(0, 0, 0, 0);
  }

  public UIButton(float w, BooleanParameter p) {
    this(w, DEFAULT_HEIGHT, p);
  }

  public UIButton(float w, float h, BooleanParameter p) {
    this(0, 0, w, h);
    setParameter(p);
    setLabel(p.getLabel());
  }

  public UIButton(float w, EnumParameter<?> p) {
    this(w, DEFAULT_HEIGHT, p);
  }

  public UIButton(float w, float h, EnumParameter<?> p) {
    this(0, 0, w, h);
    setParameter(p);
    setLabel(p.getEnum().toString());
  }

  public UIButton(float x, float y, float w, float h) {
    super(x, y, w, h);
    setBorderColor(UI.get().theme.getControlBorderColor());
    setFontColor(UI.get().theme.getControlTextColor());
    setBackgroundColor(this.inactiveColor);
  }

  /**
   * Sets the active font color
   *
   * @param activeFontColor color
   * @return this
   */
  public UIButton setActiveFontColor(int activeFontColor) {
    if (activeColor != this.activeFontColor) {
      this.activeFontColor = activeFontColor;
      redraw();
    }
    return this;
  }

  public UIButton setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      redraw();
    }
    return this;
  }

  public UIButton setTriggerable(boolean triggerable) {
    this.triggerable = triggerable;
    return this;
  }

  public UIButton setIconColor(boolean iconColor) {
    if (this.hasIconColor != iconColor) {
      this.hasIconColor = iconColor;
      redraw();
    }
    return this;
  }

  public UIButton setIconColor(int iconColor) {
    if (!this.hasIconColor || (this.iconColor != iconColor)) {
      this.hasIconColor = true;
      this.iconColor = iconColor;
      redraw();
    }
    return this;
  }

  @Override
  public String getDescription() {
    if (this.booleanParameter != null) {
      return UIParameterControl.getDescription(this.booleanParameter);
    }
    if (this.enumParameter != null) {
      return UIParameterControl.getDescription(this.enumParameter);
    }
    return super.getDescription();
  }

  @Override
  public LXListenableNormalizedParameter getParameter() {
    return (this.booleanParameter != null) ? this.booleanParameter : this.enumParameter;
  }

  public UIButton removeParameter() {
    if (this.booleanParameter != null) {
      this.booleanParameter.removeListener(this.booleanParameterListener);
      this.booleanParameter = null;
    }
    if (this.enumParameter != null) {
      this.enumParameter.removeListener(this.enumParameterListener);
      this.enumParameter = null;
    }
    return this;
  }

  public UIButton setParameter(EnumParameter<? extends Object> parameter) {
    Objects.requireNonNull(parameter, "Cannot set null UIButton.setParameter() - use removeParameter() instead");
    if (parameter != this.enumParameter) {
      removeParameter();
      if (parameter != null) {
        this.enumParameter = parameter;
        this.enumParameter.addListener(this.enumParameterListener);
        setActive(false);
        setMomentary(true);
        setLabel(this.enumParameter.getEnum().toString());
      }
    }
    return this;
  }

  public UIButton setParameter(BooleanParameter parameter) {
    Objects.requireNonNull(parameter, "Cannot set null UIButton.setParameter() - use removeParameter() instead");
    if (parameter != this.booleanParameter) {
      removeParameter();
      if (parameter != null) {
        this.booleanParameter = parameter;
        this.booleanParameter.addListener(this.booleanParameterListener);
        setMomentary(this.booleanParameter.getMode() == BooleanParameter.Mode.MOMENTARY);
        setActive(this.booleanParameter.isOn(), false);
      }
    }
    return this;
  }

  public UIButton setMomentary(boolean momentary) {
    this.isMomentary = momentary;
    return this;
  }

  public UIButton setIconOffset(float iconOffsetX, float iconOffsetY) {
    boolean redraw = false;
    if (this.iconOffsetX != iconOffsetX) {
      this.iconOffsetX = iconOffsetX;
      redraw = true;
    }
    if (this.iconOffsetY != iconOffsetY) {
      this.iconOffsetY = iconOffsetY;
      redraw = true;
    }
    if (redraw) {
      redraw();
    }
    return this;
  }

  public UIButton setIconOffsetX(float iconOffsetX) {
    if (this.iconOffsetX != iconOffsetX) {
      this.iconOffsetX = iconOffsetX;
      redraw();
    }
    return this;
  }

  public UIButton setIconOffsetY(float iconOffsetY) {
    if (this.iconOffsetY != iconOffsetY) {
      this.iconOffsetY = iconOffsetY;
      redraw();
    }
    return this;
  }

  @Override
  protected void onDraw(UI ui, VGraphics vg) {
    // A lighter gray background color when the button is disabled, or it's engaged
    // with a mouse press but the mouse has moved off the active button
    if (!this.enabled || (this.momentaryPressEngaged && !this.momentaryPressValid)) {
      vg.beginPath();
      vg.fillColor(ui.theme.getControlDisabledColor());
      vg.rect(1, 1, this.width-2, this.height-2);
      vg.fill();
    } else if (this.momentaryPressEngaged) {
      vg.beginPath();
      vg.fillColor(this.activeColor);
      vg.rect(1, 1, this.width-2, this.height-2);
      vg.fill();
    }

    VGraphics.Image icon = this.active ? this.activeIcon : this.inactiveIcon;
    if (icon != null) {
      if (!this.active && !this.momentaryPressEngaged) {
        icon.setTint(this.hasIconColor ? this.iconColor : getFontColor());
      }
      vg.beginPath();
      vg.image(icon, this.width/2 - icon.width/2 + this.iconOffsetX, this.height/2 - icon.height/2 + this.iconOffsetY);
      vg.fill();
      icon.noTint();
    } else {
      String label = this.active ? this.activeLabel : this.inactiveLabel;
      if ((label != null) && (label.length() > 0)) {
        vg.fillColor((this.active || this.momentaryPressEngaged) ? this.activeFontColor : getFontColor());
        vg.fontFace(hasFont() ? getFont() : ui.theme.getControlFont());
        if (this.textAlignVertical == VGraphics.Align.MIDDLE) {
          vg.textAlign(VGraphics.Align.CENTER, VGraphics.Align.MIDDLE);
          vg.beginPath();
          vg.text(this.width / 2 + this.textOffsetX, this.height / 2 + this.textOffsetY, label);
          vg.fill();
        } else {
          vg.beginPath();
          vg.textAlign(VGraphics.Align.CENTER);
          vg.text(this.width / 2 + this.textOffsetX, (int) (this.height * .75) + this.textOffsetY, label);
          vg.fill();
        }
      }
    }
  }

  @Override
  protected void onBlur() {
    super.onBlur();
    if (this.momentaryPressEngaged) {
      this.momentaryPressEngaged = false;
      redraw();
    }
  }

  @Override
  protected void onMouseDragged(MouseEvent mouseEvent, float mx, float my, float dx, float dy) {
    if (this.enabled && this.momentaryPressEngaged) {
      boolean mouseDownMomentary = contains(this.x + mx, this.y + my);
      if (mouseDownMomentary != this.momentaryPressValid) {
        this.momentaryPressValid = mouseDownMomentary;
        redraw();
      }
    }
  }

  @Override
  protected void onMousePressed(MouseEvent mouseEvent, float mx, float my) {
    if (this.enabled) {
      mouseEvent.consume();
      this.momentaryPressValid = this.isMomentary;
      this.momentaryPressEngaged = this.isMomentary;
      setActive(this.isMomentary ? true : !this.active);
    }
  }

  @Override
  protected void onMouseReleased(MouseEvent mouseEvent, float mx, float my) {
    if (this.enabled) {
      if (this.isMomentary) {
        mouseEvent.consume();
        setActive(false);
        if (contains(mx + this.x, my + this.y)) {
          onClick();
        }
      }
    }
    if (this.momentaryPressEngaged) {
      this.momentaryPressEngaged = false;
      redraw();
    }
  }

  @Override
  protected void onKeyPressed(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyCode == KeyEvent.VK_SPACE) || keyEvent.isEnter()) {
      if (this.enabled) {
        this.momentaryPressValid = this.isMomentary;
        this.momentaryPressEngaged = this.isMomentary;
        setActive(this.isMomentary ? true : !this.active);
      }
      keyEvent.consume();
    }
  }

  @Override
  protected void onKeyReleased(KeyEvent keyEvent, char keyChar, int keyCode) {
    if ((keyCode == KeyEvent.VK_SPACE) || keyEvent.isEnter()) {
      if (this.enabled && this.isMomentary) {
        setActive(false);
        onClick();
      }
      if (this.momentaryPressEngaged) {
        this.momentaryPressEngaged = false;
        redraw();
      }
      keyEvent.consume();
    }
  }

  public boolean isActive() {
    return this.active;
  }

  public UIButton setActive(boolean active) {
    return setActive(active, true);
  }

  protected UIButton setActive(boolean active, boolean pushToParameter) {
    if (this.active != active) {
      this.active = active;
      setBackgroundColor(active ? this.activeColor : this.inactiveColor);
      if (pushToParameter) {
        if (this.enumParameter != null) {
          if (active) {
            if (this.useCommandEngine) {
              getLX().command.perform(new LXCommand.Parameter.Increment(this.enumParameter));
            } else {
              this.enumParameter.increment();
            }
          }
        } else if (this.booleanParameter != null) {
          if (this.isMomentary) {
            this.booleanParameter.setValue(active);
          } else {
            if (this.useCommandEngine) {
              getLX().command.perform(new LXCommand.Parameter.SetNormalized(this.booleanParameter, active));
            } else {
              this.booleanParameter.setValue(active);
            }
          }

        }
      }
      onToggle(active);
      redraw();
    }
    return this;
  }

  public UIButton toggle() {
    return setActive(!this.active);
  }

  /**
   * Subclasses may override this to handle changes to the button's state
   *
   * @param active Whether button is active
   */
  protected void onToggle(boolean active) {
  }

  /**
   * Subclasses may override when a momentary button is clicked, and the click release
   * happened within the bounds of the box
   */
  protected void onClick() {
  }

  public UIButton setActiveColor(int activeColor) {
    if (this.activeColor != activeColor) {
      this.activeColor = activeColor;
      if (this.active) {
        setBackgroundColor(activeColor);
      }
    }
    return this;
  }

  public UIButton setInactiveColor(int inactiveColor) {
    if (this.inactiveColor != inactiveColor) {
      this.inactiveColor = inactiveColor;
      if (!this.active) {
        setBackgroundColor(inactiveColor);
      }
    }
    return this;
  }

  public UIButton setLabel(String label) {
    setActiveLabel(label);
    setInactiveLabel(label);
    return this;
  }

  public UIButton setActiveLabel(String activeLabel) {
    if (!this.activeLabel.equals(activeLabel)) {
      this.activeLabel = activeLabel;
      if (this.active) {
        redraw();
      }
    }
    return this;
  }

  public UIButton setInactiveLabel(String inactiveLabel) {
    if (!this.inactiveLabel.equals(inactiveLabel)) {
      this.inactiveLabel = inactiveLabel;
      if (!this.active) {
        redraw();
      }
    }
    return this;
  }

  public UIButton setIcon(VGraphics.Image icon) {
    setActiveIcon(icon);
    setInactiveIcon(icon);
    return this;
  }

  public UIButton setActiveIcon(VGraphics.Image activeIcon) {
    if (this.activeIcon != activeIcon) {
      this.activeIcon = activeIcon;
      if (this.active) {
        redraw();
      }
    }
    return this;
  }

  public UIButton setInactiveIcon(VGraphics.Image inactiveIcon) {
    if (this.inactiveIcon != inactiveIcon) {
      this.inactiveIcon = inactiveIcon;
      if (!this.active) {
        redraw();
      }
    }
    return this;
  }

  /**
   * Sets an explicit control source for the button, which may or may not match
   * its other parameter behavior. Useful for buttons that need to perform a
   * custom LXCommand rather than explicitly change parameter value, but still
   * should be mappable for modulation and MIDI.
   *
   * @param controlSource Control source
   * @return this
   */
  public UIButton setControlSource(LXParameter controlSource) {
    this.controlSource = controlSource;
    return this;
  }

  /**
   * Sets an explicit control target for the button, which may or may not match
   * its other parameter behavior. Useful for buttons that need to perform a
   * custom LXCommand rather than explicitly change parameter value, but still
   * should be mappable for modulation and MIDI.
   *
   * @param controlTarget Control target
   * @return this
   */
  public UIButton setControlTarget(LXParameter controlTarget) {
    this.controlTarget = controlTarget;
    return this;
  }

  @Override
  public LXParameter getControlTarget() {
    if (this.controlTarget != null) {
      // If one is explicitly set, doesn't have to match the rest
      return this.controlTarget;
    }
    if (isMappable()) {
      if (this.enumParameter != null) {
        if (this.enumParameter.getParent() != null) {
          return this.enumParameter.isMappable() ? this.enumParameter : null;
        }
      } else {
        return getTriggerTargetParameter();
      }
    }
    return null;
  }

  @Override
  public BooleanParameter getTriggerSource() {
    return this.triggerable ? getTriggerSourceParameter() : null;
  }

  @Override
  public BooleanParameter getTriggerTarget() {
    return this.triggerable ? getTriggerTargetParameter() : null;
  }

  protected BooleanParameter getTriggerSourceParameter() {
    if (this.controlSource instanceof BooleanParameter) {
      return (BooleanParameter) this.controlSource;
    }
    return getTriggerTargetParameter();
  }

  protected BooleanParameter getTriggerTargetParameter() {
    if (this.controlTarget instanceof BooleanParameter) {
      return (BooleanParameter) this.controlTarget;
    }
    if (this.booleanParameter != null && this.booleanParameter.isMappable() && this.booleanParameter.getParent() != null) {
      return this.booleanParameter;
    }
    return null;
  }

  @Override
  public void dispose() {
    removeParameter();
    super.dispose();
  }

}
