INSERT
INTO USERS
(NAME, EMAIL, PASSWORD, ROLE, CREATED_DATE, MODIFIED_DATE)
SELECT * FROM CSVREAD('classpath:/sample-data/user.csv');

INSERT
INTO ITEM
(NAME,PRICE,STOCK_QUANTITY,CREATED_DATE,MODIFIED_DATE)
SELECT * FROM CSVREAD('classpath:/sample-data/item.csv');

INSERT
INTO PRICE_HISTORY
(ITEM_ID,PRICE,CHANGED_DATE,PRICE_CHANGE_STATUS)
SELECT * FROM CSVREAD('classpath:/sample-data/price-history.csv');