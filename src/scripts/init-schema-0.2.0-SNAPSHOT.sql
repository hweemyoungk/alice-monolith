drop table if exists blueprint;

drop table if exists `blueprint-id-sequence`;

drop table if exists card;

drop table if exists `card-id-sequence`;

drop table if exists redeem;

drop table if exists `redeem-id-sequence`;

drop table if exists `redeem-rule-id-sequence`;

drop table if exists redeem_rule;

drop table if exists `staged-user-id-sequence`;

drop table if exists staged_user;

drop table if exists `stamp-grant-id-sequence`;

drop table if exists stamp_grant;

drop table if exists store;

drop table if exists `store-id-sequence`;

create table blueprint
(
    is_deleted                   bit     not null,
    is_publishing                bit     not null,
    num_max_issues               integer not null,
    num_max_issues_per_customer  integer not null,
    num_max_redeems              integer not null,
    num_max_stamps               integer not null,
    created_date                 datetime(6),
    expiration_date              datetime(6) not null,
    id                           bigint  not null,
    last_modified_date           datetime(6),
    store_id                     bigint,
    display_name                 varchar(30),
    stamp_grant_cond_description varchar(100),
    description                  varchar(1000),
    bg_image_id                  varchar(255),
    primary key (id)
) engine=InnoDB;

create table `blueprint-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `blueprint-id-sequence`
values (1);

create table card
(
    is_deleted           bit     not null,
    is_discarded         bit     not null,
    is_favorite          bit     not null,
    is_inactive          bit     not null,
    is_used_out          bit     not null,
    num_collected_stamps integer not null,
    num_goal_stamps      integer not null,
    num_redeemed         integer not null,
    blueprint_id         bigint,
    created_date         datetime(6),
    id                   bigint  not null,
    last_modified_date   datetime(6),
    customer_id          binary(16) not null,
    display_name         varchar(30),
    bg_image_id          varchar(255),
    primary key (id)
) engine=InnoDB;

create table `card-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `card-id-sequence`
values (1);

create table redeem
(
    is_deleted         bit          not null,
    num_stamps_after   integer      not null,
    num_stamps_before  integer      not null,
    card_id            bigint,
    created_date       datetime(6),
    id                 bigint       not null,
    last_modified_date datetime(6),
    redeem_rule_id     bigint,
    display_name       varchar(30),
    redeem_request_id  varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table `redeem-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `redeem-id-sequence`
values (1);

create table `redeem-rule-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `redeem-rule-id-sequence`
values (1);

create table redeem_rule
(
    consumes           integer not null,
    is_deleted         bit     not null,
    blueprint_id       bigint  not null,
    created_date       datetime(6),
    id                 bigint  not null,
    last_modified_date datetime(6),
    display_name       varchar(30),
    description        varchar(100),
    image_id           varchar(255),
    primary key (id)
) engine=InnoDB;

create table `staged-user-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `staged-user-id-sequence`
values (1);

create table staged_user
(
    is_deleted         bit    not null,
    created_date       datetime(6),
    id                 bigint not null,
    last_modified_date datetime(6),
    user_id            binary(16) not null,
    display_name       varchar(30),
    primary key (id)
) engine=InnoDB;

create table `stamp-grant-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `stamp-grant-id-sequence`
values (1);

create table stamp_grant
(
    is_deleted         bit     not null,
    num_stamps         integer not null,
    num_stamps_after   integer not null,
    num_stamps_before  integer not null,
    card_id            bigint,
    created_date       datetime(6),
    id                 bigint  not null,
    last_modified_date datetime(6),
    display_name       varchar(30),
    primary key (id)
) engine=InnoDB;

create table store
(
    is_closed          bit    not null,
    is_deleted         bit    not null,
    is_inactive        bit    not null,
    lat                decimal(10, 7),
    lng                decimal(10, 7),
    zipcode            varchar(7),
    created_date       datetime(6),
    id                 bigint not null,
    last_modified_date datetime(6),
    phone              varchar(15),
    owner_id           binary(16) not null,
    display_name       varchar(30),
    address            varchar(120),
    description        varchar(1000),
    bg_image_id        varchar(255),
    profile_image_id   varchar(255),
    primary key (id)
) engine=InnoDB;

create table `store-id-sequence`
(
    next_val bigint
) engine=InnoDB;

insert into `store-id-sequence`
values (1);

create index idx_card_customer_id
    on card (customer_id);

create index idx_store_owner_id
    on store (owner_id);

alter table redeem
    add constraint uk_redeem_redeem_request_id unique (redeem_request_id);

alter table staged_user
    add constraint uk_staged_user_user_id unique (user_id);

alter table blueprint
    add constraint fk_blueprint_store_store_id
        foreign key (store_id)
            references store (id);

alter table card
    add constraint fk_card_blueprint_blueprint_id
        foreign key (blueprint_id)
            references blueprint (id);

alter table redeem
    add constraint fk_redeem_card_card_id
        foreign key (card_id)
            references card (id);

alter table redeem
    add constraint fk_redeem_redeem_rule_redeem_rule_id
        foreign key (redeem_rule_id)
            references redeem_rule (id);

alter table redeem_rule
    add constraint fk_redeem_rule_blueprint_blueprint_id
        foreign key (blueprint_id)
            references blueprint (id);

alter table stamp_grant
    add constraint fk_stamp_grant_card_card_id
        foreign key (card_id)
            references card (id);
