//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.IAcademyYearService;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/academy-year"})
public class AcademyYearRest {
    private final IAcademyYearService academyYearService;

    @GetMapping
    public ResponseEntity<ResponseCustom> getAll() {
        return ResponseEntity.ok(new ResponseCustom("success", HttpStatus.OK.value(), this.academyYearService.getAll()));
    }

}
