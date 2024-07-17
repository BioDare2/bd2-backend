/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  tzielins
 * Created: 17 lip 2024
 */

/* Migration */
ALTER TABLE biodare2_prod_v3.dbsystem_info
ADD COLUMN release_date DATE;

UPDATE biodare2_prod_v3.dbsystem_info
SET release_date = DATE_ADD(DATE(creation_date), INTERVAL 3 YEAR)
WHERE id > 1;

CREATE INDEX DBSystemInfo_idx_release_date_parent_id ON biodare2_prod_v3.dbsystem_info(release_date, entity_type, parent_id);


/* INCREASING EMBARGO for existing records of the given users:
- doing post factum subscription for Maria's users and their data */

UPDATE biodare2_prod_v3.dbsystem_info di
JOIN biodare2_prod_v3.entityacl as ea ON di.acl_id = ea.id
SET di.release_date = DATE_ADD(DATE(di.creation_date), INTERVAL 10 YEAR)
WHERE di.id > 1 and ea.owner_id IN (1766, 1837, 2564, 2942);


SELECT * FROM biodare2_prod_v3.dbsystem_info di JOIN biodare2_prod_v3.entityacl ea ON di.acl_id = ea.id where ea.owner_id IN (1766, 1837, 2564, 2942);