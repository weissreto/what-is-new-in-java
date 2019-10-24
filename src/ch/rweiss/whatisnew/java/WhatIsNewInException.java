package ch.rweiss.whatisnew.java;

public class WhatIsNewInException extends RuntimeException
{
  public WhatIsNewInException(Throwable cause)
  {
    super(cause);
  }
  
  public WhatIsNewInException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
