package android.support.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public class ArcMotion extends PathMotion
{
  private static final float DEFAULT_MAX_ANGLE_DEGREES = 70.0F;
  private static final float DEFAULT_MAX_TANGENT = (float)Math.tan(Math.toRadians(35.0D));
  private static final float DEFAULT_MIN_ANGLE_DEGREES;
  private float mMaximumAngle = 70.0F;
  private float mMaximumTangent = DEFAULT_MAX_TANGENT;
  private float mMinimumHorizontalAngle = 0.0F;
  private float mMinimumHorizontalTangent = 0.0F;
  private float mMinimumVerticalAngle = 0.0F;
  private float mMinimumVerticalTangent = 0.0F;

  public ArcMotion()
  {
  }

  public ArcMotion(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.ARC_MOTION);
    XmlPullParser localXmlPullParser = (XmlPullParser)paramAttributeSet;
    setMinimumVerticalAngle(TypedArrayUtils.getNamedFloat(localTypedArray, localXmlPullParser, "minimumVerticalAngle", 1, 0.0F));
    setMinimumHorizontalAngle(TypedArrayUtils.getNamedFloat(localTypedArray, localXmlPullParser, "minimumHorizontalAngle", 0, 0.0F));
    setMaximumAngle(TypedArrayUtils.getNamedFloat(localTypedArray, localXmlPullParser, "maximumAngle", 2, 70.0F));
    localTypedArray.recycle();
  }

  private static float toTangent(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat > 90.0F))
      throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
    return (float)Math.tan(Math.toRadians(paramFloat / 2.0F));
  }

  public float getMaximumAngle()
  {
    return this.mMaximumAngle;
  }

  public float getMinimumHorizontalAngle()
  {
    return this.mMinimumHorizontalAngle;
  }

  public float getMinimumVerticalAngle()
  {
    return this.mMinimumVerticalAngle;
  }

  public Path getPath(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Path localPath = new Path();
    localPath.moveTo(paramFloat1, paramFloat2);
    float f1 = paramFloat3 - paramFloat1;
    float f2 = paramFloat4 - paramFloat2;
    float f3 = f1 * f1 + f2 * f2;
    float f4 = (paramFloat1 + paramFloat3) / 2.0F;
    float f5 = (paramFloat2 + paramFloat4) / 2.0F;
    float f6 = f3 * 0.25F;
    int i;
    float f17;
    float f9;
    float f8;
    label113: float f10;
    float f13;
    float f14;
    float f15;
    if (paramFloat2 > paramFloat4)
    {
      i = 1;
      if (Math.abs(f1) >= Math.abs(f2))
        break label279;
      f17 = Math.abs(f3 / (2.0F * f2));
      if (i == 0)
        break label267;
      f9 = paramFloat4 + f17;
      f8 = paramFloat3;
      f10 = f6 * this.mMinimumVerticalTangent * this.mMinimumVerticalTangent;
      float f11 = f4 - f8;
      float f12 = f5 - f9;
      f13 = f11 * f11 + f12 * f12;
      f14 = f6 * this.mMaximumTangent * this.mMaximumTangent;
      if (f13 >= f10)
        break label332;
      f15 = f10;
    }
    while (true)
    {
      if (f15 != 0.0F)
      {
        float f16 = (float)Math.sqrt(f15 / f13);
        f8 = f4 + f16 * (f8 - f4);
        f9 = f5 + f16 * (f9 - f5);
      }
      localPath.cubicTo((paramFloat1 + f8) / 2.0F, (paramFloat2 + f9) / 2.0F, (f8 + paramFloat3) / 2.0F, (f9 + paramFloat4) / 2.0F, paramFloat3, paramFloat4);
      return localPath;
      i = 0;
      break;
      label267: f9 = paramFloat2 + f17;
      f8 = paramFloat1;
      break label113;
      label279: float f7 = f3 / (2.0F * f1);
      if (i != 0)
      {
        f8 = paramFloat1 + f7;
        f9 = paramFloat2;
      }
      while (true)
      {
        f10 = f6 * this.mMinimumHorizontalTangent * this.mMinimumHorizontalTangent;
        break;
        f8 = paramFloat3 - f7;
        f9 = paramFloat4;
      }
      label332: boolean bool = f13 < f14;
      f15 = 0.0F;
      if (!bool)
        continue;
      f15 = f14;
    }
  }

  public void setMaximumAngle(float paramFloat)
  {
    this.mMaximumAngle = paramFloat;
    this.mMaximumTangent = toTangent(paramFloat);
  }

  public void setMinimumHorizontalAngle(float paramFloat)
  {
    this.mMinimumHorizontalAngle = paramFloat;
    this.mMinimumHorizontalTangent = toTangent(paramFloat);
  }

  public void setMinimumVerticalAngle(float paramFloat)
  {
    this.mMinimumVerticalAngle = paramFloat;
    this.mMinimumVerticalTangent = toTangent(paramFloat);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ArcMotion
 * JD-Core Version:    0.6.0
 */