package com.cometproject.server.network;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.CometSettings;
import com.cometproject.server.network.http.ManagementServer;
import com.cometproject.server.network.messages.MessageHandler;
import com.cometproject.server.network.monitor.MonitorClient;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.network.sessions.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class NetworkEngine {
    public static final AttributeKey<Session> SESSION_ATTRIBUTE_KEY = AttributeKey.valueOf("Session.attr");

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private SessionManager sessions;
    private MessageHandler messageHandler;
    private ManagementServer managementServer;
    private MonitorClient monitorClient;

    public String ip;
    public int port;

    private Channel listenChannel;

    private static Logger log = Logger.getLogger(NetworkEngine.class.getName());

    public NetworkEngine(String ip, int port) {
        this.sessions = new SessionManager();
        this.messageHandler = new MessageHandler();

        if (CometSettings.httpEnabled)
            this.managementServer = new ManagementServer();


        //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NetworkChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 1000)
                .option(ChannelOption.TCP_NODELAY, true);

        this.ip = ip;
        this.port = port;

        try {
            this.listenChannel = bootstrap.bind(new InetSocketAddress(ip, port)).channel();
        } catch (Exception e) {
            Comet.exit("Failed to initialize sockets on address: " + ip + ":" + port);
            return;
        }

        log.info("CometServer listening on port: " + port);
    }

    public SessionManager getSessions() {
        return this.sessions;
    }

    public ManagementServer getManagement() {
        return this.managementServer;
    }

    public MessageHandler getMessages() {
        return this.messageHandler;
    }
}
