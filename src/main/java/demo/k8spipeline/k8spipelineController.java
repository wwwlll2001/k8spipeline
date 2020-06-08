package demo.k8spipeline;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class k8spipelineController {

    @GetMapping("/how-are-you")
    public String howAreU() {
        return "fine, thank you, and you?";
    }
}
