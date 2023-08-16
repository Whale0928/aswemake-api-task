# 애즈위메이크

애즈위메이크의 과제전형 지원을 위한 프로젝트입니다.

## 📅 일정

- **과제 제출 기한**: `2023-08-21 17:00`

------

## 애즈위메이크 백엔드 신입 개발자 채용 - 과제

### 엔티티 설계

User (사용자)

- id
- name (사용자 이름)
- email (이메일 주소)
- password (비밀번호)
- role (권한: 마트 or 일반 사용자)

### Item (상품)

- id
- name (상품명)
- price (가격)
- priceHistory (가격 변동 내역)

### Coupon (쿠폰)

- id
- name (쿠폰 이름)
- couponType (적용 방법: 비율 / 고정)
- discountValue (적용 비율 / 적용 금액)
- couponScope (적용 범위: 주문 전체 or 특정 상품 한정)
- item (특정 상품 한정 쿠폰의 경우, 해당 상품 정보)

### Order (주문)

- id
- user (주문한 사용자)
- orderItems (주문 목록)
- deliveryFee (배달비)

### OrderItem (주문 상세)

- id
- item (상품)
- quantity (개수)

## API 설계

### User 관련

``` POST /users: 사용자 등록```<br>
``` GET /users/{id}: 사용자 정보 조회```<br>
``` PUT /users/{id}: 사용자 정보 수정```<br>

### Item 관련 (마트 권한 필요)

``` POST /items: 상품 생성 ``` <br>
``` GET /items/{id}: 상품 정보 조회 ``` <br>
``` PUT /items/{id}: 상품 가격 수정 ``` <br>
``` DELETE /items/{id}: 상품 삭제 ``` <br>
``` GET /items/{id}/price?date=YYYY-MM-DD: 특정 시점의 상품 가격 조회 ``` <br>

### Coupon 관련 (마트 권한 필요)

``` POST /coupons: 쿠폰 생성``` <br>
``` GET /coupons/{id}: 쿠폰 정보 조회``` <br>
``` PUT /coupons/{id}: 쿠폰 수정``` <br>
``` DELETE /coupons/{id}: 쿠폰 삭제``` <br>

### Order 관련

``` POST /orders: 주문 생성``` <br>
``` GET /orders/{id}: 주문 정보 조회``` <br>
``` GET /orders/{id}/total: 주문에 대한 총 금액 계산``` <br>
``` GET /orders/{id}/payment: 주문에 대한 필요 결제 금액 계산``` <br>

### 유의사항

- API 보안:
    - 마트 권한이 필요한 API는 특별한 인증 및 권한 검사가 필요.
- 가격 변동 내역:
    - 상품의 가격 변동 내역을 저장하기 위해 priceHistory를 추가. 이를 통해 특정 시점의 상품 가격을 조회할 수 있다.
- 쿠폰 적용:
    - 주문에 대한 필요 결제 금액 계산 시 쿠폰 적용 로직이 필요.

- [x] 레포지토리 public 으로 설정 필요
- [ ] 실행방법 명시 필요
- [ ] 테스트 코드 작성 필요
- [ ] API 명세 필수
- [ ] 기본 데이터 삽입 필요