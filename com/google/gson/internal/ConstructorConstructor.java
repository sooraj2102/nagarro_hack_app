package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor
{
  private final Map<Type, InstanceCreator<?>> instanceCreators;

  public ConstructorConstructor(Map<Type, InstanceCreator<?>> paramMap)
  {
    this.instanceCreators = paramMap;
  }

  private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> paramClass)
  {
    try
    {
      Constructor localConstructor = paramClass.getDeclaredConstructor(new Class[0]);
      if (!localConstructor.isAccessible())
        localConstructor.setAccessible(true);
      3 local3 = new ObjectConstructor(localConstructor)
      {
        public T construct()
        {
          try
          {
            Object localObject = this.val$constructor.newInstance(null);
            return localObject;
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new RuntimeException("Failed to invoke " + this.val$constructor + " with no args", localInstantiationException);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            throw new RuntimeException("Failed to invoke " + this.val$constructor + " with no args", localInvocationTargetException.getTargetException());
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
          }
          throw new AssertionError(localIllegalAccessException);
        }
      };
      return local3;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
    }
    return null;
  }

  private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type paramType, Class<? super T> paramClass)
  {
    if (Collection.class.isAssignableFrom(paramClass))
    {
      if (SortedSet.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new TreeSet();
          }
        };
      if (EnumSet.class.isAssignableFrom(paramClass))
        return new ObjectConstructor(paramType)
        {
          public T construct()
          {
            if ((this.val$type instanceof ParameterizedType))
            {
              Type localType = ((ParameterizedType)this.val$type).getActualTypeArguments()[0];
              if ((localType instanceof Class))
                return EnumSet.noneOf((Class)localType);
              throw new JsonIOException("Invalid EnumSet type: " + this.val$type.toString());
            }
            throw new JsonIOException("Invalid EnumSet type: " + this.val$type.toString());
          }
        };
      if (Set.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new LinkedHashSet();
          }
        };
      if (Queue.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new ArrayDeque();
          }
        };
      return new ObjectConstructor()
      {
        public T construct()
        {
          return new ArrayList();
        }
      };
    }
    if (Map.class.isAssignableFrom(paramClass))
    {
      if (ConcurrentNavigableMap.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new ConcurrentSkipListMap();
          }
        };
      if (ConcurrentMap.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new ConcurrentHashMap();
          }
        };
      if (SortedMap.class.isAssignableFrom(paramClass))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new TreeMap();
          }
        };
      if (((paramType instanceof ParameterizedType)) && (!String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)paramType).getActualTypeArguments()[0]).getRawType())))
        return new ObjectConstructor()
        {
          public T construct()
          {
            return new LinkedHashMap();
          }
        };
      return new ObjectConstructor()
      {
        public T construct()
        {
          return new LinkedTreeMap();
        }
      };
    }
    return null;
  }

  private <T> ObjectConstructor<T> newUnsafeAllocator(Type paramType, Class<? super T> paramClass)
  {
    return new ObjectConstructor(paramClass, paramType)
    {
      private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

      public T construct()
      {
        try
        {
          Object localObject = this.unsafeAllocator.newInstance(this.val$rawType);
          return localObject;
        }
        catch (Exception localException)
        {
        }
        throw new RuntimeException("Unable to invoke no-args constructor for " + this.val$type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", localException);
      }
    };
  }

  public <T> ObjectConstructor<T> get(TypeToken<T> paramTypeToken)
  {
    Type localType = paramTypeToken.getType();
    Class localClass = paramTypeToken.getRawType();
    InstanceCreator localInstanceCreator1 = (InstanceCreator)this.instanceCreators.get(localType);
    Object localObject;
    if (localInstanceCreator1 != null)
      localObject = new ObjectConstructor(localInstanceCreator1, localType)
      {
        public T construct()
        {
          return this.val$typeCreator.createInstance(this.val$type);
        }
      };
    do
    {
      return localObject;
      InstanceCreator localInstanceCreator2 = (InstanceCreator)this.instanceCreators.get(localClass);
      if (localInstanceCreator2 != null)
        return new ObjectConstructor(localInstanceCreator2, localType)
        {
          public T construct()
          {
            return this.val$rawTypeCreator.createInstance(this.val$type);
          }
        };
      localObject = newDefaultConstructor(localClass);
    }
    while (localObject != null);
    ObjectConstructor localObjectConstructor = newDefaultImplementationConstructor(localType, localClass);
    if (localObjectConstructor != null)
      return localObjectConstructor;
    return (ObjectConstructor<T>)newUnsafeAllocator(localType, localClass);
  }

  public String toString()
  {
    return this.instanceCreators.toString();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     com.google.gson.internal.ConstructorConstructor
 * JD-Core Version:    0.6.0
 */