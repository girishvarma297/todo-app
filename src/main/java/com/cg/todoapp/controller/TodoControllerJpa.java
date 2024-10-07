package com.cg.todoapp.controller;

import com.cg.todoapp.entity.Todo;
import com.cg.todoapp.repository.TodoRepository;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TodoControllerJpa {

    private TodoRepository todoRepository;

    public TodoControllerJpa(TodoRepository todoRepository) {
        this.todoRepository=todoRepository;
    }

    @RequestMapping("list-todos")
    public String listAllTodos(ModelMap model){
        String username = getLoggedInUsername(model);
        List<Todo> todos= todoRepository.findByUsername(username);
        model.addAttribute("todos",todos);
        return "listTodos";
    }

    private static String getLoggedInUsername(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @RequestMapping(value="add-todo", method= RequestMethod.GET)
    public String showNewTodo(ModelMap model){
        String username = getLoggedInUsername(model);
        Todo todo=new Todo(0,username,"",LocalDate.now().plusMonths(2),false);
        model.put("todo",todo);
        return "addTodo";
    }
    @RequestMapping(value="add-todo", method= RequestMethod.POST)
    public String addNewTodo(ModelMap model, @Valid Todo todo, BindingResult result){
        if (result.hasErrors()){
            return "addTodo";
        }
        String username = getLoggedInUsername(model);
        todo.setUsername(username);
        todoRepository.save(todo);
        //todoService.addNewTodo(username,todo.getDescription(), todo.getTargetDate(),todo.isDone());
        return "redirect:list-todos";
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id){
        todoRepository.deleteById(id);
        return "redirect:list-todos";
    }

    @RequestMapping(value="update-todo", method=RequestMethod.GET)
    public String updateTodo(@RequestParam int id, ModelMap model){
        Todo todo=todoRepository.findById(id).get();
        model.addAttribute("todo",todo);
        return "addTodo";
    }

    @RequestMapping(value="update-todo", method= RequestMethod.POST)
    public String updateTodoById(ModelMap model, @Valid Todo todo, BindingResult result){
        if (result.hasErrors()){
            return "addTodo";
        }
        String username = getLoggedInUsername(model);
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }
}
