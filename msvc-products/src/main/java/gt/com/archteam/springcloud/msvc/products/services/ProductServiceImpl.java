package gt.com.archteam.springcloud.msvc.products.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.com.archteam.libs.msvc.commons.entities.Product;
import gt.com.archteam.springcloud.msvc.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    /* Variable para poder obtener el puerto de las propiedades */
    private final Environment environment;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository, Environment environment) {
        this.productRepository = productRepository;
        this.environment = environment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return ((List<Product>) productRepository.findAll()).stream().map(product -> {
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
            return product;
        });
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return this.productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

}
