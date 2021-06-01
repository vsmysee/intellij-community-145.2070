package com.intellij.ide.actions.poem;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ShowPoemAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {

    String random = PoemToolWindowFactory.random();
    ZoomDialog.getInstance(random);


  }

}
