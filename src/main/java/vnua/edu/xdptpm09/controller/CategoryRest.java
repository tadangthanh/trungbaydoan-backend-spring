//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.CategoryDTO;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.ICategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/categories"})
@Validated
public class CategoryRest {
    private final ICategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseCustom> create(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryDTO = this.categoryService.create(categoryDTO).orElse(null);
        return categoryDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Create category failed", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Create category successfully", HttpStatus.CREATED.value(), categoryDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseCustom> getAll() {
        return ResponseEntity.ok(new ResponseCustom("Get all categories successfully", HttpStatus.OK.value(), this.categoryService.getAll()));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> delete(@PathVariable Long id) {
        this.categoryService.delete(id);
        return ResponseEntity.ok(new ResponseCustom("Delete category successfully", HttpStatus.NO_CONTENT.value(), null));
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> update(@PathVariable Long id, @RequestBody @Valid CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        categoryDTO = this.categoryService.update(categoryDTO).orElse(null);
        return categoryDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Update category failed", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Update category successfully", HttpStatus.OK.value(), categoryDTO));
    }

}
