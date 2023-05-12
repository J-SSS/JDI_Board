package com.board.jdi_board;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.service.RelationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.Test;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import scala.collection.Seq;

import java.util.*;

@SpringBootTest
public class RelationTest {
    @Autowired
    RelationsService relationsService;
    @Autowired
    BoardsService boardsService;
    ObjectMapper objectMapper = new ObjectMapper();

    // 새 게시글 등록시 연관도 분석 테스트용
    private String testString = "자바 스프링 프로그래밍 기초 강의";

    // testString을 nounExtractor() 메서드에 넣고 돌리면 나와야 되는 결과물..
    private List<String> testList = new ArrayList<>(Arrays.asList("자바","스프링","프로그래밍","기초", "강의", "널리지"));

    @Test
    public void insertTest() throws JsonProcessingException {

        // Relations 테이블의 모든 데이터를 불러온다
        List<RelationsDto> dtos = relationsService.list();

        // 출현한 모든 명사 형태소를 중복 허용으로 저장한 배열, 중복 없이 저장한 집합
        // DB에서 불러와서 Java 객체로 변환
        String jsonSet = dtos.get(0).getTerms();
        String jsonList = dtos.get(1).getTerms();

        Set termsSet = new HashSet();
        List termsList = new ArrayList<>();
        termsSet = objectMapper.readValue(jsonSet, termsSet.getClass());
        termsList = objectMapper.readValue(jsonList, termsList.getClass());

//        System.out.println("셋" + termsSet);
//        System.out.println("리스트" + termsList);

        for(String terms : testList){
            termsSet.add(terms);
            termsList.add(terms);
        }

    }

    // 명사인 형태소 추출하는 메서드
    @Test
    public void nounExtractor() {
        String content = "";

        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(content);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

        List<String> nounList = new ArrayList<>();

        for(KoreanTokenJava t : tokenList){
            if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                nounList.add(t.getText());
            }
        }

//        return nounList;
    }

    @Test
    public void jsonToJava() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonList = relationsService.detail(1).getTerms();
        List list = new ArrayList<>();
        list = objectMapper.readValue(jsonList, list.getClass());
        System.out.println(list);
    }

    @Test
    // 더미데이터 추출용
    public void termsExtraction() throws JsonProcessingException {

        // 전체 게시글 불러오기
        List<BoardsDto> boards = boardsService.list();

        // 형태소 저장위한 배열과 집합
        List<String> totalSet = new ArrayList<>();
        List<String> totalList = new ArrayList<>();

        // 형태소 추출하여 DB에 넣는 코드
        for(BoardsDto board : boards){

            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(board.getContent());
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
            List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

            List<String> tempList = new ArrayList<>();

            for(KoreanTokenJava t : tokenList){
                if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                    tempList.add(t.getText());
                    totalList.add(t.getText());
                    totalSet.add(t.getText());
                }
            }

            // 각 게시글 DB업로드
            RelationsDto dto = new RelationsDto();
            dto.setBId(board.getBId());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(tempList);
            dto.setTerms(jsonString);
            relationsService.register(dto);

        }

        // PK 1번인 DB에 total 데이터 저장 (불필요할수도...)

        RelationsDto dto = new RelationsDto();
        dto.setBId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(totalSet.stream().distinct().toList());
        dto.setTerms(jsonString);
        relationsService.register(dto);

        RelationsDto dto2 = new RelationsDto();
        dto2.setBId(2);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String jsonString2 = objectMapper2.writeValueAsString(totalList);
        System.out.println("여기"+totalList);
        dto2.setTerms(jsonString2);
        relationsService.register(dto2);

    }


}
