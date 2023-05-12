## TF-IDF 기법과 Cosine Similarity 분석을 활용한 연관 게시글 구현
<hr>

##### *) 참고한 내용
1. 자연어 처리 및 유사도 분석
  - 딥 러닝을 이용한 자연어 처리 입문(안상준, 유원준)
  - https://wikidocs.net/24603
  - https://commons.apache.org/sandbox/commons-text/jacoco/org.apache.commons.text.similarity/CosineSimilarity.java.html
  - https://needjarvis.tistory.com/516
2. TF-IDF 활용한 빈도 분석
  - https://needjarvis.tistory.com/678
3. 더미데이터 생성 : ChatGPT
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
  - 연관게시글 정보를 가진 relations 테이블 생성(이하 '<u style="color:white">R</u>'). <br> 외래키로 각 게시글의 PK를 지니는 1:1관계로 구상
  - R 테이블의 terms 칼럼은 각 게시글 내용부분을 형태소 단위로 분석하여 명사만을 추출하여 저장한다
  - R 테이블의 tf_idf 칼럼은 terms 칼럼의 명사를 tf-idf로 분석하여 행렬화한 정보를 갖는다
  - R 테이블의 1번, 2번 레코드는 TF-IDF 분석을 위한 정보를 갖도록 한다
    - 1번 레코드는 모든 게시글에서 등장한 명사를 중복 없이 저장한 배열이며, 이는 TF-IDF 행렬에서 열을 나타낸다
    - 2번 레코드는 각 게시글의 terms 칼럼을 다시 List로 묶은 정보를 지니며, TF-IDF 분석시 문서 전체에서 특정 단어의 출현 빈도를 구하기 위해 사용한다
  <br><br>
#### 2. 적용 - TF-IDF 적용
  - 게시글 본문을 형태소 분석 라이브러리 OKT를 활용하여 분석하고, 명사에 해당하는 형태소만을 키워드로 추출한다
  - 추출한 키워드를 리스트화하여 R 테이블의 2번 레코드에 저장한다(IDF 분석용)
  - 추출한 키워드가 R 테이블의 1번 레코드에 없을 경우 해당 레코드에 추가하여 키워드 목록을 업데이트한다
  - 추출한 키워드에 TF-IDF를 적용하고 그 결과를 행렬화하여 각 레코드의 tf_idf 칼럼에 추가한다<br><u>이 때, IDF가 0.6이상인 키워드는 제외한다</u>
<br><br>
#### 3. 적용 - Cosine Similarity 분석
  - 각 게시글의 TF-IDF 행렬을 Cosine Similarity로 분석하여 연관게시글을 찾는다
  - <u>1. IDF가 0.4이하인 유의미한 키워드로서, <br>2. 두 개 이상의 키워드가 겹치는 경우만을 연관글로 취급한다<br>3. 연관도가 높은 순으로 출력한다</u>
  - 새 게시글이 등록 될 때 마다 TF-IDF 분석의 바탕이 되는 데이터가 변동되므로, 새 게시글 등록시마다 모든 게시글에 대하여 TF-IDF 및 Cosine Similarity 분석을 새로 수행한다<br>(모든 게시글이 아니라 키워드 교집합을 갖는 게시글만을 갱신해줘도 될듯하나 시간관계상 생략)  


