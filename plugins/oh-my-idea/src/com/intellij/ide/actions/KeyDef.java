package com.intellij.ide.actions;


import com.intellij.ide.actions.poem.PoemToolWindowFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyDef {

  private static List<String> load(String file) {
    InputStream is = null;
    BufferedReader reader = null;
    List<String> list = new ArrayList<>();
    try {
      is = PoemToolWindowFactory.class.getResourceAsStream(file);
      reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.equals("")) {
          list.add(line);
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (is != null) {
          is.close();
        }
        if (reader != null) {
          reader.close();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return list;
  }

  static void init() {

    Map<String, String> defaultKey = new HashMap<>();

    for (String configItem : load("/icons/COMMAND1.txt")) {
      String[] split = configItem.split(" ");
      String key = split[0];
      String value = split[1];
      defaultKey.put(key, value);
    }

    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND1, defaultKey);


    Map<String, String> COMMAND2 = new HashMap<>();

    for (String configItem : load("/icons/COMMAND2.txt")) {
      String[] split = configItem.split(" ");
      String key = split[0];
      String value = split[1];
      COMMAND2.put(key, value);
    }

    //y
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND2, COMMAND2);


    Map<String, String> COMMAND3 = new HashMap<>();

    for (String configItem : load("/icons/COMMAND3.txt")) {
      String[] split = configItem.split(" ");
      String key = split[0];
      String value = split[1];
      COMMAND3.put(key, value);
    }

    //S
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND3, COMMAND3);



    Map<String, String> COMMAND4 = new HashMap<>();

    for (String configItem : load("/icons/COMMAND4.txt")) {
      String[] split = configItem.split(" ");
      String key = split[0];
      String value = split[1];
      COMMAND4.put(key, value);
    }
    //I
    MyTypedActionHandler.modeMap.put(App.EditorMode.COMMAND4, COMMAND4);


  }

}
