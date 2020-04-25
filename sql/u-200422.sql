alter table article_info add column custom_description varchar(500) default '' after `view`;
alter table article_info add column cover varchar(255) default '' after `view`;
alter table question_answer add column cover varchar(255) default '' after `view`;
alter table question_info add column cover varchar(255) default '' after `view`;