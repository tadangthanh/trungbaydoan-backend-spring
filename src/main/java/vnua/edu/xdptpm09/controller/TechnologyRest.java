//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.dto.TechnologyDTO;
import vnua.edu.xdptpm09.service.ITechnologyService;
import vnua.edu.xdptpm09.validation.AllowedValues;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping({"/api/v1/technologies"})
public class TechnologyRest {
    private final ITechnologyService technologyService;

    @PostMapping
    public ResponseEntity<ResponseCustom> create(@Validated @RequestBody TechnologyDTO technologyDTO) {
        technologyDTO =this.technologyService.create(technologyDTO).orElse(null);
        return technologyDTO == null ? ResponseEntity.ok(new ResponseCustom("Create fail", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Create success", HttpStatus.CREATED.value(), technologyDTO));
    }

    @DeleteMapping
    public ResponseEntity<ResponseCustom> delete(@RequestParam @Min(1L) Long id) {
        this.technologyService.deleteById(id);
        return ResponseEntity.ok(new ResponseCustom("Delete success", HttpStatus.OK.value(), null));
    }

    @GetMapping
    public ResponseEntity<ResponseCustom> getAll() {
        return ResponseEntity.ok(new ResponseCustom("thành công", HttpStatus.OK.value(), this.technologyService.findAll()));
    }

    @GetMapping({"/{name}"})
    public ResponseEntity<ResponseCustom> findByName(@PathVariable String name, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction),sort));
        PageResponse<?> pageProject = this.technologyService.findByName(pageRequest, name);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("empty", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @PostMapping({"/ids"})
    public ResponseEntity<ResponseCustom> findAllByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(new ResponseCustom("thành công", HttpStatus.OK.value(), this.technologyService.findAllByIdIn(ids)));
    }

}
