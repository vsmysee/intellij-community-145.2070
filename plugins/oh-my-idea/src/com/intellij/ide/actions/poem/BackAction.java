package com.intellij.ide.actions.poem;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BackAction extends AnAction {

    private static Stack<String> stack = new Stack<String>();

    public static void add(String poem) {
        stack.push(poem);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        JPanel holder = PoemToolWindowFactory.holder;
        holder.remove(0);
        String item = PoemToolWindowFactory.random();
        try {
            item = stack.pop();
            if (item.equals(ForwardAction.current)) {
                item = stack.pop();
            }
        } catch (Exception exception) {
        }

        List<String> poems = Arrays.asList(item.split(";"));

        holder.add(PoemBuilder.build(poems,false));
        holder.updateUI();
    }
}
