## 주문
 - 하나의 주문에 하나의 쿠폰이 사용될 수 도 있다
 - 하나의 주문에 여러개의 주문상품이 포함될 수 있다
 - 여러 주문은 한 사용자(고객)에서 발생할 수 있다
```java
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Orders extends BaseEntity {

    // 주문한 사용자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    // 주문 상태
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 총 결제 금액
    private Long totalAmount;

    // 주문한 상품들의 목록
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 적용된 쿠폰 정보
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
@Getter
public enum OrderStatus {
    WAITING, PAID, CANCELLED
}

```

## 주문 상품
- 하나의 주문에 여러 주문 상품들이 있을 수 있다
- 하나의 상품에 여러 주문 상품들이 있을 수 있다 
```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderItem extends BaseEntity {

    // 주문 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders order;

    // 주문한 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 주문한 상품의 개수
    @Column(nullable = false)
    private int quantity;
}
```

## 상품
- 하나의 상품에 여러 주문 상품들이 있을 수 있다
- 하나의 상품에 여러 가격 변동 기록 이 있을 수 있다.
- 하나의 상품에 여러 쿠폰이 적용 대상이 될  수 있다.
```java
@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Item extends BaseEntity {
    // 상품 이름
    private String name;
    // 상품 가격
    private Long price;

    // 주문한 상품들의 목록
    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // 대상 상품들
    @Builder.Default
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Coupon> coupons = new ArrayList<>();

    // 가격 변동 이력
    @Builder.Default
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<PriceHistory> priceHistories = new ArrayList<>();
}
```

## 상품가격 히스토리
- 하나의 상품에 여러 가격 변동 기록 이 있을 수 있다.
```java
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PriceHistory extends BaseEntity {

    // 관련 상품 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 해당 시점에서의 상품 가격
    private Long price;

    // 가격이 변경된 날짜 및 시간
    private LocalDateTime changedDate;
}
```

## 사용자
- 하나의 사용자가 여러 주문을 발생할 수 있다
```java

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Users extends BaseEntity {
    // 사용자 이름
    @Column(nullable = false)
    private String name;

    // 이메일 주소
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 권한: 마트 or 일반 사용자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // 사용자의 경우 주문상품들
    @Builder.Default
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Orders> orderList = new ArrayList<>(); // 주문한 상품들의 목록
}
@Getter
public enum UserRole {
    MART, // 마트 권한
    USER  // 일반 사용자 권한
}

```

## 쿠폰
- 하나의 쿠폰 하나가 하나의 주문에 사용될 수 있다.
- 하나의 상품에 여러 쿠폰이 적용 대상이 될  수 있다.
```java
@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseEntity {
    // 쿠폰 이름
    private String name;

    // 쿠폰 적용 방법 (비율 또는 고정)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    // 적용 비율 또는 적용 금액
    private Double discountValue;

    // 적용 범위 ( 또는 특정 상품 한정)
    // 주문 전체(배달비 제외)
    // 특정 상품 한정 (특정 상품의 모든 개수에 적용)
    @Enumerated(EnumType.STRING)
    private CouponScope couponScope;

    // 사용된 주문
    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL)
    private Orders orders;

    // 특정 상품 한정 쿠폰의 경우, 해당 상품 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void addItem(Item item) {
        this.item = item;
        item.getCoupons().add(this);
    }

    public void removeItem() {
        item.getCoupons().remove(this);
        this.item = null;
    }
}
@Getter
public enum CouponScope {
    ALL_ORDER, // 주문 전체
    SPECIFIC_ITEM // 특정 상품 한정
}
@Getter
public enum CouponType {
    PERCENTAGE, // 비율 할인
    FIXED // 고정 금액 할인
}
```
