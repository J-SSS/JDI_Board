package com.board.jdi_board;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.dto.KeywordsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.service.BoardsService;
import com.board.jdi_board.service.KeywordsService;
import com.board.jdi_board.service.RelationsImpl;
import com.board.jdi_board.service.RelationsService;
import com.board.jdi_board.vo.CosineSimilarity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scala.Char;
import scala.collection.Seq;

import java.util.*;

@SpringBootTest
public class RelationTest {
    @Autowired
    RelationsService relationsService;
    @Autowired
    KeywordsService keywordsService;
    @Autowired
    BoardsService boardsService;
    ObjectMapper objectMapper = new ObjectMapper();

//    // 새 게시글 등록시 연관도 분석 테스트용
//    private String testString = "자바 스프링 프로그래밍 기초 강의";
//
//    // testString을 nounExtractor() 메서드에 넣고 돌리면 나와야 되는 결과물..
//    private List<String> testList = new ArrayList<>(Arrays.asList("자바","스프링","프로그래밍","기초", "강의", "널리지"));

    @Test
    public void insertTest() throws JsonProcessingException {

        // 새로 생성될 게시물의 내용부분을 매개변수로 받아서 실행되는 상황을 가정..
        // 새 게시글의 컨텐츠 내용
        String testString = "자바 스프링 프로그래밍 기초 강의입니다 어쩌구저쩌구";
        // testString을 nounExtractor() 메서드에 넣고 돌려서 키워드 단어 리스트만 추출
        List<String> testList = new ArrayList<>(Arrays.asList("자바","스프링","프로그래밍","기초", "강의", "널리지"));

        // KeyWords 테이블에서 유사도분석을 위한 정보를 불러온다
        KeywordsDto keywordsDto = keywordsService.detail(1);

        // 1. 단어를 중복 없이 저장한 리스트와
        // 2. 단어를 게시글별로 2차원 리스트로 저장한 리스트
        // 3. 각 게시글의 tf-idf 행렬을 저장한 맵
        // DB에서 불러와서 Java 객체로 변환
        String jsonUniqueList = keywordsDto.getUniqueList();
        String jsonTotalList = keywordsDto.getTotalList();
        String jsonMapList = keywordsDto.getMapList();

        List<String> uniqueList = new ArrayList<>();
        List<List<String>> totalList = new ArrayList<>();
        Map<String,Map<String,Double>> mapList = new LinkedHashMap<>();

        uniqueList = objectMapper.readValue(jsonUniqueList, uniqueList.getClass());
        totalList = objectMapper.readValue(jsonTotalList, totalList.getClass());
        mapList = objectMapper.readValue(jsonMapList, mapList.getClass());

        totalList.add(testList);

        // 새 게시물의 TF-IDF 행렬
        Map<String, Double> thisMap = tfIdfMap(uniqueList, totalList, testList);

        CosineSimilarity cos = new CosineSimilarity();
//        System.out.println("cos값 = " + cos.cosineSimilarity(thisMap,mapList.get("11")));

        Map<Integer,Double> relationMap = new HashMap<>();

        for( String key : mapList.keySet() ){
            double result = cos.cosineSimilarity(mapList.get("1"),mapList.get(key));
                    if(result != 0.0 && result != 1.0){
//                        System.out.println("게시글 Id : "+ key + " 유사도 " + result );
                        relationMap.put(Integer.valueOf(key),result);
                    }
        }

        // 연관도 높은 순으로 bId 담은 리스트
        List<Integer> orderedKey = new ArrayList<>(relationMap.keySet());
        Collections.sort(orderedKey, (value1, value2) -> (relationMap.get(value2).compareTo(relationMap.get(value1))));
        String orderedString = jsonParser(orderedKey);

    }

    // json문자열로 파싱하는 메서드
    public String jsonParser(Object o) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(o);
        return jsonString;
    }

    // TF-IDF 행렬을 만드는 메서드
    // 모든 단어 정보를 담은 유니크 리스트와, 각 게시글별 단어에 대한 2차원 리스트, TF-IDF 추출 대상이 되는 게시글의 단어리스틀을 매개변수로 받음
    public Map<String, Double> tfIdfMap(List<String> termsUnique, List<List<String>> totalList, List<String> thisTermsList){
        Map<String, Double> tfIdfMap = new HashMap<>();
        for(String noun : thisTermsList){

            int index = termsUnique.indexOf(noun);
            double tfidf = tfIdf(noun, thisTermsList, totalList);

            // 이 단어가 이번 게시글에서 처음 등장하는 경우 유니크리스트에 추가하고
            // TF-IDF행렬로 만듦
            if(index == -1 && tfidf != 0.0){
                termsUnique.add(noun);
                tfIdfMap.put(String.valueOf(termsUnique.size()-1), tfidf);

                // 이 단어가 이전에도 등장한 경우 유니크리스트에서 그 인덱스를 찾아서
                // TF-IDF행렬로 만듦
            } else if(index != -1 && tfidf != 0.0) {
                tfIdfMap.put(String.valueOf(index), tfidf);
            }
        }
            /*
            System.out.println(noun+tfIdf(noun, testList, totalList));
            자바0.5827512602444134
            스프링0.0
            프로그래밍0.2841246820397375
            기초0.0
            강의0.0
            널리지0.0
            System.out.println("어딨어" + tfIdfMap);
             */
        return tfIdfMap;
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



//    @Test
//    public void jsonToJava() throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String jsonList = relationsService.detail(1).getTerms();
//        List list = new ArrayList<>();
//        list = objectMapper.readValue(jsonList, list.getClass());
//        System.out.println(list);
//    }

    @Test
    // 기존 게시글에 대한 키워드 더미데이터 입력용
    public void termsExtraction() throws JsonProcessingException {

        // 전체 게시글 불러오기
        List<BoardsDto> boards = boardsService.list();

        // 형태소 저장위한 배열과 집합
        List<String> totalSet = new ArrayList<>();
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

        // keyword테이블에 중복 없는 키워드리스트, 각 게시글별 2차원 키워드 리스트, tf-idf 행렬 리스트 입력
        KeywordsDto dtoForUnique = new KeywordsDto();
        String jsonStringUnique = objectMapper.writeValueAsString(totalSet.stream().distinct().toList());
        String jsonStringTotal = objectMapper.writeValueAsString(totalDocument);

        dtoForUnique.setUniqueList(jsonStringUnique);
        dtoForUnique.setTotalList(jsonStringTotal);
        dtoForUnique.setMapList("temp");
        keywordsService.register(dtoForUnique);
    }

    // TF-IDF 행렬 더미데이터 입력용
    @Test
    public void tfIdfMapDummy() throws JsonProcessingException {

        // Relations 테이블의 모든 데이터를 불러온다
        List<RelationsDto> dtos = relationsService.list();
        List<KeywordsDto> keywordsDto = keywordsService.list();

        // 1. 단어를 중복 없이 저장한 리스트와, 2. 단어를 게시글별로 2차원 리스트로 저장한 리스트
        // DB에서 불러와서 Java 객체로 변환
        String jsonUniqueList = keywordsDto.get(0).getUniqueList();
        String jsonTotalList = keywordsDto.get(0).getTotalList();

        List<String> termsUnique = new ArrayList<>();
        List<List<String>> totalList = new ArrayList<>();
        termsUnique = objectMapper.readValue(jsonUniqueList, termsUnique.getClass());
        totalList = objectMapper.readValue(jsonTotalList, totalList.getClass());

        Map<Integer,Map<Integer,Double>> totalMap = new HashMap<>();

//        System.out.println("토탈셋" + termsUnique);
//        System.out.println("토탈리스트" + termsNormal.get(2));

        for(int i = 0 ; i<dtos.size() ; i++){
            int pk = dtos.get(i).getBrId();
            int fk = dtos.get(i).getBId();
            List<String> termsList = new ArrayList<>();
            termsList = objectMapper.readValue(dtos.get(i).getTerms(), termsList.getClass());
            Map<Integer, Double> tfIdfMap = new HashMap<>();

            for(String noun : termsList){
                int index = termsUnique.indexOf(noun);
                double tfidf = tfIdf(noun, termsList, totalList);

                // 이 단어가 이번 게시글에서 처음 등장하는 경우 유니크리스트에 추가하고
                // TF-IDF행렬로 만듦
                if(index == -1 && tfidf != 0.0){
                    termsUnique.add(noun);
                    tfIdfMap.put(termsUnique.size()-1, tfidf);

                    // 이 단어가 이전에도 등장한 경우 유니크리스트에서 그 인덱스를 찾아서
                    // TF-IDF행렬로 만듦
                } else if(index != -1 && tfidf != 0.0) {
                    tfIdfMap.put(index, tfidf);
                }

            }
            System.out.println("fk123 = " + fk);
            totalMap.put(pk,tfIdfMap);

            RelationsDto dtoForTfIdf = new RelationsDto();
            dtoForTfIdf.setBrId(pk);
            String jsonString = objectMapper.writeValueAsString(tfIdfMap);
            dtoForTfIdf.setTfIdf(jsonString);
            relationsService.modify(dtoForTfIdf);

            System.out.println(" =============== ");
            System.out.println("pk = " + pk);
            System.out.println("tfIdfMap = " + tfIdfMap);
            System.out.println("jsonString = " + jsonString);
            System.out.println(" =============== ");

        }// 외부for

        KeywordsDto kDto = new KeywordsDto();
        String jsonStringForMap = objectMapper.writeValueAsString(totalMap);
        kDto.setKId(1);
        kDto.setMapList(jsonStringForMap);
        keywordsService.modify(kDto);
    }

    //연관글 더미 입력용
    @Test
    public void relationDummy() throws JsonProcessingException {

        KeywordsDto keywordsDto = keywordsService.detail(1);

        String jsonUniqueList = keywordsDto.getUniqueList();
        String jsonTotalList = keywordsDto.getTotalList();
        String jsonMapList = keywordsDto.getMapList();

        List<String> uniqueList = new ArrayList<>();
        List<List<String>> totalList = new ArrayList<>();
        Map<String,Map<String,Double>> mapList = new LinkedHashMap<>();

        uniqueList = objectMapper.readValue(jsonUniqueList, uniqueList.getClass());
        totalList = objectMapper.readValue(jsonTotalList, totalList.getClass());
        mapList = objectMapper.readValue(jsonMapList, mapList.getClass());

        CosineSimilarity cos = new CosineSimilarity();

        List<RelationsDto> dtos = relationsService.list();
        for(RelationsDto dto : dtos){
            RelationsDto relationsDto = new RelationsDto();
            int pk = dto.getBrId();
            String jsonMap = dto.getTfIdf();
            Map<String,Double> tfidfMap = new LinkedHashMap<>();
            tfidfMap = objectMapper.readValue(jsonMap, tfidfMap.getClass());
            Map<Integer,Double> relationMap = new HashMap<>();

            for( String key : mapList.keySet() ){
                if(Integer.parseInt(key)!=pk){
                    double result = cos.cosineSimilarity(tfidfMap,mapList.get(key));
                    if(result != 0.0 && result != 1.0){
                        relationMap.put(Integer.valueOf(key),result);
                    }
                }
            }

            List<Integer> orderedKey = new ArrayList<>(relationMap.keySet());
            Collections.sort(orderedKey, (value1, value2) -> (relationMap.get(value2).compareTo(relationMap.get(value1))));
            String orderedString = jsonParser(orderedKey);

            relationsDto.setBrId(pk);
            relationsDto.setRelBIdList(orderedString);
            relationsService.updateRelList(relationsDto);
        }
    }


} // 클래스 닫기
