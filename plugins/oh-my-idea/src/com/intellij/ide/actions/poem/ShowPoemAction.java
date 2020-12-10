package com.intellij.ide.actions.poem;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.Arrays;
import java.util.List;

public class ShowPoemAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {

    List<String> poems = Arrays.asList(PoemToolWindowFactory.random().split(";"));
    new ZoomDialog(poems);

  }

}
