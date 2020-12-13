package com.intellij.ide.actions.poem;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class ZoomDialog extends JDialog {

    private JComponent poem;

    public ZoomDialog(List<String> poems) {

        setDefaultCloseOperation(2);

        ActionListener closeAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZoomDialog.this.dispose();
            }
        };

        getRootPane().registerKeyboardAction(closeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);


        poem = PoemBuilder.build(poems,true);
        add(poem);
        pack();

        if (getWidth() < 600) {
            setSize(600,getHeight());
        }

        setLocationRelativeTo(null);


        setVisible(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    ZoomDialog.this.dispose();
                }
            }
        });

        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),
                "nextPoem");
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DOWN"),
                "nextPoem");


        getRootPane().getActionMap().put("nextPoem",
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {

                        List<String> poems = Arrays.asList(PoemToolWindowFactory.random().split(";"));
                        refresh(poems);

                    }
                });

    }




    public void refresh(List<String> poems) {

        remove(poem);

        poem = PoemBuilder.build(poems,true);
        add(poem);

        pack();

        if (getWidth() < 600) {
            setSize(600,getHeight());
        }


        setLocationRelativeTo(null);

    }


}
