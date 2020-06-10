update article_info i
    inner join article_version v on i.id = v.article_id and i.version = v.version
set i.title = v.title,
    i.description = v.description,
    i.column_id = v.column_id,
    i.source_type = v.source_type,
    i.source_data = v.source_data;

insert into article_tag
select i.id,t.tag from article_info i
                           inner join article_version v on i.id = v.article_id and i.version = v.version
                           inner join article_version_tag t on t.version = v.id;

update question_info i
    inner join question_version v on i.id = v.question_id and i.version = v.version
set i.title = v.title,
    i.description = v.description,
    i.reward_type = v.reward_type,
    i.reward_add = v.reward_add,
    i.reward_value = v.reward_value;

insert into question_tag
select i.id,t.tag from question_info i
                           inner join question_version v on i.id = v.question_id and i.version = v.version
                           inner join question_version_tag t on t.version = v.id;

update question_answer i
    inner join question_answer_version v on i.id = v.answer_id and i.version = v.version
set i.description = v.description;

update tag i
    inner join tag_version v on i.id = v.tag_id and i.version = v.version
set i.description = v.description;