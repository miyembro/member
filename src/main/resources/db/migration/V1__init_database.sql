CREATE TABLE member
(
    member_id          uuid DEFAULT uuid_generate_v4() NOT NULL,
    first_name        VARCHAR(255),
    last_name         VARCHAR(255),
    email             VARCHAR(255),
    password          VARCHAR(255),
    phone_number      VARCHAR(255),
    profile_pic_url   VARCHAR(255),
    birth_date        date,
    login_type        VARCHAR(255) NOT NULL,
    member_address_id UUID,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    CONSTRAINT pk_member PRIMARY KEY (member_id)
);

CREATE TABLE member_address
(
    member_address_id UUID DEFAULT uuid_generate_v4() NOT NULL,
    street            VARCHAR(255),
    city              VARCHAR(255),
    province_state    VARCHAR(255),
    postal_code       VARCHAR(255),
    country           VARCHAR(255),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    CONSTRAINT pk_memberaddress PRIMARY KEY (member_address_id)
);

CREATE TABLE member_role
(
    organization_id UUID NOT NULL,
    member_id       UUID NOT NULL,
    role_id         UUID NOT NULL,
    CONSTRAINT pk_memberrole PRIMARY KEY (organization_id, member_id, role_id)
);

CREATE TABLE membership
(
    membership_id        UUID DEFAULT uuid_generate_v4() NOT NULL,
    organization_id      UUID,
    member_id            UUID NOT NULL,
    membership_type_id   UUID NULL,
    membership_status_id UUID NOT NULL,
    start_date           TIMESTAMP WITHOUT TIME ZONE,
    end_date             TIMESTAMP WITHOUT TIME ZONE,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    CONSTRAINT pk_membership PRIMARY KEY (membership_id)
);

CREATE TABLE membership_status
(
    membership_status_id UUID DEFAULT uuid_generate_v4() NOT NULL,
    name                 VARCHAR(255),
    description          VARCHAR(255),
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_membershipstatus PRIMARY KEY (membership_status_id)
);

CREATE TABLE membership_type
(
    membership_type_id          UUID DEFAULT uuid_generate_v4() NOT NULL,
    organization_id             UUID,
    membership_type_validity_id UUID,
    name                        VARCHAR(255),
    description                 VARCHAR(255),
    is_default                  BOOLEAN,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    CONSTRAINT pk_membershiptype PRIMARY KEY (membership_type_id)
);

CREATE TABLE membership_type_validity
(
    membership_type_validity_id UUID DEFAULT uuid_generate_v4() NOT NULL,
    name                        VARCHAR(255),
    duration                    INTEGER,
    description                 VARCHAR(255),
    created_at                  TIMESTAMP WITHOUT TIME ZONE,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_membershiptypevalidity PRIMARY KEY (membership_type_validity_id)
);

CREATE TABLE permission
(
    permission_id UUID DEFAULT uuid_generate_v4() NOT NULL,
    name          VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_permission PRIMARY KEY (permission_id)
);

CREATE TABLE role
(
    role_id    UUID DEFAULT uuid_generate_v4() NOT NULL,
    name       VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_role PRIMARY KEY (role_id)
);

CREATE TABLE role_permission
(
    permission_id UUID NOT NULL,
    role_id       UUID NOT NULL,
    CONSTRAINT pk_role_permission PRIMARY KEY (permission_id, role_id)
);

ALTER TABLE member
    ADD CONSTRAINT uc_member_member_address UNIQUE (member_address_id);

ALTER TABLE member_role
    ADD CONSTRAINT FK_MEMBERROLE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE member_role
    ADD CONSTRAINT FK_MEMBERROLE_ON_ROLE FOREIGN KEY (role_id) REFERENCES role (role_id);

ALTER TABLE membership_type
    ADD CONSTRAINT FK_MEMBERSHIPTYPE_ON_MEMBERSHIP_TYPE_VALIDITY FOREIGN KEY (membership_type_validity_id) REFERENCES membership_type_validity (membership_type_validity_id);

ALTER TABLE membership
    ADD CONSTRAINT FK_MEMBERSHIP_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (member_id);

ALTER TABLE membership
    ADD CONSTRAINT FK_MEMBERSHIP_ON_MEMBERSHIP_STATUS FOREIGN KEY (membership_status_id) REFERENCES membership_status (membership_status_id);

ALTER TABLE membership
    ADD CONSTRAINT FK_MEMBERSHIP_ON_MEMBERSHIP_TYPE FOREIGN KEY (membership_type_id) REFERENCES membership_type (membership_type_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_MEMBER_ADDRESS FOREIGN KEY (member_address_id) REFERENCES member_address (member_address_id);

ALTER TABLE role_permission
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permission (permission_id);

ALTER TABLE role_permission
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES role (role_id);

INSERT INTO permission (permission_id, name, created_at, updated_at)
VALUES
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.viewAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.viewOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.viewOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.createAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.createOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.createOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.editAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.editOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.editOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.deleteAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.user.deleteOrgOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.viewAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.viewOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.viewOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.createAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.createOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.createOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.editAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.editOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.editOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.deleteAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.organization.deleteOrgOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.viewAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.viewOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.viewOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.createAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.createOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.createOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.editAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.editOrgAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.editOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.deleteAll', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'com.rjproj.memberapp.permission.event.deleteOrgOwn', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



INSERT INTO role (role_id, name, created_at, updated_at)
VALUES
    (uuid_generate_v4(), 'Super Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Member', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Non-Member', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         JOIN permission p ON p.name IN (
                                         'com.rjproj.memberapp.permission.user.viewAll',
                                         'com.rjproj.memberapp.permission.user.viewOrgAll',
                                         'com.rjproj.memberapp.permission.user.viewOwn',
                                         'com.rjproj.memberapp.permission.user.createAll',
                                         'com.rjproj.memberapp.permission.user.createOrgAll',
                                         'com.rjproj.memberapp.permission.user.createOwn',
                                         'com.rjproj.memberapp.permission.user.editAll',
                                         'com.rjproj.memberapp.permission.user.editOrgAll',
                                         'com.rjproj.memberapp.permission.user.editOwn',
                                         'com.rjproj.memberapp.permission.user.deleteAll',
                                         'com.rjproj.memberapp.permission.user.deleteOrgOwn',
                                         'com.rjproj.memberapp.permission.organization.viewAll',
                                         'com.rjproj.memberapp.permission.organization.viewOrgAll',
                                         'com.rjproj.memberapp.permission.organization.viewOwn',
                                         'com.rjproj.memberapp.permission.organization.createAll',
                                         'com.rjproj.memberapp.permission.organization.createOrgAll',
                                         'com.rjproj.memberapp.permission.organization.createOwn',
                                         'com.rjproj.memberapp.permission.organization.editAll',
                                         'com.rjproj.memberapp.permission.organization.editOrgAll',
                                         'com.rjproj.memberapp.permission.organization.editOwn',
                                         'com.rjproj.memberapp.permission.organization.deleteAll'
    )
WHERE r.name = 'Super Admin';


INSERT INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         JOIN permission p ON p.name IN (
                                         'com.rjproj.memberapp.permission.user.viewAll',
                                         'com.rjproj.memberapp.permission.user.viewOrgAll',
                                         'com.rjproj.memberapp.permission.user.viewOwn',
                                         'com.rjproj.memberapp.permission.user.createAll',
                                         'com.rjproj.memberapp.permission.user.createOrgAll',
                                         'com.rjproj.memberapp.permission.user.createOwn',
                                         'com.rjproj.memberapp.permission.user.editAll',
                                         'com.rjproj.memberapp.permission.user.editOrgAll',
                                         'com.rjproj.memberapp.permission.user.editOwn',
                                         'com.rjproj.memberapp.permission.user.deleteAll',
                                         'com.rjproj.memberapp.permission.user.deleteOrgOwn',
                                         'com.rjproj.memberapp.permission.organization.viewAll',
                                         'com.rjproj.memberapp.permission.organization.viewOrgAll',
                                         'com.rjproj.memberapp.permission.organization.viewOwn',
                                         'com.rjproj.memberapp.permission.organization.createAll',
                                         'com.rjproj.memberapp.permission.organization.createOrgAll',
                                         'com.rjproj.memberapp.permission.organization.createOwn',
                                         'com.rjproj.memberapp.permission.organization.editAll',
                                         'com.rjproj.memberapp.permission.organization.editOrgAll',
                                         'com.rjproj.memberapp.permission.organization.editOwn',
                                         'com.rjproj.memberapp.permission.organization.deleteAll'
    )
WHERE r.name = 'Admin';


INSERT INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         JOIN permission p ON p.name IN (
                                         'com.rjproj.memberapp.permission.user.viewAll',
                                         'com.rjproj.memberapp.permission.user.viewOrgAll',
                                         'com.rjproj.memberapp.permission.user.viewOwn',
                                         'com.rjproj.memberapp.permission.user.createOwn',
                                         'com.rjproj.memberapp.permission.user.editOwn',
                                         'com.rjproj.memberapp.permission.user.deleteOrgOwn',
                                         'com.rjproj.memberapp.permission.organization.viewAll',
                                         'com.rjproj.memberapp.permission.organization.viewOrgAll',
                                         'com.rjproj.memberapp.permission.organization.viewOwn',
                                         'com.rjproj.memberapp.permission.organization.createOwn',
                                         'com.rjproj.memberapp.permission.organization.editOwn'
    )
WHERE r.name = 'Member';


INSERT INTO role_permission (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM role r
         JOIN permission p ON p.name IN (
                                         'com.rjproj.memberapp.permission.user.viewAll',
                                         'com.rjproj.memberapp.permission.user.createOwn',
                                         'com.rjproj.memberapp.permission.user.editOwn',
                                         'com.rjproj.memberapp.permission.organization.viewAll',
                                         'com.rjproj.memberapp.permission.organization.createOwn',
                                         'com.rjproj.memberapp.permission.organization.editOwn'
    )
WHERE r.name = 'Non-Member';



INSERT INTO membership_type_validity (membership_type_validity_id, name, duration, description, created_at, updated_at)
VALUES
    (uuid_generate_v4(), 'Ends after 1 year', 365, 'Valid for one year', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Ends on January 1', NULL, 'Valid until January 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Never expires', NULL, 'No expiration date', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO membership_status (membership_status_id, name, description, created_at, updated_at)
VALUES
    (uuid_generate_v4(), 'Active', 'The membership is currently active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Expired', 'The membership has expired', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Pending', 'The membership is awaiting approval', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Cancelled', 'The membership has been cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Owner', 'The membership status is for the owner of the organization.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (uuid_generate_v4(), 'Denied', 'Denied join request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



