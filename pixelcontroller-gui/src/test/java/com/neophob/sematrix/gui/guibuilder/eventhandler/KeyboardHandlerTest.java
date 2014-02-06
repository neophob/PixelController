package com.neophob.sematrix.gui.guibuilder.eventhandler;

import static org.mockito.Mockito.when;

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
        KeyboardHandler.keyboardHandler('R', 0);

        when(callback.isTextfieldInEditMode()).thenReturn(true);
        KeyboardHandler.keyboardHandler('R', 0);
    }
}
