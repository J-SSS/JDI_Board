package com.board.jdi_board.controller;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.vo.RelationVo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.management.relation.Relation;
import java.util.List;

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
            Model model)
    {
        List<BoardsDto> boards = boardsService.list();
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
        model.addAttribute("terms",relationVo.terms(board.getContent()));
        return "detail";
    }
}
