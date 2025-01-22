package gt.com.archteam.springcloud.msvc.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({ "gt.com.archteam.libs.msvc.commons.entities" })
public class MsvcProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcProductsApplication.class, args);
	}

}
