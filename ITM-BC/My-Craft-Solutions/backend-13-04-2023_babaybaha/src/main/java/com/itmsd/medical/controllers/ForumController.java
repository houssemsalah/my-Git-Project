package com.itmsd.medical.controllers;

import java.util.Date;

import com.itmsd.medical.entities.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.itmsd.medical.entities.Message;
import com.itmsd.medical.repositories.ForumRepository;
import com.itmsd.medical.repositories.MessageRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.services.DateAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/forum")
public class ForumController {
	private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

	@Autowired
		private MessageRepository messageRepository;
		
		@Autowired
		private ForumRepository forumRepository;
		
		@Autowired
		private UserRepository userRepository;
		
		@Autowired
		private SimpMessagingTemplate simpMessagingTemplate;
		
	/*	@MessageMapping("chat/{forumName}")
		@SendTo("/topic/messages/{forumName}")
		public Message sendMessage(@PathVariable(value = "forumName") String forumName, @Payload Message message) {

			String Datedata = DateAttribute.getTime(new Date());
			message.setCreatedAt(Datedata);
			message.setForum(forumRepository.findByForumName(forumName));
			//messageRepository.save(message);
			
			//simpMessagingTemplate.convertAndSend("/topic/messages/" + forumName, message);
			return messageRepository.save(message);
		}*/
	@GetMapping("messages")
	public ResponseEntity<?> getMessagePersonnel(){
		return ResponseEntity.ok().body(messageRepository.findAll())
				;}


    	@GetMapping("/get/{forumName}")
    	public ResponseEntity<?> getforum(@PathVariable String forumName){
		return ResponseEntity.ok().body(forumRepository.findByForumName(forumName));}

		@MessageMapping("/chat/{forumName}")
		public Message sendMessage(@DestinationVariable String forumName, Message message) {

       System.out.println(" ************************* forumName = "+forumName);


			String Datedata = DateAttribute.getTime(new Date());
			message.setCreatedAt(Datedata);
			message.setAvatarUrl(userRepository.findUserByUsername(message.getUsername()).getPhotoUrl());
			logger.info(" *********** Fetching forum" , forumRepository.findByForumName(forumName));

			message.setForum(forumRepository.findByForumName(forumName));
			logger.info(" *********** Fetching message.setforum",message.getForum());

			messageRepository.save(message);
			logger.info(" *********** message ",message);
			simpMessagingTemplate.convertAndSend("/topic/messages/" + forumName, message);
return message;
		}
	@PostMapping("/send/{forumName}")
	public ResponseEntity<?> sendMessages(@DestinationVariable String forumName, Message message) {

		String Datedata = DateAttribute.getTime(new Date());
		message.setCreatedAt(Datedata);
		message.setAvatarUrl(userRepository.findUserByUsername(message.getUsername()).getPhotoUrl());
		message.setForum(forumRepository.findByForumName(forumName));
		return ResponseEntity.ok().body(messageRepository.save(message));



	}
		
}
