package demo.k8spipeline;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/apple")
public class k8spipelineController {

    @Value("${greeting}")
    private  String greeting;

    @GetMapping("/how")
    public String howAreU() {
        return "fine, thank you, and you?";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/")
    public String hi() {
        return greeting;
    }
}
