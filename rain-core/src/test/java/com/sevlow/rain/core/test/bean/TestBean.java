package com.sevlow.rain.core.test.bean;

import com.sevlow.rain.core.common.RainComponent;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
@TestComponent(hello = "world")
@RainComponent(name="aaa")
public class TestBean implements Serializable {


  @Serial
  private static final long serialVersionUID = 5791151669064899755L;

  private String hello;
}
