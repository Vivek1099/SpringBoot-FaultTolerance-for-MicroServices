package Springboot_Resilance4j_serviceone.Controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RestController
public class MyController
{
    private static final String Service_One = "service1";
    @Autowired
    RestTemplate restTemplate;

    int attempt = 1;

    @GetMapping("/test")
    public String test()
    {
        return "demo service one";
    }

    @GetMapping("/externaldata")
    @CircuitBreaker(name = Service_One, fallbackMethod = "getBackupMsg")
    public String externaldata() throws Exception
    {
        System.out.println("retry method called "+ attempt++ + " times" + " at " + new Date());
        return restTemplate.getForObject("http://localhost:4040/test", String.class);
    }

    public String getBackupMsg(Throwable throwable)
    {
        return "external end point failed....circuit breaker executed";
    }

    @GetMapping("/externaldata2")
    @Retry(name = Service_One, fallbackMethod = "retryBackup")
    public String externaldata2() throws Exception
    {
        System.out.println("retry method called "+ attempt++ + " times" + " at " + new Date());
        return restTemplate.getForObject("http://localhost:4040/test2", String.class);
    }

    public String retryBackup(Throwable throwable)
    {
        return "retry pattern executed";
    }

    int count=1;
    @GetMapping("/api/resource")
    @RateLimiter(name = "myRateLimiter")
    public String getResource() {
        System.out.println("ratelimiter method called "+ count++ + " times" + " at " + new Date());
        return "Resource accessed!";
    }
}
