package com.board.jdi_board.controller;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.service.BoardsService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/board")
@AllArgsConstructor
@Log4j2
public class BoardsController {

    private BoardsService boardsService;

    @GetMapping("/list.do")
    public String list(
            Model model)
    {
        List<BoardsDto> boards = boardsService.list();
        model.addAttribute("boards",boards);
        return "list";
    }
}
