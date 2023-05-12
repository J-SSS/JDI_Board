package com.board.jdi_board;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.service.RelationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
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

        // 단어를 중복 없이 저장한 리스트와 단어를 게시글별로 2차원 리스트로 저장한 리스트
        // DB에서 불러와서 Java 객체로 변환
        String jsonUniqueList = dtos.get(0).getTerms();
        String jsonTotalList = dtos.get(1).getTerms();

        List<String> termsUnique = new ArrayList<>();
        List<List<String>> totalList = new ArrayList<>();
        termsUnique = objectMapper.readValue(jsonUniqueList, termsUnique.getClass());
        totalList = objectMapper.readValue(jsonTotalList, totalList.getClass());

//        System.out.println("토탈셋" + termsUnique);
//        System.out.println("토탈리스트" + termsNormal.get(2));

//         // 중복 허용 리스트에는 그냥 넣음
//         termsNormal.add(noun);

//        List<Double> tfIdfList = new ArrayList<>();
        Map<Integer, Double> tfIdfMap = new HashMap<>();
        for(String noun : testList){

            int index = termsUnique.indexOf(noun);
            double tfidf = tfIdf(noun, testList, totalList);

            // 이 단어가 처음 등장하는 경우 유니크리스트에 추가하고
            // 본 게시글의 tfidf 리스트에 추가
            if(index == -1 && tfidf != 0.0){
                termsUnique.add(noun);
                tfIdfMap.put(termsUnique.size()-1, tfidf);

            // 이 단어가 이전에도 등장한 경우 유니크리스트에서 그 인덱스를 찾아서
            // 본 게시글의 tfidf 리스트에 추가
            } else if(index != -1 && tfidf != 0.0) {
               tfIdfMap.put(index, tfidf);
            }
        }
        System.out.println("어딨냐" + tfIdfMap);
            /*
            System.out.println(noun+tfIdf(noun, testList, totalList));
            자바0.5827512602444134
            스프링0.0
            프로그래밍0.2841246820397375
            기초0.0
            강의0.0
            널리지0.0
             */
    }
    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add(0,"123");
        list.add(1,"123");
        System.out.println();


    }

    // tf-idf 분석 메서드
    public double tfIdf(String noun, List<String> terms, List<List<String>> totalList){
        double thCnt = 0;
        double idfCnt = 0;
        double th = 0;
        double idf = 0;

        // 빈도
        for (String term : terms){ if (noun.equals(term)) thCnt++; }
        th =  thCnt / terms.size();
        // 역빈도
        for (List<String> list : totalList) {
            for (String term : list) {
                if (noun.equals(term)) {
                    idfCnt++;
                }
            }
        }
        idf = Math.log(totalList.size() / idfCnt);

        // 역빈도가 60% 이상이면 0을 반환하며 return
        // 어떤 단어가 이번 케이스에 처음 등장하는 경우 cnt가 0으로 남아 tf-idf가 무한대가 되므로 해당부분 처리
        double idfRatio = idfCnt / totalList.size();
        if (idfRatio >= 0.6 || (thCnt == 0 || idfCnt == 0)) {
            return 0;
        }

        return th*idf;
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
//        List<String> totalList = new ArrayList<>();
        List<List<String>> totalDocument = new ArrayList<>();

        // 형태소 추출하여 DB에 넣는 코드
        for(BoardsDto board : boards){

            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(board.getContent());
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
            List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

            List<String> tempList = new ArrayList<>();

            for(KoreanTokenJava t : tokenList){
                if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                    tempList.add(t.getText());
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

            totalDocument.add(tempList);
        }

        // 전체 형태소 정보 저장용

        RelationsDto dto = new RelationsDto();
        dto.setBId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(totalSet.stream().distinct().toList());
        dto.setTerms(jsonString);
        relationsService.register(dto);

        RelationsDto dto2 = new RelationsDto();
        dto2.setBId(2);
        ObjectMapper objectMapper2 = new ObjectMapper();
        String jsonString2 = objectMapper2.writeValueAsString(totalDocument);
        System.out.println("여기"+totalDocument);
        dto2.setTerms(jsonString2);
        relationsService.register(dto2);

    }


}
