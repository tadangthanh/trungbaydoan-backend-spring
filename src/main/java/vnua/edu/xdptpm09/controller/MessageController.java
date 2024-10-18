//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.MessageDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.IMessageService;
import vnua.edu.xdptpm09.validation.AllowedValues;

@RestController
@RequestMapping({"/api/v1/messages"})
@RequiredArgsConstructor
public class MessageController {
    private final IMessageService messageService;

    @PostMapping
    public ResponseEntity<ResponseCustom> create(@RequestBody MessageDTO messageDTO) {
        messageDTO =this.messageService.create(messageDTO).orElse(null);
        return messageDTO == null ? ResponseEntity.ok().body(new ResponseCustom("Message not created", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok().body(new ResponseCustom("Message created", HttpStatus.CREATED.value(), messageDTO));
    }

    @GetMapping({"/{receiverEmail}"})
    public ResponseEntity<ResponseCustom> getAllMessageByReceiverIdAndCurrentUser(@PathVariable String receiverEmail, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.messageService.getAllMessageByReceiverIdAndCurrentUser(pageRequest, receiverEmail);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có tin nhắn nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/latest"})
    public ResponseEntity<ResponseCustom> getLatestMessages(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.messageService.getLatestMessages(pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có tin nhắn nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

}
