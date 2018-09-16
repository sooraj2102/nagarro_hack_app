package rx.plugins;

final class RxJavaObservableExecutionHookDefault extends RxJavaObservableExecutionHook
{
  private static final RxJavaObservableExecutionHookDefault INSTANCE = new RxJavaObservableExecutionHookDefault();

  public static RxJavaObservableExecutionHook getInstance()
  {
    return INSTANCE;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     rx.plugins.RxJavaObservableExecutionHookDefault
 * JD-Core Version:    0.6.0
 */