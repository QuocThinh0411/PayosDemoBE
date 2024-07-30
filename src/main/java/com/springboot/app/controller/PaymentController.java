package com.springboot.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springboot.app.entity.OrderPaid;
import com.springboot.app.util.JsonService;
import lombok.NonNull;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import vn.payos.util.SignatureUtils;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	private final PayOS payOS;
	private JsonService service;

	public PaymentController(PayOS payOS) {
		super();
		this.payOS = payOS;
		this.service = new JsonService();
	}

	@Value("${PAYOS_CHECKSUM_KEY}")
	private String checksumKey;

	@GetMapping(path = "/getPaidList")
	public List<Long> getPaidList() throws IllegalArgumentException, IOException {
		List<Long> result = new ArrayList();
		List<OrderPaid> listUser = service.readUsersFromJson();
		for (OrderPaid user : listUser) {
			result.add(user.getOrderCode());
		}
		return result;
	}

	@PostMapping(path = "/createSignature")
	public ObjectNode createSignature(@RequestBody ObjectNode body)
			throws JsonProcessingException, IllegalArgumentException {

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode response = objectMapper.createObjectNode();
		WebhookData webhookData = objectMapper.treeToValue(body, WebhookData.class);

		try {
			// Init Response
			response.put("error", 0);
			response.put("message", "Webhook delivered");

			String signature = SignatureUtils.createSignatureFromObj(webhookData, checksumKey);
			response.put("data", signature);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", -1);
			response.put("message", e.getMessage());
			response.set("data", null);
			return response;
		}
	}

	@PostMapping(path = "/payos_transfer_handler")
	public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
			throws JsonProcessingException, IllegalArgumentException {

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode response = objectMapper.createObjectNode();
		Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

		try {
			// Init Response
			response.put("error", 0);
			response.put("message", "Webhook delivered");
			response.set("data", null);

			WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
			OrderPaid paid = new OrderPaid();
			paid.setOrderCode(data.getOrderCode());
			processOrder(paid);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", -1);
			response.put("message", e.getMessage());
			response.set("data", null);
			return response;
		}
	}

	@PostMapping(path = "/testProcessOrder")
	public void testProcessOrder(@RequestBody OrderPaid body) throws IllegalArgumentException, IOException {

//		System.out.println("orderCode" + orderCode);
//		ObjectMapper objectMapper = new ObjectMapper();
//		OrderStatus OrderStatus = objectMapper.convertValue(body, OrderStatus.class);

		processOrder(body);
	}

	private void processOrder(OrderPaid RequestBody) throws IOException {
		service.updateUser(RequestBody);

	}
}
