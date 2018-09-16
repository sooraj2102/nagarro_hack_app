package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator
{
  private static void assertInstantiable(Class<?> paramClass)
  {
    int i = paramClass.getModifiers();
    if (Modifier.isInterface(i))
      throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + paramClass.getName());
    if (Modifier.isAbstract(i))
      throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + paramClass.getName());
  }

  public static UnsafeAllocator create()
  {
    try
    {
      Class localClass = Class.forName("sun.misc.Unsafe");
      Field localField = localClass.getDeclaredField("theUnsafe");
      localField.setAccessible(true);
      Object localObject = localField.get(null);
      1 local1 = new UnsafeAllocator(localClass.getMethod("allocateInstance", new Class[] { Class.class }), localObject)
      {
        public <T> T newInstance(Class<T> paramClass)
          throws Exception
        {
          UnsafeAllocator.access$000(paramClass);
          return this.val$allocateInstance.invoke(this.val$unsafe, new Object[] { paramClass });
        }
      };
      return local1;
    }
    catch (Exception localException1)
    {
      try
      {
        Method localMethod2 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
        localMethod2.setAccessible(true);
        int i = ((Integer)localMethod2.invoke(null, new Object[] { Object.class })).intValue();
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = Class.class;
        arrayOfClass[1] = Integer.TYPE;
        Method localMethod3 = ObjectStreamClass.class.getDeclaredMethod("newInstance", arrayOfClass);
        localMethod3.setAccessible(true);
        2 local2 = new UnsafeAllocator(localMethod3, i)
        {
          public <T> T newInstance(Class<T> paramClass)
            throws Exception
          {
            UnsafeAllocator.access$000(paramClass);
            Method localMethod = this.val$newInstance;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = paramClass;
            arrayOfObject[1] = Integer.valueOf(this.val$constructorId);
            return localMethod.invoke(null, arrayOfObject);
          }
        };
        return local2;
      }
      catch (Exception localException2)
      {
        try
        {
          Method localMethod1 = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
          localMethod1.setAccessible(true);
          3 local3 = new UnsafeAllocator(localMethod1)
          {
            public <T> T newInstance(Class<T> paramClass)
              throws Exception
            {
              UnsafeAllocator.access$000(paramClass);
              return this.val$newInstance.invoke(null, new Object[] { paramClass, Object.class });
            }
          };
          return local3;
        }
        catch (Exception localException3)
        {
        }
      }
    }
    return new UnsafeAllocator()
    {
      public <T> T newInstance(Class<T> paramClass)
      {
        throw new UnsupportedOperationException("Cannot allocate " + paramClass);
      }
    };
  }

  public abstract <T> T newInstance(Class<T> paramClass)
    throws Exception;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.UnsafeAllocator
 * JD-Core Version:    0.6.0
 */