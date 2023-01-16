package com.estudo.app.inventory.service;

import com.estudo.app.inventory.dto.InventoryResponse;
import com.estudo.app.inventory.model.Inventory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estudo.app.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
	
	private final InventoryRepository inventoryRepository;
	
	@Transactional(readOnly = true)
	public List<InventoryResponse> isInStock(List<String> skuCode){
		return inventoryRepository.findBySkuCodeIn(skuCode).stream()
				.map(inventory ->
					InventoryResponse.builder()
							.skuCode(inventory.getSkuCode())
							.isInStock(inventory.getQuantity() > 0)
							.build()
				).collect(Collectors.toList());
	}
}
