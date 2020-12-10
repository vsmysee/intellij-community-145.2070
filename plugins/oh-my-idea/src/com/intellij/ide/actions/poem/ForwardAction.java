package com.intellij.ide.actions.poem;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class ForwardAction extends AnAction {

    public static String current;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        JPanel holder = PoemToolWindowFactory.holder;
        holder.remove(0);
        String random = PoemToolWindowFactory.random();
        current = random;

        BackAction.add(random);

        List<String> poems = Arrays.asList(random.split(";"));

        holder.add(PoemBuilder.build(poems,false).getPoem());
        holder.updateUI();

    }
}
