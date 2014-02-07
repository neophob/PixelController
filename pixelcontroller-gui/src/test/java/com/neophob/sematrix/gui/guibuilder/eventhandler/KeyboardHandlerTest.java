package com.neophob.sematrix.gui.guibuilder.eventhandler;

import static org.mockito.Mockito.when;

import java.awt.event.KeyEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.gui.guibuilder.GuiCallbackAction;
import com.neophob.sematrix.gui.service.PixConServer;

@RunWith(MockitoJUnitRunner.class)
public class KeyboardHandlerTest {

    @Mock
    private PixConServer pixConSrv;

    @Mock
    private GuiCallbackAction callback;

    @Test
    public void testKeyboardHandler() {
        KeyboardHandler.keyboardHandler('R', 0);

        KeyboardHandler.init(callback, pixConSrv);
        KeyboardHandler.keyboardHandler('E', 0);

        when(callback.isTextfieldInEditMode()).thenReturn(true);
        KeyboardHandler.keyboardHandler('W', 0);

        when(callback.isTextfieldInEditMode()).thenReturn(false);
        KeyboardHandler.keyboardHandler('W', 0);

        KeyboardHandler.keyboardHandler('.', KeyEvent.VK_LEFT);

        KeyboardHandler.keyboardHandler('2', 0);
    }
}
