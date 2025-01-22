package gt.com.archteam.springcloud.msvc.items.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import feign.FeignException;
import gt.com.archteam.libs.msvc.commons.entities.Product;
import gt.com.archteam.springcloud.msvc.items.clients.ProductFeignClient;
import gt.com.archteam.springcloud.msvc.items.models.Item;

@Service
public class ItemServiceFeign implements ItemService {
    private final ProductFeignClient productClient;

    private Random random = new Random();

    public ItemServiceFeign(ProductFeignClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public List<Item> findAll() {
        return productClient.findAll().stream().map(product -> new Item(product, random.nextInt(10) + 1))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        try {
            var producto = productClient.details(id);
            return Optional.of(new Item(producto, random.nextInt(10) + 1));
        } catch (FeignException e) {
            return Optional.empty();
        }
    }

    @Override
    public Product save(Product product) {
        return productClient.create(product);
    }

    @Override
    public Product update(Product product, Long id) {
        return productClient.update(id, product);
    }

    @Override
    public void delete(Long id) {
        productClient.delete(id);
    }

}
