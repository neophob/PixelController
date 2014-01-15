package com.neophob.sematrix.gui.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.neophob.sematrix.core.api.CallbackMessageInterface;
import com.neophob.sematrix.core.glue.FileUtils;
import com.neophob.sematrix.core.glue.impl.FileUtilsRemoteImpl;
import com.neophob.sematrix.core.jmx.PixelControllerStatusMBean;
import com.neophob.sematrix.core.osc.PixelControllerOscServer;
import com.neophob.sematrix.core.osc.remotemodel.ImageBuffer;
import com.neophob.sematrix.core.output.IOutput;
import com.neophob.sematrix.core.preset.PresetSettings;
import com.neophob.sematrix.core.properties.ApplicationConfigurationHelper;
import com.neophob.sematrix.core.properties.Command;
import com.neophob.sematrix.core.properties.ValidCommand;
import com.neophob.sematrix.core.rmi.RmiApi;
import com.neophob.sematrix.core.rmi.RmiApi.Protocol;
import com.neophob.sematrix.core.rmi.impl.RmiFactory;
import com.neophob.sematrix.core.sound.ISound;
import com.neophob.sematrix.core.sound.SoundDummy;
import com.neophob.sematrix.core.visual.MatrixData;
import com.neophob.sematrix.core.visual.OutputMapping;
import com.neophob.sematrix.core.visual.color.IColorSet;
import com.neophob.sematrix.gui.service.PixConServer;
import com.neophob.sematrix.mdns.client.MDnsClientException;
import com.neophob.sematrix.mdns.client.PixMDnsClient;
import com.neophob.sematrix.mdns.client.impl.MDnsClientFactory;
import com.neophob.sematrix.mdns.server.PixMDnsServer;
import com.neophob.sematrix.osc.client.OscClientException;
import com.neophob.sematrix.osc.model.OscMessage;
import com.neophob.sematrix.osc.server.OscMessageHandler;

/**
 * communicate service with remote pixelcontroller instance send and recieve
 * data via osc
 * 
 * @author michu
 * 
 */
public class RemoteOscServer extends OscMessageHandler implements PixConServer, Runnable {

    private static final Logger LOG = Logger.getLogger(RemoteOscServer.class.getName());

    private static final String DEFAULT_TARGET_HOST = "pixelcontroller.local";
    private static final int DEFAULT_REMOTE_OSC_SERVER_PORT = 9876;
    private static final Protocol SERVER_PROTOCOL = Protocol.TCP;
    private static final Protocol CLIENT_PROTOCOL = Protocol.UDP;

    private float steps;

    private Set<ValidCommand> recievedMessages;
    private RemoteOscObservable remoteObserver;
    private CallbackMessageInterface<String> setupFeedback;

    private boolean initialized;
    private String version;
    private ApplicationConfigurationHelper config;
    private MatrixData matrix;
    private List<IColorSet> colorSets;
    private ISound sound;
    private List<OutputMapping> outputMapping;
    private IOutput output;
    private ImageBuffer imageBuffer;
    private PresetSettings presetSettings;
    private FileUtils fileUtilsRemote;
    private PixelControllerStatusMBean jmxStatistics;

    private RmiApi remoteServer;

    public RemoteOscServer(CallbackMessageInterface<String> msgHandler) {
        this.setupFeedback = msgHandler;
        this.remoteServer = RmiFactory.getRmiApi(true,
                PixelControllerOscServer.REPLY_PACKET_BUFFERSIZE);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (initialized) {
                    LOG.log(Level.INFO, "Shutdown: Unregister Observer");
                    try {
                        remoteServer.sendPayload(
                                new Command(ValidCommand.UNREGISTER_VISUALOBSERVER), null);
                    } catch (OscClientException e) {
                        // ignored
                    }
                    LOG.log(Level.INFO, "Shutdown: Stop OSC Server");
                    remoteServer.shutdown();
                }
            }
        });
    }

    @Override
    public void start() {
        this.sound = new SoundDummy();
        this.steps = 1 / 12f;

        Thread startThread = new Thread(this);
        startThread.setName("GUI Poller");
        startThread.setDaemon(true);
        startThread.start();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ApplicationConfigurationHelper getConfig() {
        return config;
    }

    @Override
    public List<IColorSet> getColorSets() {
        return colorSets;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public int[] getOutputBuffer(int nr) {
        return imageBuffer.getOutputBuffer()[nr];
    }

    @Override
    public IOutput getOutput() {
        return output;
    }

    @Override
    public List<OutputMapping> getAllOutputMappings() {
        return outputMapping;
    }

    @Override
    public float getCurrentFps() {
        return jmxStatistics.getCurrentFps();
    }

    @Override
    public long getFrameCount() {
        return jmxStatistics.getFrameCount();
    }

    @Override
    public long getServerStartTime() {
        return jmxStatistics.getStartTime();
    }

    @Override
    public long getRecievedOscPackets() {
        return jmxStatistics.getRecievedOscPakets();
    }

    @Override
    public long getRecievedOscBytes() {
        return jmxStatistics.getRecievedOscBytes();
    }

    @Override
    public ISound getSoundImplementation() {
        return sound;
    }

    @Override
    public MatrixData getMatrixData() {
        return matrix;
    }

    @Override
    public int getNrOfVisuals() {
        return this.config.getNrOfScreens() + 1 + this.config.getNrOfAdditionalVisuals();
    }

    @Override
    public PresetSettings getCurrentPresetSettings() {
        return presetSettings;
    }

    @Override
    public void updateNeededTimeForMatrixEmulator(long t) {
        // ignored, as not relevant
    }

    @Override
    public void updateNeededTimeForInternalWindow(long t) {
        // ignored, as not relevant
    }

    @Override
    public void sendMessage(String[] msg) {
        try {
            remoteServer.sendPayload(new Command(msg), null);
        } catch (OscClientException e) {
            LOG.log(Level.WARNING, "Failed to parse Message!", e);
        }
    }

    @Override
    public void refreshGuiState() {
        // ignored, the gui is refreshed async
    }

    @Override
    public void observeVisualState(Observer o) {
        remoteObserver.addObserver(o);
    }

    @Override
    public int[] getVisualBuffer(int nr) {
        return imageBuffer.getVisualBuffer()[nr];
    }

    @Override
    public FileUtils getFileUtils() {
        return fileUtilsRemote;
    }

    /**
     * Incoming OSC Commands
     */
    @Override
    public void handleOscMessage(OscMessage oscIn) {
        if (StringUtils.isBlank(oscIn.getPattern())) {
            LOG.log(Level.INFO, "Ignore empty OSC message...");
            return;
        }

        String pattern = oscIn.getPattern();
        ValidCommand command;
        try {
            command = ValidCommand.valueOf(pattern);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unknown message: " + pattern, e);
            return;
        }

        recievedMessages.add(command);
        processMessage(command, oscIn.getBlob());
    }

    private void processMessage(ValidCommand command, byte[] data) {
        try {
            switch (command) {
                case GET_VERSION:
                    this.version = remoteServer.reassembleObject(data, String.class);
                    setupFeedback.handleMessage("Found PixelController Version " + this.version);
                    break;

                case GET_CONFIGURATION:
                    config = remoteServer.reassembleObject(data,
                            ApplicationConfigurationHelper.class);
                    setupFeedback.handleMessage("Recieved Configuration");
                    break;

                case GET_MATRIXDATA:
                    matrix = remoteServer.reassembleObject(data, MatrixData.class);
                    setupFeedback.handleMessage("Recieved Matrixdata");
                    break;

                case GET_COLORSETS:
                    colorSets = remoteServer.reassembleObject(data, ArrayList.class);
                    setupFeedback.handleMessage("Recieved Colorsets");
                    break;

                case GET_OUTPUTMAPPING:
                    outputMapping = remoteServer.reassembleObject(data, ArrayList.class);
                    setupFeedback.handleMessage("Recieved Output mapping");
                    break;

                case GET_OUTPUT:
                    output = remoteServer.reassembleObject(data, IOutput.class);
                    break;

                case GET_GUISTATE:
                    remoteObserver.notifyGuiUpdate(remoteServer.reassembleObject(data,
                            ArrayList.class));
                    break;

                case GET_PRESETSETTINGS:
                    presetSettings = remoteServer.reassembleObject(data, PresetSettings.class);
                    break;

                case GET_JMXSTATISTICS:
                    jmxStatistics = remoteServer.reassembleObject(data,
                            PixelControllerStatusMBean.class);
                    break;

                case GET_FILELOCATION:
                    fileUtilsRemote = remoteServer
                            .reassembleObject(data, FileUtilsRemoteImpl.class);
                    break;

                case GET_IMAGEBUFFER:
                    imageBuffer = remoteServer.reassembleObject(data, ImageBuffer.class);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to convert input data!", e);
            setupFeedback.handleMessage("Error converting " + command);
        }
    }

    @Override
    public void run() {
        LOG.log(Level.INFO, "Start PixelController Client thread");
        String targetHost = DEFAULT_TARGET_HOST;
        int serverPort;
        try {
            setupFeedback.handleMessage("Detect PixelController OSC Server");
            try {
                PixMDnsClient client = MDnsClientFactory.queryService(
                        PixMDnsServer.REMOTE_TYPE_UDP, 6000);
                client.start();
                if (client.mdnsServerFound()) {
                    serverPort = client.getPort();
                    setupFeedback.handleMessage("... found on port " + serverPort + ", ip: "
                            + client.getFirstIp());
                    targetHost = client.getFirstIp();
                } else {
                    setupFeedback.handleMessage("... not found, use default port "
                            + DEFAULT_REMOTE_OSC_SERVER_PORT);
                    serverPort = DEFAULT_REMOTE_OSC_SERVER_PORT;
                }
            } catch (MDnsClientException e) {
                LOG.log(Level.WARNING, "Service discover failed.", e);
                serverPort = DEFAULT_REMOTE_OSC_SERVER_PORT;
                setupFeedback.handleMessage("... not found, use default port "
                        + DEFAULT_REMOTE_OSC_SERVER_PORT);
            }
            LOG.log(Level.INFO, "Remote target, IP: " + targetHost + ", port: " + serverPort);

            setupFeedback.handleMessage("Start OSC Server");
            // create a tcp server, expected large messages (> then MTU)
            remoteServer.startServer(SERVER_PROTOCOL, this, serverPort - 1);
            setupFeedback.handleMessage(" ... started");

            setupFeedback.handleMessage("Connect to PixelController OSC Server");
            // use udp to communicate with the pixelcontroller OSC server
            int clientPort = serverPort - 1;
            remoteServer.startClient(CLIENT_PROTOCOL, targetHost, serverPort, clientPort);
            setupFeedback.handleMessage(" ... done");

            this.remoteObserver = new RemoteOscObservable();
            this.initialized = false;
            this.recievedMessages = new HashSet<ValidCommand>();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to start Remote OSC Server!", e);
            return;
        }

        // first step, get static values
        Set<ValidCommand> initCommands = new HashSet<ValidCommand>();
        initCommands.add(ValidCommand.GET_VERSION);
        initCommands.add(ValidCommand.GET_CONFIGURATION);
        initCommands.add(ValidCommand.GET_MATRIXDATA);
        initCommands.add(ValidCommand.GET_COLORSETS);
        initCommands.add(ValidCommand.GET_OUTPUT);
        initCommands.add(ValidCommand.GET_GUISTATE);
        initCommands.add(ValidCommand.GET_OUTPUTMAPPING);
        initCommands.add(ValidCommand.GET_PRESETSETTINGS);
        initCommands.add(ValidCommand.GET_JMXSTATISTICS);
        initCommands.add(ValidCommand.GET_FILELOCATION);
        initCommands.add(ValidCommand.GET_IMAGEBUFFER);

        int waitLoop = 0;
        while (!recievedMessages.containsAll(initCommands)) {
            for (ValidCommand cmd : initCommands) {
                if (!recievedMessages.contains(cmd)) {
                    LOG.log(Level.INFO, "Request " + cmd + " from OSC Server");
                    try {
                        remoteServer.sendPayload(new Command(cmd), null);
                    } catch (OscClientException e) {
                        LOG.log(Level.SEVERE, "failed to send osc message, " + cmd, e);
                    }
                }
            }
            try {
                waitLoop++;
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignored
            }

            if (waitLoop > 4) {
                LOG.log(Level.SEVERE, "Failed to get answer from PixelController Server!");
                setupFeedback.handleMessage("");
                setupFeedback.handleMessage("ERROR: No answer from PixelController received!");
                setupFeedback
                        .handleMessage("Start aborted, make sure PixelController is running and restart client");
                return;
            }
        }
        initialized = true;
        // now register remote observer
        try {
            remoteServer.sendPayload(new Command(ValidCommand.REGISTER_VISUALOBSERVER), null);
        } catch (OscClientException e) {
            LOG.log(Level.SEVERE, "failed to send osc message, "
                    + ValidCommand.REGISTER_VISUALOBSERVER, e);
        }
    }

    @Override
    public float getSetupSteps() {
        return steps;
    }

}
