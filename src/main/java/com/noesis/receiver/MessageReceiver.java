//package com.noesis.receiver;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TimeZone;
//import java.util.concurrent.CountDownLatch;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.noesis.domain.constants.Constants;
//import com.noesis.domain.constants.ErrorCodesEnum;
//import com.noesis.domain.persistence.NgGlobalBlackList;
//import com.noesis.domain.persistence.NgKannelInfo;
//import com.noesis.domain.persistence.NgMisMessage;
//import com.noesis.domain.persistence.NgPostCutoffMessage;
//import com.noesis.domain.persistence.NgUser;
//import com.noesis.domain.persistence.NgUserPremiumList;
//import com.noesis.domain.persistence.NgUserSenderIdMap;
//import com.noesis.domain.platform.MessageObject;
//import com.noesis.domain.repository.GlobalBlackListRepository;
//import com.noesis.domain.repository.KannelInfoRepository;
//import com.noesis.domain.repository.MisRepository;
//import com.noesis.domain.repository.UserBlackListRepository;
//import com.noesis.domain.repository.UserPremiumListRepository;
//import com.noesis.domain.service.AbusingWordsListService;
//import com.noesis.domain.service.ErrorCodesService;
//import com.noesis.domain.service.GlobalBlackListService;
//import com.noesis.domain.service.GlobalDndListService;
//import com.noesis.domain.service.GlobalFailedNumberListService;
//import com.noesis.domain.service.GlobalPremiumListService;
//import com.noesis.domain.service.KannelInfoService;
//import com.noesis.domain.service.MisService;
//import com.noesis.domain.service.PostCutoffMessageService;
//import com.noesis.domain.service.StaticDataService;
//import com.noesis.domain.service.UserBlackListService;
//import com.noesis.domain.service.UserCreditMapService;
//import com.noesis.domain.service.UserPremiumListService;
//import com.noesis.domain.service.UserSenderIdMapService;
//import com.noesis.domain.service.UserService;
//import com.noesis.producer.MessageSender;
//
//
//public class MessageReceiver{
//
//	private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class) ;
//
//	private int maxPollRecordSize;
//	
//	private CountDownLatch latch = new CountDownLatch(maxPollRecordSize);
//
//	@Autowired
//	private StaticDataService staticDataService;
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	@Value("${app.name}")
//	private String appName;
//	
//	@Value("${kafka.topic.name.mis.object}")
//	private String kafKaDbObjectQueue;
//	
//	
//	@Value("${kafka.consumer.sleep.interval.ms}")
//	private String consumerSleepInterval;
//
//	@Autowired
//    private UserService userService;
//    
//    @Autowired
//    private UserCreditMapService userCreditMapService;
//
//    @Autowired
//	private GlobalDndListService globalDndListService;						
//    
//    @Autowired
//    private GlobalFailedNumberListService globalFailedNumberListService;	
//    
//    @Autowired
//    private GlobalBlackListService globalBlackListService;	
//    
//    // global list start
//    @Autowired
//    private GlobalBlackListRepository globalBlackListRepository;
//    @Autowired
//    private UserPremiumListRepository userpremiumrepo;
//    // global list end
//    
//    @Autowired
//    private UserBlackListService userBlackListService;						
//
//    @Autowired
//    private UserPremiumListService userPremiumListService;					
//    
//    @Autowired
// 	GlobalPremiumListService globalPremiumListService;				
//    
//    @Autowired
//    private AbusingWordsListService abusingWordsListService;				
//    
//    @Autowired
//    private ErrorCodesService errorCodesService;							
//    
//    @Autowired
//    private UserSenderIdMapService userSenderIdMapService;					
//    
//    @Autowired
//    private PostCutoffMessageService postCutoffMessageService;				
//
//    @Autowired
//    private MisService misService;											
//    
//    @Autowired
//    private MessageSender messageSender;									
//
//	@Autowired
//	private KannelInfoService kannelInfoService;
//	
//	@Autowired
//	private KannelInfoRepository kannelInfoRepository;
//	
//	@Autowired
//	private MisRepository misRepository;
//	// rca
//		@Autowired
//		private UserBlackListRepository userBlackListRepository;
//		// rca
//	
//		 long starttime = System.currentTimeMillis();                     
//	
//	public MessageReceiver(int maxPollRecordSize) {
//		this.maxPollRecordSize = maxPollRecordSize;
//	}
//	
//	@KafkaListener(id = "${app.name}", topics = "${kafka.topic.name}", groupId = "${kafka.topic.name}", idIsGroup = false)
//	public void receive1(List<String> messageList,
//	      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
//	      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
//		
//        
//	    logger.error("start of batch receive of size: "+messageList.size());
//	    logger.info("our campiagn start");
//	   
////	   // load all global black list number from data base
//	    List<NgGlobalBlackList> allMobileNumber = globalBlackListRepository.findAll();
//	    Set<String> phoneNumber = allMobileNumber.stream().map(NgGlobalBlackList::getPhoneNumber).collect(Collectors.toSet());
////	  // end load number
//
//	    
//		for (int i = 0; i < messageList.size(); i++) {
//			processMessage(messageList, partitions, offsets, i, latch , phoneNumber);
//		}
//		 long endtime = System.currentTimeMillis();
//		 logger.info("total time taken to complete campiagn=>>>>>" + (endtime - starttime));
//	    logger.error("End of received batch.++");
//	    try {
//	    	logger.error("Consumer Thread Going To Sleep for "+consumerSleepInterval + "ms.");
//	    	Thread.sleep(Integer.parseInt(consumerSleepInterval));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	  }
//	
//
//	private void processMessage(List<String> messageList, List<Integer> partitions, List<Long> offsets, int i, CountDownLatch latch, Set<String> phoneNumber) {
//		Boolean continueMessageProcessing = true;
//		Boolean premiumNumber = false;
//		Boolean isMisRequired = false;
//		logger.error("received message='{}' with partition-offset='{}'", messageList.get(i),
//				partitions.get(i) + "-" + offsets.get(i));
//		MessageObject messageObject = convertReceivedJsonMessageIntoMessageObject(messageList.get(i));
//		try{
//				if (messageObject != null) {
//					String userName = messageObject.getUsername();
//					NgUser user = userService.getUserByName(messageObject.getUsername());
//					messageObject.setUserId(user.getId());
//
//					//continueMessageProcessing = checkCreditForPrepaidAccount(messageObject, userName, user, continueMessageProcessing, isMisRequired);
//					
//					//Commented when matching with Production Class
//					//Start
////					if(continueMessageProcessing){         
////						if (continueMessageProcessing && (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0 || messageObject.getTemplateId().equalsIgnoreCase("others"))){
////							continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing, isMisRequired, user);
////							if(messageObject.getTemplateId() != null)
////								messageObject.setTemplateId(messageObject.getTemplateId().trim());
////							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
////								messageObject.setEntityId(messageObject.getEntityId().trim());
////							}
////						}else{
////							continueMessageProcessing = true;
////							if(messageObject.getTemplateId() != null)
////								messageObject.setTemplateId(messageObject.getTemplateId().trim());
////							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
////								messageObject.setEntityId(messageObject.getEntityId().trim());
////							}
////						}
////					}
//					//End
//					
//					//Add-On new validation for Mesage Template
//					if (continueMessageProcessing && (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0 || messageObject.getTemplateId().equalsIgnoreCase("others"))) 
//						continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing, isMisRequired, user);
//					
//					if(continueMessageProcessing)
//						continueMessageProcessing = checkSenderId(messageObject, userName, user, continueMessageProcessing, isMisRequired);
//					
//					// Check if number contains +,0 or 91 or valid mobile number
//					if(continueMessageProcessing)
//						continueMessageProcessing = checkDestinationNumber(messageObject,continueMessageProcessing, isMisRequired, user);
//					
//					// Check If Destination Number is in Dnd.
////					if(continueMessageProcessing && (messageObject.getMessageServiceType().equals("promo") || user.getIsDndCheck() == 'Y'))
////						continueMessageProcessing = checkNumberInGlobalDndList(messageObject,continueMessageProcessing, isMisRequired, user);
//					
//					// start
//					if (continueMessageProcessing
//							&& (messageObject.getMessageServiceType().equalsIgnoreCase("trans")
//									|| messageObject.getMessageServiceType().equalsIgnoreCase("others")
//									|| messageObject.getMessageServiceType().equalsIgnoreCase("promo")
//									|| messageObject.getMessageServiceType().equalsIgnoreCase("service"))
//							&& (user.getIsDndCheck() == 'Y')) {
//						continueMessageProcessing = checkNumberInGlobalDndList(messageObject, continueMessageProcessing,
//								isMisRequired, user);
//					}
//
//					// end
//					
//					
//					
//					// Check If Destination Number is in failed database and user opted for this check.
//					if(continueMessageProcessing && user.getRejectFailedNumber()=='Y')
//						continueMessageProcessing = checkNumberInFailedNumbersList(messageObject,continueMessageProcessing, isMisRequired, user);
//					/*
//					//Check if number is in global black list.
//					if(continueMessageProcessing)
//						continueMessageProcessing = checkNumberInGlobalBlackList(messageObject, continueMessageProcessing, isMisRequired);
//					*/
//					
//					// Check if number is in global black list.
//					if(continueMessageProcessing) {
//						if(checkNumberInUserPremiumList(messageObject, user, continueMessageProcessing, isMisRequired)){
//							continueMessageProcessing = true;
//							premiumNumber= true;
//							System.out.println("premimunNumber******* is true" + premiumNumber);
//						}else if (!checkNumberInGlobalBlackList(messageObject, continueMessageProcessing, isMisRequired, phoneNumber)) {
//							continueMessageProcessing = false;
//						}
//					}
//					
//					// Check if number is in user black list.
//					if(continueMessageProcessing && user.getIsBlackListAllowed()=='Y')
//						continueMessageProcessing = checkNumberInUserBlackList(messageObject, user, continueMessageProcessing, isMisRequired);
//					
//					// Check if sender id is in global sender id black list.
//					if(continueMessageProcessing)
//						continueMessageProcessing = checkSenderIdInGlobalSenderIdBlackList(messageObject, continueMessageProcessing, isMisRequired);
//					
//					// Check if sender is in user sender id black list.
//					if(continueMessageProcessing)
//						continueMessageProcessing = checkSenderIdInUserSenderIdBlackList(messageObject, user, continueMessageProcessing, isMisRequired);
//					
//					// Check if number is in user premium list.
//					//if(continueMessageProcessing)
//						//continueMessageProcessing = checkNumberInUserPremiumList(messageObject, user, continueMessageProcessing, isMisRequired);
//					
//					// Check if number is in global premium list.
//					if(continueMessageProcessing)
//						 checkNumberInGlobalPremiumList(messageObject, user, continueMessageProcessing, isMisRequired);
//					
//					
//					// Content Filtering.
//					if(continueMessageProcessing)
//						continueMessageProcessing = filterRestrictedContent(messageObject, user, continueMessageProcessing, isMisRequired);
//					
//					// Sender Id Validation and Replace Sender Id.
//					if(continueMessageProcessing  && !(messageObject.getMessageServiceType().equals("promo"))) {
//						continueMessageProcessing = validateAndSetSenderId(messageObject, user, continueMessageProcessing, isMisRequired);
//					}else if(continueMessageProcessing  && messageObject.getMessageServiceType().equals("promo")) {
//						continueMessageProcessing = validateAndSetSenderIdForTransPromo(messageObject, user, continueMessageProcessing, isMisRequired);
//					}else if(continueMessageProcessing){
//						continueMessageProcessing = applyDefaultSenderId(messageObject, userName, continueMessageProcessing, isMisRequired, user);
//					}
//						
//					
//					// 9-9 Timing Check.
//					if(continueMessageProcessing && messageObject.getMessageServiceType().equals("promo")) {
//						continueMessageProcessing = checkNineToNineMessage(messageList, i, messageObject, userName, continueMessageProcessing, isMisRequired);
//						if(!continueMessageProcessing) {
//							isMisRequired = false;
//						}
//					}
//					
//					
//					System.out.println("going to set ROUTING ID************************************************************");
//					
//					// Populate routing id.
//					if(continueMessageProcessing) {
//						if(premiumNumber) {
////							Integer routingId = null;
////							String defaultRoutingInfoKey = "-1#-1#-1#-1#-1";
////							logger.info("{} Premium Number Found and hence going to find premium routing id with key {} : ",messageObject.getMessageId(),defaultRoutingInfoKey);	
////							if(messageObject.getMessageServiceType().equals("trans")) {
////								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("promo")) {
////								routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
////								routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
////								routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("others")) {
////								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////							}
////							messageObject.setRouteId(routingId);
////							messageObject.setGroupId(routingId);
//							
//							//Commented when match with Production class (Aman)
//							// start
//							selfRouting(messageObject, user);
//							
//													
//							//Start
//							
////							Integer circleId = 999;
////							Integer carrierId = 999;
////							String numSeries = null;
////							if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
////								numSeries = messageObject.getDestNumber().substring(2, 7);
////							}else {
////								numSeries = messageObject.getDestNumber().substring(0, 5);
////							}
////							String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
////							String carrierCircleData[] = carrierCircleInfo.split("#");
////							carrierId = Integer.parseInt(carrierCircleData[0]);
////							circleId = Integer.parseInt(carrierCircleData[1]);
////							
////							messageObject.setCarrierId(carrierId);
////							messageObject.setCircleId(circleId);
//							
//							//End
//						} else if (messageObject.isDndAllowed()) {
//							System.out.println("inside else if block for dnd********");
//							Integer routingId = null;
//							String defaultRoutingInfoKey = "-2#-2#-2#-2#-2";
//							logger.info("{} DND Number Found and hence going to find routing id with key {} : ",
//									messageObject.getMessageId(), defaultRoutingInfoKey);
//							if (messageObject.isDndAllowed()) {
//								logger.info("DND Number found in DND List :" + messageObject.isDndAllowed());
//								routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
//							}
//							messageObject.setRouteId(routingId);
//							messageObject.setGroupId(routingId);
//						} else if (messageObject.isPremiumNumber()) {
//							System.out.println("inside else if block 2 for isPremium%%%%%*******#######");
////							NgUserPremiumList findBypremiumNumber = userpremiumrepo.findBypremiumNumber(messageObject.getDestNumber());
////							messageObject.setRouteId(findBypremiumNumber.getGroupId());
////							messageObject.setGroupId(findBypremiumNumber.getGroupId());
//							Integer routingId = null;
//							String defaultRoutingInfoKey = "-1#-1#-1#-1#-1";
//							logger.info("{} Premium Number Found and hence going to find premium routing id with key {} : ",messageObject.getMessageId(),defaultRoutingInfoKey);	
//							if(messageObject.getMessageServiceType().equals("trans")) {
//								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
//							}else if(messageObject.getMessageServiceType().equals("promo")) {
//								routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
//							}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
//								routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
//							}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
//								routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
//							}else if(messageObject.getMessageServiceType().equals("others")) {
//								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
//							}
//							messageObject.setRouteId(routingId);
//							messageObject.setGroupId(routingId);
//							
//						}
//						else {
//							System.out.println("inside else block  *****************************");
//							populateRoutingId(messageObject, user);
//						}
//						
//					}
//	// ************************************************************Number Routing**************************************************************
////					if (continueMessageProcessing && premiumNumber) {
////						//NgUserPremiumList findByNgUser = userpremiumrepo.findByNgUser(user);
////						NgUserPremiumList findBypremiumNumber = userpremiumrepo.findBypremiumNumber(messageObject.getDestNumber());
////						messageObject.setRouteId(findBypremiumNumber.getGroupId());
////						messageObject.setGroupId(findBypremiumNumber.getGroupId());
////						System.out.println(  "**********USER  NAME   FOR THAT NUMBER **********"   +findBypremiumNumber.getNgUser().getUserName());
////						
////						
////						
////					}
//					
//	//************************************************************NumberRouting End********************************************************				
//					
//					// Send message to submitter.
//					if(continueMessageProcessing) {
//						String messageObjectAsString = convertMessageObjectToJsonString(messageObject);
//						String submitterQueueName = staticDataService.getSubmitterQueueForGroupId(Integer.toString(messageObject.getRouteId()));
//						messageSender.send(submitterQueueName, messageObjectAsString);
//					}
//						
//					
//					// Dump Message into DB for MIS.
//					if(isMisRequired || !continueMessageProcessing) {
//						//saveMessageInMis(messageObject);
//						
////						String ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
////						if (ngMisMessageObject != null) {
////							messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
////						} else {
////							logger.error("Error while converting message object into DB Object with message Id {} ",
////									messageObject.getMessageId());
////						}
//						 
//					
//						// TODO: Send dummy DLR for platform rejected messages.
//						//Changes for Rejected DLR (In multipart case - Aman)
//						
//						String concatinatedMessageIds = messageObject.getRequestId();
//						String[] messageIdsArray = concatinatedMessageIds.split("#");
//						
//						for (int j = 0; j < messageIdsArray.length; j++) {
//							messageObject.setMessageId(messageIdsArray[j]);
//							String status = sendRejectedDlr(messageObject);
//							logger.info("{} DLR for rejected message sent with status : {}",messageObject.getMessageId(), status);
//						}
//						
//						
//					}
//					
//					
//				}	
//			}catch (Exception e){
//				logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",messageObject.getMessageId() + messageList.get(i));
//				e.printStackTrace();
//				messageObject.setErrorCode("UNKNOWN_ERROR_OCCURED");
//				messageObject.setErrorDesc("Unknown error occured while processing message request");
//				String ngMisMessageObject;
//				try {
//					ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
//					messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
//				} catch (UnsupportedEncodingException | DecoderException e1) {
//					logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",messageObject.getMessageId() + messageList.get(i));
//					e1.printStackTrace();
//				}
//				
//			}
//		latch.countDown();
//	}
//
//	private Boolean checkSenderId(MessageObject messageObject, String userName, NgUser user,
//			Boolean continueMessageProcessing, Boolean isMisRequired) {
//		logger.info("{} Message service type {} and number {} and shortcode {}",messageObject.getMessageId(), messageObject.getMessageServiceType(), messageObject.getDestNumber(), user.getIsShortCodeAllowed());	
//		
//		
//		
////		if(messageObject.getSenderId() != null && (messageObject.getSenderId().length() < 3 || messageObject.getSenderId().length() > 14)) {
////				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////				continueMessageProcessing = false;
////				isMisRequired = true;
////				return false;
////		}else 
//		
//		//Commented
//		{
//			if((messageObject.getMessageServiceType().equals("trans") ||
//					messageObject.getMessageServiceType().equals("service") || messageObject.getMessageServiceType().equals("others"))) {
//				Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());
//		   	    if(matcher.matches() && (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
//		   	    	return true;
//		   	    }else {
//		   	    	messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//					continueMessageProcessing = false;
//					isMisRequired = true;
//					return false;
//		   	    }
//			}else if(messageObject.getMessageServiceType().equals("promo")){
//				Matcher m1 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
//				if(m1.matches()) {
//					return true;
//		   	    }else {
//		   	    	messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//					continueMessageProcessing = false;
//					isMisRequired = true;
//					return false;
//		   	    }
//			}else {
//				logger.info("{} Message service type received others.",messageObject.getMessageId());
//				Matcher m3 = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());
//				
//				if (m3.matches() && (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
//					messageObject.setMessageServiceType("trans");
//					return true;
//				}
//				m3 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
//				if (m3.matches()) {
//					messageObject.setMessageServiceType("promo");
//					return true;
//				}
//				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//				continueMessageProcessing = false;
//				isMisRequired = true;
//				return false;
//			}
//		}
//		//Commented End
//		
////		String senderId = messageObject.getSenderId();
////		boolean isValidSenderId = false;
////		
////		if (messageObject.getMessageServiceType().equals("trans") || messageObject.getMessageServiceType().equals("service") || messageObject.getMessageServiceType().equals("others")) {
////			isValidSenderId = validateAlphabeticSenderId(senderId);
////		}else if (messageObject.getMessageServiceType().equals("promo")) {
////			isValidSenderId = validateNumericSenderId(senderId);
////		}
////		
//////		else if (messageObject.getMessageServiceType().equals("others")) {
//////			isValidSenderId = validateAlphanumericSenderId(senderId);
//////		}
////		
////		else {
////			isValidSenderId = validateMixedSenderId(senderId);
////		}
////		
////		
////		if (!isValidSenderId) {
////			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////			continueMessageProcessing = false;
////			isMisRequired = true;
////		}
////		
////		return isValidSenderId;
//		
//	}
//	
//	//Sender Id validation Methods
////	private boolean validateAlphabeticSenderId(String senderId) {
////		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
////		logger.info("The sender id is 1 :" + matcher.toString());
////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
////	}
////	
////	private boolean validateNumericSenderId(String senderId) {
////		Matcher matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
////		logger.info("The sender id is 2 :" + matcher.toString());
////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
////	}
////	
//////	private boolean validateAlphanumericSenderId(String senderId) {
//////		Matcher matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
//////		logger.info("The sender id is 3 :" + matcher.toString());
//////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//////	}
////	
////	private boolean validateMixedSenderId(String senderId) {
////		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
////		logger.info("The sender id is 4 :" + matcher.toString());
////		if (matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14)) {
////			return true;
////		}
////		
////		matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
////		logger.info("The sender id is 5 :" + matcher.toString());
////		return matcher.matches() && true;
//////		if (matcher.matches()) {
//////			return true;
//////		}
////		
//////		matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
//////		logger.info("The sender id is 6 :" + matcher.toString());
//////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
////		
////	}
////	//End
//	
//	
//	
//	
//
//	private Boolean checkDestinationNumber(MessageObject messageObject, Boolean continueMessageProcessing,
//			Boolean isMisRequired, NgUser user) {
//		String strDestAddr = messageObject.getDestNumber();
//		if (strDestAddr == null || strDestAddr.trim().length() < 10 || strDestAddr.trim().length() > 12 
//				|| (strDestAddr.length() == 12 && !strDestAddr.startsWith("91"))
//				|| (strDestAddr.length() == 11 && !strDestAddr.startsWith("0"))
//				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("1"))
//				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("2"))
//				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("3"))
//				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("4"))
//				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("5"))) {
//			{
//				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				messageObject.setErrorCode(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorCode());
//				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorDesc());
//				continueMessageProcessing = false;
//				isMisRequired = true;
//				return false;
//			}
//		} else {
//			strDestAddr = strDestAddr.replaceAll("\\+", "");
//			if(strDestAddr.startsWith("0")) {
//				strDestAddr = strDestAddr.replaceFirst("0","");
//			}
//			if (strDestAddr.length() == 10) {
//				strDestAddr = "91"+strDestAddr;
//				messageObject.setDestNumber(strDestAddr);
//			} 
//		}
//		return true;
//	}
//
//	private boolean validateAndSetSenderId(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
//			Boolean isMisRequired) {
//		Integer idOfSenderId = 0;
//		if(messageObject.getSenderId() != null) {
//			messageObject.setOriginalSenderId(messageObject.getSenderId());
//			if(user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
//				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//				if(idOfSenderId == null || idOfSenderId == 0) {
//					idOfSenderId = 9999;
//					messageObject.setIdOfSenderId(idOfSenderId);
//				}else {
//					messageObject.setIdOfSenderId(idOfSenderId);
//				} 
//				if(messageObject.getEntityId() == null)
//					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
//			}//else if(user.getIsDynamicSenderId() == 'N' && user.getIsShortCodeAllowed() =='N' ){  		//Due to mismatch with Production Class(Aman)
//			else if(user.getIsDynamicSenderId() == 'N') {
//				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
//				if(idOfSenderId == null || idOfSenderId == 0) {
//					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService.getDefaultUserSenderIdByUserName(user.getUserName());
//					if(userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
//						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
//						idOfSenderId = userDefaultSenderId.getId();
//						messageObject.setIdOfSenderId(idOfSenderId);
//						messageObject.setSenderId(userDefaultSenderId.getSenderId());
//						if(messageObject.getEntityId() == null) {
//							messageObject.setEntityId(userDefaultSenderId.getEntityId());
//						}
//					}else {
//						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//						continueMessageProcessing = false;
//						isMisRequired = true;
//						return false;
//					}
//				}else {
//					messageObject.setIdOfSenderId(idOfSenderId);
//					if(messageObject.getEntityId() == null)
//						messageObject.setEntityId(entityId);
//				}
//			}
//			//Due to mismatch with Production Class (Aman)
//			//Start
//			
////			else if(user.getIsShortCodeAllowed() == 'Y' && user.getReplaceShortCode() == 'Y'){
////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
////				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
////				String shortCode = staticDataService.getShortCodeFromSenderId(senderIdKey);
////				if(shortCode != null) {
////						messageObject.setIdOfSenderId(idOfSenderId);
////						messageObject.setSenderId(shortCode);
////						messageObject.setEntityId(null);
////				}else {
////					messageObject.setIdOfSenderId(idOfSenderId);
////					if(messageObject.getEntityId() == null)
////						messageObject.setEntityId(entityId);
////				}
////			}
//			
//			//End
//		}else {
//			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//			continueMessageProcessing = false;
//			isMisRequired = true;
//			return false;
//		}
//		logger.info("{} Final Id of Sender Id {} is: {}",messageObject.getMessageId(), messageObject.getSenderId(), idOfSenderId);
//		return true;
//	}
//
//	private boolean validateAndSetSenderIdForTransPromo(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
//			Boolean isMisRequired) {
//		Integer idOfSenderId = 0;
//		if(messageObject.getSenderId() != null) {
//			messageObject.setOriginalSenderId(messageObject.getSenderId());
//			if(user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
//				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//				if(idOfSenderId == null || idOfSenderId == 0) {
//					idOfSenderId = 9999;
//					messageObject.setIdOfSenderId(idOfSenderId);
//				}else {
//					messageObject.setIdOfSenderId(idOfSenderId);
//				} 
//				if(messageObject.getEntityId() == null) {
//					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
//				}
//			}else if(user.getIsDynamicSenderId() == 'N'){
//				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
//				if(idOfSenderId == null || idOfSenderId == 0) {
//					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService.getDefaultUserSenderIdByUserName(user.getUserName());
//					if(userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
//						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
//						idOfSenderId = userDefaultSenderId.getId();
//						messageObject.setIdOfSenderId(idOfSenderId);
//						if(messageObject.getEntityId() == null) {
//							messageObject.setEntityId(userDefaultSenderId.getEntityId());
//						}
//						messageObject.setSenderId(userDefaultSenderId.getSenderId());
//					}else {
//						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//						continueMessageProcessing = false;
//						isMisRequired = true;
//						return false;
//					}
//				}else {
//					messageObject.setIdOfSenderId(idOfSenderId);
//					if(messageObject.getEntityId() == null)
//						messageObject.setEntityId(entityId);
//				}
//			}
//			if(!messageObject.isDndNumber() && !messageObject.isPremiumNumber()) {
//				List<NgUserSenderIdMap> userNonDndSenderIds = userSenderIdMapService.getNonDndDefaultUserSenderIdByUserName(user.getUserName());
//				if(userNonDndSenderIds != null && userNonDndSenderIds.size()>0) {
//					NgUserSenderIdMap userNonDndSenderId = userNonDndSenderIds.get(0);
//					idOfSenderId = userNonDndSenderId.getId();
//					messageObject.setIdOfSenderId(idOfSenderId);
//					messageObject.setSenderId(userNonDndSenderId.getSenderId());
//					if(messageObject.getEntityId() == null) {
//						messageObject.setEntityId(userNonDndSenderId.getEntityId());
//					}
//				}
//			}
//		}else {
//			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//			continueMessageProcessing = false;
//			isMisRequired = true;
//			return false;
//		}
//		logger.info("{} Final Id of Sender Id {} is: {}",messageObject.getMessageId(), messageObject.getSenderId(), idOfSenderId);
//		return true;
//	}
//	
//	private void populateRoutingId(MessageObject messageObject, NgUser user) {
//		Integer userId = messageObject.getUserId();
//		logger.info("{} MEssage service type is : ",messageObject.getMessageId(),messageObject.getMessageServiceType());
//		
//		Integer parentUserId = 0;
//		if(user.getParentId() != null) {
//			parentUserId = user.getParentId();
//		}
//		
//		Integer idOfSenderId = 0;
//		if(messageObject.getIdOfSenderId() != null)
//			idOfSenderId = messageObject.getIdOfSenderId(); 
//		
//		Integer circleId = 999;
//		Integer carrierId = 999;
//		String numSeries = null;
//		if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
//			numSeries = messageObject.getDestNumber().substring(2, 7);
//		}else {
//			numSeries = messageObject.getDestNumber().substring(0, 5);
//		}
//		String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
//		String carrierCircleData[] = carrierCircleInfo.split("#");
//		carrierId = Integer.parseInt(carrierCircleData[0]);
//		circleId = Integer.parseInt(carrierCircleData[1]);
//		
//		messageObject.setCarrierId(carrierId);
//		messageObject.setCircleId(circleId);
//		
//		Integer routingId = getRoutingId(userId, parentUserId, idOfSenderId, carrierId, circleId, user, messageObject);
//		if(routingId == null) {
//			String defaultRoutingInfoKey = "0#0#0#0#0";
//			logger.info("{} No routing id found. Hence going to fetch default routing id with key {} : ",messageObject.getMessageId(),defaultRoutingInfoKey);	
//			if(messageObject.getMessageServiceType().equals("trans")) {
//				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
//			}else if(messageObject.getMessageServiceType().equals("promo")) {
//				routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
//			}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
//				routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
//			}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
//				routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
//			}else if(messageObject.getMessageServiceType().equals("others")) {
//				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
//			}
//		}
//		logger.error("{} Final routing id is {}: ",messageObject.getMessageId(), routingId);
//		messageObject.setRouteId(routingId);
//		messageObject.setGroupId(routingId);
//	}
// 
//	private Integer getRoutingId(Integer userId, Integer parentUserId, Integer idOfSenderId, Integer carrierId,
//			Integer circleId, NgUser user, MessageObject messageObject) {
//		logger.info("{} Going to find routing id for user id {}, parent user id {}, idOfSenderId {}, carrier id {}, circle id{}",messageObject.getMessageId(), userId,  parentUserId,  idOfSenderId,  carrierId,  circleId);
//		Set<Object> routingTypeSet = staticDataService.getAllRoutingTypes();
//		Integer finalRoutingId = null;
//		for (Object routingTypeWithPriority : routingTypeSet) {
//			String consolidateRoutingType = null;
//			String finalRoutingKey = "0";
//			String routingTypeWithPri = (String) routingTypeWithPriority;
//			if (routingTypeWithPri.contains("#")) {
//				String[] routingTypeWithPriorityArray = routingTypeWithPri.split("#");
//				if (routingTypeWithPriorityArray != null && routingTypeWithPriorityArray.length > 0) {
//					consolidateRoutingType = routingTypeWithPriorityArray[0];
//					if (consolidateRoutingType != null) {
//						logger.info("{} 1. Consolidated routing type is:{} ",messageObject.getMessageId(),consolidateRoutingType);
//						if (consolidateRoutingType.contains("user")) {
//								logger.info("User keyword found in type. Adding user id in key", userId);
//								finalRoutingKey = Integer.toString(userId);
//						} else {
//							finalRoutingKey = "0";
//						}
//						
//						if (consolidateRoutingType.contains("parent")) {
//							finalRoutingKey = finalRoutingKey + "#" + parentUserId;
//						} else {
//							finalRoutingKey = finalRoutingKey + "#" + 0;
//						}
//
//						if (consolidateRoutingType.contains("carrier")) {
//							finalRoutingKey = finalRoutingKey + "#" + carrierId;
//						} else {
//							finalRoutingKey = finalRoutingKey + "#" + 0;
//						}
//					
//						if (consolidateRoutingType.contains("circle")) {
//							finalRoutingKey = finalRoutingKey + "#" + circleId;
//						} else {
//							finalRoutingKey = finalRoutingKey + "#" + 0;
//						}
//						
//						if (consolidateRoutingType.contains("senderid")) {
//							finalRoutingKey = finalRoutingKey + "#" + idOfSenderId;
//						} else {
//							finalRoutingKey = finalRoutingKey + "#" + 0;
//						}
//					}
//				}
//			}
//			logger.info("{} Going to find routing id for type {} with key {} : ",messageObject.getMessageId(),consolidateRoutingType, finalRoutingKey);
//			if(!messageObject.getMessageServiceType().equals("promo")) {
//				logger.info("{} Final routing key for message is: {}",messageObject.getMessageId(), finalRoutingKey);
//				finalRoutingId = staticDataService.getRoutingGroupForKeyForTrans(finalRoutingKey);
//			}else if(messageObject.getMessageServiceType().equals("promo")) {
//				finalRoutingId = staticDataService.getRoutingGroupForKeyForPromo(finalRoutingKey);
//			}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
//				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(finalRoutingKey);
//			}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
//				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(finalRoutingKey);
//			}
//			if (finalRoutingId != null) {
//				logger.info("{} Routing id found for key {} is {} : ",messageObject.getMessageId(),finalRoutingKey, finalRoutingId);
//				return finalRoutingId;
//			}
//		}
//		return null;
//	}
//
//	private String convertMessageObjectToJsonString(MessageObject messageObject) {
//		try {
//			return objectMapper.writeValueAsString(messageObject);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private boolean checkNumberInGlobalDndList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
//		if (messageObject.getDestNumber() != null) {
//			  boolean isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber());
//			  if(isNumberInDndList == false && messageObject.getDestNumber().startsWith("91")) {	
//				  isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber().substring(2));
//			  }
//			  logger.info("{} Is Number in Dnd List: {}",messageObject.getMessageId(),isNumberInDndList);
//			  if(isNumberInDndList && messageObject.getMessageServiceType().equals("service") && user.getIsDndAllowed() == 'Y') {
//				  logger.info("Mobile Number {} is in Global DnD List and DND message is allowed for user hence will be sent using DnD routing.",messageObject.getDestNumber());
//				  messageObject.setDndNumber(true);
//				  messageObject.setDndAllowed(true);
//				  continueMessageProcessing = true;
//				  return true;
//			  }else if(isNumberInDndList && user.getIsDndAllowed() == 'N') {
//				  logger.info("{} Mobile Number {} is in Global DnD List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getDestNumber());
//				  messageObject.setErrorCode(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//	
//	private boolean checkNumberInFailedNumbersList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
//		if(messageObject.getDestNumber()!=null) {
//			  boolean isNumberInFailedList = globalFailedNumberListService.isFailedNumber(messageObject.getDestNumber());
//			  if(isNumberInFailedList == false && messageObject.getDestNumber().startsWith("91")) {
//				  isNumberInFailedList = globalFailedNumberListService.isFailedNumber(messageObject.getDestNumber().substring(2));
//			  }
//			  logger.info("{} Is Number in FailedNumber List: {} ",messageObject.getMessageId(),isNumberInFailedList);
//			  if(isNumberInFailedList) {
//				  isMisRequired = true;
//				  messageObject.setErrorCode(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  return false;
//			  }
//		  }
//		return true;
//	}
//	// global blacklist start.......
//	private boolean checkNumberInGlobalBlackList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, Set<String> phone) {
//		if(messageObject.getDestNumber()!=null) {
//			  boolean isNumberInGlobalBlackList = globalBlackListService.isBlackListNumber(messageObject.getDestNumber(), phone);
//			  logger.info("{} Is Number in Global Black List:{} ",messageObject.getMessageId(),isNumberInGlobalBlackList);
//			  if(isNumberInGlobalBlackList) {
//				  logger.info("{} Mobile Number {} is in Global Black List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getDestNumber());
//				  messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//	
//	private boolean checkSenderIdInGlobalSenderIdBlackList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(messageObject.getSenderId() != null) {
//			  boolean isSenderIdInGlobalSenderIdBlackList = globalBlackListService.isBlackListSenderId(messageObject.getSenderId());
//			  logger.info("{} Is Sender Id in Global Black List:{} ",messageObject.getMessageId(),isSenderIdInGlobalSenderIdBlackList);
//			  if(isSenderIdInGlobalSenderIdBlackList) {
//				  logger.info("{} Sender Id {} is in Global Black List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getSenderId());
//				  messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//
//	private boolean checkNumberInUserBlackList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(messageObject.getDestNumber()!=null) {
//			  boolean isNumberInUserBlackList = userBlackListService.isUserBlackListNumberFromDB(user, messageObject.getDestNumber());
//			  logger.info("{} Is Number in User Black List: {} ",messageObject.getMessageId(),isNumberInUserBlackList);
//			  if(isNumberInUserBlackList) {
//				  logger.info("Mobile Number {} is in User Black List. Hence not sending the message and updating MIS.",messageObject.getDestNumber());
//				  messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//	
//	private boolean checkSenderIdInUserSenderIdBlackList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(messageObject.getSenderId() != null) {
//			  boolean isSenderIdInUserSenderIdBlackList = userBlackListService.isUserSenderIdBlackList(user.getUserName(), messageObject.getSenderId());
//			  logger.info("{} Is Sender Id in User Black List: {}",messageObject.getMessageId(),isSenderIdInUserSenderIdBlackList);
//			  if(isSenderIdInUserSenderIdBlackList) {
//				  logger.info("Sender Id {} is in User Black List. Hence not sending the message and updating MIS.",messageObject.getDestNumber());
//				  messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//	
//	private boolean checkNumberInUserPremiumList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(messageObject.getDestNumber()!=null) {
//			  boolean isNumberInUserPremiumList = userPremiumListService.isUserPremiumListNumber(user.getUserName(), messageObject.getDestNumber());
//			  logger.info("{} Is Number in User Premium List: {}",messageObject.getMessageId(),isNumberInUserPremiumList);
//			  if(isNumberInUserPremiumList) {
//				  // logger.info("Mobile Number {} is in User Premium List. Hence not replacing sender id and applying premium routing.",messageObject.getDestNumber());
//				  logger.info("Mobile Number {} is in User Premium List.",messageObject.getDestNumber());
//				  return isNumberInUserPremiumList;
//				  // messageObject.setPremiumNumber(true);
//			  }else {
//				  // messageObject.setPremiumNumber(false);
//			  }
//		  }
//		return false;
//	}
//	
//	private boolean checkNumberInGlobalPremiumList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(messageObject.getDestNumber()!=null) {
//			  boolean isNumberInGlobalPremiumList = globalPremiumListService.isGlobalPremiumListNumber(messageObject.getDestNumber());
//			  logger.info("{} Is Number in Global Premium List: {}",messageObject.getMessageId(),isNumberInGlobalPremiumList);
//			  if(isNumberInGlobalPremiumList) {
//				  logger.info("Mobile Number {} is in Global Premium List. Hence not replacing sender id and applying premium routing.",messageObject.getDestNumber());
//				  messageObject.setPremiumNumber(true);
//				  return isNumberInGlobalPremiumList;
//			  }else {
//				  messageObject.setPremiumNumber(false);
//			  }
//		  }
//		return false;
//	}
//	
//	private boolean filterRestrictedContent(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		Set<String> abusingWordsSet = abusingWordsListService.getUserAbusingWordsList(user.getUserName());
//		if(abusingWordsSet !=null && abusingWordsSet.size() > 0) {
//			  String message = messageObject.getMessage(); 
//			  logger.info("Message before restricted words : "+ message);
//			  List<String> abusingWordsInMessageList = Arrays.stream(message.split("(?=[.])|\\s+")).parallel().
//					  filter(t -> abusingWordsSet.contains(t.toLowerCase())).collect(Collectors.toList());
//			  
//			  if(abusingWordsInMessageList != null && abusingWordsInMessageList.size() > user.getAbusingWordsAllowedCount()) {
//				  logger.info("Abusing words ' {} ' found in the message. Hence not sending the message and updating MIS",abusingWordsInMessageList.toString());
//				  messageObject.setErrorCode(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorCode());
//				  messageObject.setErrorDesc(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorDesc());
//				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				  continueMessageProcessing = false;
//				  isMisRequired = true;
//				  return false;
//			  }
//		  }
//		return true;
//	}
//
//	private boolean checkCreditForPrepaidAccount(MessageObject messageObject, String userName, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		if(user != null && (user.getNgBillingType().getId() == 1)){
//			Integer userCredit = userCreditMapService.getUserCreditByUserNameFromRedis(userName);
//			logger.info("Available user credit before deduction from redis for user {} is {}",userName, userCredit);
//		    	if(userCredit != null &&  userCredit <= 0) {
//		    		logger.info("Insufficient Account Balance for user: "+user.getUserName());
//		    		messageObject.setErrorCode(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorCode());
//					messageObject.setErrorDesc(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorDesc());
//					messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//					continueMessageProcessing = false;
//					isMisRequired = true;
//					return false;
//		    	}
//		    }else {
//		    	logger.info("{} is a Postpaid User. Hence no need of credit check.",userName);
//		    }
//		return true;
//	}
//	
//	// new method
//	public void selfRouting(MessageObject messageObject, NgUser user) {
//		
//		try {
//			System.out.println("Setting circle id  ****************************************************");
//			Integer circleId = 999;
//			Integer carrierId = 999;
//			String numSeries = null;
//			if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
//				numSeries = messageObject.getDestNumber().substring(2, 7);
//			}else {
//				numSeries = messageObject.getDestNumber().substring(0, 5);
//			}
//			String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
//			String carrierCircleData[] = carrierCircleInfo.split("#");
//			carrierId = Integer.parseInt(carrierCircleData[0]);
//			circleId = Integer.parseInt(carrierCircleData[1]);
//			
//			messageObject.setCarrierId(carrierId);
//			messageObject.setCircleId(circleId);
//			System.out.println("Circle Id   ===  *******************************" + circleId + carrierId);
//			// end
//
//			NgUserPremiumList findBypremiumNumber = userpremiumrepo.findBypremiumNumber(messageObject.getDestNumber());
//			System.out.println("findBypremiumNumberfetched successfully....");
//			if (findBypremiumNumber == null) {
//				System.out.println("number is null********************************************************************");
//				
//			}else {
//				System.out.println("findBypremiumNumber is NOT NULL");
//			}
//			System.out.println("GROUP ID =>\t"+findBypremiumNumber.getGroupId() != null ? findBypremiumNumber.getGroupId() : -1);
//			
//			messageObject.setRouteId(findBypremiumNumber.getGroupId());
//			messageObject.setGroupId(findBypremiumNumber.getGroupId());
//			
//			
//			
//			System.out.println("MessagObject routeid => "+messageObject.getRouteId() != null ? messageObject.getRouteId() : -1);
//			System.out.println("MessageObject group id => "+messageObject.getGroupId() != null ? messageObject.getGroupId() : -1);
//			
//		} catch (Exception e) {
//			System.out.println("INSIDE CATCH BLOCK => EXCEPTION -> "+e.getMessage());
//			e.printStackTrace();
//		}
//		
//		System.out.println("##########METHOD EXECUTED GRACEFULLY##########");
//			}
//	// end new method
//
//	private boolean applyDefaultSenderId(MessageObject messageObject, String userName, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
//		List<NgUserSenderIdMap> userSenderIdList = userSenderIdMapService.getDefaultUserSenderIdByUserName(userName);
//		if (userSenderIdList != null && userSenderIdList.size() > 0) {
//			messageObject.setSenderId(userSenderIdList.get(0).getSenderId());
//			messageObject.setIdOfSenderId(userSenderIdList.get(0).getId());
//			if (messageObject.getEntityId() == null) {
//				messageObject.setEntityId(userSenderIdList.get(0).getEntityId());
//			}
//		}else {
//			  logger.info("{} No valid sender id found for user. Hence skipping further processing",messageObject.getMessageId());
//			  messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//			  messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//			  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//			  continueMessageProcessing = false;
//			  isMisRequired = true;
//			  return false;
//		  }
//		  return true;
//	}
//
//	private boolean checkNineToNineMessage(List<String> messageList, int i, MessageObject messageObject, String userName, Boolean continueMessageProcessing, Boolean isMisRequired) {
//		Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		  int currentHour = currTime.get(Calendar.HOUR_OF_DAY);
//		  if(!(currentHour >= 9 && currentHour < 21)) {
//			  logger.info("Promo message received post cut off time. Saving it for later delivery");
//			  NgPostCutoffMessage postCutoffMessage = new NgPostCutoffMessage();
//			  postCutoffMessage.setMessageId(messageObject.getMessageId());
//			  postCutoffMessage.setUserName(userName);
//			  postCutoffMessage.setMobileNumber(messageObject.getDestNumber());
//			  postCutoffMessage.setMessageObject(messageList.get(i));
//			  postCutoffMessageService.savePostCutoffMessage(postCutoffMessage);
//			  logger.info("Promo message received post cut off time. Saved it for later delivery. Hence skipping further processing");
//			  messageObject.setErrorCode(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorCode());
//			  messageObject.setErrorDesc(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorDesc());
//			  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//			  continueMessageProcessing = false;
//			  isMisRequired = true;
//			  return false;
//		  }
//		  return true;
//	}
//
//	private MessageObject convertReceivedJsonMessageIntoMessageObject(String messageObjectJsonString) {
//		MessageObject messageObject = null;
//		try {
//			messageObject = objectMapper.readValue(messageObjectJsonString, MessageObject.class);
//		} catch (Exception e){
//			logger.error("Dont retry this message as error while parsing json string: "+messageObjectJsonString);
//			e.printStackTrace();
//		} 
//		return messageObject;
//	}
//
//	public String convertMessageObjectToMisObjectAsString(MessageObject messageObject) throws UnsupportedEncodingException, DecoderException {
//		NgMisMessage ngMisMessage = new NgMisMessage();
//		ngMisMessage.setAckId(messageObject.getRequestId());
//		ngMisMessage.setCarrierId(messageObject.getCarrierId());
//		ngMisMessage.setCircleId(messageObject.getCircleId());
//		ngMisMessage.setErrorCode(messageObject.getErrorCode());
//		ngMisMessage.setErrorDesc(messageObject.getErrorDesc());
//		ngMisMessage.setFullMsg(messageObject.getFullMsg());
//		ngMisMessage.setGroupId(messageObject.getGroupId());
//		ngMisMessage.setKannelId(messageObject.getKannelId());
//		ngMisMessage.setMessageClass(messageObject.getMessageClass());
//		ngMisMessage.setMessageId(messageObject.getMessageId());
//		ngMisMessage.setMessageSource(messageObject.getInstanceId());
//		ngMisMessage.setMessageText(messageObject.getMessage());
//		ngMisMessage.setMessageType(messageObject.getMessageType());
//		ngMisMessage.setMobileNumber(messageObject.getDestNumber());
//		ngMisMessage.setOriginalMessageId(messageObject.getMessageId());
//		ngMisMessage.setPriority(messageObject.getPriority());
//		ngMisMessage.setReceivedTs(messageObject.getReceiveTime());
//		ngMisMessage.setRouteId(messageObject.getRouteId());
//		ngMisMessage.setSenderId(messageObject.getSenderId());
//		ngMisMessage.setSentTs(messageObject.getSentTime());
//		ngMisMessage.setSplitCount(messageObject.getSplitCount());
//		ngMisMessage.setStatus(messageObject.getStatus());
//		ngMisMessage.setUdh(messageObject.getUdh());
//		ngMisMessage.setUserId(messageObject.getUserId());
//		//ngMisMessage.setAckId(messageObject.getMessageId());
//		ngMisMessage.setFullMsg(messageObject.getMessage());
//		ngMisMessage.setCampaignId(messageObject.getCampaignId());
//		ngMisMessage.setCampaignName(messageObject.getCampaignName());
//		ngMisMessage.setCampaignTime(messageObject.getCampaignTime());
//		
//		//Commented due to mis-match with Production class
//		//Start
//		
////		if(messageObject.getMessageType().equalsIgnoreCase("UC")) {
////			String msgText = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()), "UTF-16BE");
////			ngMisMessage.setFullMsg(msgText);
////		}else{
////	        	ngMisMessage.setFullMsg(messageObject.getMessage());
////	    }
////		
////		if(messageObject.getCircleId() != null && messageObject.getCarrierId() != null){
////			String carrierName = staticDataService.getCarrierNameById(messageObject.getCarrierId());
////			String circleName = staticDataService.getCircleNameById(messageObject.getCircleId());
////			
////			ngMisMessage.setCarrierName(carrierName);
////			ngMisMessage.setCircleName(circleName);
////		}
//		
//		//End
//		
//		try {
//			return objectMapper.writeValueAsString(ngMisMessage);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	/**
//	 * @param args
//	 * @throws UnsupportedEncodingException
//	 */
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		String strDestAddr = "123456";
//
//		String text = "12";
//
//		Matcher m = Pattern.compile("^\\d+$").matcher(text);
//		//Matcher m = Pattern.compile("^[a-zA-Z]*$").matcher(text);
//		System.out.println(m.matches());
//
//		/*
//		 * strDestAddr = strDestAddr.replaceAll("\\+", "");
//		 * if(strDestAddr.startsWith("0")) { strDestAddr =
//		 * strDestAddr.replaceFirst("0",""); } if (strDestAddr.length() == 10) {
//		 * strDestAddr = "91"+strDestAddr; } System.out.println(strDestAddr);
//		 * 
//		 * 
//		 * String n = "919953870879"; if(n.startsWith("91")){ }
//		 */
///*
//		String patternTemplate = "Dear Customer {T} Your PTYM account is {T} is locked";
//		patternTemplate = patternTemplate.replace("{T}", "(.*)");
//		Pattern pattern = Pattern.compile(patternTemplate);
//		
//		String input = "Dear Customer Ashish Rastogi  Your PTYM account is 123456XYZ is locked";
//		
//		Matcher matcher = pattern.matcher(input);
//		if (matcher.matches()) {
//			System.out.println("Input matches with template");
//		}else {
//			System.out.println("Not matches");
//		}
//		
//*/	
//		/*String templateUnicode = "0031003000320020090f0902092c09320938002009380935093e002009090924092409300020092a093009260936002c00200020090f092c093209380020092800200031003200330020092b094b09280020092800200034003500200938092e092f0020002d00200036003700200932093f0915002000380039002000310030002009270928092f0935093e09260964";
//		String patternUnicode =  "003000300033003100300030003300300030003000330032003000300032003000300039003000460030003900300032003000390032004300300039003300320030003900330038003000300032003000300039003300380030003900330035003000390033004500300030003200300030003900300039003000390032003400300039003200340030003900330030003000300032003000300039003200410030003900330030003000390032003600300039003300360030003000320043003000300032003000300030003200300030003900300046003000390032004300300039003300320030003900330038003000300032003000300039003200380030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320042003000390034004200300039003200380030003000320030003000390032003800300030003200300030003000370042003000300032003300300030003700360030003000360031003000300037003200300030003200330030003000370044003000300032003000300039003300380030003900320045003000390032004600300030003200300030003000320044003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000390033003200300039003300460030003900310035003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320037003000390032003800300039003200460030003900330035003000390033004500300039003200360030003900360034";
//		
//		try {
//			templateUnicode = new String(Hex.decodeHex(templateUnicode.toCharArray()), "UTF-16BE");
//		} catch (DecoderException e) {
//			// 
//			e.printStackTrace();
//		}
//		System.out.println("templateUnicode : "+templateUnicode);*/
//		
//		String messageTextReceived = "";
//		String patternTemplate = "";
//		
//		/*messageTextReceived = templateUnicode;
//		patternTemplate = "102 एंबलस सवा उततर परदश,  एबलस न {#var#} फोन न {#var#} समय - {#var#} लिक {#var#} {#var#} धनयवाद।";
//		System.out.println("templateUnicode : "+templateUnicode);
//		System.out.println("ucMessage : "+patternTemplate);*/
//		//messageTextReceived = "Rs 9000.0 Credited in your Account for AEPS - CASH WITHDRAWAL against EASy order ID EASY01088460100";
//		//patternTemplate = "Rs {#var#} Debited from your Account for GPR CARD RELOAD against EASy order ID {#var#}";
//		
//		//messageTextReceived = "You have a new Account topup request for approval fro YA RAHMAN sdfs fs fsdTOUR AND TELECOM for amount. 25000.0";
//		//patternTemplate = "You have a new Account topup request for approval fro {#var#} for  amount\\. {#var#}";
//		
//		//messageTextReceived = "123 is One Time Password for .KCPrime a b c d e h Login Pleas do not share this with anyone Team KhelChamps Prime JruqUz7nWYa";
//		//patternTemplate = "{#var#} is One Time Password for \\.{#var#}\\. Please do not share this with anyone\\. Team KhelChamps Prime JruqUz7nWYa";
//		
//		/*messageTextReceived = "DEAL EXPIRY ALERT: 2 BHK 850 Sq.Ft. Apartment from Square Connect is about to expire and will be given to another Realtor.\r\n" + 
//				"  \r\n" + 
//				"Call NOW. Details here - https://xaw98.app.goo.gl/teFh";
//		patternTemplate = "{#var#} from Square Connect is about to expire and will be given to another Realtor\\. Call NOW\\. Details here \\- {#var#}";
//		*/
//		
//		//messageTextReceived = "Dear Customer, Your A/C 0004179 has been credited by Rs.10000.00 by CR. Available balance in your account is Rs.1633 as on 06-04-2021 10:45\r\nMORADABAD ZILA SAHKARI BANK LTD.";
//		//patternTemplate = "Dear Customer, Your A/C {#var#} has been credited by Rs\\.{#var#} by {#var#}\\. Available balance in your account is Rs\\.{#var#} as on {#var#}\r\n" + "MORADABAD ZILA SAHKARI BANK LTD\\.";
//		
//		/*patternTemplate = "Best Deal for Mobile Shop\r\n" + 
//				"\\*Billing Software\r\n" + 
//				"\\*Website\r\n" + 
//				"\\*Helpline Number\r\n" + 
//				"\\*Digital Promotion\r\n" + 
//				"\\*Facebook Promotion\r\n" + 
//				"24\\*7 Support\r\n" + 
//				"Call 9044887766\r\n" + 
//				"bit\\.ly/37Qq5M6";
//		
//		messageTextReceived = "Best Deal for Mobile Shop\r*Billing Software\r*Website\r*Helpline Number\r*Digital Promotion\r*Facebook Promotion\r24*7 Support\rCall 9044887766\rbit.ly/37Qq5M6";
//		*/
//		
//		/*patternTemplate = "Redy To Move\r\n" + 
//				"Area \\-{#var#}\r\n" + 
//				"Lease \\-{#var#}\r\n" + 
//				"Price \\-{#var#}\r\n" + 
//				"Invest with {#var#}\r\n" + 
//				"Call: {#var#}";
//		
//		messageTextReceived = "Redy To Move\\nArea -123\\nLease -asdfg\\nPrice -4566\\nInvest with rtyu\\nCall: ertyu";
//		*/
//
//		patternTemplate = "Hello ! (.*) units of (.*) blood required at (.*)\\. Patient: (.*), Contact: (.*) \\((.*)\\)\\. Please confirm whether you will be able to donate by sending (.*) \"space\" YES/NO to (.*)\\. Thanks, BloodMates";
//		messageTextReceived = "Hello ! 3 units of AB+ blood required at AMRI Test. Patient: Test Patient, Contact: Test Name (+911122334455). Please confirm whether you will be able to donate by sending JNC5FX \"space\" YES/NO to +918422874070. Thanks, BloodMates";
//		while(messageTextReceived.contains("  ")) {
//			messageTextReceived = messageTextReceived.replace("  "," ");
//		}
//		
//		while(patternTemplate.contains("  ")) {
//			patternTemplate = patternTemplate.replace("  "," ");
//		}
//		
//		/*if(patternTemplate.contains("\n")) {
//			patternTemplate = patternTemplate.replace("\n", "");
//		}
//		if(patternTemplate.contains("\r")) {
//			patternTemplate = patternTemplate.replace("\r", "");
//		}
//		if(messageTextReceived.contains("\n")) {
//			messageTextReceived = messageTextReceived.replace("\n", "");
//		}
//		if(messageTextReceived.contains("\\r") ) {
//			messageTextReceived = messageTextReceived.replace("\\r", "");
//		}*/
//		
//		patternTemplate = patternTemplate.replace("{#var#}", "(.*)");
//		
//		patternTemplate = patternTemplate.replaceAll("\\n", "");
//		patternTemplate = patternTemplate.replaceAll("\\r", "");
//		messageTextReceived = messageTextReceived.replaceAll("\\r", "");
//		messageTextReceived = messageTextReceived.replaceAll("\\n", "");
//		patternTemplate = patternTemplate.replace("\\n","");
//		patternTemplate = patternTemplate.replace("\n","");
//		patternTemplate = patternTemplate.replace("\\r","");
//		patternTemplate = patternTemplate.replace("\r","");
//		messageTextReceived = messageTextReceived.replace("\\r","");
//		messageTextReceived = messageTextReceived.replace("\r","");
//		messageTextReceived = messageTextReceived.replace("\\n","");
//		messageTextReceived = messageTextReceived.replace("\n","");
//		
//		System.out.println(messageTextReceived);
//		System.out.println(patternTemplate);
//		
//		/*Pattern pattern = Pattern.compile(patternTemplate);
//		Matcher matcher = pattern.matcher(messageTextReceived);
//		boolean matchResult = matcher.matches();
//		
//		System.out.println(matchResult);*/
//		
//		String[] messageTextArray = messageTextReceived.split(" ");
//        String[] patternTextArray = patternTemplate.split(" ");
//        boolean matchResult = false;
//        if(true) {
//			for (int i = 0, j= 0; i < patternTextArray.length && i<=j && j<messageTextArray.length;) {
//				String p = patternTextArray[i];
//				/*if(p.contains("(.*)")) {
//					p = p.replace(p,"(.*)");
//				}
//				System.out.println(p.length());*/
//				Pattern pattern = Pattern.compile(p);
//				Matcher matcher = pattern.matcher(messageTextArray[j]);
//				matchResult = matcher.matches();
//				System.out.println(patternTextArray[i] + " && "+ messageTextArray[j] +" && "+matchResult + " "+j + " "+i);
//				if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false && j < messageTextArray.length-1) {
//					j++;
//					messageTextArray[j] = messageTextArray[j-1]+messageTextArray[j];
//				}else if (i != 0 && patternTextArray[i-1].contains("(.*)") && matchResult == false) {
//					j++;
//				}
//				else if (matchResult == false) {
//					break;
//				}else {
//					i++;j++;
//				}
//			}
//			System.out.println(matchResult);
//        }	
//	}
//	
//	
//	public static String getHexStr(String _strObj) {
//		String hex = null;
//		if (_strObj != null) {
//			int decval = Integer.parseInt(_strObj.trim());
//			hex = Integer.toHexString(decval);
//		}
//		return hex;
//	}
//	
//	public String sendRejectedDlr(MessageObject messageObject) throws IOException {
//	/*	String dlrUrl = "http://localhost:8080/dlrlistener-0.0.1-SNAPSHOT/?dr=%a&smscid=%i&statuscd=%d&uniqID=%MSGID%&customerref=%USERNAME%"
//				+ "&receivetime=%RCVTIME%&dlrtype=9&mobile=%TO%&submittime=%SENTTIME%&expiry=12&senderid=%SENDERID%&carrierid=%CARRIERID%&circleid=%CIRCLEID%&routeid=%ROUTEID%&systemid=%SRCSYSTEM%"
//				+ "msgtxt=%MSGTXT%&currkannelid=%CURRKANNELID%&reqid=%REQID%&retrycount=%RETRYCOUNT%&usedkannelid=%USEDKANNELID%&ismultipart=%ISMULTIPART%&msgtype=%MSGTYPE%&splitcount=%SPLITCOUNT%";*/
//		NgKannelInfo ngKannelInfo = kannelInfoService.getDefaultKannelInfoForRejectedMessage();
//		
//		
//		String dlrListenerUrl = ngKannelInfo.getDlrUrl();
//		String dr = "id:%ID% sub:%ERRORCODE% dlvrd:%DLVRD% submit date:%SUBDATE% done date:%DONEDATE% stat:%STAT% err:%ERRORCODE% text:%TEXT%";
//		
//		if(messageObject.getErrorCode().equals(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode())) {
//			dr = dr.replaceAll("%ERRORCODE%", "882");
//			dr = dr.replaceAll("%DLVRD%", "001");
//			dr = dr.replaceAll("%STAT%", "FAILED");
//			messageObject.setErrorCode("");
//			messageObject.setErrorDesc("");
//			messageObject.setStatus("");
//		}else {
//			dr = dr.replaceAll("%ERRORCODE%", messageObject.getErrorCode());
//			dr = dr.replaceAll("%DLVRD%", "000");
//			dr = dr.replaceAll("%STAT%", "REJECTD");
//			
//		}
//		
//		dr = dr.replaceAll("%ID%", "" + System.currentTimeMillis() + messageObject.getDestNumber().substring(5, messageObject.getDestNumber().length()-1));
//		if(messageObject.getMessage()!= null && messageObject.getMessage().length() > 10) {
//			dr = dr.replaceAll("%TEXT%", messageObject.getMessage().substring(0,9));
//		}else {
//			dr = dr.replaceAll("%TEXT%", messageObject.getMessage());
//		}
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
//		String date = dateFormat.format(new Date());
//		dr = dr.replaceAll("%SUBDATE%", date);
//		dr = dr.replaceAll("%DONEDATE%", date);
//
//		
//		
//		dr = URLEncoder.encode(dr,"UTF-8");
//		
//		dlrListenerUrl = dlrListenerUrl.replaceFirst("%a",dr);
//		dlrListenerUrl = dlrListenerUrl.replaceFirst("smscid=%i", "smscid=000");
//		dlrListenerUrl = dlrListenerUrl.replaceFirst("statuscd=%d", "statuscd=000");
//			
//		if (messageObject.getMessageId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGID%", messageObject.getMessageId());
//
//		if (messageObject.getUsername() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%USERNAME%", messageObject.getUsername());
//
//		if(messageObject.getMessage() != null) 
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGTXT%", URLEncoder.encode(messageObject.getMessage(), "UTF-8"));
//		
//		dlrListenerUrl = dlrListenerUrl.replace("%CURRKANNELID%", ""+0);
//		
//		if(messageObject.getRequestId() != null) {
//			dlrListenerUrl = dlrListenerUrl.replace("%REQID%", URLEncoder.encode(messageObject.getRequestId(), "UTF-8"));
//		}
//		
//		Integer messageSplitCount = messageObject.getSplitCount();
//		if ((messageSplitCount != null && messageSplitCount > 1) || (messageObject.getIsMultiPart() != null && messageObject.getIsMultiPart().equals("Y"))) {
//			messageObject.setIsMultiPart("Y");
//			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());
//			
//			//Changes for Rejected DLR (In multipart case - Aman)
//			messageObject.setFullMsg(messageObject.getMessage());
//			//Integer pmSplitBytes = Integer.parseInt(plainMessageSplitBytes);
////			String concatinatedMessageIds = messageObject.getRequestId();
////			String[] messageIdsArray = concatinatedMessageIds.split("#");
//
////			logger.info("***** Important Check. Dont Ignore ******");
////			logger.info("Receiver Split Count {} and Total messages ids count {} ",
////					messageObject.getSplitCount(), messageIdsArray.length);
////			for (int j = 0; j < messageIdsArray.length; j++) {
////				
////				    messageObject.setMessageId(messageIdsArray[j]);
//				
//					saveMessageInMis(true, messageObject);
//
//				
////			}
//			logger.info("{} DLR for rejected message sent with status : {}", messageObject.getMessageId());
//
//		}else {
//			messageObject.setIsMultiPart("N");
//			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());
//			saveMessageInMis(true, messageObject);
//		}
//		
//		if(messageObject.getUsedKannelIds() != null) {
//			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%", URLEncoder.encode(messageObject.getUsedKannelIds(), "UTF-8"));
//		}else {
//			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%", "0");
//		}
//		
//		if(messageObject.getMessageType()!=null) {
//			dlrListenerUrl = dlrListenerUrl.replace("%MSGTYPE%", messageObject.getMessageType());
//		}
//		
//		if(messageObject.getRetryCount() != null) {
//			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", ""+messageObject.getRetryCount());
//		}else {
//			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", "0");
//		}
//		 
//		if(messageObject.getSplitCount()!=null && messageObject.getSplitCount() > 1) {
//			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", ""+messageObject.getSplitCount());
//		}else {
//			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", "1");
//		}
//		
////		if(messageObject.getCustRef() != null) {
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", messageObject.getCustRef());
////		}else {
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", "");
////		}
//		
//		if (messageObject.getCustRef() != null) {
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%",
//					URLEncoder.encode(messageObject.getCustRef(), "UTF-8"));
//		} else {
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", "");
//		}
//		
//		if (messageObject.getReceiveTime() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%RCVTIME%", Long.toString(messageObject.getReceiveTime().getTime()));
//
//		if (messageObject.getDestNumber() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%TO%", messageObject.getDestNumber());
// 
//		messageObject.setSentTime(new java.sql.Timestamp(new Date().getTime()));
//		if (messageObject.getSentTime() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENTTIME%", Long.toString(messageObject.getSentTime().getTime()));
//
//		if(messageObject.getOriginalSenderId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getOriginalSenderId());
//		else if (messageObject.getSenderId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getSenderId());
//		
//		messageObject.setKannelId(0);
//		if (messageObject.getKannelId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%KANNELID%", messageObject.getKannelId().toString());
//
//		if (messageObject.getCarrierId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", messageObject.getCarrierId().toString());
//		else
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", "0");
//		
//		if (messageObject.getCircleId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", messageObject.getCircleId().toString());
//		else
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", "0");
//		
//		if (messageObject.getRouteId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", messageObject.getRouteId().toString());
//		else
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", ""+0);
//		
//		if (messageObject.getInstanceId() != null)
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%SRCSYSTEM%", messageObject.getInstanceId());
//
//		logger.info("final dlr listener url for rejected message : " + dlrListenerUrl);
//		dlrListenerUrl = dlrListenerUrl.replaceAll("\\+", "%20");
//		dlrListenerUrl = dlrListenerUrl.replaceAll(" ", "%20");
//		
//		URL obj = new URL(dlrListenerUrl);
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		con.setRequestMethod("GET");
//		int responseCode = con.getResponseCode();
//		logger.info("Response from dlr listener received : " + responseCode);
//		
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//		logger.info("Response string from kannel is : ", response.toString());
//		return response.toString();
//	}
//
//	private boolean validateMessageTemplate(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) throws UnsupportedEncodingException, DecoderException {
//		boolean templateMatched = false;
//		Set<String> templateTextListForSenderId = staticDataService.getTemplateTextListForUserAndSenderId(user.getId()+"#"+messageObject.getSenderId());
//		if(templateTextListForSenderId != null || (messageObject.getTemplateId() != null && messageObject.getTemplateId().length() > 0)) {
//			for (String patternTemplateAndDltTemplateId : templateTextListForSenderId) {
//                logger.info("Pattern from list: {}", patternTemplateAndDltTemplateId);
//                String[] tempArray = patternTemplateAndDltTemplateId.split("!!~~!!");
//                String patternTemplate = tempArray[0].replace("{#var#}", "(.*)");
//                String messageTextReceived = messageObject.getMessage();
//                if (messageObject.getMessageType().equalsIgnoreCase("UC")) {
//                    messageTextReceived = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()), "UTF-16BE");
//                }
//                while(messageTextReceived.contains("  ")) {
//        			messageTextReceived = messageTextReceived.replace("  "," ");
//        		}
//        		
//        		while(patternTemplate.contains("  ")) {
//        			patternTemplate = patternTemplate.replace("  "," ");
//        		}
//        		
//        		patternTemplate = patternTemplate.replaceAll("\\n", "");
//        		patternTemplate = patternTemplate.replaceAll("\\r", "");
//        		messageTextReceived = messageTextReceived.replaceAll("\\r", "");
//        		messageTextReceived = messageTextReceived.replaceAll("\\n", "");
//        		
//        		patternTemplate = patternTemplate.replace("\\n","");
//        		patternTemplate = patternTemplate.replace("\n","");
//        		patternTemplate = patternTemplate.replace("\\r","");
//        		patternTemplate = patternTemplate.replace("\r","");
//        		messageTextReceived = messageTextReceived.replace("\\r","");
//        		messageTextReceived = messageTextReceived.replace("\r","");
//        		messageTextReceived = messageTextReceived.replace("\\n","");
//        		messageTextReceived = messageTextReceived.replace("\n","");
//        		
//        		//logger.error("message id: {} messageTextReceived: {} and patternTemplate: {}",messageObject.getMessageId(), messageTextReceived, patternTemplate);
//        		
//                String[] messageTextArray = messageTextReceived.split(" ");
//                String[] patternTextArray = patternTemplate.split(" ");
//                boolean matchResult = false;
//                if(messageTextArray.length == patternTextArray.length) {
//                	try {
//						for (int i = 0; i < patternTextArray.length; i++) {
//							Pattern pattern = Pattern.compile(patternTextArray[i]);
//							Matcher matcher = pattern.matcher(messageTextArray[i]);
//							matchResult = matcher.matches();
//							if (matchResult == false) {
//								break;
//							}
//						}
//					} catch (Exception e) {
//						logger.error("Error occured while matching template 1:message {} and pattern {}",messageTextReceived, patternTemplate );
//						e.printStackTrace();
//						matchResult = false;
//					}
//				} else if(messageTextArray.length > patternTextArray.length) { 
//					try {
//						for (int i = 0, j= 0; i < patternTextArray.length && i <= j && j<messageTextArray.length;) {
//							Pattern pattern = Pattern.compile(patternTextArray[i]);
//							Matcher matcher = pattern.matcher(messageTextArray[j]);
//							matchResult = matcher.matches();
//							//System.out.println(patternTextArray[i] + " && "+ messageTextArray[j] +" && "+matchResult);
//							if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false && j < messageTextArray.length-1) {
//								j++;
//								messageTextArray[j] = messageTextArray[j-1] + messageTextArray[j];   //Check after this line After testing
//							}else if (i != 0 && patternTextArray[i-1].contains("(.*)") && matchResult == false) {
//								j++;
//							}
//							else if (matchResult == false) {
//								break;
//							}else {
//								i++;j++;
//							}
//						}
//					} catch (Exception e) {
//						logger.error("Error occured while matching template 2:message {} and pattern {}",messageTextReceived, patternTemplate );
//						e.printStackTrace();
//						matchResult = false;
//					}
//				}else {
//					matchResult = false;
//				}
//                logger.info("SenderId {} Template text is: {} ##and## message text is: {} and match result is :{}", (Object)messageObject.getSenderId(), (Object)patternTemplate, (Object)messageObject.getMessage(), (Object)matchResult);
//                if (matchResult) {
//                    templateMatched = true;
//                    if (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0) {
//                        messageObject.setMessageServiceType(tempArray[2]);
//                        messageObject.setTemplateId(tempArray[1]);
//                    }
//                    logger.info("Pattern matched {}. Going to return true. ", (Object)patternTemplateAndDltTemplateId);
//                    break;
//                }
//            }
//			if(!templateMatched) {
//				messageObject.setStatus(Constants.MESSAGE_STATUS_REJECTED);
//				messageObject.setErrorCode(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorCode());
//				messageObject.setErrorDesc(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorDesc());
//			
//				/*
//				 * try { sendRejectedDlr(messageObject); } catch (IOException e) {
//				 * logger.error("Error while sending DLT rejected DLR."); e.printStackTrace(); }
//				 */
//				continueMessageProcessing = false;
//				isMisRequired = true;
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * This method save the template in MIS & update the table in DB(ng_mis_curr_date)(Aman)
//	 * @param isMisRequired
//	 * @param messageObject
//	 */
//	private void saveMessageInMis(boolean isMisRequired, MessageObject messageObject) {
//		if (isMisRequired) {
//			// saveMessageInMis(messageObject);
//			String ngMisMessageObject;
//			try {
//				ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
//				if (ngMisMessageObject != null) {
//					messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
//				} else {
//					logger.error("Error while converting message object into DB Object with message Id {} ",
//							messageObject.getMessageId());
//				}
//			} catch (UnsupportedEncodingException | DecoderException e) {
//				logger.error("Error while converting message object into DB Object with message Id {} ",
//						messageObject.getMessageId());
//				e.printStackTrace();
//			}
//
//		}
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	//********************************************************* new code above*******************************************************************
//	/*
//	 * @Value("${split.plain.message.bytes}") private String plainMessageSplitBytes;
//	 * 
//	 * @Value("${split.unicode.message.bytes}") private String unicodeSplitBytes;
//	 */
//
////	@KafkaListener(id = "${app.name}", topics = "${kafka.topic.name}", groupId = "${kafka.topic.name}", idIsGroup = false)
////	public void receive1(List<String> messageList,
////	      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
////	      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
////
////	    logger.error("start of batch receive of size: "+messageList.size());
////	    // here we remove user black list number
//////	     
////	   //List<Object[]> blackListNameAndNumber = userBlackListRepository.getBlackListNameAndNumber();
//////	   
////	   logger.info("our campiagn start");
//////	   
////	      //Map<String, Integer> map = new HashMap<>();
//////	   
//////	   
////        // for(Object[] o : blackListNameAndNumber) {
////		//   map.put((String)o[1], (Integer)o[0]);
////	  // }
////	     
//////	    // end
////// *******************************************************************************************************************************************	   
////	   // load all global black list number from data base
////	    List<NgGlobalBlackList> allMobileNumber = globalBlackListRepository.findAll();
////	    Set<String> phoneNumber = allMobileNumber.stream().map(NgGlobalBlackList::getPhoneNumber).collect(Collectors.toSet());
////	  
////	   
////	   // end load number
//////************************************************************************************************************************************************
////
////		
////		for (int i = 0; i < messageList.size(); i++) {
////			processMessage(messageList, partitions, offsets, i, latch,phoneNumber);
////		}
////	    logger.error("End of received batch.++");
////	    try {
////	    	logger.error("Consumer Thread Going To Sleep for "+consumerSleepInterval + "ms.");
////	    	Thread.sleep(Integer.parseInt(consumerSleepInterval));
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
////	  }
////	
////
////	private void processMessage(List<String> messageList, List<Integer> partitions, List<Long> offsets, int i, CountDownLatch latch,Set<String> phoneNumber) {
////		Boolean continueMessageProcessing = true;
////		Boolean isMisRequired = false;
////		logger.error("received message='{}' with partition-offset='{}'", messageList.get(i),
////				partitions.get(i) + "-" + offsets.get(i));
////		MessageObject messageObject = convertReceivedJsonMessageIntoMessageObject(messageList.get(i));
////		try{
////				if (messageObject != null) {
////					String userName = messageObject.getUsername();
////					NgUser user = userService.getUserByName(messageObject.getUsername());
////					messageObject.setUserId(user.getId());
////
////					//continueMessageProcessing = checkCreditForPrepaidAccount(messageObject, userName, user, continueMessageProcessing, isMisRequired);
////					
////					//Commented when matching with Production Class
////					//Start
//////					if(continueMessageProcessing){         
//////						if (continueMessageProcessing && (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0 || messageObject.getTemplateId().equalsIgnoreCase("others"))){
//////							continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing, isMisRequired, user);
//////							if(messageObject.getTemplateId() != null)
//////								messageObject.setTemplateId(messageObject.getTemplateId().trim());
//////							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
//////								messageObject.setEntityId(messageObject.getEntityId().trim());
//////							}
//////						}else{
//////							continueMessageProcessing = true;
//////							if(messageObject.getTemplateId() != null)
//////								messageObject.setTemplateId(messageObject.getTemplateId().trim());
//////							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
//////								messageObject.setEntityId(messageObject.getEntityId().trim());
//////							}
//////						}
//////					}
////					//End
////					
////					//Add-On new validation for Mesage Template
////					if (continueMessageProcessing && (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0 || messageObject.getTemplateId().equalsIgnoreCase("others"))) 
////						continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing, isMisRequired, user);
////					
////					if(continueMessageProcessing)
////						continueMessageProcessing = checkSenderId(messageObject, userName, user, continueMessageProcessing, isMisRequired);
////					
////					// Check if number contains +,0 or 91 or valid mobile number
////					if(continueMessageProcessing)
////						continueMessageProcessing = checkDestinationNumber(messageObject,continueMessageProcessing, isMisRequired, user);
////					
////					// Check If Destination Number is in Dnd.
////					if(continueMessageProcessing && (messageObject.getMessageServiceType().equals("promo") || user.getIsDndCheck() == 'Y'))
////						continueMessageProcessing = checkNumberInGlobalDndList(messageObject,continueMessageProcessing, isMisRequired, user);
////					
////					// Check If Destination Number is in failed database and user opted for this check.
////					if(continueMessageProcessing && user.getRejectFailedNumber()=='Y')
////						continueMessageProcessing = checkNumberInFailedNumbersList(messageObject,continueMessageProcessing, isMisRequired, user);
////					/*
////					//Check if number is in global black list.
////					if(continueMessageProcessing)
////						continueMessageProcessing = checkNumberInGlobalBlackList(messageObject, continueMessageProcessing, isMisRequired);
////					*/
////					
////					// Check if number is in global black list.
////					if(continueMessageProcessing) {
////						if(checkNumberInUserPremiumList(messageObject, user, continueMessageProcessing, isMisRequired)){
////							continueMessageProcessing = true;
////						}else if (!checkNumberInGlobalBlackList(messageObject, continueMessageProcessing, isMisRequired,phoneNumber)) {
////							continueMessageProcessing = false;
////						}
////					}
////					
////					// Check if number is in user black list.
////					if(continueMessageProcessing && user.getIsBlackListAllowed()=='Y')
////						continueMessageProcessing = checkNumberInUserBlackList(messageObject, user, continueMessageProcessing, isMisRequired);
////					
////					// Check if sender id is in global sender id black list.
////					if(continueMessageProcessing)
////						continueMessageProcessing = checkSenderIdInGlobalSenderIdBlackList(messageObject, continueMessageProcessing, isMisRequired);
////					
////					// Check if sender is in user sender id black list.
////					if(continueMessageProcessing)
////						continueMessageProcessing = checkSenderIdInUserSenderIdBlackList(messageObject, user, continueMessageProcessing, isMisRequired);
////					
////					// Check if number is in user premium list.
////					//if(continueMessageProcessing)
////						//continueMessageProcessing = checkNumberInUserPremiumList(messageObject, user, continueMessageProcessing, isMisRequired);
////					
////					// Check if number is in global premium list.
////					if(continueMessageProcessing)
////						 checkNumberInGlobalPremiumList(messageObject, user, continueMessageProcessing, isMisRequired);
////					
////					
////					// Content Filtering.
////					if(continueMessageProcessing)
////						continueMessageProcessing = filterRestrictedContent(messageObject, user, continueMessageProcessing, isMisRequired);
////					
////					// Sender Id Validation and Replace Sender Id.
////					if(continueMessageProcessing  && !(messageObject.getMessageServiceType().equals("promo"))) {
////						continueMessageProcessing = validateAndSetSenderId(messageObject, user, continueMessageProcessing, isMisRequired);
////					}else if(continueMessageProcessing  && messageObject.getMessageServiceType().equals("promo")) {
////						continueMessageProcessing = validateAndSetSenderIdForTransPromo(messageObject, user, continueMessageProcessing, isMisRequired);
////					}else if(continueMessageProcessing){
////						continueMessageProcessing = applyDefaultSenderId(messageObject, userName, continueMessageProcessing, isMisRequired, user);
////					}
////						
////					
////					// 9-9 Timing Check.
////					if(continueMessageProcessing && messageObject.getMessageServiceType().equals("promo")) {
////						continueMessageProcessing = checkNineToNineMessage(messageList, i, messageObject, userName, continueMessageProcessing, isMisRequired);
////						if(!continueMessageProcessing) {
////							isMisRequired = false;
////						}
////					}
////					
////					// Populate routing id.
////					if(continueMessageProcessing) {
////						if(messageObject.isPremiumNumber()) {
////							Integer routingId = null;
////							String defaultRoutingInfoKey = "-1#-1#-1#-1#-1";
////							logger.info("{} Premium Number Found and hence going to find premium routing id with key {} : ",messageObject.getMessageId(),defaultRoutingInfoKey);	
////							if(messageObject.getMessageServiceType().equals("trans")) {
////								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("promo")) {
////								routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
////								routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
////								routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
////							}else if(messageObject.getMessageServiceType().equals("others")) {
////								routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////							}
////							messageObject.setRouteId(routingId);
////							messageObject.setGroupId(routingId);
////							
////							//Commented when match with Production class (Aman)
////							//Start
////							
//////							Integer circleId = 999;
//////							Integer carrierId = 999;
//////							String numSeries = null;
//////							if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
//////								numSeries = messageObject.getDestNumber().substring(2, 7);
//////							}else {
//////								numSeries = messageObject.getDestNumber().substring(0, 5);
//////							}
//////							String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
//////							String carrierCircleData[] = carrierCircleInfo.split("#");
//////							carrierId = Integer.parseInt(carrierCircleData[0]);
//////							circleId = Integer.parseInt(carrierCircleData[1]);
//////							
//////							messageObject.setCarrierId(carrierId);
//////							messageObject.setCircleId(circleId);
////							
////							//End
////						}else {
////							populateRoutingId(messageObject, user);
////						}
////						
////					}
////					
////					// Send message to submitter.
////					if(continueMessageProcessing) {
////						String messageObjectAsString = convertMessageObjectToJsonString(messageObject);
////						String submitterQueueName = staticDataService.getSubmitterQueueForGroupId(Integer.toString(messageObject.getRouteId()));
////						messageSender.send(submitterQueueName, messageObjectAsString);
////					}
////						
////					
////					// Dump Message into DB for MIS.
////					if(isMisRequired || !continueMessageProcessing) {
////						//saveMessageInMis(messageObject);
////						
//////						String ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
//////						if (ngMisMessageObject != null) {
//////							messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
//////						} else {
//////							logger.error("Error while converting message object into DB Object with message Id {} ",
//////									messageObject.getMessageId());
//////						}
////						 
////					
////						// TODO: Send dummy DLR for platform rejected messages.
////						//Changes for Rejected DLR (In multipart case - Aman)
////						
////						String concatinatedMessageIds = messageObject.getRequestId();
////						String[] messageIdsArray = concatinatedMessageIds.split("#");
////						
////						for (int j = 0; j < messageIdsArray.length; j++) {
////							messageObject.setMessageId(messageIdsArray[j]);
////							String status = sendRejectedDlr(messageObject);
////							logger.info("{} DLR for rejected message sent with status : {}",messageObject.getMessageId(), status);
////						}
////						
////						
////					}
////					
////					
////				}	
////			}catch (Exception e){
////				logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",messageObject.getMessageId() + messageList.get(i));
////				e.printStackTrace();
////				messageObject.setErrorCode("UNKNOWN_ERROR_OCCURED");
////				messageObject.setErrorDesc("Unknown error occured while processing message request");
////				String ngMisMessageObject;
////				try {
////					ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
////					messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
////				} catch (UnsupportedEncodingException | DecoderException e1) {
////					logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",messageObject.getMessageId() + messageList.get(i));
////					e1.printStackTrace();
////				}
////				
////			}
////		latch.countDown();
////	}
////
////	private Boolean checkSenderId(MessageObject messageObject, String userName, NgUser user,
////			Boolean continueMessageProcessing, Boolean isMisRequired) {
////		logger.info("{} Message service type {} and number {} and shortcode {}",messageObject.getMessageId(), messageObject.getMessageServiceType(), messageObject.getDestNumber(), user.getIsShortCodeAllowed());	
////		
////		
////		
//////		if(messageObject.getSenderId() != null && (messageObject.getSenderId().length() < 3 || messageObject.getSenderId().length() > 14)) {
//////				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//////				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//////				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//////				continueMessageProcessing = false;
//////				isMisRequired = true;
//////				return false;
//////		}else 
////		
////		//Commented
////		{
////			if((messageObject.getMessageServiceType().equals("trans") ||
////					messageObject.getMessageServiceType().equals("service") || messageObject.getMessageServiceType().equals("others"))) {
////				Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());
////		   	    if(matcher.matches() && (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
////		   	    	return true;
////		   	    }else {
////		   	    	messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////					continueMessageProcessing = false;
////					isMisRequired = true;
////					return false;
////		   	    }
////			}else if(messageObject.getMessageServiceType().equals("promo")){
////				Matcher m1 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
////				if(m1.matches()) {
////					return true;
////		   	    }else {
////		   	    	messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////					continueMessageProcessing = false;
////					isMisRequired = true;
////					return false;
////		   	    }
////			}else {
////				logger.info("{} Message service type received others.",messageObject.getMessageId());
////				Matcher m3 = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());
////				
////				if (m3.matches() && (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
////					messageObject.setMessageServiceType("trans");
////					return true;
////				}
////				m3 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
////				if (m3.matches()) {
////					messageObject.setMessageServiceType("promo");
////					return true;
////				}
////				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////				continueMessageProcessing = false;
////				isMisRequired = true;
////				return false;
////			}
////		}
////		//Commented End
////		
//////		String senderId = messageObject.getSenderId();
//////		boolean isValidSenderId = false;
//////		
//////		if (messageObject.getMessageServiceType().equals("trans") || messageObject.getMessageServiceType().equals("service") || messageObject.getMessageServiceType().equals("others")) {
//////			isValidSenderId = validateAlphabeticSenderId(senderId);
//////		}else if (messageObject.getMessageServiceType().equals("promo")) {
//////			isValidSenderId = validateNumericSenderId(senderId);
//////		}
//////		
////////		else if (messageObject.getMessageServiceType().equals("others")) {
////////			isValidSenderId = validateAlphanumericSenderId(senderId);
////////		}
//////		
//////		else {
//////			isValidSenderId = validateMixedSenderId(senderId);
//////		}
//////		
//////		
//////		if (!isValidSenderId) {
//////			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//////			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//////			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//////			continueMessageProcessing = false;
//////			isMisRequired = true;
//////		}
//////		
//////		return isValidSenderId;
////		
////	}
////	
////	//Sender Id validation Methods
//////	private boolean validateAlphabeticSenderId(String senderId) {
//////		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
//////		logger.info("The sender id is 1 :" + matcher.toString());
//////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//////	}
//////	
//////	private boolean validateNumericSenderId(String senderId) {
//////		Matcher matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
//////		logger.info("The sender id is 2 :" + matcher.toString());
//////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//////	}
//////	
////////	private boolean validateAlphanumericSenderId(String senderId) {
////////		Matcher matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
////////		logger.info("The sender id is 3 :" + matcher.toString());
////////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
////////	}
//////	
//////	private boolean validateMixedSenderId(String senderId) {
//////		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
//////		logger.info("The sender id is 4 :" + matcher.toString());
//////		if (matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14)) {
//////			return true;
//////		}
//////		
//////		matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
//////		logger.info("The sender id is 5 :" + matcher.toString());
//////		return matcher.matches() && true;
////////		if (matcher.matches()) {
////////			return true;
////////		}
//////		
////////		matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
////////		logger.info("The sender id is 6 :" + matcher.toString());
////////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//////		
//////	}
//////	//End
////	
////	
////	
////	
////
////	private Boolean checkDestinationNumber(MessageObject messageObject, Boolean continueMessageProcessing,
////			Boolean isMisRequired, NgUser user) {
////		String strDestAddr = messageObject.getDestNumber();
////		if (strDestAddr == null || strDestAddr.trim().length() < 10 || strDestAddr.trim().length() > 12 
////				|| (strDestAddr.length() == 12 && !strDestAddr.startsWith("91"))
////				|| (strDestAddr.length() == 11 && !strDestAddr.startsWith("0"))
////				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("1"))
////				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("2"))
////				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("3"))
////				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("4"))
////				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("5"))) {
////			{
////				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				messageObject.setErrorCode(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorCode());
////				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorDesc());
////				continueMessageProcessing = false;
////				isMisRequired = true;
////				return false;
////			}
////		} else {
////			strDestAddr = strDestAddr.replaceAll("\\+", "");
////			if(strDestAddr.startsWith("0")) {
////				strDestAddr = strDestAddr.replaceFirst("0","");
////			}
////			if (strDestAddr.length() == 10 && user.getIsIntlAllowed() == 'N') {
////				strDestAddr = "91"+strDestAddr;
////				messageObject.setDestNumber(strDestAddr);
////			} 
////		}
////		return true;
////	}
////
////	private boolean validateAndSetSenderId(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
////			Boolean isMisRequired) {
////		Integer idOfSenderId = 0;
////		if(messageObject.getSenderId() != null) {
////			messageObject.setOriginalSenderId(messageObject.getSenderId());
////			if(user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
////				if(idOfSenderId == null || idOfSenderId == 0) {
////					idOfSenderId = 9999;
////					messageObject.setIdOfSenderId(idOfSenderId);
////				}else {
////					messageObject.setIdOfSenderId(idOfSenderId);
////				} 
////				if(messageObject.getEntityId() == null)
////					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
////			}//else if(user.getIsDynamicSenderId() == 'N' && user.getIsShortCodeAllowed() =='N' ){  		//Due to mismatch with Production Class(Aman)
////			else if(user.getIsDynamicSenderId() == 'N') {
////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
////				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
////				if(idOfSenderId == null || idOfSenderId == 0) {
////					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService.getDefaultUserSenderIdByUserName(user.getUserName());
////					if(userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
////						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
////						idOfSenderId = userDefaultSenderId.getId();
////						messageObject.setIdOfSenderId(idOfSenderId);
////						messageObject.setSenderId(userDefaultSenderId.getSenderId());
////						if(messageObject.getEntityId() == null) {
////							messageObject.setEntityId(userDefaultSenderId.getEntityId());
////						}
////					}else {
////						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////						continueMessageProcessing = false;
////						isMisRequired = true;
////						return false;
////					}
////				}else {
////					messageObject.setIdOfSenderId(idOfSenderId);
////					if(messageObject.getEntityId() == null)
////						messageObject.setEntityId(entityId);
////				}
////			}
////			//Due to mismatch with Production Class (Aman)
////			//Start
////			
//////			else if(user.getIsShortCodeAllowed() == 'Y' && user.getReplaceShortCode() == 'Y'){
//////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//////				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
//////				String shortCode = staticDataService.getShortCodeFromSenderId(senderIdKey);
//////				if(shortCode != null) {
//////						messageObject.setIdOfSenderId(idOfSenderId);
//////						messageObject.setSenderId(shortCode);
//////						messageObject.setEntityId(null);
//////				}else {
//////					messageObject.setIdOfSenderId(idOfSenderId);
//////					if(messageObject.getEntityId() == null)
//////						messageObject.setEntityId(entityId);
//////				}
//////			}
////			
////			//End
////		}else {
////			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////			continueMessageProcessing = false;
////			isMisRequired = true;
////			return false;
////		}
////		logger.info("{} Final Id of Sender Id {} is: {}",messageObject.getMessageId(), messageObject.getSenderId(), idOfSenderId);
////		return true;
////	}
////
////	private boolean validateAndSetSenderIdForTransPromo(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
////			Boolean isMisRequired) {
////		Integer idOfSenderId = 0;
////		if(messageObject.getSenderId() != null) {
////			messageObject.setOriginalSenderId(messageObject.getSenderId());
////			if(user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
////				if(idOfSenderId == null || idOfSenderId == 0) {
////					idOfSenderId = 9999;
////					messageObject.setIdOfSenderId(idOfSenderId);
////				}else {
////					messageObject.setIdOfSenderId(idOfSenderId);
////				} 
////				if(messageObject.getEntityId() == null) {
////					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
////				}
////			}else if(user.getIsDynamicSenderId() == 'N'){
////				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
////				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
////				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
////				if(idOfSenderId == null || idOfSenderId == 0) {
////					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService.getDefaultUserSenderIdByUserName(user.getUserName());
////					if(userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
////						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
////						idOfSenderId = userDefaultSenderId.getId();
////						messageObject.setIdOfSenderId(idOfSenderId);
////						if(messageObject.getEntityId() == null) {
////							messageObject.setEntityId(userDefaultSenderId.getEntityId());
////						}
////						messageObject.setSenderId(userDefaultSenderId.getSenderId());
////					}else {
////						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////						continueMessageProcessing = false;
////						isMisRequired = true;
////						return false;
////					}
////				}else {
////					messageObject.setIdOfSenderId(idOfSenderId);
////					if(messageObject.getEntityId() == null)
////						messageObject.setEntityId(entityId);
////				}
////			}
////			if(!messageObject.isDndNumber() && !messageObject.isPremiumNumber()) {
////				List<NgUserSenderIdMap> userNonDndSenderIds = userSenderIdMapService.getNonDndDefaultUserSenderIdByUserName(user.getUserName());
////				if(userNonDndSenderIds != null && userNonDndSenderIds.size()>0) {
////					NgUserSenderIdMap userNonDndSenderId = userNonDndSenderIds.get(0);
////					idOfSenderId = userNonDndSenderId.getId();
////					messageObject.setIdOfSenderId(idOfSenderId);
////					messageObject.setSenderId(userNonDndSenderId.getSenderId());
////					if(messageObject.getEntityId() == null) {
////						messageObject.setEntityId(userNonDndSenderId.getEntityId());
////					}
////				}
////			}
////		}else {
////			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////			continueMessageProcessing = false;
////			isMisRequired = true;
////			return false;
////		}
////		logger.info("{} Final Id of Sender Id {} is: {}",messageObject.getMessageId(), messageObject.getSenderId(), idOfSenderId);
////		return true;
////	}
////	
////	private void populateRoutingId(MessageObject messageObject, NgUser user) {
////		Integer userId = messageObject.getUserId();
////		logger.info("{} MEssage service type is : ",messageObject.getMessageId(),messageObject.getMessageServiceType());
////		
////		Integer parentUserId = 0;
////		if(user.getParentId() != null) {
////			parentUserId = user.getParentId();
////		}
////		
////		Integer idOfSenderId = 0;
////		if(messageObject.getIdOfSenderId() != null)
////			idOfSenderId = messageObject.getIdOfSenderId(); 
////		
////		Integer circleId = 999;
////		Integer carrierId = 999;
////		String numSeries = null;
////		if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
////			numSeries = messageObject.getDestNumber().substring(2, 7);
////		}else {
////			numSeries = messageObject.getDestNumber().substring(0, 5);
////		}
////		String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
////		String carrierCircleData[] = carrierCircleInfo.split("#");
////		carrierId = Integer.parseInt(carrierCircleData[0]);
////		circleId = Integer.parseInt(carrierCircleData[1]);
////		
////		messageObject.setCarrierId(carrierId);
////		messageObject.setCircleId(circleId);
////		
////		Integer routingId = getRoutingId(userId, parentUserId, idOfSenderId, carrierId, circleId, user, messageObject);
////		if(routingId == null) {
////			String defaultRoutingInfoKey = "0#0#0#0#0";
////			logger.info("{} No routing id found. Hence going to fetch default routing id with key {} : ",messageObject.getMessageId(),defaultRoutingInfoKey);	
////			if(messageObject.getMessageServiceType().equals("trans")) {
////				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////			}else if(messageObject.getMessageServiceType().equals("promo")) {
////				routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
////			}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
////				routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
////			}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
////				routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
////			}else if(messageObject.getMessageServiceType().equals("others")) {
////				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
////			}
////		}
////		logger.error("{} Final routing id is {}: ",messageObject.getMessageId(), routingId);
////		messageObject.setRouteId(routingId);
////		messageObject.setGroupId(routingId);
////	}
//// 
////	private Integer getRoutingId(Integer userId, Integer parentUserId, Integer idOfSenderId, Integer carrierId,
////			Integer circleId, NgUser user, MessageObject messageObject) {
////		logger.info("{} Going to find routing id for user id {}, parent user id {}, idOfSenderId {}, carrier id {}, circle id{}",messageObject.getMessageId(), userId,  parentUserId,  idOfSenderId,  carrierId,  circleId);
////		Set<Object> routingTypeSet = staticDataService.getAllRoutingTypes();
////		Integer finalRoutingId = null;
////		for (Object routingTypeWithPriority : routingTypeSet) {
////			String consolidateRoutingType = null;
////			String finalRoutingKey = "0";
////			String routingTypeWithPri = (String) routingTypeWithPriority;
////			if (routingTypeWithPri.contains("#")) {
////				String[] routingTypeWithPriorityArray = routingTypeWithPri.split("#");
////				if (routingTypeWithPriorityArray != null && routingTypeWithPriorityArray.length > 0) {
////					consolidateRoutingType = routingTypeWithPriorityArray[0];
////					if (consolidateRoutingType != null) {
////						logger.info("{} 1. Consolidated routing type is:{} ",messageObject.getMessageId(),consolidateRoutingType);
////						if (consolidateRoutingType.contains("user")) {
////								logger.info("User keyword found in type. Adding user id in key", userId);
////								finalRoutingKey = Integer.toString(userId);
////						} else {
////							finalRoutingKey = "0";
////						}
////						
////						if (consolidateRoutingType.contains("parent")) {
////							finalRoutingKey = finalRoutingKey + "#" + parentUserId;
////						} else {
////							finalRoutingKey = finalRoutingKey + "#" + 0;
////						}
////
////						if (consolidateRoutingType.contains("carrier")) {
////							finalRoutingKey = finalRoutingKey + "#" + carrierId;
////						} else {
////							finalRoutingKey = finalRoutingKey + "#" + 0;
////						}
////					
////						if (consolidateRoutingType.contains("circle")) {
////							finalRoutingKey = finalRoutingKey + "#" + circleId;
////						} else {
////							finalRoutingKey = finalRoutingKey + "#" + 0;
////						}
////						
////						if (consolidateRoutingType.contains("senderid")) {
////							finalRoutingKey = finalRoutingKey + "#" + idOfSenderId;
////						} else {
////							finalRoutingKey = finalRoutingKey + "#" + 0;
////						}
////					}
////				}
////			}
////			logger.info("{} Going to find routing id for type {} with key {} : ",messageObject.getMessageId(),consolidateRoutingType, finalRoutingKey);
////			if(!messageObject.getMessageServiceType().equals("promo")) {
////				logger.info("{} Final routing key for message is: {}",messageObject.getMessageId(), finalRoutingKey);
////				finalRoutingId = staticDataService.getRoutingGroupForKeyForTrans(finalRoutingKey);
////			}else if(messageObject.getMessageServiceType().equals("promo")) {
////				finalRoutingId = staticDataService.getRoutingGroupForKeyForPromo(finalRoutingKey);
////			}else if(messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
////				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(finalRoutingKey);
////			}else if(messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
////				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(finalRoutingKey);
////			}
////			if (finalRoutingId != null) {
////				logger.info("{} Routing id found for key {} is {} : ",messageObject.getMessageId(),finalRoutingKey, finalRoutingId);
////				return finalRoutingId;
////			}
////		}
////		return null;
////	}
////
////	private String convertMessageObjectToJsonString(MessageObject messageObject) {
////		try {
////			return objectMapper.writeValueAsString(messageObject);
////		} catch (JsonProcessingException e) {
////			e.printStackTrace();
////		}
////		return null;
////	}
////
////	private boolean checkNumberInGlobalDndList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
////		if (messageObject.getDestNumber() != null) {
////			  boolean isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber());
////			  if(isNumberInDndList == false && messageObject.getDestNumber().startsWith("91")) {	
////				  isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber().substring(2));
////			  }
////			  logger.info("{} Is Number in Dnd List: {}",messageObject.getMessageId(),isNumberInDndList);
////			  if(isNumberInDndList && messageObject.getMessageServiceType().equals("service") && user.getIsDndAllowed() == 'Y') {
////				  logger.info("Mobile Number {} is in Global DnD List and DND message is allowed for user hence will be sent using DnD routing.",messageObject.getDestNumber());
////				  messageObject.setDndNumber(true);
////				  messageObject.setDndAllowed(true);
////				  continueMessageProcessing = true;
////				  return true;
////			  }else if(isNumberInDndList && user.getIsDndAllowed() == 'N') {
////				  logger.info("{} Mobile Number {} is in Global DnD List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getDestNumber());
////				  messageObject.setErrorCode(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////	
////	private boolean checkNumberInFailedNumbersList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
////		if(messageObject.getDestNumber()!=null) {
////			  boolean isNumberInFailedList = globalFailedNumberListService.isFailedNumber(messageObject.getDestNumber());
////			  if(isNumberInFailedList == false && messageObject.getDestNumber().startsWith("91")) {
////				  isNumberInFailedList = globalFailedNumberListService.isFailedNumber(messageObject.getDestNumber().substring(2));
////			  }
////			  logger.info("{} Is Number in FailedNumber List: {} ",messageObject.getMessageId(),isNumberInFailedList);
////			  if(isNumberInFailedList) {
////				  isMisRequired = true;
////				  messageObject.setErrorCode(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  return false;
////			  }
////		  }
////		return true;
////	}
////	// global blacklist start.......
////	private boolean checkNumberInGlobalBlackList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired,Set<String> phone) {
////		if(messageObject.getDestNumber()!=null) {
////			  boolean isNumberInGlobalBlackList = globalBlackListService.isBlackListNumber(messageObject.getDestNumber(), phone);
////			  logger.info("{} Is Number in Global Black List:{} ",messageObject.getMessageId(),isNumberInGlobalBlackList);
////			  if(isNumberInGlobalBlackList) {
////				  logger.info("{} Mobile Number {} is in Global Black List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getDestNumber());
////				  messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////	
////	private boolean checkSenderIdInGlobalSenderIdBlackList(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(messageObject.getSenderId() != null) {
////			  boolean isSenderIdInGlobalSenderIdBlackList = globalBlackListService.isBlackListSenderId(messageObject.getSenderId());
////			  logger.info("{} Is Sender Id in Global Black List:{} ",messageObject.getMessageId(),isSenderIdInGlobalSenderIdBlackList);
////			  if(isSenderIdInGlobalSenderIdBlackList) {
////				  logger.info("{} Sender Id {} is in Global Black List. Hence not sending the message and updating MIS.",messageObject.getMessageId(),messageObject.getSenderId());
////				  messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////
////	private boolean checkNumberInUserBlackList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(messageObject.getDestNumber()!=null) {
////			  boolean isNumberInUserBlackList = userBlackListService.isUserBlackListNumberFromDB(user, messageObject.getDestNumber());
////			  logger.info("{} Is Number in User Black List: {} ",messageObject.getMessageId(),isNumberInUserBlackList);
////			  if(isNumberInUserBlackList) {
////				  logger.info("Mobile Number {} is in User Black List. Hence not sending the message and updating MIS.",messageObject.getDestNumber());
////				  messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////	
////	private boolean checkSenderIdInUserSenderIdBlackList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(messageObject.getSenderId() != null) {
////			  boolean isSenderIdInUserSenderIdBlackList = userBlackListService.isUserSenderIdBlackList(user.getUserName(), messageObject.getSenderId());
////			  logger.info("{} Is Sender Id in User Black List: {}",messageObject.getMessageId(),isSenderIdInUserSenderIdBlackList);
////			  if(isSenderIdInUserSenderIdBlackList) {
////				  logger.info("Sender Id {} is in User Black List. Hence not sending the message and updating MIS.",messageObject.getDestNumber());
////				  messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////	
////	private boolean checkNumberInUserPremiumList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(messageObject.getDestNumber()!=null) {
////			  boolean isNumberInUserPremiumList = userPremiumListService.isUserPremiumListNumber(user.getUserName(), messageObject.getDestNumber());
////			  logger.info("{} Is Number in User Premium List: {}",messageObject.getMessageId(),isNumberInUserPremiumList);
////			  if(isNumberInUserPremiumList) {
////				  // logger.info("Mobile Number {} is in User Premium List. Hence not replacing sender id and applying premium routing.",messageObject.getDestNumber());
////				  logger.info("Mobile Number {} is in User Premium List.",messageObject.getDestNumber());
////				  return isNumberInUserPremiumList;
////				  // messageObject.setPremiumNumber(true);
////			  }else {
////				  // messageObject.setPremiumNumber(false);
////			  }
////		  }
////		return false;
////	}
////	
////	private boolean checkNumberInGlobalPremiumList(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(messageObject.getDestNumber()!=null) {
////			  boolean isNumberInGlobalPremiumList = globalPremiumListService.isGlobalPremiumListNumber(messageObject.getDestNumber());
////			  logger.info("{} Is Number in Global Premium List: {}",messageObject.getMessageId(),isNumberInGlobalPremiumList);
////			  if(isNumberInGlobalPremiumList) {
////				  logger.info("Mobile Number {} is in Global Premium List. Hence not replacing sender id and applying premium routing.",messageObject.getDestNumber());
////				  messageObject.setPremiumNumber(true);
////				  return isNumberInGlobalPremiumList;
////			  }else {
////				  messageObject.setPremiumNumber(false);
////			  }
////		  }
////		return false;
////	}
////	
////	private boolean filterRestrictedContent(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		Set<String> abusingWordsSet = abusingWordsListService.getUserAbusingWordsList(user.getUserName());
////		if(abusingWordsSet !=null && abusingWordsSet.size() > 0) {
////			  String message = messageObject.getMessage(); 
////			  logger.info("Message before restricted words : "+ message);
////			  List<String> abusingWordsInMessageList = Arrays.stream(message.split("(?=[.])|\\s+")).parallel().
////					  filter(t -> abusingWordsSet.contains(t.toLowerCase())).collect(Collectors.toList());
////			  
////			  if(abusingWordsInMessageList != null && abusingWordsInMessageList.size() > user.getAbusingWordsAllowedCount()) {
////				  logger.info("Abusing words ' {} ' found in the message. Hence not sending the message and updating MIS",abusingWordsInMessageList.toString());
////				  messageObject.setErrorCode(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorCode());
////				  messageObject.setErrorDesc(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorDesc());
////				  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////				  continueMessageProcessing = false;
////				  isMisRequired = true;
////				  return false;
////			  }
////		  }
////		return true;
////	}
////
////	private boolean checkCreditForPrepaidAccount(MessageObject messageObject, String userName, NgUser user, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		if(user != null && (user.getNgBillingType().getId() == 1)){
////			Integer userCredit = userCreditMapService.getUserCreditByUserNameFromRedis(userName);
////			logger.info("Available user credit before deduction from redis for user {} is {}",userName, userCredit);
////		    	if(userCredit != null &&  userCredit <= 0) {
////		    		logger.info("Insufficient Account Balance for user: "+user.getUserName());
////		    		messageObject.setErrorCode(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorCode());
////					messageObject.setErrorDesc(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorDesc());
////					messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////					continueMessageProcessing = false;
////					isMisRequired = true;
////					return false;
////		    	}
////		    }else {
////		    	logger.info("{} is a Postpaid User. Hence no need of credit check.",userName);
////		    }
////		return true;
////	}
////
////	private boolean applyDefaultSenderId(MessageObject messageObject, String userName, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
////		List<NgUserSenderIdMap> userSenderIdList = userSenderIdMapService.getDefaultUserSenderIdByUserName(userName);
////		if (userSenderIdList != null && userSenderIdList.size() > 0) {
////			messageObject.setSenderId(userSenderIdList.get(0).getSenderId());
////			messageObject.setIdOfSenderId(userSenderIdList.get(0).getId());
////			if (messageObject.getEntityId() == null) {
////				messageObject.setEntityId(userSenderIdList.get(0).getEntityId());
////			}
////		}else {
////			  logger.info("{} No valid sender id found for user. Hence skipping further processing",messageObject.getMessageId());
////			  messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
////			  messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
////			  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////			  continueMessageProcessing = false;
////			  isMisRequired = true;
////			  return false;
////		  }
////		  return true;
////	}
////
////	private boolean checkNineToNineMessage(List<String> messageList, int i, MessageObject messageObject, String userName, Boolean continueMessageProcessing, Boolean isMisRequired) {
////		Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("IST"));
////		  int currentHour = currTime.get(Calendar.HOUR_OF_DAY);
////		  if(!(currentHour >= 9 && currentHour < 21)) {
////			  logger.info("Promo message received post cut off time. Saving it for later delivery");
////			  NgPostCutoffMessage postCutoffMessage = new NgPostCutoffMessage();
////			  postCutoffMessage.setMessageId(messageObject.getMessageId());
////			  postCutoffMessage.setUserName(userName);
////			  postCutoffMessage.setMobileNumber(messageObject.getDestNumber());
////			  postCutoffMessage.setMessageObject(messageList.get(i));
////			  postCutoffMessageService.savePostCutoffMessage(postCutoffMessage);
////			  logger.info("Promo message received post cut off time. Saved it for later delivery. Hence skipping further processing");
////			  messageObject.setErrorCode(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorCode());
////			  messageObject.setErrorDesc(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorDesc());
////			  messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
////			  continueMessageProcessing = false;
////			  isMisRequired = true;
////			  return false;
////		  }
////		  return true;
////	}
////
////	private MessageObject convertReceivedJsonMessageIntoMessageObject(String messageObjectJsonString) {
////		MessageObject messageObject = null;
////		try {
////			messageObject = objectMapper.readValue(messageObjectJsonString, MessageObject.class);
////		} catch (Exception e){
////			logger.error("Dont retry this message as error while parsing json string: "+messageObjectJsonString);
////			e.printStackTrace();
////		} 
////		return messageObject;
////	}
////
////	public String convertMessageObjectToMisObjectAsString(MessageObject messageObject) throws UnsupportedEncodingException, DecoderException {
////		NgMisMessage ngMisMessage = new NgMisMessage();
////		ngMisMessage.setAckId(messageObject.getRequestId());
////		ngMisMessage.setCarrierId(messageObject.getCarrierId());
////		ngMisMessage.setCircleId(messageObject.getCircleId());
////		ngMisMessage.setErrorCode(messageObject.getErrorCode());
////		ngMisMessage.setErrorDesc(messageObject.getErrorDesc());
////		ngMisMessage.setFullMsg(messageObject.getFullMsg());
////		ngMisMessage.setGroupId(messageObject.getGroupId());
////		ngMisMessage.setKannelId(messageObject.getKannelId());
////		ngMisMessage.setMessageClass(messageObject.getMessageClass());
////		ngMisMessage.setMessageId(messageObject.getMessageId());
////		ngMisMessage.setMessageSource(messageObject.getInstanceId());
////		ngMisMessage.setMessageText(messageObject.getMessage());
////		ngMisMessage.setMessageType(messageObject.getMessageType());
////		ngMisMessage.setMobileNumber(messageObject.getDestNumber());
////		ngMisMessage.setOriginalMessageId(messageObject.getMessageId());
////		ngMisMessage.setPriority(messageObject.getPriority());
////		ngMisMessage.setReceivedTs(messageObject.getReceiveTime());
////		ngMisMessage.setRouteId(messageObject.getRouteId());
////		ngMisMessage.setSenderId(messageObject.getSenderId());
////		ngMisMessage.setSentTs(messageObject.getSentTime());
////		ngMisMessage.setSplitCount(messageObject.getSplitCount());
////		ngMisMessage.setStatus(messageObject.getStatus());
////		ngMisMessage.setUdh(messageObject.getUdh());
////		ngMisMessage.setUserId(messageObject.getUserId());
////		//ngMisMessage.setAckId(messageObject.getMessageId());
////		ngMisMessage.setFullMsg(messageObject.getMessage());
////		ngMisMessage.setCampaignId(messageObject.getCampaignId());
////		ngMisMessage.setCampaignName(messageObject.getCampaignName());
////		ngMisMessage.setCampaignTime(messageObject.getCampaignTime());
////		
////		//Commented due to mis-match with Production class
////		//Start
////		
//////		if(messageObject.getMessageType().equalsIgnoreCase("UC")) {
//////			String msgText = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()), "UTF-16BE");
//////			ngMisMessage.setFullMsg(msgText);
//////		}else{
//////	        	ngMisMessage.setFullMsg(messageObject.getMessage());
//////	    }
//////		
//////		if(messageObject.getCircleId() != null && messageObject.getCarrierId() != null){
//////			String carrierName = staticDataService.getCarrierNameById(messageObject.getCarrierId());
//////			String circleName = staticDataService.getCircleNameById(messageObject.getCircleId());
//////			
//////			ngMisMessage.setCarrierName(carrierName);
//////			ngMisMessage.setCircleName(circleName);
//////		}
////		
////		//End
////		
////		try {
////			return objectMapper.writeValueAsString(ngMisMessage);
////		} catch (JsonProcessingException e) {
////			e.printStackTrace();
////		}
////		return null;
////	}
////	
////	/**
////	 * @param args
////	 * @throws UnsupportedEncodingException
////	 */
////	public static void main(String[] args) throws UnsupportedEncodingException {
////		String strDestAddr = "123456";
////
////		String text = "12";
////
////		Matcher m = Pattern.compile("^\\d+$").matcher(text);
////		//Matcher m = Pattern.compile("^[a-zA-Z]*$").matcher(text);
////		System.out.println(m.matches());
////
////		/*
////		 * strDestAddr = strDestAddr.replaceAll("\\+", "");
////		 * if(strDestAddr.startsWith("0")) { strDestAddr =
////		 * strDestAddr.replaceFirst("0",""); } if (strDestAddr.length() == 10) {
////		 * strDestAddr = "91"+strDestAddr; } System.out.println(strDestAddr);
////		 * 
////		 * 
////		 * String n = "919953870879"; if(n.startsWith("91")){ }
////		 */
/////*
////		String patternTemplate = "Dear Customer {T} Your PTYM account is {T} is locked";
////		patternTemplate = patternTemplate.replace("{T}", "(.*)");
////		Pattern pattern = Pattern.compile(patternTemplate);
////		
////		String input = "Dear Customer Ashish Rastogi  Your PTYM account is 123456XYZ is locked";
////		
////		Matcher matcher = pattern.matcher(input);
////		if (matcher.matches()) {
////			System.out.println("Input matches with template");
////		}else {
////			System.out.println("Not matches");
////		}
////		
////*/	
////		/*String templateUnicode = "0031003000320020090f0902092c09320938002009380935093e002009090924092409300020092a093009260936002c00200020090f092c093209380020092800200031003200330020092b094b09280020092800200034003500200938092e092f0020002d00200036003700200932093f0915002000380039002000310030002009270928092f0935093e09260964";
////		String patternUnicode =  "003000300033003100300030003300300030003000330032003000300032003000300039003000460030003900300032003000390032004300300039003300320030003900330038003000300032003000300039003300380030003900330035003000390033004500300030003200300030003900300039003000390032003400300039003200340030003900330030003000300032003000300039003200410030003900330030003000390032003600300039003300360030003000320043003000300032003000300030003200300030003900300046003000390032004300300039003300320030003900330038003000300032003000300039003200380030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320042003000390034004200300039003200380030003000320030003000390032003800300030003200300030003000370042003000300032003300300030003700360030003000360031003000300037003200300030003200330030003000370044003000300032003000300039003300380030003900320045003000390032004600300030003200300030003000320044003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000390033003200300039003300460030003900310035003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320037003000390032003800300039003200460030003900330035003000390033004500300039003200360030003900360034";
////		
////		try {
////			templateUnicode = new String(Hex.decodeHex(templateUnicode.toCharArray()), "UTF-16BE");
////		} catch (DecoderException e) {
////			// 
////			e.printStackTrace();
////		}
////		System.out.println("templateUnicode : "+templateUnicode);*/
////		
////		String messageTextReceived = "";
////		String patternTemplate = "";
////		
////		/*messageTextReceived = templateUnicode;
////		patternTemplate = "102 एंबलस सवा उततर परदश,  एबलस न {#var#} फोन न {#var#} समय - {#var#} लिक {#var#} {#var#} धनयवाद।";
////		System.out.println("templateUnicode : "+templateUnicode);
////		System.out.println("ucMessage : "+patternTemplate);*/
////		//messageTextReceived = "Rs 9000.0 Credited in your Account for AEPS - CASH WITHDRAWAL against EASy order ID EASY01088460100";
////		//patternTemplate = "Rs {#var#} Debited from your Account for GPR CARD RELOAD against EASy order ID {#var#}";
////		
////		//messageTextReceived = "You have a new Account topup request for approval fro YA RAHMAN sdfs fs fsdTOUR AND TELECOM for amount. 25000.0";
////		//patternTemplate = "You have a new Account topup request for approval fro {#var#} for  amount\\. {#var#}";
////		
////		//messageTextReceived = "123 is One Time Password for .KCPrime a b c d e h Login Pleas do not share this with anyone Team KhelChamps Prime JruqUz7nWYa";
////		//patternTemplate = "{#var#} is One Time Password for \\.{#var#}\\. Please do not share this with anyone\\. Team KhelChamps Prime JruqUz7nWYa";
////		
////		/*messageTextReceived = "DEAL EXPIRY ALERT: 2 BHK 850 Sq.Ft. Apartment from Square Connect is about to expire and will be given to another Realtor.\r\n" + 
////				"  \r\n" + 
////				"Call NOW. Details here - https://xaw98.app.goo.gl/teFh";
////		patternTemplate = "{#var#} from Square Connect is about to expire and will be given to another Realtor\\. Call NOW\\. Details here \\- {#var#}";
////		*/
////		
////		//messageTextReceived = "Dear Customer, Your A/C 0004179 has been credited by Rs.10000.00 by CR. Available balance in your account is Rs.1633 as on 06-04-2021 10:45\r\nMORADABAD ZILA SAHKARI BANK LTD.";
////		//patternTemplate = "Dear Customer, Your A/C {#var#} has been credited by Rs\\.{#var#} by {#var#}\\. Available balance in your account is Rs\\.{#var#} as on {#var#}\r\n" + "MORADABAD ZILA SAHKARI BANK LTD\\.";
////		
////		/*patternTemplate = "Best Deal for Mobile Shop\r\n" + 
////				"\\*Billing Software\r\n" + 
////				"\\*Website\r\n" + 
////				"\\*Helpline Number\r\n" + 
////				"\\*Digital Promotion\r\n" + 
////				"\\*Facebook Promotion\r\n" + 
////				"24\\*7 Support\r\n" + 
////				"Call 9044887766\r\n" + 
////				"bit\\.ly/37Qq5M6";
////		
////		messageTextReceived = "Best Deal for Mobile Shop\r*Billing Software\r*Website\r*Helpline Number\r*Digital Promotion\r*Facebook Promotion\r24*7 Support\rCall 9044887766\rbit.ly/37Qq5M6";
////		*/
////		
////		/*patternTemplate = "Redy To Move\r\n" + 
////				"Area \\-{#var#}\r\n" + 
////				"Lease \\-{#var#}\r\n" + 
////				"Price \\-{#var#}\r\n" + 
////				"Invest with {#var#}\r\n" + 
////				"Call: {#var#}";
////		
////		messageTextReceived = "Redy To Move\\nArea -123\\nLease -asdfg\\nPrice -4566\\nInvest with rtyu\\nCall: ertyu";
////		*/
////
////		patternTemplate = "Hello ! (.*) units of (.*) blood required at (.*)\\. Patient: (.*), Contact: (.*) \\((.*)\\)\\. Please confirm whether you will be able to donate by sending (.*) \"space\" YES/NO to (.*)\\. Thanks, BloodMates";
////		messageTextReceived = "Hello ! 3 units of AB+ blood required at AMRI Test. Patient: Test Patient, Contact: Test Name (+911122334455). Please confirm whether you will be able to donate by sending JNC5FX \"space\" YES/NO to +918422874070. Thanks, BloodMates";
////		while(messageTextReceived.contains("  ")) {
////			messageTextReceived = messageTextReceived.replace("  "," ");
////		}
////		
////		while(patternTemplate.contains("  ")) {
////			patternTemplate = patternTemplate.replace("  "," ");
////		}
////		
////		/*if(patternTemplate.contains("\n")) {
////			patternTemplate = patternTemplate.replace("\n", "");
////		}
////		if(patternTemplate.contains("\r")) {
////			patternTemplate = patternTemplate.replace("\r", "");
////		}
////		if(messageTextReceived.contains("\n")) {
////			messageTextReceived = messageTextReceived.replace("\n", "");
////		}
////		if(messageTextReceived.contains("\\r") ) {
////			messageTextReceived = messageTextReceived.replace("\\r", "");
////		}*/
////		
////		patternTemplate = patternTemplate.replace("{#var#}", "(.*)");
////		
////		patternTemplate = patternTemplate.replaceAll("\\n", "");
////		patternTemplate = patternTemplate.replaceAll("\\r", "");
////		messageTextReceived = messageTextReceived.replaceAll("\\r", "");
////		messageTextReceived = messageTextReceived.replaceAll("\\n", "");
////		patternTemplate = patternTemplate.replace("\\n","");
////		patternTemplate = patternTemplate.replace("\n","");
////		patternTemplate = patternTemplate.replace("\\r","");
////		patternTemplate = patternTemplate.replace("\r","");
////		messageTextReceived = messageTextReceived.replace("\\r","");
////		messageTextReceived = messageTextReceived.replace("\r","");
////		messageTextReceived = messageTextReceived.replace("\\n","");
////		messageTextReceived = messageTextReceived.replace("\n","");
////		
////		System.out.println(messageTextReceived);
////		System.out.println(patternTemplate);
////		
////		/*Pattern pattern = Pattern.compile(patternTemplate);
////		Matcher matcher = pattern.matcher(messageTextReceived);
////		boolean matchResult = matcher.matches();
////		
////		System.out.println(matchResult);*/
////		
////		String[] messageTextArray = messageTextReceived.split(" ");
////        String[] patternTextArray = patternTemplate.split(" ");
////        boolean matchResult = false;
////        if(true) {
////			for (int i = 0, j= 0; i < patternTextArray.length && i<=j && j<messageTextArray.length;) {
////				String p = patternTextArray[i];
////				/*if(p.contains("(.*)")) {
////					p = p.replace(p,"(.*)");
////				}
////				System.out.println(p.length());*/
////				Pattern pattern = Pattern.compile(p);
////				Matcher matcher = pattern.matcher(messageTextArray[j]);
////				matchResult = matcher.matches();
////				System.out.println(patternTextArray[i] + " && "+ messageTextArray[j] +" && "+matchResult + " "+j + " "+i);
////				if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false && j < messageTextArray.length-1) {
////					j++;
////					messageTextArray[j] = messageTextArray[j-1]+messageTextArray[j];
////				}else if (i != 0 && patternTextArray[i-1].contains("(.*)") && matchResult == false) {
////					j++;
////				}
////				else if (matchResult == false) {
////					break;
////				}else {
////					i++;j++;
////				}
////			}
////			System.out.println(matchResult);
////        }	
////	}
////	
////	
////	public static String getHexStr(String _strObj) {
////		String hex = null;
////		if (_strObj != null) {
////			int decval = Integer.parseInt(_strObj.trim());
////			hex = Integer.toHexString(decval);
////		}
////		return hex;
////	}
////	
////	public String sendRejectedDlr(MessageObject messageObject) throws IOException {
////	/*	String dlrUrl = "http://localhost:8080/dlrlistener-0.0.1-SNAPSHOT/?dr=%a&smscid=%i&statuscd=%d&uniqID=%MSGID%&customerref=%USERNAME%"
////				+ "&receivetime=%RCVTIME%&dlrtype=9&mobile=%TO%&submittime=%SENTTIME%&expiry=12&senderid=%SENDERID%&carrierid=%CARRIERID%&circleid=%CIRCLEID%&routeid=%ROUTEID%&systemid=%SRCSYSTEM%"
////				+ "msgtxt=%MSGTXT%&currkannelid=%CURRKANNELID%&reqid=%REQID%&retrycount=%RETRYCOUNT%&usedkannelid=%USEDKANNELID%&ismultipart=%ISMULTIPART%&msgtype=%MSGTYPE%&splitcount=%SPLITCOUNT%";*/
////		NgKannelInfo ngKannelInfo = kannelInfoService.getDefaultKannelInfoForRejectedMessage();
////		
////		
////		String dlrListenerUrl = ngKannelInfo.getDlrUrl();
////		String dr = "id:%ID% sub:%ERRORCODE% dlvrd:%DLVRD% submit date:%SUBDATE% done date:%DONEDATE% stat:%STAT% err:%ERRORCODE% text:%TEXT%";
////		
////		if(messageObject.getErrorCode().equals(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode())) {
////			dr = dr.replaceAll("%ERRORCODE%", "882");
////			dr = dr.replaceAll("%DLVRD%", "001");
////			dr = dr.replaceAll("%STAT%", "FAILED");
////			messageObject.setErrorCode("");
////			messageObject.setErrorDesc("");
////			messageObject.setStatus("");
////		}else {
////			dr = dr.replaceAll("%ERRORCODE%", messageObject.getErrorCode());
////			dr = dr.replaceAll("%DLVRD%", "000");
////			dr = dr.replaceAll("%STAT%", "REJECTD");
////			
////		}
////		
////		dr = dr.replaceAll("%ID%", "" + System.currentTimeMillis() + messageObject.getDestNumber().substring(5, messageObject.getDestNumber().length()-1));
////		if(messageObject.getMessage()!= null && messageObject.getMessage().length() > 10) {
////			dr = dr.replaceAll("%TEXT%", messageObject.getMessage().substring(0,9));
////		}else {
////			dr = dr.replaceAll("%TEXT%", messageObject.getMessage());
////		}
////		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
////		String date = dateFormat.format(new Date());
////		dr = dr.replaceAll("%SUBDATE%", date);
////		dr = dr.replaceAll("%DONEDATE%", date);
////
////		
////		
////		dr = URLEncoder.encode(dr,"UTF-8");
////		
////		dlrListenerUrl = dlrListenerUrl.replaceFirst("%a",dr);
////		dlrListenerUrl = dlrListenerUrl.replaceFirst("smscid=%i", "smscid=000");
////		dlrListenerUrl = dlrListenerUrl.replaceFirst("statuscd=%d", "statuscd=000");
////			
////		if (messageObject.getMessageId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGID%", messageObject.getMessageId());
////
////		if (messageObject.getUsername() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%USERNAME%", messageObject.getUsername());
////
////		if(messageObject.getMessage() != null) 
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGTXT%", URLEncoder.encode(messageObject.getMessage(), "UTF-8"));
////		
////		dlrListenerUrl = dlrListenerUrl.replace("%CURRKANNELID%", ""+0);
////		
////		if(messageObject.getRequestId() != null) {
////			dlrListenerUrl = dlrListenerUrl.replace("%REQID%", URLEncoder.encode(messageObject.getRequestId(), "UTF-8"));
////		}
////		
////		Integer messageSplitCount = messageObject.getSplitCount();
////		if ((messageSplitCount != null && messageSplitCount > 1) || (messageObject.getIsMultiPart() != null && messageObject.getIsMultiPart().equals("Y"))) {
////			messageObject.setIsMultiPart("Y");
////			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());
////			
////			//Changes for Rejected DLR (In multipart case - Aman)
////			messageObject.setFullMsg(messageObject.getMessage());
////			//Integer pmSplitBytes = Integer.parseInt(plainMessageSplitBytes);
//////			String concatinatedMessageIds = messageObject.getRequestId();
//////			String[] messageIdsArray = concatinatedMessageIds.split("#");
////
//////			logger.info("***** Important Check. Dont Ignore ******");
//////			logger.info("Receiver Split Count {} and Total messages ids count {} ",
//////					messageObject.getSplitCount(), messageIdsArray.length);
//////			for (int j = 0; j < messageIdsArray.length; j++) {
//////				
//////				    messageObject.setMessageId(messageIdsArray[j]);
////				
////					saveMessageInMis(true, messageObject);
////
////				
//////			}
////			logger.info("{} DLR for rejected message sent with status : {}", messageObject.getMessageId());
////
////		}else {
////			messageObject.setIsMultiPart("N");
////			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());
////			saveMessageInMis(true, messageObject);
////		}
////		
////		if(messageObject.getUsedKannelIds() != null) {
////			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%", URLEncoder.encode(messageObject.getUsedKannelIds(), "UTF-8"));
////		}else {
////			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%", "0");
////		}
////		
////		if(messageObject.getMessageType()!=null) {
////			dlrListenerUrl = dlrListenerUrl.replace("%MSGTYPE%", messageObject.getMessageType());
////		}
////		
////		if(messageObject.getRetryCount() != null) {
////			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", ""+messageObject.getRetryCount());
////		}else {
////			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", "0");
////		}
////		 
////		if(messageObject.getSplitCount()!=null && messageObject.getSplitCount() > 1) {
////			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", ""+messageObject.getSplitCount());
////		}else {
////			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", "1");
////		}
////		
////		if(messageObject.getCustRef() != null) {
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", messageObject.getCustRef());
////		}else {
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", "");
////		}
////		
////		if (messageObject.getReceiveTime() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%RCVTIME%", Long.toString(messageObject.getReceiveTime().getTime()));
////
////		if (messageObject.getDestNumber() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%TO%", messageObject.getDestNumber());
//// 
////		messageObject.setSentTime(new java.sql.Timestamp(new Date().getTime()));
////		if (messageObject.getSentTime() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENTTIME%", Long.toString(messageObject.getSentTime().getTime()));
////
////		if(messageObject.getOriginalSenderId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getOriginalSenderId());
////		else if (messageObject.getSenderId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getSenderId());
////		
////		messageObject.setKannelId(0);
////		if (messageObject.getKannelId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%KANNELID%", messageObject.getKannelId().toString());
////
////		if (messageObject.getCarrierId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", messageObject.getCarrierId().toString());
////		else
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", "0");
////		
////		if (messageObject.getCircleId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", messageObject.getCircleId().toString());
////		else
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", "0");
////		
////		if (messageObject.getRouteId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", messageObject.getRouteId().toString());
////		else
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", ""+0);
////		
////		if (messageObject.getInstanceId() != null)
////			dlrListenerUrl = dlrListenerUrl.replaceAll("%SRCSYSTEM%", messageObject.getInstanceId());
////
////		logger.info("final dlr listener url for rejected message : " + dlrListenerUrl);
////		dlrListenerUrl = dlrListenerUrl.replaceAll("\\+", "%20");
////		dlrListenerUrl = dlrListenerUrl.replaceAll(" ", "%20");
////		
////		URL obj = new URL(dlrListenerUrl);
////		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
////		con.setRequestMethod("GET");
////		int responseCode = con.getResponseCode();
////		logger.info("Response from dlr listener received : " + responseCode);
////		
////		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
////		String inputLine;
////		StringBuffer response = new StringBuffer();
////
////		while ((inputLine = in.readLine()) != null) {
////			response.append(inputLine);
////		}
////		in.close();
////		logger.info("Response string from kannel is : ", response.toString());
////		return response.toString();
////	}
////
////	private boolean validateMessageTemplate(MessageObject messageObject, Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) throws UnsupportedEncodingException, DecoderException {
////		boolean templateMatched = false;
////		Set<String> templateTextListForSenderId = staticDataService.getTemplateTextListForUserAndSenderId(user.getId()+"#"+messageObject.getSenderId());
////		if(templateTextListForSenderId != null || (messageObject.getTemplateId() != null && messageObject.getTemplateId().length() > 0)) {
////			for (String patternTemplateAndDltTemplateId : templateTextListForSenderId) {
////                logger.info("Pattern from list: {}", patternTemplateAndDltTemplateId);
////                String[] tempArray = patternTemplateAndDltTemplateId.split("!!~~!!");
////                String patternTemplate = tempArray[0].replace("{#var#}", "(.*)");
////                String messageTextReceived = messageObject.getMessage();
////                if (messageObject.getMessageType().equalsIgnoreCase("UC")) {
////                    messageTextReceived = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()), "UTF-16BE");
////                }
////                while(messageTextReceived.contains("  ")) {
////        			messageTextReceived = messageTextReceived.replace("  "," ");
////        		}
////        		
////        		while(patternTemplate.contains("  ")) {
////        			patternTemplate = patternTemplate.replace("  "," ");
////        		}
////        		
////        		patternTemplate = patternTemplate.replaceAll("\\n", "");
////        		patternTemplate = patternTemplate.replaceAll("\\r", "");
////        		messageTextReceived = messageTextReceived.replaceAll("\\r", "");
////        		messageTextReceived = messageTextReceived.replaceAll("\\n", "");
////        		
////        		patternTemplate = patternTemplate.replace("\\n","");
////        		patternTemplate = patternTemplate.replace("\n","");
////        		patternTemplate = patternTemplate.replace("\\r","");
////        		patternTemplate = patternTemplate.replace("\r","");
////        		messageTextReceived = messageTextReceived.replace("\\r","");
////        		messageTextReceived = messageTextReceived.replace("\r","");
////        		messageTextReceived = messageTextReceived.replace("\\n","");
////        		messageTextReceived = messageTextReceived.replace("\n","");
////        		
////        		//logger.error("message id: {} messageTextReceived: {} and patternTemplate: {}",messageObject.getMessageId(), messageTextReceived, patternTemplate);
////        		
////                String[] messageTextArray = messageTextReceived.split(" ");
////                String[] patternTextArray = patternTemplate.split(" ");
////                boolean matchResult = false;
////                if(messageTextArray.length == patternTextArray.length) {
////                	try {
////						for (int i = 0; i < patternTextArray.length; i++) {
////							Pattern pattern = Pattern.compile(patternTextArray[i]);
////							Matcher matcher = pattern.matcher(messageTextArray[i]);
////							matchResult = matcher.matches();
////							if (matchResult == false) {
////								break;
////							}
////						}
////					} catch (Exception e) {
////						logger.error("Error occured while matching template 1:message {} and pattern {}",messageTextReceived, patternTemplate );
////						e.printStackTrace();
////						matchResult = false;
////					}
////				} else if(messageTextArray.length > patternTextArray.length) { 
////					try {
////						for (int i = 0, j= 0; i < patternTextArray.length && i <= j && j<messageTextArray.length;) {
////							Pattern pattern = Pattern.compile(patternTextArray[i]);
////							Matcher matcher = pattern.matcher(messageTextArray[j]);
////							matchResult = matcher.matches();
////							//System.out.println(patternTextArray[i] + " && "+ messageTextArray[j] +" && "+matchResult);
////							if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false && j < messageTextArray.length-1) {
////								j++;
////								messageTextArray[j] = messageTextArray[j-1] + messageTextArray[j];   //Check after this line After testing
////							}else if (i != 0 && patternTextArray[i-1].contains("(.*)") && matchResult == false) {
////								j++;
////							}
////							else if (matchResult == false) {
////								break;
////							}else {
////								i++;j++;
////							}
////						}
////					} catch (Exception e) {
////						logger.error("Error occured while matching template 2:message {} and pattern {}",messageTextReceived, patternTemplate );
////						e.printStackTrace();
////						matchResult = false;
////					}
////				}else {
////					matchResult = false;
////				}
////                logger.info("SenderId {} Template text is: {} ##and## message text is: {} and match result is :{}", (Object)messageObject.getSenderId(), (Object)patternTemplate, (Object)messageObject.getMessage(), (Object)matchResult);
////                if (matchResult) {
////                    templateMatched = true;
////                    if (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0) {
////                        messageObject.setMessageServiceType(tempArray[2]);
////                        messageObject.setTemplateId(tempArray[1]);
////                    }
////                    logger.info("Pattern matched {}. Going to return true. ", (Object)patternTemplateAndDltTemplateId);
////                    break;
////                }
////            }
////			if(!templateMatched) {
////				messageObject.setStatus(Constants.MESSAGE_STATUS_REJECTED);
////				messageObject.setErrorCode(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorCode());
////				messageObject.setErrorDesc(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorDesc());
////			
////				/*
////				 * try { sendRejectedDlr(messageObject); } catch (IOException e) {
////				 * logger.error("Error while sending DLT rejected DLR."); e.printStackTrace(); }
////				 */
////				continueMessageProcessing = false;
////				isMisRequired = true;
////				return false;
////			}
////		}
////		return true;
////	}
////	
////	/**
////	 * This method save the template in MIS & update the table in DB(ng_mis_curr_date)(Aman)
////	 * @param isMisRequired
////	 * @param messageObject
////	 */
////	private void saveMessageInMis(boolean isMisRequired, MessageObject messageObject) {
////		if (isMisRequired) {
////			// saveMessageInMis(messageObject);
////			String ngMisMessageObject;
////			try {
////				ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
////				if (ngMisMessageObject != null) {
////					messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
////				} else {
////					logger.error("Error while converting message object into DB Object with message Id {} ",
////							messageObject.getMessageId());
////				}
////			} catch (UnsupportedEncodingException | DecoderException e) {
////				logger.error("Error while converting message object into DB Object with message Id {} ",
////						messageObject.getMessageId());
////				e.printStackTrace();
////			}
////
////		}
////	}
//	
//	
//	
//	
//	
//	
//	
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

package com.noesis.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
//import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noesis.domain.constants.Constants;
import com.noesis.domain.constants.ErrorCodesEnum;
//import com.noesis.domain.persistence.NgGlobalBlackList;
import com.noesis.domain.persistence.NgKannelInfo;
import com.noesis.domain.persistence.NgMisMessage;
import com.noesis.domain.persistence.NgPostCutoffMessage;
import com.noesis.domain.persistence.NgUser;
import com.noesis.domain.persistence.NgUserPremiumList;
import com.noesis.domain.persistence.NgUserSenderIdMap;
import com.noesis.domain.platform.MessageObject;
//import com.noesis.domain.repository.GlobalBlackListRepository;
import com.noesis.domain.repository.KannelInfoRepository;
import com.noesis.domain.repository.MisRepository;
import com.noesis.domain.repository.UserPremiumListRepository;
import com.noesis.domain.service.AbusingWordsListService;
import com.noesis.domain.service.ErrorCodesService;
import com.noesis.domain.service.GlobalBlackListService;
import com.noesis.domain.service.GlobalDndListService;
import com.noesis.domain.service.GlobalFailedNumberListService;
import com.noesis.domain.service.GlobalPremiumListService;
import com.noesis.domain.service.KannelInfoService;
import com.noesis.domain.service.MisService;
import com.noesis.domain.service.PostCutoffMessageService;
import com.noesis.domain.service.PreferenceDndListService;
import com.noesis.domain.service.StaticDataService;
import com.noesis.domain.service.UserBlackListService;
import com.noesis.domain.service.UserCreditMapService;
import com.noesis.domain.service.UserPremiumListService;
import com.noesis.domain.service.UserSenderIdMapService;
import com.noesis.domain.service.UserService;
import com.noesis.producer.MessageSender;

public class MessageReceiver {

	private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

	private int maxPollRecordSize;

	private CountDownLatch latch = new CountDownLatch(maxPollRecordSize);

	@Autowired
	private StaticDataService staticDataService;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${app.name}")
	private String appName;

	@Value("${kafka.topic.name.mis.object}")
	private String kafKaDbObjectQueue;

	@Value("${kafka.consumer.sleep.interval.ms}")
	private String consumerSleepInterval;

	@Autowired
	private UserService userService;

	@Autowired
	private UserCreditMapService userCreditMapService;

	@Autowired
	private GlobalDndListService globalDndListService;

	@Autowired
	private GlobalFailedNumberListService globalFailedNumberListService;

	@Autowired
	private GlobalBlackListService globalBlackListService;
	@Autowired
    private UserPremiumListRepository userpremiumrepo;

	@Autowired
	private UserBlackListService userBlackListService;

	@Autowired
	private UserPremiumListService userPremiumListService;

	@Autowired
	GlobalPremiumListService globalPremiumListService;

	@Autowired
	private AbusingWordsListService abusingWordsListService;

	@Autowired
	private ErrorCodesService errorCodesService;

	@Autowired
	private UserSenderIdMapService userSenderIdMapService;

	@Autowired
	private PostCutoffMessageService postCutoffMessageService;

	@Autowired
	private MisService misService;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private KannelInfoService kannelInfoService;

	@Autowired
	private KannelInfoRepository kannelInfoRepository;

	@Autowired
	private MisRepository misRepository;

	@Autowired
	private PreferenceDndListService prefDndListService;



	public MessageReceiver(int maxPollRecordSize) {
		this.maxPollRecordSize = maxPollRecordSize;
	}

	/*
	 * @Value("${split.plain.message.bytes}") private String plainMessageSplitBytes;
	 * 
	 * @Value("${split.unicode.message.bytes}") private String unicodeSplitBytes;
	 */

	@KafkaListener(id = "${app.name}", topics = "${kafka.topic.name}", groupId = "${kafka.topic.name}", idIsGroup = false)
	public void receive1(List<String> messageList, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets) {

		logger.error("start of batch receive of size: " + messageList.size());



		for (int i = 0; i < messageList.size(); i++) {
			processMessage(messageList, partitions, offsets, i, latch);
		}
		logger.error("End of received batch.++");
		try {
			logger.error("Consumer Thread Going To Sleep for " + consumerSleepInterval + "ms.");
			Thread.sleep(Integer.parseInt(consumerSleepInterval));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processMessage(List<String> messageList, List<Integer> partitions, List<Long> offsets, int i,
			CountDownLatch latch) {
		Boolean continueMessageProcessing = true;
		Boolean isMisRequired = false;
		Boolean premiumNumber = false;
		
		logger.error("received message='{}' with partition-offset='{}'", messageList.get(i),
				partitions.get(i) + "-" + offsets.get(i));
		MessageObject messageObject = convertReceivedJsonMessageIntoMessageObject(messageList.get(i));
		try {
			if (messageObject != null) {
				String userName = messageObject.getUsername();
				NgUser user = userService.getUserByName(messageObject.getUsername());
				messageObject.setUserId(user.getId());

				// continueMessageProcessing = checkCreditForPrepaidAccount(messageObject,
				// userName, user, continueMessageProcessing, isMisRequired);

				// Commented when matching with Production Class
				// Start
//					if(continueMessageProcessing){         
//						if (continueMessageProcessing && (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0 || messageObject.getTemplateId().equalsIgnoreCase("others"))){
//							continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing, isMisRequired, user);
//							if(messageObject.getTemplateId() != null)
//								messageObject.setTemplateId(messageObject.getTemplateId().trim());
//							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
//								messageObject.setEntityId(messageObject.getEntityId().trim());
//							}
//						}else{
//							continueMessageProcessing = true;
//							if(messageObject.getTemplateId() != null)
//								messageObject.setTemplateId(messageObject.getTemplateId().trim());
//							if(messageObject.getEntityId() != null && messageObject.getEntityId().length() != 0){
//								messageObject.setEntityId(messageObject.getEntityId().trim());
//							}
//						}
//					}
				// End

				// Add-On new validation for Message Template
				if (continueMessageProcessing
						&& (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0
								|| messageObject.getTemplateId().equalsIgnoreCase("others")))
					continueMessageProcessing = validateMessageTemplate(messageObject, continueMessageProcessing,
							isMisRequired, user);

				if (continueMessageProcessing)
					continueMessageProcessing = checkSenderId(messageObject, userName, user, continueMessageProcessing,
							isMisRequired);

				// Check if number contains +,0 or 91 or valid mobile number
				if (continueMessageProcessing)
					continueMessageProcessing = checkDestinationNumber(messageObject, continueMessageProcessing,
							isMisRequired, user);

				// start
				if (continueMessageProcessing
						&& (messageObject.getMessageServiceType().equalsIgnoreCase("trans")
								|| messageObject.getMessageServiceType().equalsIgnoreCase("others")
								|| messageObject.getMessageServiceType().equalsIgnoreCase("promo")
								|| messageObject.getMessageServiceType().equalsIgnoreCase("service"))
						&& (user.getIsDndCheck() == 'Y')) {
					continueMessageProcessing = checkNumberInGlobalDndList(messageObject, continueMessageProcessing,
							isMisRequired, user);
				}

				// end

				// Check If Destination Number is in Preference DND
//				if (continueMessageProcessing && (user.getIsPreferenceCheck() == 'Y')) {
//					logger.info("Enter in Preference check block :" + user.getIsPreferenceCheck());
//					if (messageObject.getMessageServiceType().equals("promo")
//							|| messageObject.getMessageServiceType().equals("trans")
//							|| messageObject.getMessageServiceType().equals("service")
//							|| messageObject.getMessageServiceType().equals("others")) {
//						logger.info("Message service type is :" + messageObject.getMessageServiceType());
//
//						continueMessageProcessing = checkPreferenceDndList(messageObject, continueMessageProcessing,
//								isMisRequired, user);
//					}
//
//				}

				// Check If Destination Number is in failed database and user opted for this
				// check.
				if (continueMessageProcessing && user.getRejectFailedNumber() == 'Y')
					continueMessageProcessing = checkNumberInFailedNumbersList(messageObject, continueMessageProcessing,
							isMisRequired, user);
				/*
				 * //Check if number is in global black list. if(continueMessageProcessing)
				 * continueMessageProcessing = checkNumberInGlobalBlackList(messageObject,
				 * continueMessageProcessing, isMisRequired);
				 */

				// Check if number is in global black list.
				if (continueMessageProcessing) {
					logger.info("Going to chechk number in redis for premium");
					if (checkNumberInUserPremiumList(messageObject, user, continueMessageProcessing, isMisRequired)) {
						continueMessageProcessing = true;
						premiumNumber= true;
					} else if (!checkNumberInGlobalBlackList(messageObject, continueMessageProcessing, isMisRequired)) {
						continueMessageProcessing = false;
					}
				}

				// Check if number is in user black list.
				if (continueMessageProcessing && user.getIsBlackListAllowed() == 'Y')
					continueMessageProcessing = checkNumberInUserBlackList(messageObject, user,
							continueMessageProcessing, isMisRequired);

				// Check if sender id is in global sender id black list.
				if (continueMessageProcessing)
					continueMessageProcessing = checkSenderIdInGlobalSenderIdBlackList(messageObject,
							continueMessageProcessing, isMisRequired);

				// Check if sender is in user sender id black list.
				if (continueMessageProcessing)
					continueMessageProcessing = checkSenderIdInUserSenderIdBlackList(messageObject, user,
							continueMessageProcessing, isMisRequired);

				// Check if number is in user premium list.
				// if(continueMessageProcessing)
				// continueMessageProcessing = checkNumberInUserPremiumList(messageObject, user,
				// continueMessageProcessing, isMisRequired);

				// Check if number is in global premium list.
				if (continueMessageProcessing)
					checkNumberInGlobalPremiumList(messageObject, user, continueMessageProcessing, isMisRequired);

				// Content Filtering.
				if (continueMessageProcessing)
					continueMessageProcessing = filterRestrictedContent(messageObject, user, continueMessageProcessing,
							isMisRequired);

				// Sender Id Validation and Replace Sender Id.
				if (continueMessageProcessing && !(messageObject.getMessageServiceType().equals("promo"))) {
					continueMessageProcessing = validateAndSetSenderId(messageObject, user, continueMessageProcessing,
							isMisRequired);
				} else if (continueMessageProcessing && messageObject.getMessageServiceType().equals("promo")) {
					continueMessageProcessing = validateAndSetSenderIdForTransPromo(messageObject, user,
							continueMessageProcessing, isMisRequired);
				} else if (continueMessageProcessing) {
					continueMessageProcessing = applyDefaultSenderId(messageObject, userName, continueMessageProcessing,
							isMisRequired, user);
				}

				// 9-9 Timing Check.
				if (continueMessageProcessing && messageObject.getMessageServiceType().equals("promo")) {
					continueMessageProcessing = checkNineToNineMessage(messageList, i, messageObject, userName,
							continueMessageProcessing, isMisRequired);
					if (!continueMessageProcessing) {
						isMisRequired = false;
					}
				}

				// Populate routing id.
				if ( continueMessageProcessing) {
					if (premiumNumber) {
						selfRouting(messageObject, user);

						// End
					} else if (messageObject.isDndAllowed()) {
						Integer routingId = null;
						String defaultRoutingInfoKey = "-2#-2#-2#-2#-2";
						logger.info("{} DND Number Found and hence going to find routing id with key {} : ",
								messageObject.getMessageId(), defaultRoutingInfoKey);
						if (messageObject.isDndAllowed()) {
							logger.info("DND Number found in DND List :" + messageObject.isDndAllowed());
							routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
						}
						messageObject.setRouteId(routingId);
						messageObject.setGroupId(routingId);
					}else if (messageObject.isPremiumNumber()) {
						Integer routingId = null;
						String defaultRoutingInfoKey = "-1#-1#-1#-1#-1";
						logger.info("{} Premium Number Found and hence going to find premium routing id with key {} : ",
								messageObject.getMessageId(), defaultRoutingInfoKey);
						if (messageObject.getMessageServiceType().equals("trans")) {
							routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
						} else if (messageObject.getMessageServiceType().equals("promo")) {
							routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
						} else if (messageObject.getMessageServiceType().equals("service")
								&& messageObject.isDndNumber()) {
							routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
						} else if (messageObject.getMessageServiceType().equals("service")
								&& !messageObject.isDndNumber()) {
							routingId = staticDataService
									.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
						} else if (messageObject.getMessageServiceType().equals("others")) {
							routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
						}
						messageObject.setRouteId(routingId);
						messageObject.setGroupId(routingId);

						// Commented when match with Production class (Aman)
						// Start

//							Integer circleId = 999;
//							Integer carrierId = 999;
//							String numSeries = null;
//							if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
//								numSeries = messageObject.getDestNumber().substring(2, 7);
//							}else {
//								numSeries = messageObject.getDestNumber().substring(0, 5);
//							}
//							String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
//							String carrierCircleData[] = carrierCircleInfo.split("#");
//							carrierId = Integer.parseInt(carrierCircleData[0]);
//							circleId = Integer.parseInt(carrierCircleData[1]);
//							
//							messageObject.setCarrierId(carrierId);
//							messageObject.setCircleId(circleId);

						// End

						
						
					} 
					
					else {
						populateRoutingId(messageObject, user);
					}

				}

				// Send message to submitter.
				if (continueMessageProcessing) {
					String messageObjectAsString = convertMessageObjectToJsonString(messageObject);
					String submitterQueueName = staticDataService
							.getSubmitterQueueForGroupId(Integer.toString(messageObject.getRouteId()));
					messageSender.send(submitterQueueName, messageObjectAsString);
				}

				// Dump Message into DB for MIS.
				if (isMisRequired || !continueMessageProcessing) {
					// saveMessageInMis(messageObject);

//						String ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
//						if (ngMisMessageObject != null) {
//							messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
//						} else {
//							logger.error("Error while converting message object into DB Object with message Id {} ",
//									messageObject.getMessageId());
//						}

					// TODO: Send dummy DLR for platform rejected messages.
					// Changes for Rejected DLR (In multipart case - Aman)

					String concatinatedMessageIds = messageObject.getRequestId();
					String[] messageIdsArray = concatinatedMessageIds.split("#");

					for (int j = 0; j < messageIdsArray.length; j++) {
						messageObject.setMessageId(messageIdsArray[j]);
						String status = sendRejectedDlr(messageObject);
						logger.info("{} DLR for rejected message sent with status : {}", messageObject.getMessageId(),
								status);
					}

				}

			}
		} catch (Exception e) {
			logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",
					messageObject.getMessageId() + messageList.get(i));
			e.printStackTrace();
			messageObject.setErrorCode("UNKNOWN_ERROR_OCCURED");
			messageObject.setErrorDesc("Unknown error occured while processing message request");
			String ngMisMessageObject;
			try {
				ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
				messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
			} catch (UnsupportedEncodingException | DecoderException e1) {
				logger.error("{} Exception occured while processing message. Hence skipping this message: {} ",
						messageObject.getMessageId() + messageList.get(i));
				e1.printStackTrace();
			}

		}
		latch.countDown();
	}
	
	// redis new
	
	
	// redis new

	private Boolean checkSenderId(MessageObject messageObject, String userName, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		logger.info("{} Message service type {} and number {} and shortcode {}", messageObject.getMessageId(),
				messageObject.getMessageServiceType(), messageObject.getDestNumber(), user.getIsShortCodeAllowed());

//		if(messageObject.getSenderId() != null && (messageObject.getSenderId().length() < 3 || messageObject.getSenderId().length() > 14)) {
//				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//				continueMessageProcessing = false;
//				isMisRequired = true;
//				return false;
//		}else 

		// Commented
		{
			if ((messageObject.getMessageServiceType().equals("trans")
					|| messageObject.getMessageServiceType().equals("service")
					|| messageObject.getMessageServiceType().equals("others"))) {
				Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());
				if (matcher.matches()
						&& (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
					return true;
				} else {
					messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
					continueMessageProcessing = false;
					isMisRequired = true;
					return false;
				}
			} else if (messageObject.getMessageServiceType().equals("promo")) {
				Matcher m1 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
				if (m1.matches()) {
					return true;
				} else {
					messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
					messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
					messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
					continueMessageProcessing = false;
					isMisRequired = true;
					return false;
				}
			} else {
				logger.info("{} Message service type received others.", messageObject.getMessageId());
				Matcher m3 = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(messageObject.getSenderId());

				if (m3.matches()
						&& (messageObject.getSenderId().length() >= 3 || messageObject.getSenderId().length() <= 14)) {
					messageObject.setMessageServiceType("trans");
					return true;
				}
				m3 = Pattern.compile("\\d{3,14}").matcher(messageObject.getSenderId());
				if (m3.matches()) {
					messageObject.setMessageServiceType("promo");
					return true;
				}
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		// Commented End

//		String senderId = messageObject.getSenderId();
//		boolean isValidSenderId = false;
//		
//		if (messageObject.getMessageServiceType().equals("trans") || messageObject.getMessageServiceType().equals("service") || messageObject.getMessageServiceType().equals("others")) {
//			isValidSenderId = validateAlphabeticSenderId(senderId);
//		}else if (messageObject.getMessageServiceType().equals("promo")) {
//			isValidSenderId = validateNumericSenderId(senderId);
//		}
//		
////		else if (messageObject.getMessageServiceType().equals("others")) {
////			isValidSenderId = validateAlphanumericSenderId(senderId);
////		}
//		
//		else {
//			isValidSenderId = validateMixedSenderId(senderId);
//		}
//		
//		
//		if (!isValidSenderId) {
//			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
//			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
//			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
//			continueMessageProcessing = false;
//			isMisRequired = true;
//		}
//		
//		return isValidSenderId;

	}

	// Sender Id validation Methods
//	private boolean validateAlphabeticSenderId(String senderId) {
//		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
//		logger.info("The sender id is 1 :" + matcher.toString());
//		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//	}
//	
//	private boolean validateNumericSenderId(String senderId) {
//		Matcher matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
//		logger.info("The sender id is 2 :" + matcher.toString());
//		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//	}
//	
////	private boolean validateAlphanumericSenderId(String senderId) {
////		Matcher matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
////		logger.info("The sender id is 3 :" + matcher.toString());
////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
////	}
//	
//	private boolean validateMixedSenderId(String senderId) {
//		Matcher matcher = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$").matcher(senderId);
//		logger.info("The sender id is 4 :" + matcher.toString());
//		if (matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14)) {
//			return true;
//		}
//		
//		matcher = Pattern.compile("\\d{3,14}").matcher(senderId);
//		logger.info("The sender id is 5 :" + matcher.toString());
//		return matcher.matches() && true;
////		if (matcher.matches()) {
////			return true;
////		}
//		
////		matcher = Pattern.compile("^[a-zA-Z0-9]+$").matcher(senderId);
////		logger.info("The sender id is 6 :" + matcher.toString());
////		return matcher.matches() && (senderId.length() >= 3 && senderId.length() <= 14);
//		
//	}
//	//End

	private Boolean checkDestinationNumber(MessageObject messageObject, Boolean continueMessageProcessing,
			Boolean isMisRequired, NgUser user) {
		String strDestAddr = messageObject.getDestNumber();
		if (strDestAddr == null || strDestAddr.trim().length() < 10 || strDestAddr.trim().length() > 12
				|| (strDestAddr.length() == 12 && !strDestAddr.startsWith("91"))
				|| (strDestAddr.length() == 11 && !strDestAddr.startsWith("0"))
				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("1"))
				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("2"))
				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("3"))
				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("4"))
				|| (strDestAddr.length() == 10 && strDestAddr.startsWith("5"))) {
			{
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				messageObject.setErrorCode(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.INVALID_DESTINATION_NUMBER.getErrorDesc());
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		} else {
			strDestAddr = strDestAddr.replaceAll("\\+", "");
			if (strDestAddr.startsWith("0")) {
				strDestAddr = strDestAddr.replaceFirst("0", "");
			}
			if (strDestAddr.length() == 10) {
				strDestAddr = "91" + strDestAddr;
				messageObject.setDestNumber(strDestAddr);
			}
		}
		return true;
	}

	private boolean validateAndSetSenderId(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
			Boolean isMisRequired) {
		Integer idOfSenderId = 0;
		if (messageObject.getSenderId() != null) {
			messageObject.setOriginalSenderId(messageObject.getSenderId());
			if (user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
				String senderIdKey = user.getId() + "#" + messageObject.getSenderId();
				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
				if (idOfSenderId == null || idOfSenderId == 0) {
					idOfSenderId = 9999;
					messageObject.setIdOfSenderId(idOfSenderId);
				} else {
					messageObject.setIdOfSenderId(idOfSenderId);
				}
				if (messageObject.getEntityId() == null)
					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
			} // else if(user.getIsDynamicSenderId() == 'N' && user.getIsShortCodeAllowed()
				// =='N' ){ //Due to mismatch with Production Class(Aman)
			else if (user.getIsDynamicSenderId() == 'N') {
				String senderIdKey = user.getId() + "#" + messageObject.getSenderId();
				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
				if (idOfSenderId == null || idOfSenderId == 0) {
					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService
							.getDefaultUserSenderIdByUserName(user.getUserName());
					if (userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
						idOfSenderId = userDefaultSenderId.getId();
						messageObject.setIdOfSenderId(idOfSenderId);
						messageObject.setSenderId(userDefaultSenderId.getSenderId());
						if (messageObject.getEntityId() == null) {
							messageObject.setEntityId(userDefaultSenderId.getEntityId());
						}
					} else {
						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
						continueMessageProcessing = false;
						isMisRequired = true;
						return false;
					}
				} else {
					messageObject.setIdOfSenderId(idOfSenderId);
					if (messageObject.getEntityId() == null)
						messageObject.setEntityId(entityId);
				}
			}
			// Due to mismatch with Production Class (Aman)
			// Start

//			else if(user.getIsShortCodeAllowed() == 'Y' && user.getReplaceShortCode() == 'Y'){
//				String senderIdKey = user.getId()+"#"+messageObject.getSenderId();
//				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
//				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
//				String shortCode = staticDataService.getShortCodeFromSenderId(senderIdKey);
//				if(shortCode != null) {
//						messageObject.setIdOfSenderId(idOfSenderId);
//						messageObject.setSenderId(shortCode);
//						messageObject.setEntityId(null);
//				}else {
//					messageObject.setIdOfSenderId(idOfSenderId);
//					if(messageObject.getEntityId() == null)
//						messageObject.setEntityId(entityId);
//				}
//			}

			// End
		} else {
			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
			continueMessageProcessing = false;
			isMisRequired = true;
			return false;
		}
		logger.info("{} Final Id of Sender Id {} is: {}", messageObject.getMessageId(), messageObject.getSenderId(),
				idOfSenderId);
		return true;
	}

	private boolean validateAndSetSenderIdForTransPromo(MessageObject messageObject, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		Integer idOfSenderId = 0;
		if (messageObject.getSenderId() != null) {
			messageObject.setOriginalSenderId(messageObject.getSenderId());
			if (user.getIsDynamicSenderId() == 'Y' || messageObject.isPremiumNumber()) {
				String senderIdKey = user.getId() + "#" + messageObject.getSenderId();
				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
				if (idOfSenderId == null || idOfSenderId == 0) {
					idOfSenderId = 9999;
					messageObject.setIdOfSenderId(idOfSenderId);
				} else {
					messageObject.setIdOfSenderId(idOfSenderId);
				}
				if (messageObject.getEntityId() == null) {
					messageObject.setEntityId(staticDataService.getEntityIdFromSenderId(senderIdKey));
				}
			} else if (user.getIsDynamicSenderId() == 'N') {
				String senderIdKey = user.getId() + "#" + messageObject.getSenderId();
				idOfSenderId = staticDataService.getIdFromSenderId(senderIdKey);
				String entityId = staticDataService.getEntityIdFromSenderId(senderIdKey);
				if (idOfSenderId == null || idOfSenderId == 0) {
					List<NgUserSenderIdMap> userDefaultAndActiveSenderIds = userSenderIdMapService
							.getDefaultUserSenderIdByUserName(user.getUserName());
					if (userDefaultAndActiveSenderIds != null && userDefaultAndActiveSenderIds.size() > 0) {
						NgUserSenderIdMap userDefaultSenderId = userDefaultAndActiveSenderIds.get(0);
						idOfSenderId = userDefaultSenderId.getId();
						messageObject.setIdOfSenderId(idOfSenderId);
						if (messageObject.getEntityId() == null) {
							messageObject.setEntityId(userDefaultSenderId.getEntityId());
						}
						messageObject.setSenderId(userDefaultSenderId.getSenderId());
					} else {
						messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
						messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
						messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
						continueMessageProcessing = false;
						isMisRequired = true;
						return false;
					}
				} else {
					messageObject.setIdOfSenderId(idOfSenderId);
					if (messageObject.getEntityId() == null)
						messageObject.setEntityId(entityId);
				}
			}
			if (!messageObject.isDndNumber() && !messageObject.isPremiumNumber()) {
				List<NgUserSenderIdMap> userNonDndSenderIds = userSenderIdMapService
						.getNonDndDefaultUserSenderIdByUserName(user.getUserName());
				if (userNonDndSenderIds != null && userNonDndSenderIds.size() > 0) {
					NgUserSenderIdMap userNonDndSenderId = userNonDndSenderIds.get(0);
					idOfSenderId = userNonDndSenderId.getId();
					messageObject.setIdOfSenderId(idOfSenderId);
					messageObject.setSenderId(userNonDndSenderId.getSenderId());
					if (messageObject.getEntityId() == null) {
						messageObject.setEntityId(userNonDndSenderId.getEntityId());
					}
				}
			}
		} else {
			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
			continueMessageProcessing = false;
			isMisRequired = true;
			return false;
		}
		logger.info("{} Final Id of Sender Id {} is: {}", messageObject.getMessageId(), messageObject.getSenderId(),
				idOfSenderId);
		return true;
	}

	private void populateRoutingId(MessageObject messageObject, NgUser user) {
		Integer userId = messageObject.getUserId();
		logger.info("{} MEssage service type is : ", messageObject.getMessageId(),
				messageObject.getMessageServiceType());

		Integer parentUserId = 0;
		if (user.getParentId() != null) {
			parentUserId = user.getParentId();
		}

		Integer idOfSenderId = 0;
		if (messageObject.getIdOfSenderId() != null)
			idOfSenderId = messageObject.getIdOfSenderId();

		Integer circleId = 999;
		Integer carrierId = 999;
		String numSeries = null;

		if (messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
			numSeries = messageObject.getDestNumber().substring(2, 7);
		} else {
			numSeries = messageObject.getDestNumber().substring(0, 5);
		}

		String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
		String carrierCircleData[] = carrierCircleInfo.split("#");
		carrierId = Integer.parseInt(carrierCircleData[0]);
		circleId = Integer.parseInt(carrierCircleData[1]);

		messageObject.setCarrierId(carrierId);
		messageObject.setCircleId(circleId);

		Integer routingId = getRoutingId(userId, parentUserId, idOfSenderId, carrierId, circleId, user, messageObject);
		if (routingId == null) {
			String defaultRoutingInfoKey = "0#0#0#0#0";
			logger.info("{} No routing id found. Hence going to fetch default routing id with key {} : ",
					messageObject.getMessageId(), defaultRoutingInfoKey);
			if (messageObject.getMessageServiceType().equals("trans")) {
				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
			} else if (messageObject.getMessageServiceType().equals("promo")) {
				routingId = staticDataService.getRoutingGroupForKeyForPromo(defaultRoutingInfoKey);
			} else if (messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
				routingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(defaultRoutingInfoKey);
			} else if (messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
				routingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(defaultRoutingInfoKey);
			} else if (messageObject.getMessageServiceType().equals("others")) {
				routingId = staticDataService.getRoutingGroupForKeyForTrans(defaultRoutingInfoKey);
			}
		}
		logger.error("{} Final routing id is {}: ", messageObject.getMessageId(), routingId);
		messageObject.setRouteId(routingId);
		messageObject.setGroupId(routingId);
	}

	private Integer getRoutingId(Integer userId, Integer parentUserId, Integer idOfSenderId, Integer carrierId,
			Integer circleId, NgUser user, MessageObject messageObject) {
		logger.info(
				"{} Going to find routing id for user id {}, parent user id {}, idOfSenderId {}, carrier id {}, circle id {} ",
				messageObject.getMessageId(), userId, parentUserId, idOfSenderId, carrierId, circleId);
		Set<Object> routingTypeSet = staticDataService.getAllRoutingTypes();

		Integer finalRoutingId = null;
		for (Object routingTypeWithPriority : routingTypeSet) {
			String consolidateRoutingType = null;
			String finalRoutingKey = "0";
			String routingTypeWithPri = (String) routingTypeWithPriority;
			if (routingTypeWithPri.contains("#")) {
				String[] routingTypeWithPriorityArray = routingTypeWithPri.split("#");
				if (routingTypeWithPriorityArray != null && routingTypeWithPriorityArray.length > 0) {
					consolidateRoutingType = routingTypeWithPriorityArray[0];
					if (consolidateRoutingType != null) {
						logger.info("{} 1. Consolidated routing type is:{} ", messageObject.getMessageId(),
								consolidateRoutingType);
						if (consolidateRoutingType.contains("user")) {
							logger.info("User keyword found in type. Adding user id in key", userId);
							finalRoutingKey = Integer.toString(userId);
						} else {
							finalRoutingKey = "0";
						}

						if (consolidateRoutingType.contains("parent")) {
							finalRoutingKey = finalRoutingKey + "#" + parentUserId;
						} else {
							finalRoutingKey = finalRoutingKey + "#" + 0;
						}

						if (consolidateRoutingType.contains("carrier")) {
							finalRoutingKey = finalRoutingKey + "#" + carrierId;
						} else {
							finalRoutingKey = finalRoutingKey + "#" + 0;
						}

						if (consolidateRoutingType.contains("circle")) {
							finalRoutingKey = finalRoutingKey + "#" + circleId;
						} else {
							finalRoutingKey = finalRoutingKey + "#" + 0;
						}

						if (consolidateRoutingType.contains("senderid")) {
							finalRoutingKey = finalRoutingKey + "#" + idOfSenderId;
						} else {
							finalRoutingKey = finalRoutingKey + "#" + 0;
						}
					}
				}
			}
			logger.info("{} Going to find routing id for type {} with key {} : ", messageObject.getMessageId(),
					consolidateRoutingType, finalRoutingKey);
			if (!messageObject.getMessageServiceType().equals("promo")) {
				logger.info("{} Final routing key for message is: {}", messageObject.getMessageId(), finalRoutingKey);
				finalRoutingId = staticDataService.getRoutingGroupForKeyForTrans(finalRoutingKey);
			} else if (messageObject.getMessageServiceType().equals("promo")) {
				finalRoutingId = staticDataService.getRoutingGroupForKeyForPromo(finalRoutingKey);
			} else if (messageObject.getMessageServiceType().equals("service") && messageObject.isDndNumber()) {
				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoDnd(finalRoutingKey);
			} else if (messageObject.getMessageServiceType().equals("service") && !messageObject.isDndNumber()) {
				finalRoutingId = staticDataService.getRoutingGroupForKeyForTransPromoNonDnd(finalRoutingKey);
			}
			if (finalRoutingId != null) {
				logger.info("{} Routing id found for key {} is {} : ", messageObject.getMessageId(), finalRoutingKey,
						finalRoutingId);
				return finalRoutingId;
			}
		}
		return null;
	}

	private String convertMessageObjectToJsonString(MessageObject messageObject) {
		try {
			return objectMapper.writeValueAsString(messageObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkNumberInGlobalDndList(MessageObject messageObject, Boolean continueMessageProcessing,
			Boolean isMisRequired, NgUser user) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber());
			boolean isNumberInPreferenceList = prefDndListService.isPrefDndNumber(messageObject.getDestNumber());
			if (isNumberInDndList == false && messageObject.getDestNumber().startsWith("91")) {
				isNumberInDndList = globalDndListService.isDndNumber(messageObject.getDestNumber().substring(2));
			}
			logger.info("{} Is Number in Dnd List: {}", messageObject.getMessageId(), isNumberInDndList);
			if (isNumberInPreferenceList
					&& (messageObject.getMessageServiceType().equals("promo")
							|| messageObject.getMessageServiceType().equals("trans")
							|| messageObject.getMessageServiceType().equals("service")
							|| messageObject.getMessageServiceType().equals("others"))
					&& user.getIsDndAllowed() == 'Y') {
				logger.info(
						"Mobile Number {} is in Preference DnD List and DND message is allowed for user hence will be sent using Preference DnD routing.",
						messageObject.getDestNumber());
				messageObject.setDndNumber(true);
				messageObject.setDndAllowed(true);
				continueMessageProcessing = true;
				return true;
			} else if (isNumberInDndList && user.getIsDndAllowed() == 'N') {
				logger.info(
						"{} Mobile Number {} is in Global DnD List. Hence not sending the message and updating MIS.",
						messageObject.getMessageId(), messageObject.getDestNumber());
				messageObject.setErrorCode(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.DND_NUMBER_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	// Check Mark for Preference DND List
//	private boolean checkPreferenceDndList(MessageObject messageObject, Boolean continueMessageProcessing,
//			Boolean isMisRequired, NgUser ngUser) {
//		logger.info("Destination number will be :" + messageObject.getDestNumber() + "and preference check will be :"
//				+ ngUser.getIsPreferenceCheck());
//		if (messageObject.getDestNumber() != null) {
//			boolean isNumberInPreferenceList = prefDndListService.isPrefDndNumber(messageObject.getDestNumber());
//			logger.info("{} Is Number in Preference DND List: {}", messageObject.getMessageId(),
//					isNumberInPreferenceList);
//			if (isNumberInPreferenceList) {
//				logger.info(
//						"Mobile Number {} is in Preference DnD List and DND message is allowed for user hence will be sent using Preference DnD routing.",
//						messageObject.getDestNumber());
//				messageObject.setPreferenceDnd(true);
//				continueMessageProcessing = true;
//				return true;
//			} else {
//				messageObject.setPreferenceDnd(false);
//			}
//		}
//		return true;
//	}

	private boolean checkNumberInFailedNumbersList(MessageObject messageObject, Boolean continueMessageProcessing,
			Boolean isMisRequired, NgUser user) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInFailedList = globalFailedNumberListService.isFailedNumber(messageObject.getDestNumber());
			if (isNumberInFailedList == false && messageObject.getDestNumber().startsWith("91")) {
				isNumberInFailedList = globalFailedNumberListService
						.isFailedNumber(messageObject.getDestNumber().substring(2));
			}
			logger.info("{} Is Number in FailedNumber List: {} ", messageObject.getMessageId(), isNumberInFailedList);
			if (isNumberInFailedList) {
				isMisRequired = true;
				messageObject.setErrorCode(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				return false;
			}
		}
		return true;
	}
	
	// new method
		public void selfRouting(MessageObject messageObject, NgUser user) {
			
			try {
				//System.out.println("Setting circle id  ****************************************************");
				Integer circleId = 999;
				Integer carrierId = 999;
				String numSeries = null;
				if(messageObject.getDestNumber() != null && messageObject.getDestNumber().startsWith("91")) {
					numSeries = messageObject.getDestNumber().substring(2, 7);
				}else {
					numSeries = messageObject.getDestNumber().substring(0, 5);
				}
				String carrierCircleInfo = staticDataService.getCarrierCircleForSeries(numSeries);
				String carrierCircleData[] = carrierCircleInfo.split("#");
				carrierId = Integer.parseInt(carrierCircleData[0]);
				circleId = Integer.parseInt(carrierCircleData[1]);
				
				messageObject.setCarrierId(carrierId);
				messageObject.setCircleId(circleId);
				System.out.println("Circle Id   ===  *******************************" + circleId + carrierId);
				// end
	
				NgUserPremiumList findBypremiumNumber = userpremiumrepo.findBypremiumNumber(messageObject.getDestNumber(),messageObject.getUserId());
				System.out.println("findBypremiumNumberfetched successfully....");
				if (findBypremiumNumber == null) {
					System.out.println("number is null********************************************************************");
					
				}else {
					System.out.println("findBypremiumNumber is NOT NULL");
				}
				//System.out.println("GROUP ID =>\t"+findBypremiumNumber.getGroupId() != null ? findBypremiumNumber.getGroupId() : -1);
				
				messageObject.setRouteId(findBypremiumNumber.getGroupId());
				messageObject.setGroupId(findBypremiumNumber.getGroupId());
				
				
				
				System.out.println("MessagObject routeid => "+messageObject.getRouteId() != null ? messageObject.getRouteId() : -1);
				System.out.println("MessageObject group id => "+messageObject.getGroupId() != null ? messageObject.getGroupId() : -1);
				
			} catch (Exception e) {
				System.out.println("INSIDE CATCH BLOCK => EXCEPTION -> "+e.getMessage());
				e.printStackTrace();
			}
			
			System.out.println("##########METHOD EXECUTED GRACEFULLY##########");
				}
		// end new method
	
	
	

	// global blacklist start.......
	private boolean checkNumberInGlobalBlackList(MessageObject messageObject, Boolean continueMessageProcessing,
			Boolean isMisRequired) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInGlobalBlackList = globalBlackListService.isBlackListNumber(messageObject.getDestNumber());
			//boolean isNumberInGlobalBlackList = CheckNumberFromRedis(messageObject.getDestNumber());
			logger.info("{} Is Number in Global Black List:{} ", messageObject.getMessageId(),
					isNumberInGlobalBlackList);
			if (isNumberInGlobalBlackList) {
				logger.info(
						"{} Mobile Number {} is in Global Black List. Hence not sending the message and updating MIS.",
						messageObject.getMessageId(), messageObject.getDestNumber());
				messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	private boolean checkSenderIdInGlobalSenderIdBlackList(MessageObject messageObject,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (messageObject.getSenderId() != null) {
			boolean isSenderIdInGlobalSenderIdBlackList = globalBlackListService
					.isBlackListSenderId(messageObject.getSenderId());
			logger.info("{} Is Sender Id in Global Black List:{} ", messageObject.getMessageId(),
					isSenderIdInGlobalSenderIdBlackList);
			if (isSenderIdInGlobalSenderIdBlackList) {
				logger.info("{} Sender Id {} is in Global Black List. Hence not sending the message and updating MIS.",
						messageObject.getMessageId(), messageObject.getSenderId());
				messageObject.setErrorCode(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.GLOBAL_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	private boolean checkNumberInUserBlackList(MessageObject messageObject, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInUserBlackList = userBlackListService.isUserBlackListNumberFromDB(user,
					messageObject.getDestNumber());
			logger.info("{} Is Number in User Black List: {} ", messageObject.getMessageId(), isNumberInUserBlackList);
			if (isNumberInUserBlackList) {
				logger.info("Mobile Number {} is in User Black List. Hence not sending the message and updating MIS.",
						messageObject.getDestNumber());
				messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_NUMBER_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	private boolean checkSenderIdInUserSenderIdBlackList(MessageObject messageObject, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (messageObject.getSenderId() != null) {
			boolean isSenderIdInUserSenderIdBlackList = userBlackListService.isUserSenderIdBlackList(user.getUserName(),
					messageObject.getSenderId());
			logger.info("{} Is Sender Id in User Black List: {}", messageObject.getMessageId(),
					isSenderIdInUserSenderIdBlackList);
			if (isSenderIdInUserSenderIdBlackList) {
				logger.info("Sender Id {} is in User Black List. Hence not sending the message and updating MIS.",
						messageObject.getDestNumber());
				messageObject.setErrorCode(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.USER_BLACK_LIST_SENDER_ID_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	private boolean checkNumberInUserPremiumList(MessageObject messageObject, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInUserPremiumList = userPremiumListService.isUserPremiumListNumber(user.getUserName(),
					messageObject.getDestNumber());
			logger.info("{} Is Number in User Premium List: {}", messageObject.getMessageId(),
					isNumberInUserPremiumList);
			if (isNumberInUserPremiumList) {
				// logger.info("Mobile Number {} is in User Premium List. Hence not replacing
				// sender id and applying premium routing.",messageObject.getDestNumber());
				logger.info("Mobile Number {} is in User Premium List.", messageObject.getDestNumber());
				return isNumberInUserPremiumList;
				// messageObject.setPremiumNumber(true);
			} else {
				// messageObject.setPremiumNumber(false);
			}
		}
		return false;
	}

	private boolean checkNumberInGlobalPremiumList(MessageObject messageObject, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (messageObject.getDestNumber() != null) {
			boolean isNumberInGlobalPremiumList = globalPremiumListService
					.isGlobalPremiumListNumber(messageObject.getDestNumber());
			logger.info("{} Is Number in Global Premium List: {}", messageObject.getMessageId(),
					isNumberInGlobalPremiumList);
			if (isNumberInGlobalPremiumList) {
				logger.info(
						"Mobile Number {} is in Global Premium List. Hence not replacing sender id and applying premium routing.",
						messageObject.getDestNumber());
				messageObject.setPremiumNumber(true);
				return isNumberInGlobalPremiumList;
			} else {
				messageObject.setPremiumNumber(false);
			}
		}
		return false;
	}

	private boolean filterRestrictedContent(MessageObject messageObject, NgUser user, Boolean continueMessageProcessing,
			Boolean isMisRequired) {
		Set<String> abusingWordsSet = abusingWordsListService.getUserAbusingWordsList(user.getUserName());
		if (abusingWordsSet != null && abusingWordsSet.size() > 0) {
			String message = messageObject.getMessage();
			logger.info("Message before restricted words : " + message);
			List<String> abusingWordsInMessageList = Arrays.stream(message.split("(?=[.])|\\s+")).parallel()
					.filter(t -> abusingWordsSet.contains(t.toLowerCase())).collect(Collectors.toList());

			if (abusingWordsInMessageList != null
					&& abusingWordsInMessageList.size() > user.getAbusingWordsAllowedCount()) {
				logger.info("Abusing words ' {} ' found in the message. Hence not sending the message and updating MIS",
						abusingWordsInMessageList.toString());
				messageObject.setErrorCode(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.RESTRICTED_CONTENT_FOUND.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	private boolean checkCreditForPrepaidAccount(MessageObject messageObject, String userName, NgUser user,
			Boolean continueMessageProcessing, Boolean isMisRequired) {
		if (user != null && (user.getNgBillingType().getId() == 1)) {
			Integer userCredit = userCreditMapService.getUserCreditByUserNameFromRedis(userName);
			logger.info("Available user credit before deduction from redis for user {} is {}", userName, userCredit);
			if (userCredit != null && userCredit <= 0) {
				logger.info("Insufficient Account Balance for user: " + user.getUserName());
				messageObject.setErrorCode(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.INSUFFICIENT_BALANCE.getErrorDesc());
				messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		} else {
			logger.info("{} is a Postpaid User. Hence no need of credit check.", userName);
		}
		return true;
	}

	private boolean applyDefaultSenderId(MessageObject messageObject, String userName,
			Boolean continueMessageProcessing, Boolean isMisRequired, NgUser user) {
		List<NgUserSenderIdMap> userSenderIdList = userSenderIdMapService.getDefaultUserSenderIdByUserName(userName);
		if (userSenderIdList != null && userSenderIdList.size() > 0) {
			messageObject.setSenderId(userSenderIdList.get(0).getSenderId());
			messageObject.setIdOfSenderId(userSenderIdList.get(0).getId());
			if (messageObject.getEntityId() == null) {
				messageObject.setEntityId(userSenderIdList.get(0).getEntityId());
			}
		} else {
			logger.info("{} No valid sender id found for user. Hence skipping further processing",
					messageObject.getMessageId());
			messageObject.setErrorCode(ErrorCodesEnum.INVALID_SENDER_ID.getErrorCode());
			messageObject.setErrorDesc(ErrorCodesEnum.INVALID_SENDER_ID.getErrorDesc());
			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
			continueMessageProcessing = false;
			isMisRequired = true;
			return false;
		}
		return true;
	}

	private boolean checkNineToNineMessage(List<String> messageList, int i, MessageObject messageObject,
			String userName, Boolean continueMessageProcessing, Boolean isMisRequired) {
		Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		int currentHour = currTime.get(Calendar.HOUR_OF_DAY);
		if (!(currentHour >= 9 && currentHour < 21)) {
			logger.info("Promo message received post cut off time. Saving it for later delivery");
			NgPostCutoffMessage postCutoffMessage = new NgPostCutoffMessage();
			postCutoffMessage.setMessageId(messageObject.getMessageId());
			postCutoffMessage.setUserName(userName);
			postCutoffMessage.setMobileNumber(messageObject.getDestNumber());
			postCutoffMessage.setMessageObject(messageList.get(i));
			postCutoffMessageService.savePostCutoffMessage(postCutoffMessage);
			logger.info(
					"Promo message received post cut off time. Saved it for later delivery. Hence skipping further processing");
			messageObject.setErrorCode(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorCode());
			messageObject.setErrorDesc(ErrorCodesEnum.MESSAGE_RECEIVED_POST_CUT_OFF.getErrorDesc());
			messageObject.setStatus(Constants.MESSAGE_STATUS_FAILED);
			continueMessageProcessing = false;
			isMisRequired = true;
			return false;
		}
		return true;
	}

	private MessageObject convertReceivedJsonMessageIntoMessageObject(String messageObjectJsonString) {
		MessageObject messageObject = null;
		try {
			messageObject = objectMapper.readValue(messageObjectJsonString, MessageObject.class);
		} catch (Exception e) {
			logger.error("Dont retry this message as error while parsing json string: " + messageObjectJsonString);
			e.printStackTrace();
		}
		return messageObject;
	}

	public String convertMessageObjectToMisObjectAsString(MessageObject messageObject)
			throws UnsupportedEncodingException, DecoderException {
		NgMisMessage ngMisMessage = new NgMisMessage();
		ngMisMessage.setAckId(messageObject.getRequestId());
		ngMisMessage.setCarrierId(messageObject.getCarrierId());
		ngMisMessage.setCircleId(messageObject.getCircleId());
		ngMisMessage.setErrorCode(messageObject.getErrorCode());
		ngMisMessage.setErrorDesc(messageObject.getErrorDesc());
		ngMisMessage.setFullMsg(messageObject.getFullMsg());
		ngMisMessage.setGroupId(messageObject.getGroupId());
		ngMisMessage.setKannelId(messageObject.getKannelId());
		ngMisMessage.setMessageClass(messageObject.getMessageClass());
		ngMisMessage.setMessageId(messageObject.getMessageId());
		ngMisMessage.setMessageSource(messageObject.getInstanceId());
		ngMisMessage.setMessageText(messageObject.getMessage());
		ngMisMessage.setMessageType(messageObject.getMessageType());
		ngMisMessage.setMobileNumber(messageObject.getDestNumber());
		ngMisMessage.setOriginalMessageId(messageObject.getMessageId());
		ngMisMessage.setPriority(messageObject.getPriority());
		ngMisMessage.setReceivedTs(messageObject.getReceiveTime());
		ngMisMessage.setRouteId(messageObject.getRouteId());
		ngMisMessage.setSenderId(messageObject.getSenderId());
		ngMisMessage.setSentTs(messageObject.getSentTime());
		ngMisMessage.setSplitCount(messageObject.getSplitCount());
		ngMisMessage.setStatus(messageObject.getStatus());
		ngMisMessage.setUdh(messageObject.getUdh());
		ngMisMessage.setUserId(messageObject.getUserId());
		// ngMisMessage.setAckId(messageObject.getMessageId());
		ngMisMessage.setFullMsg(messageObject.getMessage());
		ngMisMessage.setCampaignId(messageObject.getCampaignId());
		ngMisMessage.setCampaignName(messageObject.getCampaignName());
		ngMisMessage.setCampaignTime(messageObject.getCampaignTime());

		// Commented due to mis-match with Production class
		// Start

//		if(messageObject.getMessageType().equalsIgnoreCase("UC")) {
//			String msgText = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()), "UTF-16BE");
//			ngMisMessage.setFullMsg(msgText);
//		}else{
//	        	ngMisMessage.setFullMsg(messageObject.getMessage());
//	    }
//		
//		if(messageObject.getCircleId() != null && messageObject.getCarrierId() != null){
//			String carrierName = staticDataService.getCarrierNameById(messageObject.getCarrierId());
//			String circleName = staticDataService.getCircleNameById(messageObject.getCircleId());
//			
//			ngMisMessage.setCarrierName(carrierName);
//			ngMisMessage.setCircleName(circleName);
//		}

		// End

		try {
			return objectMapper.writeValueAsString(ngMisMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		String strDestAddr = "123456";

		String text = "12";

		Matcher m = Pattern.compile("^\\d+$").matcher(text);
		// Matcher m = Pattern.compile("^[a-zA-Z]*$").matcher(text);
		System.out.println(m.matches());

		/*
		 * strDestAddr = strDestAddr.replaceAll("\\+", "");
		 * if(strDestAddr.startsWith("0")) { strDestAddr =
		 * strDestAddr.replaceFirst("0",""); } if (strDestAddr.length() == 10) {
		 * strDestAddr = "91"+strDestAddr; } System.out.println(strDestAddr);
		 * 
		 * 
		 * String n = "919953870879"; if(n.startsWith("91")){ }
		 */
		/*
		 * String patternTemplate =
		 * "Dear Customer {T} Your PTYM account is {T} is locked"; patternTemplate =
		 * patternTemplate.replace("{T}", "(.*)"); Pattern pattern =
		 * Pattern.compile(patternTemplate);
		 * 
		 * String input =
		 * "Dear Customer Ashish Rastogi  Your PTYM account is 123456XYZ is locked";
		 * 
		 * Matcher matcher = pattern.matcher(input); if (matcher.matches()) {
		 * System.out.println("Input matches with template"); }else {
		 * System.out.println("Not matches"); }
		 * 
		 */
		/*
		 * String templateUnicode =
		 * "0031003000320020090f0902092c09320938002009380935093e002009090924092409300020092a093009260936002c00200020090f092c093209380020092800200031003200330020092b094b09280020092800200034003500200938092e092f0020002d00200036003700200932093f0915002000380039002000310030002009270928092f0935093e09260964";
		 * String patternUnicode =
		 * "003000300033003100300030003300300030003000330032003000300032003000300039003000460030003900300032003000390032004300300039003300320030003900330038003000300032003000300039003300380030003900330035003000390033004500300030003200300030003900300039003000390032003400300039003200340030003900330030003000300032003000300039003200410030003900330030003000390032003600300039003300360030003000320043003000300032003000300030003200300030003900300046003000390032004300300039003300320030003900330038003000300032003000300039003200380030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320042003000390034004200300039003200380030003000320030003000390032003800300030003200300030003000370042003000300032003300300030003700360030003000360031003000300037003200300030003200330030003000370044003000300032003000300039003300380030003900320045003000390032004600300030003200300030003000320044003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000390033003200300039003300460030003900310035003000300032003000300030003700420030003000320033003000300037003600300030003600310030003000370032003000300032003300300030003700440030003000320030003000300037004200300030003200330030003000370036003000300036003100300030003700320030003000320033003000300037004400300030003200300030003900320037003000390032003800300039003200460030003900330035003000390033004500300039003200360030003900360034";
		 * 
		 * try { templateUnicode = new
		 * String(Hex.decodeHex(templateUnicode.toCharArray()), "UTF-16BE"); } catch
		 * (DecoderException e) { // e.printStackTrace(); }
		 * System.out.println("templateUnicode : "+templateUnicode);
		 */

		String messageTextReceived = "";
		String patternTemplate = "";

		/*
		 * messageTextReceived = templateUnicode; patternTemplate =
		 * "102 एंबलस सवा उततर परदश,  एबलस न {#var#} फोन न {#var#} समय - {#var#} लिक {#var#} {#var#} धनयवाद।"
		 * ; System.out.println("templateUnicode : "+templateUnicode);
		 * System.out.println("ucMessage : "+patternTemplate);
		 */
		// messageTextReceived = "Rs 9000.0 Credited in your Account for AEPS - CASH
		// WITHDRAWAL against EASy order ID EASY01088460100";
		// patternTemplate = "Rs {#var#} Debited from your Account for GPR CARD RELOAD
		// against EASy order ID {#var#}";

		// messageTextReceived = "You have a new Account topup request for approval fro
		// YA RAHMAN sdfs fs fsdTOUR AND TELECOM for amount. 25000.0";
		// patternTemplate = "You have a new Account topup request for approval fro
		// {#var#} for amount\\. {#var#}";

		// messageTextReceived = "123 is One Time Password for .KCPrime a b c d e h
		// Login Pleas do not share this with anyone Team KhelChamps Prime JruqUz7nWYa";
		// patternTemplate = "{#var#} is One Time Password for \\.{#var#}\\. Please do
		// not share this with anyone\\. Team KhelChamps Prime JruqUz7nWYa";

		/*
		 * messageTextReceived =
		 * "DEAL EXPIRY ALERT: 2 BHK 850 Sq.Ft. Apartment from Square Connect is about to expire and will be given to another Realtor.\r\n"
		 * + "  \r\n" + "Call NOW. Details here - https://xaw98.app.goo.gl/teFh";
		 * patternTemplate =
		 * "{#var#} from Square Connect is about to expire and will be given to another Realtor\\. Call NOW\\. Details here \\- {#var#}"
		 * ;
		 */

		// messageTextReceived = "Dear Customer, Your A/C 0004179 has been credited by
		// Rs.10000.00 by CR. Available balance in your account is Rs.1633 as on
		// 06-04-2021 10:45\r\nMORADABAD ZILA SAHKARI BANK LTD.";
		// patternTemplate = "Dear Customer, Your A/C {#var#} has been credited by
		// Rs\\.{#var#} by {#var#}\\. Available balance in your account is Rs\\.{#var#}
		// as on {#var#}\r\n" + "MORADABAD ZILA SAHKARI BANK LTD\\.";

		/*
		 * patternTemplate = "Best Deal for Mobile Shop\r\n" + "\\*Billing Software\r\n"
		 * + "\\*Website\r\n" + "\\*Helpline Number\r\n" + "\\*Digital Promotion\r\n" +
		 * "\\*Facebook Promotion\r\n" + "24\\*7 Support\r\n" + "Call 9044887766\r\n" +
		 * "bit\\.ly/37Qq5M6";
		 * 
		 * messageTextReceived =
		 * "Best Deal for Mobile Shop\r*Billing Software\r*Website\r*Helpline Number\r*Digital Promotion\r*Facebook Promotion\r24*7 Support\rCall 9044887766\rbit.ly/37Qq5M6"
		 * ;
		 */

		/*
		 * patternTemplate = "Redy To Move\r\n" + "Area \\-{#var#}\r\n" +
		 * "Lease \\-{#var#}\r\n" + "Price \\-{#var#}\r\n" + "Invest with {#var#}\r\n" +
		 * "Call: {#var#}";
		 * 
		 * messageTextReceived =
		 * "Redy To Move\\nArea -123\\nLease -asdfg\\nPrice -4566\\nInvest with rtyu\\nCall: ertyu"
		 * ;
		 */

		patternTemplate = "Hello ! (.*) units of (.*) blood required at (.*)\\. Patient: (.*), Contact: (.*) \\((.*)\\)\\. Please confirm whether you will be able to donate by sending (.*) \"space\" YES/NO to (.*)\\. Thanks, BloodMates";
		messageTextReceived = "Hello ! 3 units of AB+ blood required at AMRI Test. Patient: Test Patient, Contact: Test Name (+911122334455). Please confirm whether you will be able to donate by sending JNC5FX \"space\" YES/NO to +918422874070. Thanks, BloodMates";
		while (messageTextReceived.contains("  ")) {
			messageTextReceived = messageTextReceived.replace("  ", " ");
		}

		while (patternTemplate.contains("  ")) {
			patternTemplate = patternTemplate.replace("  ", " ");
		}

		/*
		 * if(patternTemplate.contains("\n")) { patternTemplate =
		 * patternTemplate.replace("\n", ""); } if(patternTemplate.contains("\r")) {
		 * patternTemplate = patternTemplate.replace("\r", ""); }
		 * if(messageTextReceived.contains("\n")) { messageTextReceived =
		 * messageTextReceived.replace("\n", ""); }
		 * if(messageTextReceived.contains("\\r") ) { messageTextReceived =
		 * messageTextReceived.replace("\\r", ""); }
		 */

		patternTemplate = patternTemplate.replace("{#var#}", "(.*)");

		patternTemplate = patternTemplate.replaceAll("\\n", "");
		patternTemplate = patternTemplate.replaceAll("\\r", "");
		messageTextReceived = messageTextReceived.replaceAll("\\r", "");
		messageTextReceived = messageTextReceived.replaceAll("\\n", "");
		patternTemplate = patternTemplate.replace("\\n", "");
		patternTemplate = patternTemplate.replace("\n", "");
		patternTemplate = patternTemplate.replace("\\r", "");
		patternTemplate = patternTemplate.replace("\r", "");
		messageTextReceived = messageTextReceived.replace("\\r", "");
		messageTextReceived = messageTextReceived.replace("\r", "");
		messageTextReceived = messageTextReceived.replace("\\n", "");
		messageTextReceived = messageTextReceived.replace("\n", "");

		System.out.println(messageTextReceived);
		System.out.println(patternTemplate);

		/*
		 * Pattern pattern = Pattern.compile(patternTemplate); Matcher matcher =
		 * pattern.matcher(messageTextReceived); boolean matchResult =
		 * matcher.matches();
		 * 
		 * System.out.println(matchResult);
		 */

		String[] messageTextArray = messageTextReceived.split(" ");
		String[] patternTextArray = patternTemplate.split(" ");
		boolean matchResult = false;
		if (true) {
			for (int i = 0, j = 0; i < patternTextArray.length && i <= j && j < messageTextArray.length;) {
				String p = patternTextArray[i];
				/*
				 * if(p.contains("(.*)")) { p = p.replace(p,"(.*)"); }
				 * System.out.println(p.length());
				 */
				Pattern pattern = Pattern.compile(p);
				Matcher matcher = pattern.matcher(messageTextArray[j]);
				matchResult = matcher.matches();
				System.out.println(
						patternTextArray[i] + " && " + messageTextArray[j] + " && " + matchResult + " " + j + " " + i);
				if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false
						&& j < messageTextArray.length - 1) {
					j++;
					messageTextArray[j] = messageTextArray[j - 1] + messageTextArray[j];
				} else if (i != 0 && patternTextArray[i - 1].contains("(.*)") && matchResult == false) {
					j++;
				} else if (matchResult == false) {
					break;
				} else {
					i++;
					j++;
				}
			}
			System.out.println(matchResult);
		}
	}

	public static String getHexStr(String _strObj) {
		String hex = null;
		if (_strObj != null) {
			int decval = Integer.parseInt(_strObj.trim());
			hex = Integer.toHexString(decval);
		}
		return hex;
	}

	public String sendRejectedDlr(MessageObject messageObject) throws IOException {
		/*
		 * String dlrUrl =
		 * "http://localhost:8080/dlrlistener-0.0.1-SNAPSHOT/?dr=%a&smscid=%i&statuscd=%d&uniqID=%MSGID%&customerref=%USERNAME%"
		 * +
		 * "&receivetime=%RCVTIME%&dlrtype=9&mobile=%TO%&submittime=%SENTTIME%&expiry=12&senderid=%SENDERID%&carrierid=%CARRIERID%&circleid=%CIRCLEID%&routeid=%ROUTEID%&systemid=%SRCSYSTEM%"
		 * +
		 * "msgtxt=%MSGTXT%&currkannelid=%CURRKANNELID%&reqid=%REQID%&retrycount=%RETRYCOUNT%&usedkannelid=%USEDKANNELID%&ismultipart=%ISMULTIPART%&msgtype=%MSGTYPE%&splitcount=%SPLITCOUNT%";
		 */
		NgKannelInfo ngKannelInfo = kannelInfoService.getDefaultKannelInfoForRejectedMessage();

		String dlrListenerUrl = ngKannelInfo.getDlrUrl();
		String dr = "id:%ID% sub:%ERRORCODE% dlvrd:%DLVRD% submit date:%SUBDATE% done date:%DONEDATE% stat:%STAT% err:%ERRORCODE% text:%TEXT%";

		if (messageObject.getErrorCode().equals(ErrorCodesEnum.FAILED_NUMBER_FOUND.getErrorCode())) {
			dr = dr.replaceAll("%ERRORCODE%", "882");
			dr = dr.replaceAll("%DLVRD%", "001");
			dr = dr.replaceAll("%STAT%", "FAILED");
			messageObject.setErrorCode("");
			messageObject.setErrorDesc("");
			messageObject.setStatus("");
		} else {
			dr = dr.replaceAll("%ERRORCODE%", messageObject.getErrorCode());
			dr = dr.replaceAll("%DLVRD%", "000");
			dr = dr.replaceAll("%STAT%", "REJECTD");

		}

		dr = dr.replaceAll("%ID%", "" + System.currentTimeMillis()
				+ messageObject.getDestNumber().substring(5, messageObject.getDestNumber().length() - 1));
		if (messageObject.getMessage() != null && messageObject.getMessage().length() > 10) {
			dr = dr.replaceAll("%TEXT%", messageObject.getMessage().substring(0, 9));
		} else {
			dr = dr.replaceAll("%TEXT%", messageObject.getMessage());
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
		String date = dateFormat.format(new Date());
		dr = dr.replaceAll("%SUBDATE%", date);
		dr = dr.replaceAll("%DONEDATE%", date);

		dr = URLEncoder.encode(dr, "UTF-8");

		dlrListenerUrl = dlrListenerUrl.replaceFirst("%a", dr);
		dlrListenerUrl = dlrListenerUrl.replaceFirst("smscid=%i", "smscid=000");
		dlrListenerUrl = dlrListenerUrl.replaceFirst("statuscd=%d", "statuscd=000");

		if (messageObject.getMessageId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGID%", messageObject.getMessageId());

		if (messageObject.getUsername() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%USERNAME%", messageObject.getUsername());

		if (messageObject.getMessage() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%MSGTXT%",
					URLEncoder.encode(messageObject.getMessage(), "UTF-8"));

		dlrListenerUrl = dlrListenerUrl.replace("%CURRKANNELID%", "" + 0);

		if (messageObject.getRequestId() != null) {
			dlrListenerUrl = dlrListenerUrl.replace("%REQID%",
					URLEncoder.encode(messageObject.getRequestId(), "UTF-8"));
		}

		Integer messageSplitCount = messageObject.getSplitCount();
		if ((messageSplitCount != null && messageSplitCount > 1)
				|| (messageObject.getIsMultiPart() != null && messageObject.getIsMultiPart().equals("Y"))) {
			messageObject.setIsMultiPart("Y");
			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());

			// Changes for Rejected DLR (In multipart case - Aman)
			messageObject.setFullMsg(messageObject.getMessage());
			// Integer pmSplitBytes = Integer.parseInt(plainMessageSplitBytes);
//			String concatinatedMessageIds = messageObject.getRequestId();
//			String[] messageIdsArray = concatinatedMessageIds.split("#");

//			logger.info("***** Important Check. Dont Ignore ******");
//			logger.info("Receiver Split Count {} and Total messages ids count {} ",
//					messageObject.getSplitCount(), messageIdsArray.length);
//			for (int j = 0; j < messageIdsArray.length; j++) {
//				
//				    messageObject.setMessageId(messageIdsArray[j]);

			saveMessageInMis(true, messageObject);

//			}
			logger.info("{} DLR for rejected message sent with status : {}", messageObject.getMessageId());

		} else {
			messageObject.setIsMultiPart("N");
			dlrListenerUrl = dlrListenerUrl.replace("%ISMULTIPART%", messageObject.getIsMultiPart());
			saveMessageInMis(true, messageObject);
		}

		if (messageObject.getUsedKannelIds() != null) {
			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%",
					URLEncoder.encode(messageObject.getUsedKannelIds(), "UTF-8"));
		} else {
			dlrListenerUrl = dlrListenerUrl.replace("%USEDKANNELID%", "0");
		}

		if (messageObject.getMessageType() != null) {
			dlrListenerUrl = dlrListenerUrl.replace("%MSGTYPE%", messageObject.getMessageType());
		}

		if (messageObject.getRetryCount() != null) {
			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", "" + messageObject.getRetryCount());
		} else {
			dlrListenerUrl = dlrListenerUrl.replace("%RETRYCOUNT%", "0");
		}

		if (messageObject.getSplitCount() != null && messageObject.getSplitCount() > 1) {
			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", "" + messageObject.getSplitCount());
		} else {
			dlrListenerUrl = dlrListenerUrl.replace("%SPLITCOUNT%", "1");
		}

//		if (messageObject.getCustRef() != null) {
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", messageObject.getCustRef());
//		} else {
//			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", "");
//		}

		if (messageObject.getCustRef() != null) {
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%",
					URLEncoder.encode(messageObject.getCustRef(), "UTF-8"));
		} else {
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CUSTREF%", "");
		}

		if (messageObject.getReceiveTime() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%RCVTIME%",
					Long.toString(messageObject.getReceiveTime().getTime()));

		if (messageObject.getDestNumber() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%TO%", messageObject.getDestNumber());

		messageObject.setSentTime(new java.sql.Timestamp(new Date().getTime()));
		if (messageObject.getSentTime() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENTTIME%",
					Long.toString(messageObject.getSentTime().getTime()));

		if (messageObject.getOriginalSenderId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getOriginalSenderId());
		else if (messageObject.getSenderId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%SENDERID%", messageObject.getSenderId());

		messageObject.setKannelId(0);
		if (messageObject.getKannelId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%KANNELID%", messageObject.getKannelId().toString());

		if (messageObject.getCarrierId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", messageObject.getCarrierId().toString());
		else
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CARRIERID%", "0");

		if (messageObject.getCircleId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", messageObject.getCircleId().toString());
		else
			dlrListenerUrl = dlrListenerUrl.replaceAll("%CIRCLEID%", "0");

		if (messageObject.getRouteId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", messageObject.getRouteId().toString());
		else
			dlrListenerUrl = dlrListenerUrl.replaceAll("%ROUTEID%", "" + 0);

		if (messageObject.getInstanceId() != null)
			dlrListenerUrl = dlrListenerUrl.replaceAll("%SRCSYSTEM%", messageObject.getInstanceId());

		logger.info("final dlr listener url for rejected message : " + dlrListenerUrl);
		dlrListenerUrl = dlrListenerUrl.replaceAll("\\+", "%20");
		dlrListenerUrl = dlrListenerUrl.replaceAll(" ", "%20");

		URL obj = new URL(dlrListenerUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		logger.info("Response from dlr listener received : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		logger.info("Response string from kannel is : ", response.toString());
		return response.toString();
	}

	private boolean validateMessageTemplate(MessageObject messageObject, Boolean continueMessageProcessing,
			Boolean isMisRequired, NgUser user) throws UnsupportedEncodingException, DecoderException {
		boolean templateMatched = false;
		Set<String> templateTextListForSenderId = staticDataService
				.getTemplateTextListForUserAndSenderId(user.getId() + "#" + messageObject.getSenderId());
		if (templateTextListForSenderId != null
				|| (messageObject.getTemplateId() != null && messageObject.getTemplateId().length() > 0)) {
			for (String patternTemplateAndDltTemplateId : templateTextListForSenderId) {
				logger.info("Pattern from list: {}", patternTemplateAndDltTemplateId);
				String[] tempArray = patternTemplateAndDltTemplateId.split("!!~~!!");
				String patternTemplate = tempArray[0].replace("{#var#}", "(.*)");
				String messageTextReceived = messageObject.getMessage();
				if (messageObject.getMessageType().equalsIgnoreCase("UC")) {
					messageTextReceived = new String(Hex.decodeHex(messageObject.getMessage().toCharArray()),
							"UTF-16BE");
				}
				while (messageTextReceived.contains("  ")) {
					messageTextReceived = messageTextReceived.replace("  ", " ");
				}

				while (patternTemplate.contains("  ")) {
					patternTemplate = patternTemplate.replace("  ", " ");
				}

				patternTemplate = patternTemplate.replaceAll("\\n", "");
				patternTemplate = patternTemplate.replaceAll("\\r", "");
				messageTextReceived = messageTextReceived.replaceAll("\\r", "");
				messageTextReceived = messageTextReceived.replaceAll("\\n", "");

				patternTemplate = patternTemplate.replace("\\n", "");
				patternTemplate = patternTemplate.replace("\n", "");
				patternTemplate = patternTemplate.replace("\\r", "");
				patternTemplate = patternTemplate.replace("\r", "");
				messageTextReceived = messageTextReceived.replace("\\r", "");
				messageTextReceived = messageTextReceived.replace("\r", "");
				messageTextReceived = messageTextReceived.replace("\\n", "");
				messageTextReceived = messageTextReceived.replace("\n", "");

				// logger.error("message id: {} messageTextReceived: {} and patternTemplate:
				// {}",messageObject.getMessageId(), messageTextReceived, patternTemplate);

				String[] messageTextArray = messageTextReceived.split(" ");
				String[] patternTextArray = patternTemplate.split(" ");
				boolean matchResult = false;
				if (messageTextArray.length == patternTextArray.length) {
					try {
						for (int i = 0; i < patternTextArray.length; i++) {
							Pattern pattern = Pattern.compile(patternTextArray[i]);
							Matcher matcher = pattern.matcher(messageTextArray[i]);
							matchResult = matcher.matches();
							if (matchResult == false) {
								break;
							}
						}
					} catch (Exception e) {
						logger.error("Error occured while matching template 1:message {} and pattern {}",
								messageTextReceived, patternTemplate);
						e.printStackTrace();
						matchResult = false;
					}
				} else if (messageTextArray.length > patternTextArray.length) {
					try {
						for (int i = 0, j = 0; i < patternTextArray.length && i <= j && j < messageTextArray.length;) {
							Pattern pattern = Pattern.compile(patternTextArray[i]);
							Matcher matcher = pattern.matcher(messageTextArray[j]);
							matchResult = matcher.matches();
							// System.out.println(patternTextArray[i] + " && "+ messageTextArray[j] +" &&
							// "+matchResult);
							if (i != 0 && patternTextArray[i].contains("(.*)") && matchResult == false
									&& j < messageTextArray.length - 1) {
								j++;
								messageTextArray[j] = messageTextArray[j - 1] + messageTextArray[j]; // Check after this
																										// line After
																										// testing
							} else if (i != 0 && patternTextArray[i - 1].contains("(.*)") && matchResult == false) {
								j++;
							} else if (matchResult == false) {
								break;
							} else {
								i++;
								j++;
							}
						}
					} catch (Exception e) {
						logger.error("Error occured while matching template 2:message {} and pattern {}",
								messageTextReceived, patternTemplate);
						e.printStackTrace();
						matchResult = false;
					}
				} else {
					matchResult = false;
				}
				logger.info("SenderId {} Template text is: {} ##and## message text is: {} and match result is :{}",
						(Object) messageObject.getSenderId(), (Object) patternTemplate,
						(Object) messageObject.getMessage(), (Object) matchResult);
				if (matchResult) {
					templateMatched = true;
					if (messageObject.getTemplateId() == null || messageObject.getTemplateId().length() == 0) {
						messageObject.setMessageServiceType(tempArray[2]);
						messageObject.setTemplateId(tempArray[1]);
					}
					logger.info("Pattern matched {}. Going to return true. ", (Object) patternTemplateAndDltTemplateId);
					break;
				}
			}
			if (!templateMatched) {
				messageObject.setStatus(Constants.MESSAGE_STATUS_REJECTED);
				messageObject.setErrorCode(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorCode());
				messageObject.setErrorDesc(ErrorCodesEnum.CONTENT_TEMPLATE_MISMATCH.getErrorDesc());

				/*
				 * try { sendRejectedDlr(messageObject); } catch (IOException e) {
				 * logger.error("Error while sending DLT rejected DLR."); e.printStackTrace(); }
				 */
				continueMessageProcessing = false;
				isMisRequired = true;
				return false;
			}
		}
		return true;
	}

	/**
	 * This method save the template in MIS & update the table in
	 * DB(ng_mis_curr_date)(Aman)
	 * 
	 * @param isMisRequired
	 * @param messageObject
	 */
	private void saveMessageInMis(boolean isMisRequired, MessageObject messageObject) {
		if (isMisRequired) {
			// saveMessageInMis(messageObject);
			String ngMisMessageObject;
			try {
				ngMisMessageObject = convertMessageObjectToMisObjectAsString(messageObject);
				if (ngMisMessageObject != null) {
					messageSender.send(kafKaDbObjectQueue, ngMisMessageObject);
				} else {
					logger.error("Error while converting message object into DB Object with message Id {} ",
							messageObject.getMessageId());
				}
			} catch (UnsupportedEncodingException | DecoderException e) {
				logger.error("Error while converting message object into DB Object with message Id {} ",
						messageObject.getMessageId());
				e.printStackTrace();
			}

		}
	}

}

