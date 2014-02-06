package com.neophob.sematrix.gui.guibuilder.eventhandler;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neophob.sematrix.gui.guibuilder.GeneratorGui;
import com.neophob.sematrix.gui.model.GuiElement;
import com.neophob.sematrix.gui.service.PixConServer;

import controlP5.ControlEvent;
import controlP5.ControlGroup;

@RunWith(MockitoJUnitRunner.class)
public class P5EventListenerTest {

    @Mock
    private PixConServer pixConSrv;

    @Mock
    private GeneratorGui callback;

    @Mock
    private ControlEvent theEvent;

    @Mock
    private ControlGroup controlGroup;

    @Test
    public void testEventListener() {
        P5EventListener event = new P5EventListener(pixConSrv, callback);

        when(theEvent.getName()).thenReturn(GuiElement.BRIGHTNESS.toString());
        when(theEvent.isGroup()).thenReturn(true);
        when(theEvent.getGroup()).thenReturn(controlGroup);
        when(controlGroup.getValue()).thenReturn(333f);
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn("does not exist...");
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(null);
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.CURRENT_OUTPUT.toString());
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.BLINKENLIGHTS_DROPDOWN.toString());
        event.controlEvent(theEvent);

        when(theEvent.isGroup()).thenReturn(false);
        event.controlEvent(theEvent);
    }
}
