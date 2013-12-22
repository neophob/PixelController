package com.neophob.sematrix.core.rmi;

import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.properties.ConfigConstant;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.rmi.RmiApi.Protocol;
import com.neophob.sematrix.core.rmi.impl.RmiFactory;
import com.neophob.sematrix.osc.model.OscMessage;

public class RmiTest implements Observer {

	private static final int BUFFERSIZE = 1024*60;
	private static final int PORT = 12346;
	
	private OscMessage m;
	
	@Before
	public void setUp() {
		m = null;
	}
	
	@Test
	public void RmiTestSimpleCompressed() throws Exception {
		RmiApi remoteServer = RmiFactory.getRmiApi(true, BUFFERSIZE);
		remoteServer.startServer(Protocol.UDP, this, PORT);
		remoteServer.startClient(Protocol.UDP, "localhost", PORT);
		remoteServer.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_A), null);
		Thread.sleep(200);
		assertNotNull(m);
		assertEquals(ValidCommand.CHANGE_GENERATOR_A.toString(), m.getPattern());
		remoteServer.shutdown();
	}

	@Test
	public void RmiTestSimpleUnCompressed() throws Exception {
		RmiApi remoteServer = RmiFactory.getRmiApi(false, BUFFERSIZE);
		remoteServer.startServer(Protocol.UDP, this, PORT);
		remoteServer.startClient(Protocol.UDP, "localhost", PORT);
		remoteServer.sendPayload(new Command(ValidCommand.CHANGE_GENERATOR_A), null);
		Thread.sleep(200);
		assertNotNull(m);
		assertEquals(ValidCommand.CHANGE_GENERATOR_A.toString(), m.getPattern());
		remoteServer.shutdown();
	}

	@Test
	public void RmiTestAdjustCompressionSetting() throws Exception {
		Properties p = new Properties();
		p.put(ConfigConstant.FPS, 234);
		ApplicationConfigurationHelper ach = new ApplicationConfigurationHelper(p);
		
		RmiApi remoteServer = RmiFactory.getRmiApi(true, BUFFERSIZE);
		remoteServer.startServer(Protocol.UDP, this, PORT);
		
		RmiApi remoteClient = RmiFactory.getRmiApi(false, BUFFERSIZE);		
		remoteClient.startClient(Protocol.UDP, "localhost", PORT);		
		remoteClient.sendPayload(new Command(ValidCommand.GET_CONFIGURATION), ach);
		
		Thread.sleep(200);
		
		assertNotNull(m);
		assertEquals(ValidCommand.GET_CONFIGURATION.toString(), m.getPattern());
		
		ApplicationConfigurationHelper ach2 = remoteServer.reassembleObject(m.getBlob(), ApplicationConfigurationHelper.class);
		assertEquals(ach.parseFps(), ach2.parseFps(), 0.0001);
		remoteServer.shutdown();
		remoteClient.shutdown();
	}
	
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof OscMessage) {
			m = (OscMessage)arg;			
		}
	}
}
