package gt.com.archteam.springcloud.msvc.items;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${config.baseurl.endpoint.msvc-products}")
    private String url;

    /*
     * Se le agrega un balanceo de carga con la anotacion @LoadBalanced.
     * En feign no es necesario porque por defecto ya lo implementa.
     */
    // @Bean
    // @LoadBalanced
    // WebClient.Builder webClient() {
    //     return WebClient.builder().baseUrl(url);
    // }


    /**
     * Misma funcion que la de arriba pero para que el traceId se mantenga se hacen
     * estos cambios.
     */
    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return webClientBuilder.baseUrl(url).filter(lbFunction).build();
    }

}
