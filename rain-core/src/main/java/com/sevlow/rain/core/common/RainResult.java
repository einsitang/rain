package com.sevlow.rain.core.common;

import lombok.Getter;

/**
 * RainComponent
 * <p>
 * 标记 Component 包装类
 *
 * @param <T> T extends Object
 * @author einsitnag
 */
@Getter
public class RainResult<T> {

  private boolean match = true;

  private final T obj;

  private final String name;

  public RainResult(T obj, String name) {
    this.obj = obj;
    this.name = name;
  }

  public static RainResult<?> notMatch() {
    RainResult<?> rc = new RainResult<>(null, null);
    rc.match = false;
    return rc;
  }

  public static <T> RainResult<T> match(T obj) {
    return new RainResult<>(obj, null);
  }

  public static <T> RainResult<T> match(T obj, String name) {
    return new RainResult<>(obj, name);
  }

}
