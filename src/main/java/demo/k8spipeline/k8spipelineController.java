package demo.k8spipeline;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/apple")
public class k8spipelineController {

    @GetMapping("/how")
    public String howAreU() {
        return "nnnnnn, fine, thank you, and you?";
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
