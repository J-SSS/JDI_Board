# 📌. TF-IDF 기법과 Cosine Similarity 분석을 활용한 연관 게시글 구현(23.05.14)
<hr>

##### *) 참고한 내용
1. 자연어 처리 및 유사도 분석
   - 딥 러닝을 이용한 자연어 처리 입문(안상준, 유원준)
   - https://wikidocs.net/24603
   - https://commons.apache.org/sandbox/commons-text/jacoco/org.apache.commons.text.similarity/CosineSimilarity.java.html
   - https://needjarvis.tistory.com/516
2. TF-IDF 활용한 빈도 분석
    - https://needjarvis.tistory.com/678
3. 강의 제목 & 설명 부분 더미데이터 생성 : ChatGPT
<hr>


## Ⅰ. 개발환경
- JAVA / Spring Boot / Thymeleaf
- MySQL / MyBatis
- Gradle
- IntelliJ, Git & Git Hub
- Open Korean Text(OKT) 라이브러리 (형태소 추출)
<br><br>

## Ⅱ. 구현 내용
### (1). 게시판 CRUD
- MVC 패턴 설계
- 게시글 리스트 보기, 상세보기 및 게시글 등록 기능 구현
  <br><br>
### (2). 연관 게시글 기능
#### 0. 연관성 분석 기본 로직
  - 각 게시글의 내용에서 명사에 해당하는 형태소 추출
  - TF-IDF 기법으로 빈도 분석
  - Cosine Similarity로 유사도 분석하여 높은 순으로 연관 게시글 등록
  <br><br>
#### 1. 적용 - DB설계
![111](https://github.com/J-SSS/JDI_Board/assets/118149752/fc01c46c-9be2-45c6-8643-197390642925)
  - 연관게시글 정보를 가진 relations 테이블 생성(이하 '<u style="color:white">R</u>'). <br> 외래키로 각 게시글의 PK를 지니고, 이를 유니크키로 하여 1:1관계로 설정
  - R 테이블의 terms 칼럼은 각 게시글 내용부분을 형태소 단위로 분석하여 명사만을 추출하여 저장한다
  - R 테이블의 tf_idf 칼럼은 terms 칼럼의 명사를 tf-idf로 분석하여 행렬화한 정보를 갖는다
  - R 테이블의 rel_b_id_list 칼럼은 연관게시글의 Id를 연관도 순으로 정렬한 리스트이다
  <br><br>
  - 연관도 분석을 위한 배경 정보를 가진 keywords 테이블 생성(이하 K)
  - K 테이블의 uniqueList는 모든 게시글에 등장한 키워드를 중복 없이 나열한 리스트이다
  - K 테이블의 totalList는 각 게시글의 키워드리스트를 다시 게시글 단위로 저장한 2차원 리스트이다
  - K 테이블의 mapList는 각 게시글의 tf-idf 행렬을 게시글 단위로 저장한 2차원 맵(=행렬)이다
  <br><br>
#### 2. 적용 - TF-IDF 분석
![111](https://github.com/J-SSS/JDI_Board/assets/118149752/a69f9513-071b-4eed-be7b-af0796c495c0)
  - 게시글 본문을 형태소 분석 라이브러리 OKT를 활용하여 분석하고, 명사에 해당하는 형태소만을 연관성 분석을 위한 단어로 추출한다
  - 추출한 각 단어가 K 테이블의 uniqueList에 없을 경우 해당 리스트에 추가하여 단어 목록을 업데이트한다
  - 추출한 단어들을 리스트화하여 K 테이블의 totalList에 저장한다(IDF 분석용 - 각 게시글에 등장한 단어 리스트를 2차원 리스트로 저장)
  - 추출한 각 단어에 TF-IDF를 적용하고 그 결과를 각 레코드의 tf_idf 칼럼에 추가한다<br><u>이 때, IDF가 0.6이상인 키워드는 제외한다</u>
  - 각 단어는 K 테이블의 uniqueList에도 존재하며, 중복을 제거한 리스트이기에 여기서의 index를 해당 단어의 고유한 Key로 취급할 수 있다. 이를 이용하면 각 게시글의 TF-IDF 결과를 다른 게시글과 비교하여 행렬처럼 다루는 것이 가능하다
<br><br>
#### 3. 적용 - Cosine Similarity 분석
![2222](https://github.com/J-SSS/JDI_Board/assets/118149752/a6d400fd-cacb-41f4-9863-c61d576e5349)
  - 각 게시글의 TF-IDF 행렬을 Cosine Similarity로 분석하여 연관게시글을 찾는다
  - ***(조건1)*** IDF가 0.4이하인 유의미한 키워드로서, ***📂. vo/RelationVo.java : 190*** <br> 
  - ***(조건2)*** 두 개 이상의 키워드가 겹치는 경우만을 연관글로 취급한다 ***📂. vo/CosineSimilarity.java : 14*** <br> 
  - ***(조건3)*** 연관도가 높은 순으로 R 테이블에 저장한다 ***📂. vo/RelationVo.java : 79***
<br><br>
#### 5. 결과
![333](https://github.com/J-SSS/JDI_Board/assets/118149752/765f3630-b103-433c-a971-60250f27a762)
![444](https://github.com/J-SSS/JDI_Board/assets/118149752/b54ee26f-3460-4588-9852-acadf04d69da)

