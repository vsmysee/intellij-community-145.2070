package com.intellij.ide.actions.poem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PoemBuilder {

  private static final int titleSize = 25;
  private static final int bodySize = 35;


  private static JComponent title(List<String> poems) {

    Box horizontalBox = Box.createHorizontalBox();

    horizontalBox.add(Box.createHorizontalGlue());

    JPanel title = new JPanel();

    String text = poems.get(1);
    int endIndex = text.indexOf("《");
    String author = text.substring(0, endIndex);
    JLabel comp = buildJLabel(text, titleSize);
    comp.setText("<html><font color=rgb(72,72,72)>" + author + "</font><font color='blue'>" + text.substring(endIndex) + "</font></html>");

    horizontalBox.add(title);

    horizontalBox.add(Box.createHorizontalGlue());

    title.add(comp);
    return horizontalBox;
  }

  public static JComponent build(List<String> poems, boolean pop) {

    JComponent poemRoot = Box.createVerticalBox();

    poemRoot.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

    poemRoot.add(title(poems));

    poemRoot.add(new JSeparator());
    poemRoot.add(Box.createVerticalStrut(10));

    List<JPanel> poemPanel = new ArrayList<>();

    int index = 2;

    if (pop) {

      while (index < poems.size()) {

        JPanel panel = new JPanel();

        String left = poems.get(index).trim();
        panel.add(buildJLabel(left, bodySize));

        if ((index + 1) < poems.size()) {

          String right = poems.get(index + 1).trim();

          if (left.length() == right.length()) {
            panel.add(buildJLabel("，", bodySize));
            panel.add(buildJLabel(right, bodySize));
            index++;
          }

        }

        index++;

        poemPanel.add(panel);

      }


    }
    else {


      while (index < poems.size()) {

        JPanel panel = new JPanel();

        String item = poems.get(index).trim();
        panel.add(buildJLabel(item, bodySize));

        index++;

        poemPanel.add(panel);

      }

    }


    Box poemContent = Box.createVerticalBox();
    for (JPanel poemLabel : poemPanel) {
      poemContent.add(poemLabel);
    }


    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottom.add(new CirclePanel(poems.get(0), 20, 25));
    poemContent.add(bottom);


    poemRoot.add(poemContent);

    return poemRoot;
  }

  private static JLabel buildJLabel(String text, int size) {
    JLabel item = new JLabel(text);
    item.setFont(new Font(Setting.FONT, Font.BOLD, size));
    return item;
  }


}
