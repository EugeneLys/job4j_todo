package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.service.TaskService;

@Slf4j
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/list")
    public String getAll(Model model,
                         @RequestParam(value = "filter", defaultValue = "all") String filter) {
        model.addAttribute("tasks", taskService.findByFilter(filter));
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model, HttpSession session) {
        model.addAttribute("task", new Task());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("task") Task task, Model model) {
        try {
            taskService.save(task);
            return "redirect:/tasks";
        } catch (Exception exception) {
            log.error("Failed to create task: {}", task, exception);
            model.addAttribute("message", exception.getMessage());
            return "errors/500";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "No task with such id was found");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        return "tasks/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, Model model) {
        try {
            var isUpdated = taskService.update(task);
            if (!isUpdated) {
                model.addAttribute("message", "No task was found.");
                return "errors/404";
            }
            return "redirect:/tasks";
        } catch (Exception exception) {
            log.error("Error during task update, id: {}", task.getId(), exception);
            model.addAttribute("message", exception.getMessage());
            return "errors/500";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = taskService.delete(id);
        if (!isDeleted) {
            model.addAttribute("message", "No task was found.");
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/done/{id}")
    public String makeDone(@PathVariable int id, Model model) {
        var doneTrue = taskService.done(id);
        if (!doneTrue) {
            model.addAttribute("message", "Couldn't make task done");
            return "errors/404";
        }
        return "redirect:/tasks/" + id;
    }
}
