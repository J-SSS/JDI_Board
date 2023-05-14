package com.board.jdi_board.vo;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.dto.KeywordsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.service.KeywordsService;
import com.board.jdi_board.service.RelationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tomcat.util.json.JSONParser;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.util.*;

@Data
@Component
public class RelationVo {

    private RelationsService relationsService;
    private KeywordsService keywordsService;

    public RelationVo(RelationsService relationsService, KeywordsService keywordsService) {
        this.relationsService = relationsService;
        this.keywordsService = keywordsService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    private CosineSimilarity cos = new CosineSimilarity();
    private RelationsDto rDto = new RelationsDto();
    private KeywordsDto kDto = new KeywordsDto();

    /**
     *
     * @throws JsonProcessingException
     */
    public int insertRelations(int bId, String content) throws JsonProcessingException {

        // 새 게시글의 단어를 추출한 리스트
        List<String> thisWords = nounExtractor(content);

        // KeyWords 테이블에서 유사도분석을 위한 정보를 불러온다
        KeywordsDto keywordsDto = keywordsService.detail(1);

        // (1). 단어를 중복 없이 저장한 리스트와
        // (2). 단어를 게시글별로 2차원 리스트로 저장한 리스트
        // (3). 각 게시글의 tf-idf 행렬을 저장한 맵
        // DB에서 불러와서 Java 객체로 변환

        List<String> uniqueList = new ArrayList<>();
        List<List<String>> totalList = new ArrayList<>();
        Map<String,Map<String,Double>> mapList = new LinkedHashMap<>();

        uniqueList = objectMapper.readValue(keywordsDto.getUniqueList(), uniqueList.getClass());
        totalList = objectMapper.readValue(keywordsDto.getTotalList(), totalList.getClass());
        mapList = objectMapper.readValue(keywordsDto.getMapList(), mapList.getClass());

        // 새 게시물의 TF-IDF 행렬 도출
        Map<String, Double> thisTF = tfIdfMap(thisWords, uniqueList, totalList);


        // 연관글의 Key(=게시글아이디)와 유사도 Value를 담을 Map
        Map<Integer,Double> relationMap = new HashMap<>();

        for( String key : mapList.keySet() ){
            double result = cos.cosineSimilarity(thisTF, mapList.get(key));
            if(result != 0.0 && result != 1.0){
                relationMap.put(Integer.valueOf(key),result);
            }
        }

        // relationMap을 유사도 Value 기준 내림차순 정렬하고, 그 Key값만 뽑아서 리스트화
        // Key값이 곧 게시글 아이디이기에, 새 게시글과의 유사도가 높은 순서로 정렬된 BId를 갖게된다
        List<Integer> orderedKey = new ArrayList<>(relationMap.keySet());
        Collections.sort(orderedKey, (value1, value2) -> (relationMap.get(value2).compareTo(relationMap.get(value1))));
        String orderedString = jsonParser(orderedKey);

        // 새 게시글과 1:1 대응하는 Relations테이블에 분석 결과 입력
        rDto.setBId(bId);
        rDto.setTerms(jsonParser(thisWords));
        rDto.setTfIdf(jsonParser(thisTF));
        rDto.setRelBIdList(orderedString);
        int rResult = relationsService.register(rDto);

        // 새 게시글의 단어 리스트를 (2).의 게시글별 단어리스트에 추가
        // 새 게시글의 TF-IDF 행렬을 (3).의 게시글별 TF-IDF 행렬맵에 추가
        // Keywords 테이블에 업데이트
        totalList.add(thisWords);
        mapList.put(String.valueOf(bId), thisTF);

        kDto.setKId(1);
        kDto.setUniqueList(jsonParser(uniqueList));
        kDto.setTotalList(jsonParser(totalList));
        kDto.setMapList(jsonParser(mapList));
        int kResult = keywordsService.modify(kDto);

        if(rResult == 1 && kResult ==1 ) return 1;
        else return 0;
    }


    /**
     * TF-IDF 행렬을 만드는 메서드
     * @param : 새 개시글에서 추출한 키워드 단어 리스트
     * @param : 각 게시글에서 출현한 모든 단어를 중복 없이 저장한 리스트
     * @param : 각 게시글의 키워드 단어를 List로 담은 2차원 List
     * @return : 새 게시글의 TF-IDF 행렬을 반환
     */
    public Map<String, Double> tfIdfMap(List<String> thisTermsList, List<String> uniqueList, List<List<String>> totalList){
        Map<String, Double> tfIdfMap = new HashMap<>();
        for(String noun : thisTermsList){

            int index = uniqueList.indexOf(noun);
            double tfidf = tfIdf(noun, thisTermsList, totalList);

            // 이 단어가 이번 게시글에서 처음 등장하는 경우 유니크리스트에 추가하고
            // TF-IDF행렬로 만듦
            if(index == -1){
                uniqueList.add(noun);
                if(tfidf != 0.0) tfIdfMap.put(String.valueOf(uniqueList.size()-1), tfidf);

                // 이 단어가 이전에도 등장한 경우 유니크리스트에서 그 인덱스를 찾아서
                // TF-IDF행렬로 만듦
            } else if(index != -1 && tfidf != 0.0) {
                tfIdfMap.put(String.valueOf(index), tfidf);
            }
        }
        return tfIdfMap;
    }

    /**
     * @param content : 새 게시글 본문에 해당하는 내용
     * @return : 명사에 해당하는 단어만을 List로 반환
     */
    public List<String> nounExtractor(String content) {

        // 1. OKT 라이브러리로 게시글 내용을 형태소 단위로 분리
        // 2. 정규화(normalize) : 처리할 수 있는 문장으로 정제
        // 3. 토큰화(tokenize) 및 어근화(..koreanToken..) : 문장을 가능한 작은 단위로 쪼개고, 한국어 어근만을 필터링
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(content);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

        // 4. 다시 명사(한,영)에 해당하는 단어만을 필터링하여 리스트화
        List<String> nounList = new ArrayList<>();

        for(KoreanTokenJava t : tokenList){
            if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                nounList.add(t.getText());
            }
        }

        // 5. 반환
        return nounList;
    }

    /**
     * TF-IDF 분석용메서드
     * @param : 빈도 분석에 사용할 단어
     * @param : 새 게시글의 키워드 단어 List
     * @param : 각 게시글의 키워드 단어를 List로 담은 2차원 List
     * @return : 해당 단어에 대한 TF-IDF 값
     */
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

    /**
     * 참조형데이터를 JSON 문자열로 바꾸는 메서드
     * @param o : Object
     * @return : JSON 문자열로 반환
     */
    public String jsonParser(Object o) throws JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(o);
        return jsonString;
    }


}
