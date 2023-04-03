SELECT SUBSTRING_INDEX(email, '.', -1) as country, institution, count(*) as nr FROM biodare2_prod_v3.user_account where last_login > '2022-04-03' group by SUBSTRING_INDEX(email, '.', -1), institution order by nr desc;

SELECT count(*) as nr FROM biodare2_prod_v3.user_account group by SUBSTRING_INDEX(email, '.', -1), institution order by nr desc;

SELECT SUBSTRING_INDEX(email, '.', -1) from biodare2_prod_v3.user_account;

select count(*) FROM biodare2_prod_v3.user_account where last_login > '2022-04-03';

SELECT SUBSTRING_INDEX(email, '.', -1) as country, SUBSTRING_INDEX(email, '\@', -1) as domain, institution, count(*) as nr FROM biodare2_prod_v3.user_account where last_login > '2022-04-03' group by SUBSTRING_INDEX(email, '.', -1), SUBSTRING_INDEX(email, '\@', -1), institution order by country;

SELECT SUBSTRING_INDEX(email, '.', -1) as country, SUBSTRING_INDEX(email, '\@', -1) as domain, institution, count(*) as nr FROM biodare2_prod_v3.user_account group by SUBSTRING_INDEX(email, '.', -1), SUBSTRING_INDEX(email, '\@', -1), institution order by country;

select count(*) FROM biodare2_prod_v3.user_account

