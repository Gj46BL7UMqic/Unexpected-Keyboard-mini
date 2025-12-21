package juloo.keyboard2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * A minimal-height view that appears when the keyboard is minimized.
 * Shows a small pill-shaped button that can be tapped to restore the keyboard.
 * The view height is minimal to avoid blocking screen content.
 */
public class FloatingButtonView extends View
{
  public interface OnRestoreListener
  {
    void onRestore();
  }

  private static final int BUTTON_HEIGHT_DP = 32;
  private static final int BUTTON_WIDTH_DP = 64;
  private static final int PADDING_DP = 4;

  private OnRestoreListener _restoreListener;
  private Paint _bgPaint;
  private Paint _iconPaint;

  private int _buttonWidth;
  private int _buttonHeight;
  private int _totalHeight;
  private float _density;

  public FloatingButtonView(Context context)
  {
    this(context, null);
  }

  public FloatingButtonView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init();
  }

  private void init()
  {
    DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
    _density = dm.density;
    _buttonWidth = (int)(BUTTON_WIDTH_DP * _density);
    _buttonHeight = (int)(BUTTON_HEIGHT_DP * _density);
    _totalHeight = (int)((BUTTON_HEIGHT_DP + PADDING_DP * 2) * _density);

    _bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    _bgPaint.setStyle(Paint.Style.FILL);

    _iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    _iconPaint.setStyle(Paint.Style.STROKE);
    _iconPaint.setStrokeWidth(2 * _density);
    _iconPaint.setStrokeCap(Paint.Cap.ROUND);
  }

  public void setOnRestoreListener(OnRestoreListener listener)
  {
    _restoreListener = listener;
  }

  public void setColors(int bgColor, int iconColor)
  {
    _bgPaint.setColor(bgColor);
    _iconPaint.setColor(iconColor);
    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
    int width = dm.widthPixels;
    // Only take up minimal height - just enough for the button
    setMeasuredDimension(width, _totalHeight);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    int viewWidth = getWidth();
    int viewHeight = getHeight();

    // Center the button horizontally
    float buttonLeft = (viewWidth - _buttonWidth) / 2f;
    float buttonTop = (viewHeight - _buttonHeight) / 2f;
    float buttonRight = buttonLeft + _buttonWidth;
    float buttonBottom = buttonTop + _buttonHeight;
    float cornerRadius = _buttonHeight / 2f;

    // Draw pill-shaped button background
    canvas.drawRoundRect(buttonLeft, buttonTop, buttonRight, buttonBottom,
        cornerRadius, cornerRadius, _bgPaint);

    // Draw keyboard icon (three horizontal lines)
    float centerX = viewWidth / 2f;
    float centerY = viewHeight / 2f;
    float iconWidth = _buttonWidth * 0.35f;
    float lineSpacing = _buttonHeight * 0.18f;

    // Top line
    canvas.drawLine(centerX - iconWidth, centerY - lineSpacing,
        centerX + iconWidth, centerY - lineSpacing, _iconPaint);
    // Middle line
    canvas.drawLine(centerX - iconWidth, centerY,
        centerX + iconWidth, centerY, _iconPaint);
    // Bottom line
    canvas.drawLine(centerX - iconWidth, centerY + lineSpacing,
        centerX + iconWidth, centerY + lineSpacing, _iconPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch (event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        return true;

      case MotionEvent.ACTION_UP:
        // Tap detected - restore keyboard
        if (_restoreListener != null)
          _restoreListener.onRestore();
        return true;
    }
    return super.onTouchEvent(event);
  }
}
