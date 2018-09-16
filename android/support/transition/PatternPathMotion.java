package android.support.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public class PatternPathMotion extends PathMotion
{
  private Path mOriginalPatternPath;
  private final Path mPatternPath = new Path();
  private final Matrix mTempMatrix = new Matrix();

  public PatternPathMotion()
  {
    this.mPatternPath.lineTo(1.0F, 0.0F);
    this.mOriginalPatternPath = this.mPatternPath;
  }

  public PatternPathMotion(Context paramContext, AttributeSet paramAttributeSet)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.PATTERN_PATH_MOTION);
    String str;
    try
    {
      str = TypedArrayUtils.getNamedString(localTypedArray, (XmlPullParser)paramAttributeSet, "patternPathData", 0);
      if (str == null)
        throw new RuntimeException("pathData must be supplied for patternPathMotion");
    }
    finally
    {
      localTypedArray.recycle();
    }
    setPatternPath(PathParser.createPathFromPathData(str));
    localTypedArray.recycle();
  }

  public PatternPathMotion(Path paramPath)
  {
    setPatternPath(paramPath);
  }

  private static float distance(float paramFloat1, float paramFloat2)
  {
    return (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
  }

  public Path getPath(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramFloat3 - paramFloat1;
    float f2 = paramFloat4 - paramFloat2;
    float f3 = distance(f1, f2);
    double d = Math.atan2(f2, f1);
    this.mTempMatrix.setScale(f3, f3);
    this.mTempMatrix.postRotate((float)Math.toDegrees(d));
    this.mTempMatrix.postTranslate(paramFloat1, paramFloat2);
    Path localPath = new Path();
    this.mPatternPath.transform(this.mTempMatrix, localPath);
    return localPath;
  }

  public Path getPatternPath()
  {
    return this.mOriginalPatternPath;
  }

  public void setPatternPath(Path paramPath)
  {
    PathMeasure localPathMeasure = new PathMeasure(paramPath, false);
    float f1 = localPathMeasure.getLength();
    float[] arrayOfFloat = new float[2];
    localPathMeasure.getPosTan(f1, arrayOfFloat, null);
    float f2 = arrayOfFloat[0];
    float f3 = arrayOfFloat[1];
    localPathMeasure.getPosTan(0.0F, arrayOfFloat, null);
    float f4 = arrayOfFloat[0];
    float f5 = arrayOfFloat[1];
    if ((f4 == f2) && (f5 == f3))
      throw new IllegalArgumentException("pattern must not end at the starting point");
    this.mTempMatrix.setTranslate(-f4, -f5);
    float f6 = f2 - f4;
    float f7 = f3 - f5;
    float f8 = 1.0F / distance(f6, f7);
    this.mTempMatrix.postScale(f8, f8);
    double d = Math.atan2(f7, f6);
    this.mTempMatrix.postRotate((float)Math.toDegrees(-d));
    paramPath.transform(this.mTempMatrix, this.mPatternPath);
    this.mOriginalPatternPath = paramPath;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.PatternPathMotion
 * JD-Core Version:    0.6.0
 */