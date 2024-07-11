SELECT SUBSTRING_INDEX(email, '.', -1) as country, institution, count(*) as nr FROM biodare2_prod_v3.user_account where last_login > '2022-04-03' group by SUBSTRING_INDEX(email, '.', -1), institution order by nr desc;

SELECT count(*) as nr FROM biodare2_prod_v3.user_account group by SUBSTRING_INDEX(email, '.', -1), institution order by nr desc;

SELECT SUBSTRING_INDEX(email, '.', -1) from biodare2_prod_v3.user_account;

select count(*) FROM biodare2_prod_v3.user_account where last_login > '2022-04-03';

SELECT SUBSTRING_INDEX(email, '.', -1) as country, SUBSTRING_INDEX(email, '\@', -1) as domain, institution, count(*) as nr FROM biodare2_prod_v3.user_account where last_login > '2022-04-03' group by SUBSTRING_INDEX(email, '.', -1), SUBSTRING_INDEX(email, '\@', -1), institution order by country;

SELECT SUBSTRING_INDEX(email, '.', -1) as country, SUBSTRING_INDEX(email, '\@', -1) as domain, institution, count(*) as nr FROM biodare2_prod_v3.user_account group by SUBSTRING_INDEX(email, '.', -1), SUBSTRING_INDEX(email, '\@', -1), institution order by country;

select count(*) FROM biodare2_prod_v3.user_account

getting number of exp per YEAR
SELECT year(creation_date) AS YEAR, COUNT(*) As NR FROM biodare2_prod_v3.dbsystem_info group by year(creation_date) order by YEAR;


getting number of users depositing per year
SELECT YEAR(creation_date) as year, COUNT(DISTINCT owner_id) FROM biodare2_prod_v3.entityacl group by YEAR(creation_date) order by year;