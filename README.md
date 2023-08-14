# 애즈위메이크

애즈위메이크의 과제전형 지원을 위한 프로젝트입니다.

## 📅 일정

- **과제 제출 기한**: `2023-08-21 17:00`

------

## 애즈위메이크 백엔드 신입 개발자 채용 - 과제

### 기술 요구사항

1. Spring MVC
2. JPA
3. RDBMS
4. 테스트 코드 작성
5. 서비스 요구사항에 대한 API 제공

### 서비스 요구사항

1. 마트 권한과 일반 사용자 권한이 구분되어있다. 
   - spring security를 이용한 인가 구현 필요
2. 상품에 대한 생성, 수정, 삭제는 마트 권한이 필요하다.
   - spring security를 이용한 인증 구현 필요
3. 상품을 생성할 수 있다. 
   - 마트 권한 인증 필요
4. 상품 가격을 수정할 수 있다.
   - 마트 권한 인증 필요
5. 특정 시점의 상품 가격을 조회할 수 있다.
    - 예시
        - 2023-01-01 00:00:00 시점의 A 상품 가격 = 1500원
        - 2023-01-15 12:00:00 시점의 A 상품 가격 = 2000원
6. 상품을 삭제할 수 있다.
    - 마트 권한 인증 필요
7. 주문에 대한 총 금액을 계산할 수 있다.
    - (각 주문 목록의 상품가격 * 개수) + 배달비
8. 주문에 대한 필요 결제 금액을 계산할 수 있다.
    - 쿠폰을 적용하는 경우, 쿠폰으로 할인되는 금액을 반영해서 계산

### 필수 데이터

- 상품
    - 상품명
    - 가격
- 쿠폰
    - 쿠폰 적용 방법
        - 비율 / 고정
        - 각 적용 방법에 따른 적용 비율 / 적용 금액
    - 쿠폰 적용 범위
        - 주문 전체 (배달비 제외)
        - 특정 상품 한정 (특정 상품의 모든 개수에 적용)
- 주문
    - 주문 목록
        - 상품
        - 개수
    - 배달비

### 유의사항

- [x] 레포지토리 public 으로 설정 필요
- [ ] 실행방법 명시 필요
- [ ] 테스트 코드 작성 필요
- [ ] API 명세 필수
- [ ] 기본 데이터 삽입 필요