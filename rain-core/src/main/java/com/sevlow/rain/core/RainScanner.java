package com.sevlow.rain.core;

import java.io.File;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

/**
 * classes scanner
 *
 * @author einsitang
 */
@Slf4j
public class RainScanner {

  private static final String CHECK_FILE_TYPE = ".class";

  private final String basePackage;

  public RainScanner(String basePackage) {
    this.basePackage = basePackage;
  }

  public void scan(ScanHandler scHandler) {
    URL url = ClassLoader.getSystemResource(
        basePackage.replaceAll("[.]", String.valueOf(File.separatorChar)));
    File file = new File(url.getFile());
    dfs4File(file, basePackage.substring(0, basePackage.lastIndexOf(".")), scHandler);
  }

  /**
   * 深度遍历，找出所有底层class
   *
   * @param file        class 文件
   * @param packageName 深度遍历的包名
   * @param scHandler   扫描处理器
   */
  private void dfs4File(File file, String packageName, ScanHandler scHandler) {
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files == null) {
        return;
      }
      for (File f : files) {
        dfs4File(f, packageName + "." + file.getName(), scHandler);
      }
    } else {
      String fileName = file.getName();
      // 检查是不是class
      if (fileName.lastIndexOf(".") + CHECK_FILE_TYPE.length() == fileName.length()) {
        // 符合
        String className = packageName + "." + className(fileName);
        log.debug("found class : [ " + fileName + " ] => " + className);
        scHandler.map(className);
      }
    }
  }

  private String className(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf("."));
  }

  @FunctionalInterface
  public interface ScanHandler {

    void map(String className);
  }

}
