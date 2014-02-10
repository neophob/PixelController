package com.neophob.sematrix.gui.guibuilder.eventhandler;

import static org.mockito.Mockito.when;

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

    // @Test
    public void testEventListener() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            // this test will initialize process, which cannot run on a headless
            // machine!
            return;
        }
        P5EventListener event = new P5EventListener(pixConSrv, callback);

        when(theEvent.getName()).thenReturn(GuiElement.BRIGHTNESS.toString());
        when(theEvent.isGroup()).thenReturn(true);
        when(theEvent.getGroup()).thenReturn(controlGroup);
        when(controlGroup.getValue()).thenReturn(333f);
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn("does not exist...");
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.RANDOM_ELEMENT.toString());
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.RANDOM_ELEMENT.toString());
        when(theEvent.getGroup().getArrayValue()).thenReturn(new float[] { 2, 4, 5, 2, 3 });
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.BUTTONS_RANDOM_MODE.toString());
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.EFFECT_ONE_DROPDOWN.toString());
        event.controlEvent(theEvent);

        when(theEvent.getName()).thenReturn(GuiElement.GENERATOR_TWO_DROPDOWN.toString());
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
