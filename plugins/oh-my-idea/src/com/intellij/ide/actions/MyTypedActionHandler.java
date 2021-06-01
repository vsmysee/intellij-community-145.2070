package com.intellij.ide.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MyTypedActionHandler implements TypedActionHandler {

  public static Map<App.EditorMode, Map<String, String>> modeMap = new HashMap<App.EditorMode, Map<String, String>>();

  static {
    KeyDef.init();
  }

  private TypedActionHandler origHandler;

  public MyTypedActionHandler(TypedActionHandler handler) {
    this.origHandler = handler;
  }


  private void setCursors(App.EditorMode editorMode) {
    for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
      editor.getSettings().setBlockCursor(editorMode != App.EditorMode.INSERT);
    }
  }

  private static Map<Character, App.EditorMode> modMapping = new HashMap<>();

  static {
    modMapping.put('i', App.EditorMode.INSERT);
    modMapping.put('y', App.EditorMode.COMMAND2);
    modMapping.put('S', App.EditorMode.COMMAND3);
    modMapping.put('I', App.EditorMode.COMMAND4);
  }

  @Override
  public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {

    if (App.editorMode == App.EditorMode.COMMAND1) {

      if (modMapping.containsKey(charTyped)) {
        App.editorMode = modMapping.get(charTyped);
        setCursors(App.editorMode);
        return;

      }
    }

    if (App.editorMode == App.EditorMode.INSERT) {
      origHandler.execute(editor, charTyped, dataContext);
      return;
    }

    Map<String, String> keyMapping = modeMap.get(App.editorMode);
    if (keyMapping.containsKey(String.valueOf(charTyped))) {
      doAction(dataContext, keyMapping.get(String.valueOf(charTyped)));
    }

  }


  private void doAction(final DataContext dataContext, String actionName) {
    final AnAction action = ActionManager.getInstance().getAction(actionName);
    if (action != null) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {

        @Override
        public void run() {
          try {
            action.actionPerformed(AnActionEvent.createFromAnAction(action, null, "", dataContext));
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }

      });
    }
  }


  public TypedActionHandler getOriginalTypedHandler() {
    return origHandler;
  }


}
