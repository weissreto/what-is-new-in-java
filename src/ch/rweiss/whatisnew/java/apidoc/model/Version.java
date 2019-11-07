package ch.rweiss.whatisnew.java.apidoc.model;

import org.apache.commons.lang3.StringUtils;

public class Version
{
  public static final Version UNDEFINED = new Version("UNDEFINED");
  private final String version;

  private Version(String version)
  {
    this.version = version;
  }

  public static Version valueOf(String version)
  {
    if (StringUtils.isBlank(version))
    {
      return UNDEFINED;
    }
    return new Version(version.trim());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (obj == null || obj.getClass() != Version.class)
    {
      return false;
    }
    Version other = (Version) obj;
    return other.version.equals(this.version);
  }

  @Override
  public int hashCode()
  {
    return version.hashCode();
  }

  @Override
  public String toString()
  {
    return version;
  }
}
