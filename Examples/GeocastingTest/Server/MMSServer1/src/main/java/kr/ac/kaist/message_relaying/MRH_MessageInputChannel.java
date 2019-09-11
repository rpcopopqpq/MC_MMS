package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MRH_MessageInputChannel.java
	
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.4.0

Rev. history : 2017-03-22
	Added SSL handler and modified MessageRelayingHandler in order to handle HTTPS functionalities.
	Added member variable protocol in order to handle HTTPS.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int sessionId to String SessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2018-04-23
Version : 0.7.1
	Removed NULL_RETURN_STD hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-04-18
Version : 0.8.2
	Catch channelInactive event and terminate Http request.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-09
Version : 0.9.0
	Added session counting functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.1
	If client is disconnected, 
	drop the duplicate id from duplicate hash map.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	From now, MessageParser is initialized in MRH_MessageInputChannel class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-23
Version : 0.9.1
	Fixed a problem where rabbitmq connection was not terminated even when client disconnected by using context-channel attribute.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-29
Version : 0.9.1
	Resolved a bug related to realtime log function.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	HOTFIX: Resolved a bug related to message ordering.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr),
		Yunho Choi (choiking10@kaist.ac.kr)
		
Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-20
Version : 0.9.2
	HOTFIX: polling authentication bug.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-07
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-08
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

 Rev. history : 2019-07-16
 Version : 0.9.4
 	Revised bugs related to MessageOrderingHandler and SeamlessRoamingHandler.
 Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
 
 Rev. history : 2019-07-16
 Version : 0.9.4
 	Added bean release() in channelInactive().
 Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.mms_server.ChannelTerminateListener;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import kr.co.nexsys.HomeMmsHttpSend;
import kr.co.nexsys.HomeMmsService;
import kr.com.nexsys.distributed.HomeManager;

import java.io.IOException;


public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	public static final AttributeKey<LinkedList<ChannelTerminateListener>> TERMINATOR = AttributeKey.newInstance("terminator");
	private static final Logger logger = LoggerFactory.getLogger(MRH_MessageInputChannel.class); 

	private String sessionId = "";

	private MessageParser parser;
	private String protocol = "";
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
    private MessageRelayingHandler relayingHandler;
    
    private ChannelBean bean = null;
	
    private String duplicateId="";
    

	public MRH_MessageInputChannel(String protocol) {
		super();
		this.protocol = protocol;
		
		
	}
	
	/*
	public boolean isRemainJob(ChannelHandlerContext ctx) {
		ConnectionThread thread = relayingHandler.getConnectionThread();
        if (thread != null) {
        	return true;
        }
        LinkedList<ChannelTerminateListener> listeners = ctx.channel().attr(TERMINATOR).get();
        for(ChannelTerminateListener listener: listeners) {
        	return true;
        }
        return false;
	}*/
	
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (acceptInboundMessage(msg)) {
                imsg = (FullHttpRequest) msg;
                channelRead0(ctx, imsg);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
        	// TODO Carefully inspect this code. There is a risk of memory leak.
            if (!isRemainJob(ctx) && release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }*/
    
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		//System.out.println("Message in channelRead0");
		
		try {
			mmsLog = MMSLog.getInstance();
			mmsLogForDebug = MMSLogForDebug.getInstance();

			sessionId = ctx.channel().id().asShortText();
			SessionManager.putSessionInfo(sessionId, "");
			
			logger.debug("\r------------------{}////---------------------",sessionId);
			this.parser = new MessageParser(sessionId);
			bean = new ChannelBean(protocol, ctx, req, sessionId, parser);
			bean.retain();
			//System.out.println("0-"+bean.refCnt());
			//System.out.println("0-"+bean.getReq().refCnt());
			try {
				parser.parseMessage(ctx, req);
				
			} catch (IOException | NumberFormatException | NullPointerException  e) {
				mmsLog.info(logger, sessionId, ErrorCode.MESSAGE_PARSING_ERROR.toString());
				
			} 
			if (!parser.isRealtimeLogReq()) {
				mmsLog.info(logger, sessionId, "Receive a message."); 
			}// If a request is not a realtime logging service request.
			
			String svcMRN = parser.getSvcMRN();
    		String srcMRN = parser.getSrcMRN();
    		duplicateId = srcMRN+svcMRN;

    		ctx.channel().attr(TERMINATOR).set(new LinkedList<ChannelTerminateListener>());
    		
    		/**
    		 * 여기서부터 visited SC 처리하는 source code
    		 * 완성된 후 VisiteManager.java로 이동
    		 */
    		HomeMmsService homeMmsService = new HomeMmsService();
			
			String srcMrn = parser.getSrcMRN();
			/**
			 * setting visited mms info
			 * 
			 * visited SC의 요청(현재 json file로 요청 여부 판단)은 반드시 visited 상태(file 내visitSc정보가 반드시 있어야 됨)이므로 HomeMms 처리 필요 없다
			 */
			String visitSc = null;
			
			visitSc = parser.getVisitSc();
			
			boolean myChildSc = homeMmsService.findClientInfo(visitSc);

			// if destination is thisMMS's child then searching visited info from redis server
			String dstMrn = parser.getDstMRN();
			boolean awayChild = false; //req가 service의 relaying인지 확인
			boolean otherMmsChild=false;
			boolean isService = false;
			
			//source MRN이 visited 목록에 있으면 away상태의 this MMS's child이다. 
			if(homeMmsService.getVisited(srcMrn)) {//visited:다른 MMS에 나가있는 MRN인가?
				awayChild = true;
			}
			//destination MRN이 visited 목록에 있으면 away상태의 this MMS's child이다.
			if(homeMmsService.getVisited(dstMrn)) {
				awayChild = true;
			}
			//else
			if(!myChildSc) {//this MMS's child도 아니면 home manager에서 뒤져야 하나...
				/**
				 * Temporary code : implements it in HomeMmsService
				 */
				HomeManager homeManager = new HomeManager(sessionId);
				List<HashMap<String, String>> scList = homeManager.getScMrnList();
				for (int i = 0; i < scList.size(); i++) {
					Map<String, String> scMap = scList.get(i);

					if (srcMrn.equals(scMap.get("scMrn"))) {
						otherMmsChild = true;
						logger.debug("req dstMRN:[{}] is MMS MRN [{}]'s client...", scMap.get("scMrn"), scMap.get("homeMrn"));
						break;
					}
				}
				if(
						///!awayChild && 
						!otherMmsChild) {//나가있는 thisMMS' SC도 아니고, 다른 MMS의 SC도 아니면, 이것은 service provider이다 라고 간주 20190903현재 기준
					isService = true;
					///parser.setIsRelayingToVisitedSc(true);//visited MMS에 sp의 message를 away SC에 전송하도록 한다.
					///setIsRelayingToVisitedSc 필요없다
					//parser.setIsService(isService);
					//단순히 polling된 sc에 sp가 보내는것도 writing for visited sc로 처리된다.
				}
				if(isService && awayChild) {
					parser.setIsRelayingToVisitedSc(true);
					///parser.setIsWritingForVisitedSc(isService);
					parser.getVisitMmsHostName();
					parser.getVisitMmsPort();
					
					///homeMmsService.findVisitedMmsInfo(dstMrn);
					//TODO refactoring it
					String visitedMmsIp = "";
					String visitedMmsPort = "";
					String visitedMmsMrn=null;
					
					Map<String, String> visitedMmsInfoMap = new HashMap<String, String>();
					visitedMmsInfoMap = homeMmsService.findVisitedMmsInfo(dstMrn);
					
					
					visitedMmsIp = visitedMmsInfoMap.get("ip");
					if(visitedMmsInfoMap.get("port")!=null) {
						visitedMmsPort = visitedMmsInfoMap.get("port");
					}
					visitedMmsMrn = visitedMmsInfoMap.get("visitMms");
					
					parser.setVisitMmsHostName(visitedMmsIp);
					parser.setVisitMmsPort(visitedMmsPort);
				}
			}
			
			String thisMmsMrn = MMSConfiguration.getMmsMrn();
			/**
			 * MMS-x의 SC가 this MMS에 visit일 때
			 * this redis에는 "visitSc"의 MRN과  VSC의 home MMS의 MRN이 저장되어 있어야 한다 (ip, port)
			 * VSC의 home MMS에 이 event(visited SC가 registration)를 알린다. /register-visiting
			 * VSC의 MMS는 해당 SC가 다른 MMS에 visited 됐다는 정보를 redis에 등록한다.
			 *      ex)"VL_urn:mrn:smart-navi:s:krsh003", homeMms:urn:mrn:smart-navi:device:mmsX
			 */
			if(null != visitSc) {//visited의 요청이다
			
				String homeMmsMrn = parser.getHomeMms();///home MMS정보는 header에 없다; SC는 homeMmsMrn정보가 없다
				
				/**
				 * findHomeMmsMrn()
				 */
				
				Map<String, String> homeMmsInfoMap = new HashMap<String, String>();
				
				//TODO home manager가 완성되면 이 line을 적용한다
				homeMmsInfoMap = homeMmsService.findHomeMmsInfo(visitSc, sessionId); ///visitSc의 MRN
				
				if(homeMmsInfoMap.size()>0) {
					String homeMmsIp = "";
					int homeMmsPort = 0;
					
					homeMmsIp = homeMmsInfoMap.get("ip");
					if(homeMmsInfoMap.get("port")!=null) {
						homeMmsPort = Integer.parseInt(homeMmsInfoMap.get("port").toString());
					}
					homeMmsMrn = homeMmsInfoMap.get("homeMrn");
					
					homeMmsService.insertVisited(visitSc, homeMmsMrn, homeMmsIp, homeMmsPort);
					
					logger.debug("visited MmsMrn==={}, home mms MRN {}", thisMmsMrn, homeMmsMrn);
					
					//send to (visited SC) home mms
					/**
					 * visitSc, homeMmsIp, homeMmsPort, parser.getDstMRN(), visitedMms, thisMMSport
					 */
					HomeMmsHttpSend homeMmsHttpSend = new HomeMmsHttpSend(visitSc, homeMmsIp, Integer.toString(homeMmsPort), parser.getDstMRN(), MMSConfiguration.getMmsMrn(), Integer.toString(MMSConfiguration.getHttpPort()), "/register-visiting", "");
					homeMmsHttpSend.sendToVisitedMmsInfo();
					relayingHandler = new MessageRelayingHandler(bean);
				}else {
					logger.error("not found Home MMS info of visited SC");
				}
			}
			/**
			 * visited info가 없으면 this MMS의 child SC이다.
			 * redis에 this MMS의 child SC 정보가 등록되어 있는가?
			 * homeManager DB(maria/mysql) access를 가급적 피하려면 redis에서 먼저 등록여부 확인하고 homeManager.selectMyScInfo한 다음에 redis에 regist 한다
			 */
			else if(!parser.isRegisterVisit() && !parser.isRequestRemoveMrn() && !isService){//단순 long-polling이냐
				if (!parser.isGeocastingMsg()) {
					boolean myScInRedis = false;
					myScInRedis = homeMmsService.findClientInfo(srcMrn);

					boolean myScInHomeManager = false;
					boolean awayVisited = false;

					/**
					 * Temporary code : implements it in HomeMmsService
					 */
					HomeManager homeManager = new HomeManager(sessionId);
					List<HashMap<String, String>> scList = homeManager.getScMrnList();
					for (int i = 0; i < scList.size(); i++) {
						Map<String, String> scMap = scList.get(i);

						if (thisMmsMrn.equals(scMap.get("homeMrn")) && srcMrn.equals(scMap.get("scMrn"))) {
							myScInHomeManager = true;
							logger.debug("req scMRN:[{}], this MMS MRN [{}]", scMap.get("scMrn"), scMap.get("homeMrn"));
							break;
						}
					}

					/// myScInHomeManager = homeMmsService.findMyScInfo(srcMrn); //TODO implements it in HomeMmsService

					awayVisited = homeMmsService.getVisited(srcMrn);

					if (!myScInRedis) {
						//service provider도 안한다
						///homeMmsService.registMyScInfo(srcMrn); /// polling일 때는 regist할 필요 없다
					}
					/**
					 * visited MMS에 등록했던 register-visiting 정보를 삭제하도록 http요청한다
					 */
					//else {
					if (myScInHomeManager && awayVisited) {
							//TODO refactoring it
							String visitedMmsIp = "";
							int visitedMmsPort = 0;
							String visitedMmsMrn=null;
							
							Map<String, String> visitedMmsInfoMap = new HashMap<String, String>();
							visitedMmsInfoMap = homeMmsService.findVisitedMmsInfo(srcMrn);
							
							
							visitedMmsIp = visitedMmsInfoMap.get("ip");
							if(visitedMmsInfoMap.get("port")!=null) {
								visitedMmsPort = Integer.parseInt(visitedMmsInfoMap.get("port").toString());
							}
							visitedMmsMrn = visitedMmsInfoMap.get("visitMms");
							
							/**
							 * String srcMrn, String targetMmsIp, String targetMmsPort, String dstMrn, String visitMmsMrn, String httpPort, String uri
							 */
							HomeMmsHttpSend homeMmsHttpSend =
									new HomeMmsHttpSend(srcMrn, visitedMmsIp,
											Integer.toString(visitedMmsPort),
											parser.getDstMRN(),
											visitedMmsMrn, Integer.toString(MMSConfiguration.getHttpPort()), "/remove-mrn", "");
							homeMmsHttpSend.sendToVisitedMmsInfo();
							//refactoring it:end
							
							long result = homeMmsService.removeAway(srcMrn);
							logger.debug("away SC MRN {}row deleted", result);
						}
					//}//end of else
				}//end of ; 단순 long-polling이냐

				relayingHandler = new MessageRelayingHandler(bean);
				// System.out.println("Successfully processed");
			}
			else if(parser.isRegisterVisit()){
				
				//이미 visited 정보가 있으면 해당 mms에 remove-mrn 요청
				if(homeMmsService.getVisited(srcMrn)) {
					//TODO refactoring it
					String visitedMmsIp = "";
					int visitedMmsPort = 0;
					String visitedMmsMrn=null;
					
					Map<String, String> visitedMmsInfoMap = new HashMap<String, String>();
					visitedMmsInfoMap = homeMmsService.findVisitedMmsInfo(srcMrn);
					
					
					visitedMmsIp = visitedMmsInfoMap.get("ip");
					if(visitedMmsInfoMap.get("port")!=null) {
						visitedMmsPort = Integer.parseInt(visitedMmsInfoMap.get("port").toString());
					}
					visitedMmsMrn = visitedMmsInfoMap.get("visitMms");
					
					/**
					 * String srcMrn, String targetMmsIp, String targetMmsPort, String dstMrn, String visitMmsMrn, String httpPort, String uri
					 */
					HomeMmsHttpSend homeMmsHttpSend =
							new HomeMmsHttpSend(srcMrn, visitedMmsIp,
									Integer.toString(visitedMmsPort),
									parser.getDstMRN(),
									visitedMmsMrn, Integer.toString(MMSConfiguration.getHttpPort()), "/remove-mrn", "");
					homeMmsHttpSend.sendToVisitedMmsInfo();
					//refactoring it:end
				};
				
				String awayScMrn = parser.getSrcMRN();
				String visitMrn = "";
				
				visitMrn = parser.getVisitMmsMrn();
				String visitedMmsIp = parser.getSrcIP();
				
				int visitedMmsPort= Integer.parseInt(parser.getVisitMmsPort());
				
				homeMmsService.insertAway(awayScMrn, visitMrn, visitedMmsIp, visitedMmsPort);
				relayingHandler = new MessageRelayingHandler(bean);
			}
			else if(parser.isRequestRemoveMrn()) {
				long removeResult = homeMmsService.removeVisitedScMrn(srcMrn);
				logger.debug("visited SC MRN {}row deleted", removeResult);
				relayingHandler = new MessageRelayingHandler(bean);
			}else if(isService) {
				logger.debug("source MRN {} is Service Provider, it's relaying to {}", srcMrn, dstMrn);
				relayingHandler = new MessageRelayingHandler(bean);
			}
			/**
    		 * 여기서부터 visited SC 처리하는 source code
    		 * 완성된 후 VisiteManager.java로 이동
    		 */
			///여기에 원본의 relayingHandler = new MessageRelayingHandler(bean);
			///가 있어야 한다.
		}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		finally {
			bean.release();
		}
	}
	
	
	
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    	super.channelInactive(ctx);

        //System.out.println(sessionId +", ChannelInactive");
		//System.out.println(sessionId +", REFERENCE COUNT="+bean.getReq().refCnt());

        if (relayingHandler != null) {
        	ConnectionThread thread = relayingHandler.getConnectionThread();
        	if (thread != null) {
            	if (bean.refCnt() > 0) {
					mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
					bean.release();
				}

                thread.terminate();
            }
        	relayingHandler = null;
        	
        }
        

        
        LinkedList<ChannelTerminateListener> listeners = ctx.channel().attr(TERMINATOR).get();
        for(ChannelTerminateListener listener: listeners) {
        	listener.terminate(ctx);
        }
        
		if (bean != null) {
			while (bean.refCnt() > 0) {
				bean.release();
			}
			bean = null;
		}
		
		if (!ctx.isRemoved()) {
			ctx.close();
		}
    }

	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
	    //System.out.println("incomming message");
		if (ctx.pipeline().get(SslHandler.class) != null){
			// Once session is secured, send a greeting and register the channel to the global channel
	        // list so the channel received the messages from others.
	        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
		        new GenericFutureListener<Future<Channel>>() {
		            @Override
		            public void operationComplete(Future<Channel> future) throws Exception {
		                ctx.writeAndFlush(
		                        "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
		                ctx.writeAndFlush(
		                        "Your session is protected by " +
		                                ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
		                                " cipher suite.\n");
		
		                channels.add(ctx.channel());
		            }
		        });
		}
	}
	// TODO: Youngjin Kim must inspect this following code.
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    	String clientType = SessionManager.getSessionType(sessionId);
    	if (clientType != null) {
    		SessionManager.removeSessionInfo(sessionId);

    		if (clientType.equals("p")) {
    			mmsLog.info(logger, this.sessionId, ErrorCode.POLLING_CLIENT_DISCONNECTED.toString());
    		} 
    		else if (clientType.equals("lp")) {
    			mmsLog.info(logger, this.sessionId, ErrorCode.LONG_POLLING_CLIENT_DISCONNECTED.toString());
    		}
    		else {
    			mmsLog.info(logger, this.sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
    		}
    	}
    	if (!ctx.isRemoved()){
    		ctx.close();
    	}
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

//    	ctx.channel().
    	String clientType = SessionManager.getSessionType(sessionId);
//    	ctx.pipeline().get(HttpHeaderValues.class);
//    	channels.
    	
    	if (cause instanceof IOException && parser != null){
    		int srcPort = 0;
        	String srcIP = null;
        	String[] reqInfo;
        	final int minDynamicPort = 49152;
     
        	if (parser.getSrcIP() == null) {
            	InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        	    InetAddress inetaddress = socketAddress.getAddress();
        	    MNSInteractionHandler handler = new MNSInteractionHandler(sessionId);
        	    if (inetaddress != null) {
        	    	srcIP = inetaddress.getHostAddress(); // IP address of client
        	    }
        	    srcPort = socketAddress.getPort(); // port number of client
        	    String request = null;
        	    if(srcPort >= minDynamicPort) {
        	    	request = srcIP + ":0";
        	    } 
              else {
        	    	request = srcIP + ":" + srcPort;
        	    }
        	    
        	    String srcMRN = handler.requestIPtoMRN(request);

        	    reqInfo = new String[2];
        	    reqInfo[0] = srcIP;
        	    reqInfo[1] = srcMRN;

        	} 
          else {
        		reqInfo = new String[5];
        		reqInfo[0] = parser.getSrcIP();
        		reqInfo[1] = parser.getSrcMRN();
        		reqInfo[2] = parser.getDstIP();
        		reqInfo[3] = parser.getDstMRN();
        		reqInfo[4] = parser.getSvcMRN();
        	
        	}
    		
    	    printError(srcIP, reqInfo, clientType);
    	}
    	if (clientType != null) {
    		SessionManager.removeSessionInfo(sessionId);    		
      }
    	
    	Object obj  = relayingHandler.getConnectionThread();
    	
    	System.out.println("///relayingHandler.getConnectionThread ==="+obj);
    	
    	LinkedList<ChannelTerminateListener> listeners = ctx.channel().attr(TERMINATOR).get();
    	int ls = listeners.size();
        for(int i=0; i< ls; i++) {
        	ChannelTerminateListener listener = listeners.get(i);

        	System.out.println("///==="+listener.getClass());
        	System.out.println("///==="+listener.toString());
        	listener.terminate(ctx);
        }
        
    	if (!ctx.isRemoved()){
    		  ctx.close();
      }

    }
    
    private void printError(String channelID, String[] reqInfo, String clientType){
        // reqInfo is ordering to srcIP, srcMRN, dstIP, dstMRN, svcMRN

  //    	System.out.println("\n/*****************************************/");
  //		System.out.println("The connection is disconnected by the client");
  //    	System.out.println("Error Channel ID: " + channelID);
        String errorlog = null;

        if (clientType != null){
          if(clientType.equals("p")){
  //	    System.out.println("Client type: Polling Client");
            errorlog = new String("Client Type=Polling");
          } 
           else if(clientType.equals("lp")){
  //		System.out.println("Client type: Long Polling Client");
            errorlog = new String("Client Type=Long Polling");
          } 
           else {
  //	    System.out.println("Client type: Normal Client");
            errorlog = new String("Client Type=Normal");
          }
        }
        else {
  //      System.out.println("Client type is unknown");
          errorlog = new String("Client Type=Unknown");
        }

  //	System.out.println("srcIP: " + reqInfo[0]);
  //	System.out.println("srcMRN=" +  reqInfo[1]);
        errorlog += " srcIP=" + reqInfo[0] + " srcMRN=" + reqInfo[1];
      if (reqInfo.length == 5){
  //	System.out.println("dstIP=" +  reqInfo[2]);
  //	System.out.println("dstMRN=" +  reqInfo[3]);
  //	System.out.println("svcMRN=" + reqInfo[4]);
        errorlog += " dstIP=" + reqInfo[2] + " dstMRN=" + reqInfo[3] + " svcMRN=" + reqInfo[4];
      }
  //  System.out.println("/*****************************************/");
	
      mmsLog.info(logger, this.sessionId, ErrorCode.CLIENT_DISCONNECTED.toString() + " " + errorlog + ".");
     
    }
    
    public class ChannelBean {
    	private FullHttpRequest req = null;
    	private ChannelHandlerContext ctx = null;
    	private String sessionId = null;
    	private String protocol = null;
    	private MessageParser parser = null;
    	private MRH_MessageOutputChannel moc = null;
    	private MessageTypeDecider.msgType type = null;
    	private int refCnt = 0;
    	
    	
    	ChannelBean (String protocol, ChannelHandlerContext ctx, FullHttpRequest req, String sessionId, MessageParser parser){
    		this.protocol = protocol;
    		this.ctx = ctx;
    		this.req = req;
    		this.sessionId = sessionId;
    		this.parser = parser;
    		this.refCnt = this.req.refCnt();
    	}
    	
    	public void setOutputChannel(MRH_MessageOutputChannel moc) {
    		this.moc = moc;
    	}
    	
    	public void setType(MessageTypeDecider.msgType type) {
    		this.type = type;
    	}
    	
    	public FullHttpRequest getReq() {
    		return req;
    	}
    	
    	public ChannelHandlerContext getCtx() {
    		return ctx;
    	}
    	
    	public MRH_MessageOutputChannel getOutputChannel() {
    		return moc;
    	}
    	
    	public String getProtocol() {
    		return protocol;
    	}
    	
    	public String getSessionId() {
    		return sessionId;
    	}
    	
    	public MessageParser getParser() {
    		return parser;
    	}
    	
    	public MessageTypeDecider.msgType getType(){
    		return type;
    	}
    	
 
    	public synchronized void release() {
    		//System.out.print(sessionId+", ");
    		if (req != null && req.refCnt() > 0 && refCnt > 0) {
	    		req.release();
	    		refCnt--;
				//System.out.println("REFERENCE COUNT="+req.refCnt());
    		}
    		if (req != null && req.refCnt() == 0 && refCnt == 1) {
    	    	refCnt = 0;
    	    	//System.out.println("REFERENCE COUNT=0");
    		}
    	}
    	
    	public synchronized void retain() {
    		if (req != null) {
	    		req.retain();
	    		refCnt++;
    		}
    	}
    	
    	public synchronized int refCnt() {
    		return refCnt;
    	}
    }
}
