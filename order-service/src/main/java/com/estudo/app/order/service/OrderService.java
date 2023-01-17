package com.estudo.app.order.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.estudo.app.order.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estudo.app.order.dto.OrderLineItemsDto;
import com.estudo.app.order.dto.OrderRequest;
import com.estudo.app.order.model.Order;
import com.estudo.app.order.model.OrderLineItems;
import com.estudo.app.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	
	private final OrderRepository orderRepository;

	private final WebClient.Builder webClientBuilder;

	public void placeOrder(OrderRequest orderRequest){
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		.stream()
		.map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
		.collect(Collectors.toList());
		
		order.setOrderLineItems(orderLineItems);

		List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).collect(Collectors.toList());

		InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
				.uri("http://inventory-service/api/inventory",
						uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
				.retrieve()
				.bodyToMono(InventoryResponse[].class)
				.block();
		boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
				.allMatch(InventoryResponse::isInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Product is not in stock, please try again later.");
		}


	}
	
	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
		
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		
		return orderLineItems;
	}
}
