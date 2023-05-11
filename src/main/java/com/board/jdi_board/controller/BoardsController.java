package com.board.jdi_board.controller;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.vo.RelationVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/board")
@AllArgsConstructor
@Log4j2
public class BoardsController {

    private BoardsService boardsService;
    private RelationVo relationVo;

    // 게시글 리스트
    @GetMapping("/list.do")
    public String list(
            Model model) throws JsonProcessingException {
        List<BoardsDto> boards = boardsService.list();
//        relationVo.terms2(boards);
        model.addAttribute("boards",boards);
        return "list";
    }

    // 게시글 상세
    @GetMapping("/{bId}/detail.do")
    public String detail(
            Model model,
            @PathVariable int bId)
    {
        BoardsDto board = boardsService.detail(bId);

        model.addAttribute("board",board);
        HashSet<String> termList = new HashSet<>();
        model.addAttribute("terms",relationVo.terms(termList, board.getContent()));
        return "detail";
    }

//    @PostMapping("/insert.do")
//    public void insert(
//            @ModelAttribute BoardsDto boardsDto) throws JsonProcessingException {
//        List<BoardsDto> boards = boardsService.list();
//        relationVo.terms3(boards);
//    }
}
