package com.example.api.service;

import com.example.api.model.Product;
import com.example.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository repository;

    @InjectMocks
    ProductService service;

    @Test
    void getAllProducts_returnsList() {
        List<Product> products = List.of(
            new Product(1, "Laptop", 999.99),
            new Product(2, "Mouse", 29.99)
        );
        when(repository.findAll()).thenReturn(products);

        List<Product> result = service.getAllProducts();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getProduct_returnsProduct_whenFound() {
        Product product = new Product(1, "Laptop", 999.99);
        when(repository.findById(1)).thenReturn(Optional.of(product));

        Optional<Product> result = service.getProduct(1);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void getProduct_returnsEmpty_whenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertTrue(service.getProduct(99).isEmpty());
    }

    @Test
    void createProduct_savesAndReturnsProduct() {
        Product incoming = new Product(0, "Keyboard", 79.99);
        Product saved    = new Product(3, "Keyboard", 79.99);
        when(repository.save(incoming)).thenReturn(saved);

        Product result = service.createProduct(incoming);

        assertEquals(3, result.getId());
        verify(repository).save(incoming);
    }

    @Test
    void updateProduct_returnsUpdated_whenExists() {
        Product existing = new Product(1, "Laptop", 999.99);
        Product update   = new Product(0, "Laptop Pro", 1299.99);
        Product saved    = new Product(1, "Laptop Pro", 1299.99);
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(update)).thenReturn(saved);

        Optional<Product> result = service.updateProduct(1, update);

        assertTrue(result.isPresent());
        assertEquals("Laptop Pro", result.get().getName());
    }

    @Test
    void updateProduct_returnsEmpty_whenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertTrue(service.updateProduct(99, new Product()).isEmpty());
    }

    @Test
    void deleteProduct_returnsTrue_whenDeleted() {
        when(repository.deleteById(1)).thenReturn(true);
        assertTrue(service.deleteProduct(1));
    }

    @Test
    void deleteProduct_returnsFalse_whenNotFound() {
        when(repository.deleteById(99)).thenReturn(false);
        assertFalse(service.deleteProduct(99));
    }
}
