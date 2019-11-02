package ch.rweiss.whatisnew.java.model;

import java.util.Optional;

public enum ApiModifier 
{
  PUBLIC,
  PROTECTED,
  STATIC,
  FINAL;
  
  public static Optional<ApiModifier> parse(String str)
  {
	for (ApiModifier modifier : values())
	{
		if (modifier.name().equalsIgnoreCase(str))
		{
			return Optional.of(modifier);
		}
	}
	return Optional.empty(); 
  }
}
