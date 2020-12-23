package com.intellij.ide.actions.poem;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

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

  private static JComponent build2(List<String> poems) {
    Box horizontalBox = Box.createHorizontalBox();

    String title = poems.get(1);
    int index = title.indexOf("《");

    for (int i = poems.size() - 1; i > 1; i--) {
      horizontalBox.add(convertToHtml(poems.get(i), false));
    }

    Box box = Box.createVerticalBox();
    CirclePanel song = new CirclePanel(poems.get(0));
    song.setMaximumSize(new Dimension(16, 16));
    box.add(buildAuthor("   "));
    box.add(song);
    box.add(buildAuthor(" "));
    box.add(buildAuthor(title.substring(0, index)));


    horizontalBox.add(box);
    horizontalBox.add(Box.createHorizontalStrut(15));


    title = title.substring(index);
    horizontalBox.add(convertToHtml(title.replace("《", "").replace("》", ""), true));

    horizontalBox.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    return horizontalBox;
  }


  private static JLabel buildAuthor(String poem) {
    String[] split = poem.split("");
    StringBuffer sb = new StringBuffer();
    sb.append("<html>");
    sb.append("<font color='grey'>");

    for (String item : split) {
      sb.append(item);
      sb.append("<br/>");
    }

    sb.append("</font>");
    sb.append("</html>");
    JLabel label = buildJLabel(sb.toString(), 25);
    return label;
  }

  private static JLabel convertToHtml(String poem, boolean title) {
    String[] split = poem.split("");
    StringBuffer sb = new StringBuffer();
    sb.append("<html>");
    if (title) {
      sb.append("<font color='blue'>");
    }

    for (String item : split) {
      sb.append(item);
      sb.append("<br/>");
    }

    if (title) {
      sb.append("</font>");
    }
    sb.append("</html>");
    JLabel label = buildJLabel(sb.toString(), title ? 25 : 35);
    return label;
  }

  public static JComponent build(List<String> poems, boolean pop) {

    if (poems.size() >= 12 && pop) {
      return build2(poems);
    }

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

    if (poems.size() > 20) {
      return new JBScrollPane(poemRoot,VERTICAL_SCROLLBAR_AS_NEEDED,HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    return poemRoot;
  }

  private static JLabel buildJLabel(String text, int size) {
    JLabel item = new JLabel(text);
    item.setFont(new Font(Setting.FONT, Font.BOLD, size));
    return item;
  }


}
