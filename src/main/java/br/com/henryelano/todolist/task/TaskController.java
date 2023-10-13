package br.com.henryelano.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.henryelano.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel oTask, HttpServletRequest request) {
        oTask.setIdUser(this.getIdUserRequest(request));
        var sRetornoValidaData = this.validaData(oTask);
        if(!sRetornoValidaData.isEmpty()) {
            return ResponseEntity.badRequest().body(sRetornoValidaData);
        }
        var task = this.taskRepository.save(oTask);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        return taskRepository.findByIdUser(this.getIdUserRequest(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@RequestBody TaskModel oTask, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null) {
            return ResponseEntity.badRequest().body("A tarefa pode ser encontrada!");
        }

        if(!task.getIdUser().equals(getIdUserRequest(request))) {
            return ResponseEntity.badRequest().body("O usuário não tem permissão para alterar essa tarefa!");
        }
        Utils.copyNonNullProperties(oTask, task);
        return ResponseEntity.ok(this.taskRepository.save(task));
    }
    
    private String validaData(TaskModel oTask) {
        var currentDate = LocalDateTime.now();
        var sMessage = "";
        if(currentDate.isAfter(oTask.getStartAt()) || currentDate.isAfter(oTask.getEndAt())) {
            sMessage = "A data de início / data de término deve ser maior que a data atual!";
        }
        if(oTask.getStartAt().isAfter(oTask.getEndAt())) {
            sMessage = "A data inicial deve ser maior que a data final";
        }
        return sMessage;
    }

    private UUID getIdUserRequest(HttpServletRequest request) {
        return (UUID) request.getAttribute("idUser");
    }

}