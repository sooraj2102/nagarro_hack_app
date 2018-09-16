package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.animation.Interpolator;
import org.xmlpull.v1.XmlPullParser;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PathInterpolatorCompat
  implements Interpolator
{
  public static final double EPSILON = 1.E-05D;
  public static final int MAX_NUM_POINTS = 3000;
  private static final float PRECISION = 0.002F;
  private float[] mX;
  private float[] mY;

  public PathInterpolatorCompat(Context paramContext, AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet, paramXmlPullParser);
  }

  public PathInterpolatorCompat(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet, XmlPullParser paramXmlPullParser)
  {
    TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.STYLEABLE_PATH_INTERPOLATOR);
    parseInterpolatorFromTypeArray(localTypedArray, paramXmlPullParser);
    localTypedArray.recycle();
  }

  private void initCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.cubicTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, 1.0F, 1.0F);
    initPath(localPath);
  }

  private void initPath(Path paramPath)
  {
    PathMeasure localPathMeasure = new PathMeasure(paramPath, false);
    float f1 = localPathMeasure.getLength();
    int i = Math.min(3000, 1 + (int)(f1 / 0.002F));
    if (i <= 0)
      throw new IllegalArgumentException("The Path has a invalid length " + f1);
    this.mX = new float[i];
    this.mY = new float[i];
    float[] arrayOfFloat1 = new float[2];
    for (int j = 0; j < i; j++)
    {
      localPathMeasure.getPosTan(f1 * j / (i - 1), arrayOfFloat1, null);
      this.mX[j] = arrayOfFloat1[0];
      this.mY[j] = arrayOfFloat1[1];
    }
    if ((Math.abs(this.mX[0]) > 1.E-05D) || (Math.abs(this.mY[0]) > 1.E-05D) || (Math.abs(this.mX[(i - 1)] - 1.0F) > 1.E-05D) || (Math.abs(this.mY[(i - 1)] - 1.0F) > 1.E-05D))
      throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1) start: " + this.mX[0] + "," + this.mY[0] + " end:" + this.mX[(i - 1)] + "," + this.mY[(i - 1)]);
    float f2 = 0.0F;
    int k = 0;
    int n;
    for (int m = 0; k < i; m = n)
    {
      float[] arrayOfFloat2 = this.mX;
      n = m + 1;
      float f3 = arrayOfFloat2[m];
      if (f3 < f2)
        throw new IllegalArgumentException("The Path cannot loop back on itself, x :" + f3);
      this.mX[k] = f3;
      f2 = f3;
      k++;
    }
    if (localPathMeasure.nextContour())
      throw new IllegalArgumentException("The Path should be continuous, can't have 2+ contours");
  }

  private void initQuad(float paramFloat1, float paramFloat2)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.quadTo(paramFloat1, paramFloat2, 1.0F, 1.0F);
    initPath(localPath);
  }

  private void parseInterpolatorFromTypeArray(TypedArray paramTypedArray, XmlPullParser paramXmlPullParser)
  {
    if (TypedArrayUtils.hasAttribute(paramXmlPullParser, "pathData"))
    {
      String str = TypedArrayUtils.getNamedString(paramTypedArray, paramXmlPullParser, "pathData", 4);
      Path localPath = PathParser.createPathFromPathData(str);
      if (localPath == null)
        throw new InflateException("The path is null, which is created from " + str);
      initPath(localPath);
      return;
    }
    if (!TypedArrayUtils.hasAttribute(paramXmlPullParser, "controlX1"))
      throw new InflateException("pathInterpolator requires the controlX1 attribute");
    if (!TypedArrayUtils.hasAttribute(paramXmlPullParser, "controlY1"))
      throw new InflateException("pathInterpolator requires the controlY1 attribute");
    float f1 = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "controlX1", 0, 0.0F);
    float f2 = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "controlY1", 1, 0.0F);
    boolean bool = TypedArrayUtils.hasAttribute(paramXmlPullParser, "controlX2");
    if (bool != TypedArrayUtils.hasAttribute(paramXmlPullParser, "controlY2"))
      throw new InflateException("pathInterpolator requires both controlX2 and controlY2 for cubic Beziers.");
    if (!bool)
    {
      initQuad(f1, f2);
      return;
    }
    initCubic(f1, f2, TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "controlX2", 2, 0.0F), TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "controlY2", 3, 0.0F));
  }

  public float getInterpolation(float paramFloat)
  {
    if (paramFloat <= 0.0F)
      return 0.0F;
    if (paramFloat >= 1.0F)
      return 1.0F;
    int i = 0;
    int j = -1 + this.mX.length;
    while (j - i > 1)
    {
      int k = (i + j) / 2;
      if (paramFloat < this.mX[k])
      {
        j = k;
        continue;
      }
      i = k;
    }
    float f1 = this.mX[j] - this.mX[i];
    if (f1 == 0.0F)
      return this.mY[i];
    float f2 = (paramFloat - this.mX[i]) / f1;
    float f3 = this.mY[i];
    return f3 + f2 * (this.mY[j] - f3);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.graphics.drawable.PathInterpolatorCompat
 * JD-Core Version:    0.6.0
 */