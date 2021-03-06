package com.singhb.examples.springboot.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {
	public static Logger logger = LoggerFactory.getLogger(DemoRestController.class);
	@Autowired
	@Qualifier("kafkaTemplateBoot")
	private KafkaTemplate<String, String> template;

	@GetMapping("/producer/{message}")
	public String sendMessage(@PathVariable(name = "message") String message) {

		try {
			send(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Done Sending-->" + message;
	}

	@Transactional
	public void send(String message) throws InterruptedException {
		this.template.send("myTopic", System.currentTimeMillis() + "", message);
	}

	// , Acknowledgment ack
	@KafkaListener(topics = "myTopic", containerFactory = "kafkaListenerContainerFactory")
	public void listen(String data, Acknowledgment ack) throws Exception {

		if (data.equals("foo31")) {
			logger.info("There was an exception -->" + data.toString());
			throw new Exception();
		} else {
			logger.info("Received -->" + data.toString());
		}

		ack.acknowledge();
	}

}
