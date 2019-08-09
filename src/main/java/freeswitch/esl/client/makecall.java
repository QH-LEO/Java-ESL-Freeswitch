package org.freeswitch.esl.client;

//import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.IEslEventListener;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.internal.Context;
import org.freeswitch.esl.client.internal.IModEslApi;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Java esl调用FreeSWITCH发起呼叫等
 *
 * @author YY
 *
 */
public class makecall {
    private static final Logger log = LoggerFactory.getLogger(makecall.class);
    private static String host = "192.168.1.2";
    private static int port = 5060;
    private static String password = "fs";

    public static Client inBand() throws InboundConnectionFailure {

        final Client inboudClient = new Client();
        inboudClient.connect(new InetSocketAddress("localhost", 8021), "ClueCon", 10);


        // 注册事件处理程序
        inboudClient.addEventListener(new IEslEventListener() {
            @Override
            public void onEslEvent(Context ctx, EslEvent event) {

            }

            public void eventReceived(EslEvent event) {
                // System.out.println("Event received [{}]" + event.getEventHeaders());
                if (event.getEventName().equals("CHANNEL_ANSWER")) {
                    System.err.println("CHANNEL_ANSWER"); // 呼叫应答事件
                }
//                if (event.getEventName().equals("CHANNEL_BRIDGE")) {
//                    System.err.println("CHANNEL_BRIDGE"); // 一个呼叫两个端点之间的桥接事件
//                }
//
//                if (event.getEventName().equals("CHANNEL_DESTROY")) {
//                    System.err.println("CHANNEL_DESTROY"); // 销毁事件
//                }
//
//                if (event.getEventName().equals("CHANNEL_HANGUP_COMPLETE")) {
//                    System.err.println("CHANNEL_HANGUP_COMPLETE"); // 挂机完成事件
//                }
            }

            public void backgroundJobResultReceived(EslEvent event) {
                // String uuid = event.getEventHeaders().get("Job-UUID");
                log.info("Background job result received+:" + event.getEventName() + "/" + event.getEventHeaders());// +"/"+JoinString(event.getEventHeaders())+"/"+JoinString(event.getEventBodyLines()));
            }
        });
        inboudClient.setEventSubscriptions(IModEslApi.EventFormat.PLAIN, "all");
        return inboudClient;
    }

    public  void call (String FILENAME) {
        Client client = null;
        try {
            client = inBand();
        } catch (InboundConnectionFailure inboundConnectionFailure) {
            inboundConnectionFailure.printStackTrace();
        }
        if (client != null) {
            // 呼叫1002-播放语音
            client.sendApiCommand("originate", "{ignore_early_media=true}user/1001 &playback("+FILENAME+".wav)");
            // 呼叫手机-执行lua脚本
            // client.sendSyncApiCommand("originate", "{ignore_early_media=true}sofia/gateway/fs_sg/18621730742 &lua(welcome.lua)");
            // 建立1002和1000的通话
            // client.sendSyncApiCommand("originate", "user/1002 &bridge(user/1000)");
            client.close();
        }
    }
}