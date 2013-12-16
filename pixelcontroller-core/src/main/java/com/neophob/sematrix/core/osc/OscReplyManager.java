package com.neophob.sematrix.core.osc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neophob.sematrix.core.api.PixelController;
import com.neophob.sematrix.core.properties.ValidCommands;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.ColorSet;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.client.PixOscClient;
import com.neophob.sematrix.osc.model.OscMessage;

/**
 * the osc reply manager sends back gui information, encoded
 * as osc package.
 * 
 * @author michu
 *
 */
public class OscReplyManager {

	private static final Logger LOG = Logger.getLogger(OscReplyManager.class.getName());
	
	private PixOscClient oscClient;
	private PixelController pixelController;
	
	public OscReplyManager(PixelController pixelController, PixOscClient oscClient, OscMessage oscIn) throws OscClientException {
		this.pixelController = pixelController;
		this.oscClient = oscClient;
	}

	public void sendReply(String[] msg) throws OscClientException {
				
		ValidCommands cmd = ValidCommands.valueOf(msg[0]);
		OscMessage reply = null;

		switch (cmd) {
		case GET_CONFIGURATION:				
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getConfig()));						
			break;
			
		case GET_MATRIXDATA:				
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getMatrix()));			
			break;

		case GET_VERSION:
			reply = new OscMessage(cmd.toString(), pixelController.getVersion());
			break;
			
		case GET_COLORSETS:
			reply = new OscMessage(cmd.toString(), convertFromObject((ArrayList<ColorSet>)pixelController.getColorSets()));
			break;
		
		case GET_OUTPUTMAPPING:
			reply = new OscMessage(cmd.toString(), convertFromObject((CopyOnWriteArrayList<OutputMapping>)pixelController.getAllOutputMappings()));
			break;
			
		case GET_OUTPUTBUFFER:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getOutput()));
			break;
			
		case GET_GUISTATE:
			reply = new OscMessage(cmd.toString(), convertFromObject((ArrayList<String>)pixelController.getGuiState()));
			break;
			
		case GET_PRESETSETTINGS:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getPresetService().getSelectedPresetSettings()));
			break;
			
		case GET_JMXSTATISTICS:
			reply = new OscMessage(cmd.toString(), convertFromObject(pixelController.getPixConStat()));
			break;
			
		}
		
		if (reply!=null) {
			oscClient.sendMessage(reply);
			LOG.log(Level.INFO, cmd.toString()+" reply size: "+reply.getMessageSize());			
		}
	}
	
	private byte[] convertFromObject(Serializable s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(s);
		  return bos.toByteArray();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Failed to serializable object", e);
			return new byte[0];
		} finally {
		  try {
		    if (out != null) {
		      out.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
	}
}
