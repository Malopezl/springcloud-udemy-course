package gt.com.archteam.springcloud.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class MsvcGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcGatewayServerApplication.class, args);
	}

	/**
	 * Metodo para configurar el filtro de circuit breaker para productos, ya que
	 * con gateway
	 * MVC no se puede configurar desde el YAML.
	 * 
	 * @return
	 */
	@Bean
	RouterFunction<ServerResponse> routerConfig() {
		return GatewayRouterFunctions.route("msvc-products")
				.route(GatewayRequestPredicates.path("/api/products/**"), HandlerFunctions.http())
				// filtro personalizado
				.filter((request, next) -> {
					// agregar headers/cookies/otros al request
					ServerRequest requestModified = ServerRequest.from(request)
							.header("message-request", "algun mensaje al request").build();
					// agregar headers/cookies/otros al response
					ServerResponse response = next.handle(requestModified);
					response.headers().add("message-response", "algun mensaje para el response");
					return response;
				})
				// filtro de load balancer
				.filter(LoadBalancerFilterFunctions.lb("msvc-products"))
				// filtro de circuit breaker
				.filter(CircuitBreakerFilterFunctions.circuitBreaker(config -> config.setId("products")
						.setStatusCodes("500").setFallbackPath("forward:/api/items/5")))
				.before(BeforeFilterFunctions.stripPrefix(2)).build();
	}

}
