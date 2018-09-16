package android.support.transition;

import android.animation.TypeEvaluator;

class FloatArrayEvaluator
  implements TypeEvaluator<float[]>
{
  private float[] mArray;

  FloatArrayEvaluator(float[] paramArrayOfFloat)
  {
    this.mArray = paramArrayOfFloat;
  }

  public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float[] arrayOfFloat = this.mArray;
    if (arrayOfFloat == null)
      arrayOfFloat = new float[paramArrayOfFloat1.length];
    for (int i = 0; i < arrayOfFloat.length; i++)
    {
      float f = paramArrayOfFloat1[i];
      arrayOfFloat[i] = (f + paramFloat * (paramArrayOfFloat2[i] - f));
    }
    return arrayOfFloat;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.FloatArrayEvaluator
 * JD-Core Version:    0.6.0
 */