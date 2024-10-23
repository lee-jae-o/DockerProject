package kopo.poly.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Controller
public class ApiController {

    @GetMapping(value = "map")
    public String showKakaoMap() {
        return "api/map";
    }
}
