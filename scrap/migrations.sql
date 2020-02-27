
V2 -> V3 //search info

CREATE TABLE `search_info` (
  `id` bigint(20) NOT NULL,
  `creation_date` datetime DEFAULT NULL,
  `execution_date` datetime NOT NULL,
  `first_author` varchar(25) NOT NULL,
  `indexed_date` datetime DEFAULT NULL,
  `modification_date` datetime NOT NULL,
  `name` varchar(50) NOT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `SearchInfo_modificationDateIX` (`modification_date`),
  KEY `SearchInfo_executionDateIX` (`execution_date`),
  KEY `SearchInfo_indexedDateIX` (`indexed_date`),
  KEY `SearchInfo_nameIX` (`name`),
  KEY `SearchInfo_firstAuthorIX` (`first_author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table dbsystem_info add column search_info_id bigint;
alter table dbsystem_info add constraint FKhj1maldrtderlvedgk038erq5 foreign key (search_info_id) references search_info (id);

ALTER TABLE user_account CHANGE `system` `is_system` bit(1) NOT NULL;
ALTER TABLE user_group CHANGE `system` `is_system` bit(1) NOT NULL;

V2 -> V2A

CREATE TABLE `user_token` (
  `token` varchar(64) NOT NULL,
  `created` datetime DEFAULT NULL,
  `expiring` datetime NOT NULL,
  `kind` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`token`),
  KEY `UserToken_expiring` (`expiring`),
  KEY `FK5hgukmk3rmslg7we538w1chgm` (`user_id`),
  CONSTRAINT `FK5hgukmk3rmslg7we538w1chgm` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


V1->V2

ALTER TABLE `user_account` ADD COLUMN `registration_date` DATE NULL AFTER `version`;
ALTER TABLE `user_account` ADD COLUMN `activation_date` DATE NULL AFTER `version`;
ALTER TABLE `user_account` ADD COLUMN `creation_date` DATETIME NULL AFTER `version`;
ALTER TABLE `user_account` ADD COLUMN `modification_date` DATETIME NULL AFTER `version`;

update user_account set creation_date = now(), modification_date=now(), activation_date=curdate(), registration_date=curdate()   where id > 0;

ALTER TABLE `user_group` ADD COLUMN `creation_date` DATETIME NULL AFTER `version`;
ALTER TABLE `user_group` ADD COLUMN `modification_date` DATETIME NULL AFTER `version`;
update user_group set creation_date = now(), modification_date=now() where id > 0;

ALTER TABLE `entityacl` ADD COLUMN `creation_date` DATETIME NULL AFTER `version`;
ALTER TABLE `entityacl` ADD COLUMN `modification_date` DATETIME NULL AFTER `version`;
update entityacl set creation_date = now(), modification_date=now() where id > 0;

ALTER TABLE `dbsystem_info` ADD COLUMN `creation_date` DATETIME NULL AFTER `version`;
ALTER TABLE `dbsystem_info` ADD COLUMN `modification_date` DATETIME NULL AFTER `version`;
update dbsystem_info set creation_date = now(), modification_date=now() where id > 0;

create table rdmuser_aspect (account_id bigint not null, cohort integer, version bigint not null, primary key (account_id));
alter table rdmuser_aspect add constraint FK86vkodq29rckhgkho0elb2het foreign key (account_id) references user_account (id);

V0->V1

create table account_subscription (account_id bigint not null, kind integer, modified_date datetime not null, renew_date date not null, start_date date not null, version bigint not null, primary key (account_id));

alter table account_subscription add constraint FKf7vauoh9ualv7puopkhiejij1 foreign key (account_id) references user_account (id);

ALTER TABLE `user_account` CHANGE COLUMN `terms_versions` `terms_version` VARCHAR(255) NULL ;

ALTER TABLE `user_account` ADD COLUMN `failed_attempts` integer NOT NULL DEFAULT 0 AFTER `version`;

ALTER TABLE `user_account` ADD COLUMN `last_login` DATETIME NULL AFTER `failed_attempts`;

ALTER TABLE `user_account` ADD COLUMN `last_login_address` VARCHAR(255) NULL AFTER `last_login`;
