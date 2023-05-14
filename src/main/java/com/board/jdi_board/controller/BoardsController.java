package com.board.jdi_board.controller;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.vo.RelationVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @GetMapping("/list")
    public String list(
            Model model) throws JsonProcessingException {
        List<BoardsDto> boards = boardsService.list();
        model.addAttribute("boards",boards);
        return "list";
    }

    // 새 게시글 입력 폼
    @GetMapping("/registerForm")
    public String registerForm()
    {
        return "registerForm";
    }

    @PostMapping("/register.do")
    public String register(
            @ModelAttribute BoardsDto boardsDto) throws JsonProcessingException
    {
        boardsService.register(boardsDto);
        int bId = boardsDto.getBId();
        String content = boardsDto.getContent();
        relationVo.insertRelations(bId,content);

        return "redirect:/board/list";
    }

    // 게시글 상세
    @GetMapping("/{bId}/detail.do")
    public String detail(
            Model model,
            @PathVariable int bId) throws JsonProcessingException
    {
        BoardsDto board = boardsService.detail(bId);
        List<BoardsDto> relList = new ArrayList<>();

        /**
         Relations 테이블에서 관련글 BId가 담긴 데이터를 가져와서
         List<BoardsDto>를 만들고 뷰 렌더링시 사용
         */
        // 자바 리스트 타입으로 변환
        List<Integer> relBIdList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        relBIdList = objectMapper.readValue(board.getRelList(),relBIdList.getClass());

        for(int relBId : relBIdList){
            relList.add(boardsService.detail(relBId));
        }


        model.addAttribute("rels",relList);
        model.addAttribute("board",board);
        return "detail";
    }

//    @PostMapping("/insert.do")
//    public void insert(
//            @ModelAttribute BoardsDto boardsDto) throws JsonProcessingException {
//        List<BoardsDto> boards = boardsService.list();
//        relationVo.terms3(boards);
//    }
}
